package monoServer.http;

import monoServer.request.HttpRequest;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

public class PostRequest extends HttpLancher {


    public static void post(String url,Command command) throws UnsupportedEncodingException {
        //2.设置路径参数
        url = wrapPathParams(url,command.httpRequest.getPathVaribles());
        //1.设置url参数
        url = wrapParams(url,command.httpRequest);
        HttpPost httpPost = new HttpPost(url);
        //3.设置header
        wrapHeaders(httpPost,command.httpRequest);
        //4.设置body
        StringEntity entity = new StringEntity(command.httpRequest.getRequestBody());

        httpPost.setEntity(entity);
        HttpAsyncClient.AsyncHttpClient.getInstance().execute(httpPost,new CallBack(command,url));
    }
}
