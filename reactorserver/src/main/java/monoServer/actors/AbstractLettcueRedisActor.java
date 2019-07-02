package monoServer.actors;

import akka.actor.AbstractActor;
import com.google.common.cache.Cache;
import monoServer.common.ActContext;
import monoServer.enums.RedisCommandEnum;
import monoServer.lettuce.LettuceClient;

import java.util.List;
import java.util.function.Consumer;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

public abstract class AbstractLettcueRedisActor extends AbstractAsynActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActContext.class, context -> {
                    RedisCommand redisCommand= (RedisCommand) this.buildExecuteData(context);
                    excuteComand(redisCommand,context);
                })
                .build();
    }

    @Override
    public void onSuccess(ActContext context, Object result) {
        Class<? extends BaseActor> actorRefClass =  executeAndNext(context,result);
        dispatch(actorRefClass,context);
    }

    @Override
    public void onFail(ActContext context, Throwable exception) {
        // todo error handle
        dispatch(null,context);
    }

    public void excuteComand(RedisCommand redisCommand,ActContext context){
        if(redisCommand.command.equals(RedisCommandEnum.GET)) {

            LettuceClient.getInstance().get(redisCommand.key,
                    list -> onSuccess(context, list),
                    error -> onFail(context, (Throwable) error));
        }
    }

    public static class RedisCommand{
        RedisCommandEnum command;
        String key;
    }

}
