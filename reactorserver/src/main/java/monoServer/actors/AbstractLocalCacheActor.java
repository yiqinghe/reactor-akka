package monoServer.actors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import monoServer.common.ActContext;
import java.util.concurrent.TimeUnit;

public abstract class AbstractLocalCacheActor extends BaseActor{
    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(ActContext.class, context -> {
                    Class<? extends BaseActor> actorRefClass = this.execute(context);
                    dispatch(actorRefClass,context);
                })
                .build();
    }

    protected Cache getCache(){
        return LOCAL_CACHE;
    }

    protected Object getValueFromLocalCache(String key)  {
        return LOCAL_CACHE.getIfPresent(key);
    }



    public abstract Class<? extends BaseActor> execute(ActContext context);

    private static Cache LOCAL_CACHE = CacheBuilder.newBuilder().concurrencyLevel(1)
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();

}
