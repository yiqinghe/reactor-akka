package monoServer.feignclient;

import annotation.AkkaFeignClient;
import annotation.AkkaRequestMapping;
import monoServer.AsynRpcInovker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("feign-server")
public interface FeignClientSao {

    @RequestMapping("/hi")
    public String hi(AsynRpcInovker.Command command);
}
