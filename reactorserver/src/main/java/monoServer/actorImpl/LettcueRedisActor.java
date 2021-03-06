package monoServer.actorImpl;

import monoServer.actors.AbstractLettcueRedisActor;
import monoServer.actors.BaseActor;
import monoServer.command.RedisCommand;
import monoServer.common.ActContext;
import monoServer.enums.RedisCommandEnum;

import java.util.List;

public class LettcueRedisActor extends AbstractLettcueRedisActor {

    @Override
    public RedisCommand buildExecuteData(ActContext context) {
        RedisCommand command = new RedisCommand()
                .setKey("jett1")
                .setCommandType(RedisCommandEnum.HGETALL);
       return command;
    }

    @Override
    public Class<? extends BaseActor> executeNextOnSuccess(ActContext context, Object data) {
        List<String> result = (List<String>)data;

        if(result == null || result.size() < 1){
            //redis获取不到数据走DB
            return SyncDBActor.class;
        }else{
            //有数据走下一步
             context.getContextData().put("LettcueRedisActorResult",result);
            return IntegrateDataActor.class;
        }
    }

    @Override
    public Class<? extends BaseActor> executeNextOnError(ActContext context, Throwable exception) {
        return null;
    }
}
