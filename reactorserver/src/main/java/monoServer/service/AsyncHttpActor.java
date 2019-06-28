package monoServer.service;

import akka.actor.ActorRef;
import monoServer.Request;
import monoServer.actors.AbstractAsyncHttpActor;
import monoServer.actors.BaseActor;
import monoServer.http.HttpAsynInovker;

public class AsyncHttpActor extends AbstractAsyncHttpActor {



    public AsyncHttpActor(ActorRef nextStep) {
        super(nextStep);
    }


    @Override
    public void execute(Object request) {
        HttpAsynInovker.Command command = new HttpAsynInovker.Command((Request) request, System.currentTimeMillis(),
                this);
        // 发起异步远程调用


    }

    @Override
    public ActorRef onCommandDone(Request request) {
        //异步http执行完以后的操作

        if(true){

        }else{

        }
        context.get(Hello.class)
        return null;
    }


}
