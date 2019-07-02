package monoServer.actorImpl;

import monoServer.actors.AbstractFirstActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;

public class SayHelloFristActor extends AbstractFirstActor {

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
        return AsyncHttpActor.class;
    }
}
