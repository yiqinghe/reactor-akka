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
                    this.execute(context);
                })
                .build();
    }

    public abstract void execute(ActContext context);

    public abstract Class<? extends BaseActor> onCompeletedListener(List<String> result,ActContext context);

    public abstract Class<? extends BaseActor> onErrorListener(Object error,ActContext context);



    public void asyncGet(String key,ActContext context){
        LettuceClient.getInstance().get(key,list->{
            Cache localCache = context.getLocalCache();
            if(localCache != null && localCache.getIfPresent(context.getLocalCachekey()) == null){
                localCache.put(context.getLocalCachekey(),list);
            }
            dispatch(onCompeletedListener(list,context),context);

        },error->{
            dispatch(onErrorListener(error,context),context);
        });
    }

}
