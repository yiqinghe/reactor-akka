package monoServer.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "actor")
public class ActorProperties {

    private Integer instanceCount;

    public Integer getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(Integer instanceCount) {
        this.instanceCount = instanceCount;
    }
}
