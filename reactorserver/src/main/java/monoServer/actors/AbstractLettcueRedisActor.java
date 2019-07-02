package monoServer.actors;

import akka.actor.AbstractActor;
import com.google.common.cache.Cache;
import monoServer.common.ActContext;
import monoServer.lettuce.LettuceClient;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractLettcueRedisActor extends BaseActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActContext.class, context -> {
                    Object o= this.buildExcuteData(context);
                    excuteComand("",context);
                })
                .build();
    }

    public void onCompeletedListener(List<String> result,ActContext context){
        Class<? extends BaseActor> actorRefClass =  excuteAndNext(context,result);
        dispatch(actorRefClass,context);
    }

    public abstract Class<? extends BaseActor> onErrorListener(Object error,ActContext context);



    public void excuteComand(String key,ActContext context){
        if("get".equals("get")){

            LettuceClient.getInstance().get(key,
                    list->onCompeletedListener(list,context),
                    error->dispatch(onErrorListener(error,context),context));
        }
    }

}
