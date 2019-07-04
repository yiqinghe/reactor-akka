package monoServer.request;

import com.sun.org.apache.regexp.internal.RE;
import feign.Request.HttpMethod;

import java.util.Map;

public class HttpRequest{
    private HttpMethod requestMethod;

    private String requestPath;

    private Map requestBody;

    private String httpResult;

    private String serviceName;

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

    public Map getRequestBody() {
        return requestBody;
    }

    public HttpRequest withRequestBody(Map requestBody) {
        this.requestBody = requestBody;
        return this;
    }
}
