package monoServer.actorImpl;

import monoServer.actors.AbstractHttpActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;
import monoServer.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class HttpActor extends AbstractHttpActor {

    @Override
    public HttpRequest buildExecuteData(ActContext context) {
        //组装http请求信息，具体请求由框架执行
        return new HttpRequest();
    }

    @Override
    public Class<? extends BaseActor> executeAndNext(ActContext context,Object data) {
        //异步http执行完以后的操作,返回null就直接走responseActor
        Map<String,Object> result = new HashMap<>();
        result.put("CODE","200");
        result.put("MSG","OK");
        result.put("DATA",null);
        context.setResponseData(result);
        return null;
    }




}
