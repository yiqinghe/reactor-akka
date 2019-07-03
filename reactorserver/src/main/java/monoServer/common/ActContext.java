package monoServer.common;

import monoServer.enums.ActorGroupIdEnum;
import reactor.core.publisher.MonoSink;

import java.util.HashMap;
import java.util.Map;

public class ActContext {

    // 请求数据
    private Map<String,Object> requestData = new HashMap<>();
    // 结果数据
    private Map<String,Object> responseData = new HashMap<>();
    // 中间上下文数据
    private Map<String,Object> contextData = new HashMap<>();

    private String traceId;

    private MonoSink<String> monoSink;

    private ActorGroupIdEnum actorGroupIdEnum;

    public ActorGroupIdEnum getActorGroupIdEnum() {
        return actorGroupIdEnum;
    }

    public void setActorGroupIdEnum(ActorGroupIdEnum actorGroupIdEnum) {
        this.actorGroupIdEnum = actorGroupIdEnum;
    }

    public Map<String, Object> getResponseData() {
        return responseData;
    }

    public void setResponseData(Map<String, Object> responseData) {
        this.responseData = responseData;
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

    public Map<String, Object> getRequestData() {
        return requestData;
    }

    public void setRequestData(Map<String, Object> requestData) {
        this.requestData = requestData;
    }

    public Map<String, Object> getContextData() {
        return contextData;
    }

    public void setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;
    }
}
