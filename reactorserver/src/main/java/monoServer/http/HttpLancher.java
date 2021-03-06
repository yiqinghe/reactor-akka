package monoServer.http;

import com.netflix.ribbon.proxy.annotation.Http;
import feign.Request;
import monoServer.MyRoundLoadBalancer;
import monoServer.SpringContext;
import monoServer.request.HttpRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.AbstractExecutionAwareRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static org.apache.commons.lang.CharEncoding.UTF_8;

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

    public static void wrapHeaders(AbstractExecutionAwareRequest request, HttpRequest requestParams){
        //把请求头也放进去
        if(requestParams.getHeaders() != null && requestParams.getHeaders().size() > 0) {
            for (Map.Entry<String, String> entry : requestParams.getHeaders().entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        //httpGet.setHeader("Content-Type","application/json;charset=UTF-8")
        if(!(requestParams.getHeaders() != null && requestParams.getHeaders().get("Content-Type") != null)){
            request.addHeader("Content-Type","application/json;charset=UTF-8");
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

    public static String wrapParams(String url, HttpRequest requestParams) throws UnsupportedEncodingException {
        return url.concat("?").concat(createUrlParams(requestParams.getParams()));
    }



    public static class CallBack implements FutureCallback<HttpResponse> {

        private Command command;
        private String url;

        public CallBack(Command command, String url) {
            this.command = command;
            this.url = url;
        }

        @Override
        public void completed(final HttpResponse response) {
            String body = "";
            try {
                // http 访问成功，回调
                HttpEntity entity = response.getEntity();
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                    final InputStream instream = entity.getContent();
                    try {
                        final StringBuilder sb = new StringBuilder();
                        final char[] tmp = new char[1024];
                        final Reader reader = new InputStreamReader(instream,UTF_8);
                        int l;
                        while ((l = reader.read(tmp)) != -1) {
                            sb.append(tmp, 0, l);
                        }
                        body = sb.toString();
                    } finally {
                        instream.close();
                        EntityUtils.consume(entity);
                    }
                    command.httpRequest.setHttpResult("success");
                    command.asynCommandListener.onSuccess(command.context,body);
                }else{
                    command.httpRequest.setHttpResult("failed");
                    SpringContext.getBean(MyRoundLoadBalancer.class).notifyFailedNode(command.serviceInstance);
                    command.asynCommandListener.onFail(command.context,new Throwable(response.getStatusLine().getReasonPhrase()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failed(final Exception ex) {
            // http 访问失败，回调
            System.out.println("failed:"+url + "->" + ex);
            command.httpRequest.setHttpResult("failed");
            SpringContext.getBean(MyRoundLoadBalancer.class).notifyFailedNode(command.serviceInstance);
            command.asynCommandListener.onFail(command.context,ex);

        }

        @Override
        public void cancelled() {
            System.out.println("cancelled:"+url + " cancelled");
            command.httpRequest.setHttpResult("cancelled");
            command.asynCommandListener.onSuccess(command.context,"cancelled");
        }
    }
}
