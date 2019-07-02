package monoServer.request;

import monoServer.Request;
import reactor.core.publisher.MonoSink;

public class HttpRequest{
    private String requestMethod;

    private String requestUrl;

    private String requestBody;

    private String httpResult;

    public String getHttpResult() {
        return httpResult;
    }

    public void setHttpResult(String httpResult) {
        this.httpResult = httpResult;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
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
