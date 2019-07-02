package monoServer.request;

import monoServer.Request;
import reactor.core.publisher.MonoSink;

public class HttpRequest{
    private feign.Request.HttpMethod requestMethod;

    private String requestUrl;

    private String requestBody;

    private String httpResult;

    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public void setRequestMethod(feign.Request.HttpMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
