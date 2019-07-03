package monoServer.actors;

import akka.actor.ActorRef;
import monoServer.common.ActContext;
import monoServer.enums.ActorGroupIdEnum;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ActorTopo {

    private Class<? extends BaseActor> frist;



    private Map<Class<? extends BaseActor>,List<ActorRef>> totalActors;

    private ActorGroupIdEnum actorGroupIdEnum;

    private AtomicInteger nextServerCyclicCounter;

    public Map<Class<? extends BaseActor>, List<ActorRef>> getTotalActors() {
        return totalActors;
    }



    public ActorTopo(Class<? extends BaseActor> frist, ActorGroupIdEnum actorGroupIdEnum
            , Map<Class<? extends BaseActor>,List<ActorRef>> totalActors) {
        this.frist = frist;
        this.totalActors = totalActors;
        this.actorGroupIdEnum = actorGroupIdEnum;
        this.nextServerCyclicCounter = new AtomicInteger(0);
    }

    public Mono<String> start(Map<String, Object> params){

        Mono<String> monoResult = Mono.create(deferredResult->{

            ActContext context = new ActContext();
            context.setRequestData(params);
            context.setMonoSink(deferredResult);
            context.setActorGroupIdEnum(actorGroupIdEnum);
            totalActors.get(frist).get(incrementAndGetModulo(20)).tell(context,ActorRef.noSender());
        });
        return monoResult;
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