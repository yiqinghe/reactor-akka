package monoServer.actors;

import com.alibaba.fastjson.JSON;
import monoServer.common.ActContext;

public class ResponseActor extends BaseActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActContext.class, context -> {

                    context.getMonoSink().success(JSON.toJSONString(context.getResponseData()));

                })
                .build();
    }


    @Override
    public Object buildExecuteData(ActContext context) {
        return null;
    }

    @Override
    public Class<? extends BaseActor> executeNextOnSuccess(ActContext context, Object data) {
        return null;
    }
}
