package Mono;

import reactor.core.publisher.MonoSink;

public class Request {

    public final long requestTime;
    public final String uid;
    public final MonoSink deferredResult;

    public Request(long requestTime, String uid, MonoSink deferredResult) {
        this.requestTime = requestTime;
        this.uid = uid;
        this.deferredResult = deferredResult;
    }
}
