package monoServer.http;

import monoServer.request.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.UnsupportedEncodingException;

public class GetRequest extends HttpLancher {

    public static void get(String url, Command command) throws UnsupportedEncodingException {
        //2.设置路径参数
        url = wrapPathParams(url,command.httpRequest.getPathVaribles());
        final HttpGet httpGet = new HttpGet(url);
        url = packParamsAndUrl(httpGet,command.httpRequest,url);
        HttpAsyncClient.AsyncHttpClient.getInstance().execute(httpGet, new CallBack(command,url));
    }

}
