package client;

import akka.actor.AbstractActor;

public abstract class AbstractBusinessActor extends AbstractActor {
    @Override
    public void preStart() throws Exception{
        super.preStart();
        System.out.println("actor:"+getSelf().path()+" preStart");

    }
    @Override
    public void postStop() throws Exception{
        super.postStop();
        System.out.println("actor:"+getSelf().path()+" postStop");

    }
    @Override
    public void postRestart(Throwable reason) throws Exception{
        super.postRestart(reason);
        System.out.println("actor:"+getSelf().path()+" postRestart");

    }
}
