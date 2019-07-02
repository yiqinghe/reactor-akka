package monoServer.enums;

public enum RedisCommandEnum {
    GET("get");

    private String command;

    RedisCommandEnum(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
