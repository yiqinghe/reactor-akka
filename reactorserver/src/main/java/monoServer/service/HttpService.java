package monoServer.service;

import akka.actor.ActorRef;
import monoServer.AsynRpcInovker;
import monoServer.Request;
import monoServer.actors.AsyncHttpActor;
import monoServer.actors.FristActor;
import monoServer.http.HttpAsynInovker;

public class HttpService extends AsyncHttpActor{


    public HttpService(ActorRef nextStep) {
        super(nextStep);
    }


    @Override
    public void execute(Object request) {
        HttpAsynInovker.Command command = new HttpAsynInovker.Command((Request) request, System.currentTimeMillis(),
                this);
        // 发起异步远程调用


    }

    @Override
    public void onCommandDone(Request request) {
        //异步http执行完以后的操作

    }


}
