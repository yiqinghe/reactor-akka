package monoServer.http;

import monoServer.request.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.UnsupportedEncodingException;

public class GetRequest extends HttpLancher {

    public static void get(String url, Command command) throws UnsupportedEncodingException {
        //1.设置路径参数
        url = wrapPathParams(url,command.httpRequest.getPathVaribles());
        //2.设置url参数
        url = wrapParams(url,command.httpRequest);
        HttpGet httpGet = new HttpGet(url);
       //httpGet.setHeader("Content-Type","application/json;charset=UTF-8");
        //3.设置header
        wrapHeaders(httpGet,command.httpRequest);
        HttpAsyncClient.AsyncHttpClient.getInstance().execute(httpGet, new CallBack(command,url));
    }

}
