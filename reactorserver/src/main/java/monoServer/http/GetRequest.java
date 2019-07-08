package monoServer.http;

import monoServer.request.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.UnsupportedEncodingException;

public class GetRequest extends HttpLancher {

    public static void get(String url, Command command) throws UnsupportedEncodingException {
        final HttpGet httpGet = new HttpGet(url);
        //1.设置headers
        //wrapHeaders(httpGet,command.httpRequest);
        //2 设置pathvarible
       // wrapPathParams(url,command.httpRequest.getPathVaribles());
        //3 设置请求参数
        url = packParamsAndUrl(httpGet,command.httpRequest,url);
        HttpAsyncClient.AsyncHttpClient.getInstance().execute(httpGet, new CallBack(command,url));
    }

}
