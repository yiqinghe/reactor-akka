package monoServer.actorImpl;

import annotation.AkkaMaper;
import dao.UserDao;
import monoServer.actors.AbstractDBActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;

public class SyncDBActor extends AbstractDBActor {

    @AkkaMaper("userDao")
    private UserDao userDao;

    @Override
    public Object buildExecuteData(ActContext context) {
        return null;
    }

    @Override
    public Class<? extends BaseActor> executeAndNext(ActContext context, Object data) {
        return null;
    }
}
