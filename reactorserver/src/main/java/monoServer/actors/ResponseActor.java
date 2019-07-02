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
}
