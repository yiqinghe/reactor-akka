package monoServer.http;

import monoServer.request.HttpRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

public class DeleteRequest extends HttpLancher {


    public static void delete(String url,Command command) throws UnsupportedEncodingException {
        HttpDelete httpDelete= new HttpDelete(url);
        packParamsAndUrl(httpDelete,command.httpRequest,url);
        HttpAsyncClient.AsyncHttpClient.getInstance().execute(httpDelete,new CallBack(command,url));
    }

}
