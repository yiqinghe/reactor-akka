package monoServer.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.FI;
import monoServer.AsynRpcInovker;
import monoServer.AsynRpcInovker.Command;
import monoServer.Controller;
import monoServer.Request;
import monoServer.SayHelloService;
import monoServer.common.ActContext;
import monoServer.common.GlobalActorHolder;
import reactor.core.publisher.MonoSink;

/**
 * 通用执行单元，一般处理入口业务、或者整合逻辑，以及决定下一个执行器
 */
public abstract class AbstractCommonActor extends BaseActor {

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(ActContext.class, context -> {
                    Object firstObject = this.buildExecuteData(context);
                    Class<? extends BaseActor> actorRefClass = this.executeAndNext(context,firstObject);
                    dispatch(actorRefClass,context);
                })
                .build();
    }

}