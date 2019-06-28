package monoServer.enums;

public enum ActorGroupIdEnum {
    SAY_HELLO("sayhello","123");

    private String serviceName;

    private String serviceId;

    ActorGroupIdEnum(String serviceName, String serviceId) {
        this.serviceName = serviceName;
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
