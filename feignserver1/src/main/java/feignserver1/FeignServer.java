package feignserver1;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FeignServer {

    @RequestMapping(method = RequestMethod.GET,value = "/test1/ha{name}")
    public Integer test1(@RequestParam("number") Integer number
                    ,@PathVariable("name")String name,@RequestBody SimpleClass body){
        System.out.println(name);
        System.out.println(body.getMsg()+"---"+body.getCode());
        return number+1;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/test1/ha{name}")
    public Integer test2(@RequestParam("number") Integer number
            ,@PathVariable("name")String name,@RequestBody SimpleClass body){
        System.out.println(name);
        System.out.println(body.getMsg()+"---"+body.getCode());
        return number+1;
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/test1/ha{name}")
    public Integer test3(@RequestParam("number") Integer number
            ,@PathVariable("name")String name,@RequestBody SimpleClass body){
        System.out.println(name);
        System.out.println(body.getMsg()+"---"+body.getCode());
        return number+1;
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/test1/ha{name}")
    public Integer test4(@RequestParam("number") Integer number
            ,@PathVariable("name")String name,@RequestBody SimpleClass body){
        System.out.println(name);
        System.out.println(body.getMsg()+"---"+body.getCode());
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
