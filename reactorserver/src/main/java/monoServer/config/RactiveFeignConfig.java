package monoServer.config;



import feign.Client;
import monoServer.feignclient.ReactiveLoadBlanceFeignClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RactiveFeignConfig {


    @Bean
    public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory,
                              SpringClientFactory clientFactory) {
        return new ReactiveLoadBlanceFeignClient(new Client.Default(null, null), cachingFactory,
                clientFactory);
    }
}
