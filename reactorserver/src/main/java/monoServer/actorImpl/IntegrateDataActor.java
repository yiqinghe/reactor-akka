package monoServer.actorImpl;

import monoServer.actors.AbstractFirstActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;

public class IntegrateDataActor extends AbstractFirstActor {

    @Override
    public Class<? extends BaseActor> executeAndNext(ActContext context, Object data) {
        return null;
    }
}
