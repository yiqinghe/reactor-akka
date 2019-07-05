package monoServer.actorImpl;

import monoServer.annotation.AkkaMaper;
import monoServer.dao.UserDao;
import monoServer.actors.AbstractDBActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;

public class SyncDBActor extends AbstractDBActor {

    @AkkaMaper
    private UserDao userDao;

    @Override
    public Object buildExecuteData(ActContext context) {
        return null;
    }

    @Override
    public Class<? extends BaseActor> executeNextOnSuccess(ActContext context, Object data) {
        String s = userDao.get();
        //System.out.println(s);
        context.getResponseData().put("db result",s);
        return null;
    }
}
