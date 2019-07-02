package monoServer.actorImpl;

import monoServer.actors.AbstractLettcueRedisActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;

import java.util.List;

public class LettcueRedisActor extends AbstractLettcueRedisActor {


    @Override
    public void execute(ActContext context) {
       asyncGet("test1",context);
    }

    @Override
    public Class<? extends BaseActor> onCompeletedListener(List<String> result,ActContext context) {
        if(result == null || result.size() < 1){
            //redis获取不到数据走DB
            return SyncDBActor.class;
        }else{
            //有数据走下一步
        }
        return null;
    }

    @Override
    public Class<? extends BaseActor> onErrorListener(Object error,ActContext context) {
        return SyncDBActor.class;
    }
}
