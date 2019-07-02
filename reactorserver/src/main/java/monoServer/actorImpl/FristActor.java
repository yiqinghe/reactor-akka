package monoServer.actorImpl;

import monoServer.actors.AbstractCommonActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;

public class FristActor extends AbstractCommonActor {

    /*
    返回值是传递给下一个actor的参数
     */
    @Override
    public Class<? extends BaseActor> executeAndNext(ActContext context,Object data) {
        // todo do some business
        return AsyncHttpActor.class;
    }
}
