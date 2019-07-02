package monoServer.actorImpl;

import akka.actor.ActorRef;
import monoServer.actors.AbstractAsyncHttpActor;
import monoServer.actors.BaseActor;
import monoServer.actors.ResponseActor;
import monoServer.common.ActContext;
import monoServer.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class AsyncHttpActor extends AbstractAsyncHttpActor {




    @Override
    public HttpRequest execute(ActContext context) {
        //组装http请求信息，具体请求由框架执行
        return new HttpRequest();

    }

    @Override
    public Class<? extends BaseActor> onCommandDone(ActContext context) {
        //异步http执行完以后的操作,返回null就直接走responseActor
        Map<String,Object> result = new HashMap<>();
        result.put("CODE","200");
        result.put("MSG","OK");
        result.put("DATA",null);
        context.setBusinessData(result);
        return LocalCacheActor.class;
    }


}
