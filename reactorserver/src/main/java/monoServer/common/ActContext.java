package monoServer.common;

import reactor.core.publisher.MonoSink;

import java.util.Map;

public class ActContext {

    private Map<String,Object> businessData;

    private String traceId;

    private MonoSink<String> monoSink;

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
