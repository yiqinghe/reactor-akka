package monoServer.http;

import monoServer.request.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

public class PutRequest extends HttpLancher {


    public static void put(String url,Command command) throws UnsupportedEncodingException {
        //2.设置路径参数
        url = wrapPathParams(url,command.httpRequest.getPathVaribles());
        //1.设置url参数
        url = wrapParams(url,command.httpRequest);
        HttpPut httpPut = new HttpPut(url);
        //3.设置header
        wrapHeaders(httpPut,command.httpRequest);
        //4.设置body
        StringEntity entity = new StringEntity(command.httpRequest.getRequestBody());

        httpPut.setEntity(entity);
        HttpAsyncClient.AsyncHttpClient.getInstance().execute(httpPut,new CallBack(command,url));
    }
}
