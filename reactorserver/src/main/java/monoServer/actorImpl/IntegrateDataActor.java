package monoServer.actorImpl;

import monoServer.actors.AbstractCommonActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;

public class IntegrateDataActor extends AbstractCommonActor{

    @Override
    public Class<? extends BaseActor> excuteAndNext(ActContext context, Object data) {
        return null;
    }
}
