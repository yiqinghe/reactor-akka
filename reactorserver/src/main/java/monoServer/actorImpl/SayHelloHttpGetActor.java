package monoServer.actorImpl;

import com.alibaba.fastjson.JSON;
import feign.Request;
import monoServer.SimpleClass;
import monoServer.actors.AbstractHttpActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;
import monoServer.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class SayHelloHttpGetActor extends AbstractHttpActor {


    @Override
    public HttpRequest buildExecuteData(ActContext context) {
        //组装http请求信息，具体请求由框架执行
        SimpleClass body = new SimpleClass();
        body.setCode("200");
        body.setMsg("OK");
        return new HttpRequest().withServiceName("feign-server")
                .withRequestPath("/test1/ha{name}")
                .withRequestMethod(Request.HttpMethod.GET)
                .withParam("number","1")
                .withPathVarible("name","jett")
                .withHeader("Content-Type","application/json;charset=UTF-8")
                .withRequestBody(JSON.toJSONString(body));
    }

    @Override
    public Class<? extends BaseActor> executeNextOnSuccess(ActContext context, Object data) {
        //异步http执行完以后的操作,返回null就直接走responseActor
        Map<String,Object> result = new HashMap<>();
        result.put("CODE","200");
        result.put("MSG","OK");
        result.put("GETDATA",data);
        context.setResponseData(result);
        return SayHelloHttpPostActor.class;
    }


    @Override
    public Class<? extends BaseActor> executeNextOnError(ActContext context, Throwable exception) {
        return null;
    }
}
