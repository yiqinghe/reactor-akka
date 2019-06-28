package monoServer.service;

import akka.actor.ActorRef;
import akka.actor.Props;
import monoServer.AsynRpcInovker;
import monoServer.Request;
import monoServer.actors.FristActor;
import reactor.core.publisher.MonoSink;

import java.util.UUID;

public class FristService extends FristActor{


    public FristService(ActorRef nextStep) {
        super(nextStep);
    }

    /*
    返回值是传递给下一个actor的参数
     */

    @Override
    public ActorRef execute(MonoSink param) {
        //do some thing

        contextData
        {
            monoSink,
                    Map < String, T > businessDate;
            Request request = new Request(System.currentTimeMillis(), UUID.randomUUID().toString(),param);
            businessDate.put(request)

        }

                buider
        return null;
    }


}
