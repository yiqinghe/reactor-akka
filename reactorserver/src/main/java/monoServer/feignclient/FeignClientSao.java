package monoServer.feignclient;

import monoServer.AsynRpcInovker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("feign-server")
public interface FeignClientSao {

    @RequestMapping("/hi/{msg}")
    public String hi2(@RequestHeader("Accept-Encoding") String encoding,
                     @RequestHeader("Keep-Alive") long keepAlive,
                     @RequestParam("name")String name,
                     @RequestBody String body,
                     @PathVariable("msg")String msg);

    @RequestMapping("/hi")
    public String hi();
}
