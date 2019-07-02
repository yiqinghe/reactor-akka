package monoServer.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Creator;
import monoServer.common.ActContext;
import monoServer.common.GlobalActorHolder;
import monoServer.request.HttpRequest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseActor extends AbstractActor{

    public BaseActor() {
    }

    //#greeter-messages
    public static Props props(Class<? extends BaseActor> actorClass) throws IllegalAccessException, InstantiationException {
        return Props.create(actorClass);
    }

    public void dispatch(Class<? extends BaseActor> actorRefClass, ActContext context){
        if(actorRefClass == null){
            actorRefClass = ResponseActor.class;
        }
        List<ActorRef> actorRefs = GlobalActorHolder.holders
                .get(context.getActorGroupIdEnum()).getTotalActors().get(actorRefClass);
        ActorRef nextRef = actorRefs.get(incrementAndGetModulo(20));
        nextRef.tell(context,getSelf());
    }
    private  AtomicInteger nextServerCyclicCounter = new AtomicInteger(0);

    protected int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next))
                return next;
        }
    }

    /**
     * 组装需要执行数据
     * @param context
     * @return
     */
    public abstract Object buildExecuteData(ActContext context);

    /**
     * 执行完后，拿到结果数据，根据结果数据决定下一个执行步骤
     * 如果不需要执行，直接返回null，系统自动将context.responseData返回。
     * 如果context.responseData为空,组装默认返回数据
     * @param context
     * @param data
     * @return
     */
    public abstract Class<? extends BaseActor> executeAndNext(ActContext context,Object data);
}
