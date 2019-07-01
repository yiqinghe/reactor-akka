package monoServer.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Creator;
import monoServer.actorImpl.FristActor;
import monoServer.common.ActContext;
import monoServer.common.GlobalActorHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseActor extends AbstractActor{

    public BaseActor() {
    }

    //#greeter-messages
    public static Props props(Class<? extends BaseActor> actorClass) throws IllegalAccessException, InstantiationException {
        return Props.create(actorClass);
    }

    private AtomicInteger nextServerCyclicCounter = new AtomicInteger(0);


    public void dispatch(Class<? extends BaseActor> actorRefClass, ActContext context){
        List<ActorRef> actorRefs = GlobalActorHolder.holders.get(context.getActorGroupIdEnum()).;
        ActorRef nextRef = actorRefs.get(incrementAndGetModulo(20));
        nextRef.tell(context,getSelf());
    }

    private int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next))
                return next;
        }
    }
}
