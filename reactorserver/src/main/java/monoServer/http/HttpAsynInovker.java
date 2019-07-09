package monoServer.http;

import monoServer.SpringContext;
import monoServer.MyRoundLoadBalancer;
import monoServer.Request;
import monoServer.common.Contance;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;
import org.springframework.cloud.client.ServiceInstance;

public class HttpAsynInovker {

    public static class Command {

        public Request request;
        final public long startTime;
        public ServiceInstance serviceInstance;



        final public CommandDoneListener commandDoneListener;

        public Command(Request request, long startTime, CommandDoneListener commandDoneListener) {
            this.request = request;
            this.startTime = startTime;
            this.commandDoneListener = commandDoneListener;
        }

        public interface CommandDoneListener {
            void recall(Request request);
        }
    }

    private static HttpAsynInovker asynRpcInovker;


    public HttpAsynInovker asynCall(Command command) {

        MyRoundLoadBalancer myRoundLoadBalancer = SpringContext.getBean(MyRoundLoadBalancer.class);
        ServiceInstance chose = myRoundLoadBalancer.rotationChose("feign-server");
        command.serviceInstance = chose;
        AsynHttpClient.get(chose.getUri().toString()+"/hi",command);
        return asynRpcInovker;
    }


    public static HttpAsynInovker getInstance() {
        if (asynRpcInovker == null) {
            synchronized (HttpAsynInovker.class) {
                if (asynRpcInovker == null) {
                    asynRpcInovker = new HttpAsynInovker();
                }
            }
        }
        return asynRpcInovker;
    }

    /**
     * 异步http
     */
    static public class AsynHttpClient {
        static CloseableHttpAsyncClient client;
        public static  CloseableHttpAsyncClient getInstance(){
            if(client==null){
                synchronized (AsynHttpClient.class){
                    if(client==null){
                        RequestConfig requestConfig = RequestConfig.custom()
                                .setConnectTimeout(Contance.setConnectTimeout)
                                .setSocketTimeout(Contance.setSocketTimeout)
                                .setConnectionRequestTimeout(Contance.setConnectionRequestTimeout)
                                .build();

                        //配置io线程
                        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().
                                setIoThreadCount(Runtime.getRuntime().availableProcessors())
                                .setSoKeepAlive(true)
                                .build();
                        //设置连接池大小
                        ConnectingIOReactor ioReactor=null;
                        try {
                            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
                        } catch (IOReactorException e) {
                            e.printStackTrace();
                        }
                        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
                        connManager.setMaxTotal(Contance.setMaxTotal);
                        connManager.setDefaultMaxPerRoute(Contance.setDefaultMaxPerRoute);

                        client = HttpAsyncClients.custom().
                                setConnectionManager(connManager)
                                .setDefaultRequestConfig(requestConfig)
                                .build();
                        client.start();

                    }
                }
            }

            return client;
        }
        public static void get(String url, HttpAsynInovker.Command command){
            final HttpGet request2 = new HttpGet(url);
            getInstance().execute(request2, new FutureCallback<HttpResponse>() {

                public void completed(final HttpResponse response2) {
                    // System.out.println(request2.getRequestLine() + "->" + response2.getStatusLine());
                    try {
                        // http 访问成功，回调
                        System.out.println(EntityUtils.toString(response2.getEntity()));
                        command.request.setHttpResult("success");
                        command.commandDoneListener.recall(command.request);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void failed(final Exception ex) {
                    // http 访问失败，回调
                    System.out.println("failed:"+request2.getRequestLine() + "->" + ex);
                    command.request.setHttpResult("failed");
                    SpringContext.getBean(MyRoundLoadBalancer.class).notifyFailedNode(command.serviceInstance);
                    command.commandDoneListener.recall(command.request);

                }

                public void cancelled() {
                    System.out.println("cancelled:"+request2.getRequestLine() + " cancelled");
                    command.request.setHttpResult("cancelled");
                    command.commandDoneListener.recall(command.request);
                }

            });
        }
    }

}
