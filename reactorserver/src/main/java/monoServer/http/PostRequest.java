package monoServer.http;

import monoServer.request.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

public class PostRequest extends HttpLancher {


    public static void post(String url,Command command) throws UnsupportedEncodingException {
        url = wrapPathParams(url,command.httpRequest.getPathVaribles());
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(command.httpRequest.getRequestBody());
        httpPost.setEntity(entity);
        url = packParamsAndUrl(httpPost,command.httpRequest,url);
        HttpAsyncClient.AsyncHttpClient.getInstance().execute(httpPost,new CallBack(command,url));
    }
}
