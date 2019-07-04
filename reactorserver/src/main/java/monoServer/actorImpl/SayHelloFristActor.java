package monoServer.actorImpl;

import monoServer.actors.AbstractCommonActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;

public class SayHelloFristActor extends AbstractCommonActor {

    @Override
    public  Object buildExecuteData(ActContext context){
        return null;
    }


    /**
     *
     * @param context
     * @param data
     * @return
     */
    @Override
    public Class<? extends BaseActor> executeAndNext(ActContext context,Object data) {
        // todo do some business
        return SyncDBActor.class;
    }
}
