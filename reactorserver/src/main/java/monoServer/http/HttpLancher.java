package monoServer.http;

import com.netflix.ribbon.proxy.annotation.Http;
import feign.Request;
import monoServer.MyRoundLoadBalancer;
import monoServer.SpringContext;
import monoServer.request.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.AbstractExecutionAwareRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public  class HttpLancher {

    public static void lanchRequest(String url, Command command) throws UnsupportedEncodingException {
        switch (command.httpRequest.getRequestMethod()){
            case GET:
                GetRequest.get(url,command);
                break;
            case POST:
                PostRequest.post(url,command);
                break;
            case PUT:
                PutRequest.put(url,command);
                break;
            case DELETE:
                DeleteRequest.delete(url,command);
                break;
            default:
                break;
        }
    }

    /**
     　　* 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     　　* @param params 需要排序并参与字符拼接的参数组
     　　* @return 拼接后字符串
     　　* @throws UnsupportedEncodingException
     　　*/
    public static String createUrlParams(Map<String, String> params) throws UnsupportedEncodingException {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            value = URLEncoder.encode(value, "UTF-8");
            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    private static void wrapHeaders(AbstractExecutionAwareRequest request, HttpRequest requestParams){
        //把请求头也放进去
        if(requestParams.getHeaders() != null && requestParams.getHeaders().size() > 0) {
            for (Map.Entry<String, String> entry : requestParams.getHeaders().entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    public static String wrapPathParams(String url, Map<String,String> pathVaribles){
        //eg localhost:8080/hello/{name}
        for(Map.Entry<String,String> entry : pathVaribles.entrySet()){
            String path = "{".concat(entry.getKey()).concat("}");
            if(url.contains(path)){
                url = url.replace(path,entry.getValue());
            }
        }
        return url;
    }

    private static String wrapParams(String url, HttpRequest requestParams) throws UnsupportedEncodingException {
        return url.concat("?").concat(createUrlParams(requestParams.getParams()));
    }



    public static String packParamsAndUrl(AbstractExecutionAwareRequest request, HttpRequest requestParams
        ,String url) throws UnsupportedEncodingException {
        //1.设置headers
        wrapHeaders(request,requestParams);
        //2.设置url参数
        url = wrapParams(url,requestParams);
        return url;
    }

    public static class CallBack implements FutureCallback<HttpResponse> {

        private Command command;
        private String url;

        public CallBack(Command command, String url) {
            this.command = command;
            this.url = url;
        }

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
            System.out.println("failed:"+url + "->" + ex);
            command.httpRequest.setHttpResult("failed");
            SpringContext.getBean(MyRoundLoadBalancer.class).notifeFailedNode(command.serviceInstance);
            command.asynCommandListener.onFail(command.context,ex);

        }

        public void cancelled() {
            System.out.println("cancelled:"+url + " cancelled");
            command.httpRequest.setHttpResult("cancelled");
            command.asynCommandListener.onSuccess(command.context,"cancelled");
        }
    }
}
