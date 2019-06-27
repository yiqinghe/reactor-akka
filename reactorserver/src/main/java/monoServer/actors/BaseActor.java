package monoServer.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public abstract class BaseActor extends AbstractActor{

    //#greeter-messages
    public Props props() {
        return Props.create(FristActor.class, this);
    }


    protected ActorRef nextStep;


    public void setNextStep(ActorRef nextStep){
        this.nextStep=nextStep;
    }
}
