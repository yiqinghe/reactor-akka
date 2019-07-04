package monoServer.common;

import monoServer.feignclient.AkkaFeignClientSpecification;
import org.springframework.cloud.context.named.NamedContextFactory;

public class AkkaFeignContext extends NamedContextFactory<AkkaFeignClientSpecification> {
    public AkkaFeignContext() {
        super(AkkaFeignClientSpecification.class, "feign", "feign.client.name");
    }
}
