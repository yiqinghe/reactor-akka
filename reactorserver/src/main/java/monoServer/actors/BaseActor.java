package monoServer.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import monoServer.actors.topo.ActorTopo;
import monoServer.common.ActContext;
import monoServer.common.GlobalActorHolder;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseActor extends AbstractActor{

    public BaseActor() {
    }

    //#greeter-messages
    public static Props props(Class<? extends BaseActor> actorClass) {
        return Props.create(actorClass);
    }

    public void dispatch(Class<? extends BaseActor> actorRefClass, ActContext context){
        if(actorRefClass == null){
            actorRefClass = ResponseActor.class;
        }

        ActorTopo actorTopo = GlobalActorHolder.holders.get(context.getActorGroupIdEnum());
        List<ActorRef> actorRefs = actorTopo.getTotalActors().get(actorRefClass);
        ActorRef nextRef = actorRefs.get(incrementAndGetModulo(actorTopo.getInstanceCount(),actorTopo.getRoundCounters().get(this.getClass())));
        nextRef.tell(context,getSelf());
    }

    protected int incrementAndGetModulo(int modulo,AtomicInteger nextServerCyclicCounter) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next))
                //System.out.println(this.getClass().getSimpleName()+"---"+next);
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
    public abstract Class<? extends BaseActor> executeNextOnSuccess(ActContext context, Object data);
}
