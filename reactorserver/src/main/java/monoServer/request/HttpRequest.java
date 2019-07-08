package monoServer.request;

import com.sun.org.apache.regexp.internal.RE;
import feign.Request.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest{
    private HttpMethod requestMethod;

    private String requestPath;

    private Map<String,String> pathVarible = new HashMap<>();

    private String requestBody;

    private String httpResult;

    private String serviceName;

    private Map<String,String> headers = new HashMap<>();

    private Map<String,String> params = new HashMap<>();

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getPathVaribles() {
        return pathVarible;
    }

    public HttpRequest withPathVarible(String pathName,String value) {
        this.pathVarible.put(pathName,value);
        return this;
    }

    public HttpRequest withPathVaribles(Map<String, String> pathVarible) {
        this.pathVarible.putAll(pathVarible);
        return this;
    }


    public HttpRequest withHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public HttpRequest withHeader(String headerName,String value) {
        this.headers.put(headerName,value);
        return this;
    }

    public HttpRequest withParam(String key, String value){
        params.put(key,value);
        return this;
    }

    public HttpRequest withParams(Map<String,String> parsms){
        params.putAll(parsms);
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public HttpRequest withServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getHttpResult() {
        return httpResult;
    }

    public void setHttpResult(String httpResult) {
        this.httpResult = httpResult;
    }

    public feign.Request.HttpMethod getRequestMethod() {
        return requestMethod;
    }

    public HttpRequest withRequestMethod(feign.Request.HttpMethod requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public HttpRequest withRequestPath(String requestPath) {
        this.requestPath = requestPath;
        return this;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public HttpRequest withRequestBody(String requestBody) {
        //对象对json序列化
        this.requestBody = requestBody;
        return this;
    }
}
