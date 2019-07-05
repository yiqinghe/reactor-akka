package monoServer.request;

import com.sun.org.apache.regexp.internal.RE;
import feign.Request.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest{
    private HttpMethod requestMethod;

    private String requestPath;

    private String pathVarible;

    private String requestBody;

    private String httpResult;

    private String serviceName;

    private Map<String,Object> params = new HashMap<>();


    public HttpRequest withParam(String key,Object value){
        params.put(key,value);
        return this;
    }

    public HttpRequest withParams(Map<String,Object> parsms){
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
        this.requestBody = requestBody;
        return this;
    }
}
