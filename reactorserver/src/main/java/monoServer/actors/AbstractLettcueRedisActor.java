package monoServer.actors;

import monoServer.command.RedisCommand;
import monoServer.common.ActContext;
import monoServer.enums.RedisCommandEnum;
import monoServer.lettuce.LettuceClient;


public abstract class AbstractLettcueRedisActor extends AbstractAsynActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActContext.class, context -> {
                    RedisCommand redisCommand= this.buildExecuteData(context);
                    LettuceClient.getInstance().excuteComand(redisCommand,context,this);
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
        Class<? extends BaseActor> aClass = executeNextOnError(context, exception);
        dispatch(aClass,context);
    }

}
