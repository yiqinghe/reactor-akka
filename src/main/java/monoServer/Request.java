package monoServer;

import reactor.core.publisher.MonoSink;

public class Request {

    public final long requestTime;
    public final String uid;
    private String phone;
    private String httpResult;
    public final MonoSink deferredResult;

    public Request(long requestTime, String uid, MonoSink deferredResult) {
        this.requestTime = requestTime;
        this.uid = uid;
        this.deferredResult = deferredResult;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHttpResult() {
        return httpResult;
    }

    public void setHttpResult(String httpResult) {
        this.httpResult = httpResult;
    }
}
