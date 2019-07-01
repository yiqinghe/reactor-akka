package monoServer.actorImpl;

import akka.actor.ActorRef;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.MonoSink;

public class FristActor extends monoServer.actors.AbstractFristActor {


    /*
    返回值是传递给下一个actor的参数
     */

    @Override
    public Class<? extends BaseActor> execute(ActContext context) {
        return AsyncHttpActor.class;
    }


}
