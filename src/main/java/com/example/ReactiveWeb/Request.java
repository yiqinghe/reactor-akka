package com.example.ReactiveWeb;

import org.springframework.web.context.request.async.DeferredResult;

public class Request {

    public final long requestTime;
    public final String uid;
    public final DeferredResult deferredResult;

    public Request(long requestTime, String uid, DeferredResult deferredResult) {
        this.requestTime = requestTime;
        this.uid = uid;
        this.deferredResult = deferredResult;
    }
}
