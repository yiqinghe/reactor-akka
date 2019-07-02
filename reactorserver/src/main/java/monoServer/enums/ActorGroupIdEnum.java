package monoServer.enums;

public enum ActorGroupIdEnum {
    SAY_HELLO("sayhello123");


    private String serviceId;

    ActorGroupIdEnum(String serviceId) {
        this.serviceId = serviceId;
    }


    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
