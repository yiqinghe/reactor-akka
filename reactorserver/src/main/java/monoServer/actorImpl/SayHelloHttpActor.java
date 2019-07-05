package monoServer.actorImpl;

import feign.Request;
import monoServer.actors.AbstractHttpActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;
import monoServer.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class SayHelloHttpActor extends AbstractHttpActor {


    @Override
    public HttpRequest buildExecuteData(ActContext context) {
        //组装http请求信息，具体请求由框架执行

        return new HttpRequest().withServiceName("feign-server")
                .withRequestPath("/hi")
                .withRequestMethod(Request.HttpMethod.GET)
                .withParam("name","jim")
                .withRequestBody("{name:jerry,age:18}");
    }

    @Override
    public Class<? extends BaseActor> executeNextOnSuccess(ActContext context, Object data) {
        //异步http执行完以后的操作,返回null就直接走responseActor
        Map<String,Object> result = new HashMap<>();
        result.put("CODE","200");
        result.put("MSG","OK");
        result.put("DATA",data);
        context.setResponseData(result);
        return null;
    }


    @Override
    public Class<? extends BaseActor> executeNextOnError(ActContext context, Throwable exception) {
        return null;
    }
}
