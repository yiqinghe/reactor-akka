package monoServer.http;

import monoServer.MyRoundLoadBalancer;
import monoServer.SpringContext;
import monoServer.common.ActContext;
import monoServer.common.Contance;
import monoServer.listener.AsynCommandListener;
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

public class HttpAsyncClient {

    public static class Command {

        public ActContext context;
        final public long startTime;
        public ServiceInstance serviceInstance;
        private final monoServer.request.HttpRequest httpRequest;



        final public AsynCommandListener asynCommandListener;

        public Command(ActContext context, long startTime, AsynCommandListener asynCommandListener
        ,monoServer.request.HttpRequest httpRequest) {
            this.context = context;
            this.startTime = startTime;
            this.asynCommandListener = asynCommandListener;
            this.httpRequest = httpRequest;
        }

    }

    private static HttpAsyncClient asynRpcInovker;


    public HttpAsyncClient asynCall(Command command) {

        MyRoundLoadBalancer myRoundLoadBalancer = SpringContext.getBean(MyRoundLoadBalancer.class);
        ServiceInstance chose = myRoundLoadBalancer.rotationChose(command.httpRequest.getServiceName());
        command.serviceInstance = chose;
        if(feign.Request.HttpMethod.GET.equals(command.httpRequest.getRequestMethod())) {
            AsynHttpClient.get(chose.getUri().toString() + "/" + command.httpRequest.getRequestUrl(), command);
        }else if(feign.Request.HttpMethod.POST.equals(command.httpRequest.getRequestMethod())){

        }
        return asynRpcInovker;
    }


    public static HttpAsyncClient getInstance() {
        if (asynRpcInovker == null) {
            synchronized (HttpAsyncClient.class) {
                if (asynRpcInovker == null) {
                    asynRpcInovker = new HttpAsyncClient();
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
        public static void get(String url, HttpAsyncClient.Command command){
            final HttpGet request2 = new HttpGet(url);
            getInstance().execute(request2, new FutureCallback<HttpResponse>() {

                public void completed(final HttpResponse response2) {
                    // System.out.println(request2.getRequestLine() + "->" + response2.getStatusLine());
                    try {
                        // http 访问成功，回调
                        System.out.println(EntityUtils.toString(response2.getEntity()));
                        command.httpRequest.setHttpResult("success");
                        command.asynCommandListener.onSuccess(command.context,EntityUtils.toString(response2.getEntity()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void failed(final Exception ex) {
                    // http 访问失败，回调
                    System.out.println("failed:"+request2.getRequestLine() + "->" + ex);
                    command.httpRequest.setHttpResult("failed");
                    SpringContext.getBean(MyRoundLoadBalancer.class).notifeFailedNode(command.serviceInstance);
                    command.asynCommandListener.onFail(command.context,ex);

                }

                public void cancelled() {
                    System.out.println("cancelled:"+request2.getRequestLine() + " cancelled");
                    command.httpRequest.setHttpResult("cancelled");
                    command.asynCommandListener.onSuccess(command.context,"cancelled");
                }

            });
        }
    }

}
