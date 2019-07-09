package monoServer.enums;

public enum RedisCommandEnum {
    GET("get"),SET("set"),
    MGET("mget"),MSET("mset"),
    HGET("hget"),HSET("hset"),HGETALL("hgetall"),
    SETBIT("setbit"),GETBIT("getbit");

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
