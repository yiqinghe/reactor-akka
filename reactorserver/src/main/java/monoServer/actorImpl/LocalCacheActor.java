package monoServer.actorImpl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import monoServer.actors.AbstractCommonActor;
import monoServer.actors.AbstractLocalCacheActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class LocalCacheActor extends AbstractLocalCacheActor {
    @Override
    public Class<? extends BaseActor> execute(ActContext context) {

        Object localcache = getValueFromLocalCache("localcache");
        if(localcache != null) {
            //有值 直接走下一步
            context.getBusinessData().put("cache",localcache);
            return IntegrateDataActor.class;
        }else{
            //没值返回 获取数据的actor，并且传人 CACHE
            context.setLocalCache(getCache());
            return LettcueRedisActor.class;
        }
    }




}
