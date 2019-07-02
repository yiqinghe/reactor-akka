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

public abstract class BaseActor <E,R> extends AbstractActor{

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

    public abstract E buildExcuteData(ActContext context);

    public abstract Class<? extends BaseActor> excuteAndNext(ActContext context,R data);
}
