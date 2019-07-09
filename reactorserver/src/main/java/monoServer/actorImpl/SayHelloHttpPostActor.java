package monoServer.actorImpl;

import com.alibaba.fastjson.JSON;
import feign.Request;
import monoServer.SimpleClass;
import monoServer.actors.AbstractHttpActor;
import monoServer.actors.BaseActor;
import monoServer.common.ActContext;
import monoServer.request.HttpRequest;
import scala.Int;

import java.util.HashMap;
import java.util.Map;

public class SayHelloHttpPostActor extends AbstractHttpActor {


    @Override
    public HttpRequest buildExecuteData(ActContext context) {
        //组装http请求信息，具体请求由框架执行
        SimpleClass body = new SimpleClass();
        body.setCode("200");
        body.setMsg("OK");
        return new HttpRequest().withServiceName("feign-server")
                .withRequestPath("/test2/ha{name}")
                .withRequestMethod(Request.HttpMethod.POST)
                .withParam("number",(Integer.valueOf((String) context.getResponseData().get("GETDATA")))+"1")
                .withPathVarible("name","jett")
                .withHeader("header1","headervalue")
                .withRequestBody(JSON.toJSONString(body));
    }

    @Override
    public Class<? extends BaseActor> executeNextOnSuccess(ActContext context, Object data) {
        //异步http执行完以后的操作,返回null就直接走responseActor
        Map<String,Object> result = new HashMap<>();
        result.put("CODE","200");
        result.put("MSG","OK");
        result.put("POSTDATA",data);
        context.setResponseData(result);
        return SayHelloHttpPutActor.class;
    }


    @Override
    public Class<? extends BaseActor> executeNextOnError(ActContext context, Throwable exception) {
        return null;
    }
}
