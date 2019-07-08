package monoServer.http;

import monoServer.MyRoundLoadBalancer;
import monoServer.SpringContext;
import monoServer.common.Contance;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.cloud.client.ServiceInstance;
import java.io.UnsupportedEncodingException;

public class HttpAsyncClient {



    private static HttpAsyncClient asynRpcInovker;


    public HttpAsyncClient asynCall(Command command) throws UnsupportedEncodingException {

        MyRoundLoadBalancer myRoundLoadBalancer = SpringContext.getBean(MyRoundLoadBalancer.class);
        ServiceInstance chose = myRoundLoadBalancer.rotationChose(command.httpRequest.getServiceName());
        command.serviceInstance = chose;
        String url = chose.getUri().toString() + "/" + command.httpRequest.getRequestPath();
        HttpLancher.lanchRequest(url,command);
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
    static public class AsyncHttpClient {
        static CloseableHttpAsyncClient client;
        public static  CloseableHttpAsyncClient getInstance(){
            if(client==null){
                synchronized (AsyncHttpClient.class){
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
    }
}
