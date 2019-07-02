package monoServer.common;

import com.google.common.cache.Cache;
import monoServer.enums.ActorGroupIdEnum;
import monoServer.request.HttpRequest;
import reactor.core.publisher.MonoSink;

import java.util.HashMap;
import java.util.Map;

public class ActContext {

    private Map<String,Object> businessData = new HashMap<>();

    private String traceId;

    private MonoSink<String> monoSink;

    private ActorGroupIdEnum actorGroupIdEnum;

    private HttpRequest httpRequest;

    private Cache localCache;

    private String localCachekey;

    public Cache getLocalCache() {
        return localCache;
    }

    public void setLocalCache(Cache localCache) {
        this.localCache = localCache;
    }

    public String getLocalCachekey() {
        return localCachekey;
    }

    public void setLocalCachekey(String localCachekey) {
        this.localCachekey = localCachekey;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public ActorGroupIdEnum getActorGroupIdEnum() {
        return actorGroupIdEnum;
    }

    public void setActorGroupIdEnum(ActorGroupIdEnum actorGroupIdEnum) {
        this.actorGroupIdEnum = actorGroupIdEnum;
    }

    public Map<String, Object> getBusinessData() {
        return businessData;
    }

    public void setBusinessData(Map<String, Object> businessData) {
        this.businessData = businessData;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public MonoSink<String> getMonoSink() {
        return monoSink;
    }

    public void setMonoSink(MonoSink<String> monoSink) {
        this.monoSink = monoSink;
    }
}
