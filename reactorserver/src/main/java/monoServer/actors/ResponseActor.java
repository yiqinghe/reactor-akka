package monoServer.actors;

import akka.actor.AbstractActor;
import com.alibaba.fastjson.JSON;
import monoServer.Request;
import monoServer.common.ActContext;

public class ResponseActor extends BaseActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActContext.class, context -> {

                    context.getMonoSink().success(JSON.toJSONString(context.getBusinessData()));

                })
                .build();
    }


    @Override
    public Object buildExecuteData(ActContext context) {
        return null;
    }

    @Override
    public Class<? extends BaseActor> executeAndNext(ActContext context, Object data) {
        return null;
    }
}
