package monoServer.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.List;
import java.util.Map;

public abstract class BaseActor extends AbstractActor{

    //#greeter-messages
    public Props props() {
        return Props.create(AbstractFristActor.class, this);
    }

    public Map<Class<ActorRef>, List<ActorRef>> getActorMap() {
        return actorMap;
    }

    public void setActorMap(Map<Class<ActorRef>, List<ActorRef>> actorMap) {
        this.actorMap = actorMap;
    }

    private Map<Class<ActorRef>,List<ActorRef>> actorMap;
}
