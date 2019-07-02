package monoServer.actorImpl;

import monoServer.actors.AbstractLettcueRedisActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;

import java.util.List;

public class LettcueRedisActor extends AbstractLettcueRedisActor {

    @Override
    public Object buildExecuteData(ActContext context) {
       return null;
    }

    @Override
    public Class<? extends BaseActor> executeAndNext(ActContext context, Object data) {
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
    public Class<? extends BaseActor> onErrorListener(Object error,ActContext context) {
        return SyncDBActor.class;
    }
}
