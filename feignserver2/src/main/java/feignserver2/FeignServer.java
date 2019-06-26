package feignserver2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FeignServer {

    @RequestMapping(method = RequestMethod.GET,value = "/test1")
    public Integer test1(@RequestParam("number") Integer number){
        return number+1;
    }


    @Value("${server.port}")
    String port;

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/hi")
    public String toString() {
        List<ServiceInstance> serviceInstances = this.discoveryClient.getInstances("feign-server");
        for(ServiceInstance serviceInstance:serviceInstances){
            System.out.println("uri:"+serviceInstance.getUri());
        }
        return "这是端口为："+port+"实例项目";
    }
}
