package monoServer.actors;

import akka.actor.AbstractActor;
import monoServer.common.ActContext;
import monoServer.lettuce.LettuceClient;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractLettcueRedisActor extends BaseActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActContext.class, context -> {
                    this.execute(context);

                })
                .build();
    }

    public abstract void execute(ActContext context);

    public abstract Class<? extends BaseActor> onCompeletedListener(List<String> result,ActContext context);

    public abstract Class<? extends BaseActor> onErrorListener(Object error,ActContext context);



    public void asyncGet(String key,ActContext context){
        LettuceClient.getInstance().get(key,list->{
            onCompeletedListener(list,context);
        },error->{
            onErrorListener(error,context);
        });
    }

}
