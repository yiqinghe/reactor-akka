package monoServer.http;

import monoServer.request.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

public class PutRequest extends HttpLancher {


    public static void put(String url,Command command) throws UnsupportedEncodingException {
        url = wrapPathParams(url,command.httpRequest.getPathVaribles());
        HttpPut httpPut = new HttpPut(url);
        StringEntity entity = new StringEntity(command.httpRequest.getRequestBody());
        httpPut.setEntity(entity);
        url = packParamsAndUrl(httpPut,command.httpRequest,url);
        HttpAsyncClient.AsyncHttpClient.getInstance().execute(httpPut,new CallBack(command,url));
    }
}
