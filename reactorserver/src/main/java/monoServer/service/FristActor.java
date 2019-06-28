package monoServer.service;

import akka.actor.ActorRef;
import monoServer.Request;
import reactor.core.publisher.MonoSink;

import java.util.UUID;

public class FristActor extends monoServer.actors.AbstractFristActor {


    /*
    返回值是传递给下一个actor的参数
     */

    @Override
    public Class<ActorRef> execute(MonoSink param) {
        //do some thing

//        contextData
//        {
//            monoSink,
//                    Map < String, T > businessDate;
//            Request request = new Request(System.currentTimeMillis(), UUID.randomUUID().toString(),param);
//            businessDate.put(request)
//
//        }
//
//                buider
        return null;
    }


}
