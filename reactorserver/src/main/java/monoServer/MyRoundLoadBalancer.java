package monoServer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyRoundLoadBalancer {

    @Autowired
    private DiscoveryClient discoveryClient;

    private ConcurrentHashMap<String,String> cacheServices = new ConcurrentHashMap<>();

    public ServiceInstance chose(String serviceName){
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if(instances == null || instances.size() < 1){
            throw new RuntimeException("no avalible server");
        }
        for(ServiceInstance serviceInstance:instances){
            System.out.println("uri:"+serviceInstance.getUri());
        }
        if(instances.size() == 1){
            cacheServices.put(serviceName,instances.get(0).getUri().toString());
            return instances.get(0);
        }
        String cachesercer = cacheServices.get(serviceName);
        if(cachesercer == null){
            cacheServices.put(serviceName,instances.get(0).getUri().toString());
            return instances.get(0);
        }
        for (int i = 0; i < instances.size(); i++) {
            ServiceInstance serviceInstance = instances.get(0);
            if(serviceInstance.getUri().toString().equals(cachesercer)){
                if(instances.size()  > i +1){
                    cacheServices.put(serviceName,instances.get(i+1).getUri().toString());
                    return instances.get(i+1);
                }
                cacheServices.put(serviceName,instances.get(0).getUri().toString());
                return instances.get(0);
            }

        }
        //历史server已经down掉
        cacheServices.put(serviceName,instances.get(0).getUri().toString());
        return instances.get(0);

    }
}
