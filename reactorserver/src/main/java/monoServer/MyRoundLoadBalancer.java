package monoServer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyRoundLoadBalancer {

    @Autowired
    private DiscoveryClient discoveryClient;

    private ConcurrentHashMap<String,String> cacheServices = new ConcurrentHashMap<>();

    private  ConcurrentHashMap<String,Long> failedServices = new ConcurrentHashMap<>();

    public ServiceInstance chose(String serviceName){
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if(instances == null || instances.size() < 1){
            throw new RuntimeException("no avalible server");
        }
        Iterator<ServiceInstance> it = instances.iterator();
        while(it.hasNext()){
            ServiceInstance serviceInstance = it.next();
            System.out.println("uri:"+serviceInstance.getUri());
            // fixme tmp 30秒内失败的实例不能使用,时间需要大于erureka获取最新的服务器节点时间
            Long failedTime =failedServices.get(serviceInstance.getUri().toString());
            if( failedTime !=null && failedTime + 1000*30 > new Date().getTime()){
                System.out.println("remove failed node");
                it.remove();

            }else{
                failedServices.remove(serviceInstance.getUri().toString());
            }
        }
        if(instances.size()==0){
            System.out.println("no available nodes");
            return null;
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

    /**
     * 简单处理失败实例
     * @param serviceInstance
     */
    public void notifeFailedNode(ServiceInstance serviceInstance){
        if(serviceInstance != null){
            failedServices.put(serviceInstance.getUri().toString(),new Date().getTime());
        }
    }
}
