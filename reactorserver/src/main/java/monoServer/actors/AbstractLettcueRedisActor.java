package monoServer.actors;

import monoServer.common.ActContext;
import monoServer.enums.RedisCommandEnum;
import monoServer.lettuce.LettuceClient;


public abstract class AbstractLettcueRedisActor extends AbstractAsynActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActContext.class, context -> {
                    RedisCommand redisCommand= this.buildExecuteData(context);
                    excuteComand(redisCommand,context);
                })
                .build();
    }

    @Override
    public void onSuccess(ActContext context, Object result) {
        Class<? extends BaseActor> actorRefClass =  executeNextOnSuccess(context,result);
        dispatch(actorRefClass,context);
    }

    /**
     * 需要实现要发起redis请求的命令、key等信息
     * @param context
     * @return
     */
    public abstract RedisCommand buildExecuteData(ActContext context);

    @Override
    public void onFail(ActContext context, Throwable exception) {
        Class<? extends BaseActor> aClass = errorAndNext(context, exception);
        dispatch(aClass,context);
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
