package monoServer.http;

import monoServer.request.HttpRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

public class DeleteRequest extends HttpLancher {


    public static void delete(String url,Command command) throws UnsupportedEncodingException {
        //1.设置路径参数
        url = wrapPathParams(url,command.httpRequest.getPathVaribles());
        //2.设置url参数
        url = wrapParams(url,command.httpRequest);
        HttpDelete httpDelete = new HttpDelete(url);
        //httpGet.setHeader("Content-Type","application/json;charset=UTF-8");
        //3.设置header
        wrapHeaders(httpDelete,command.httpRequest);
        HttpAsyncClient.AsyncHttpClient.getInstance().execute(httpDelete, new CallBack(command,url));
    }

}
