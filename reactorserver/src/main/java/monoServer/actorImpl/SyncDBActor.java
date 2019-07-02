package monoServer.actorImpl;

import annotation.AkkaMaper;
import dao.UserDao;
import monoServer.actors.AbstractCommonActor;
import monoServer.actors.AbstractDBActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;

public class SyncDBActor extends AbstractDBActor {

    @AkkaMaper("userDao")
    private UserDao userDao;

    @Override
    public Class<? extends BaseActor> execute(ActContext context) {
        return null;
    }
}
