package monoServer.command;

import monoServer.enums.RedisCommandEnum;

public class RedisCommand {

    private RedisCommandEnum command;
    private String key;
    private String field;
    private String value;
    private long offset;
    private boolean falg;
    private String[] keys;

    public RedisCommandEnum getCommandType() {
        return command;
    }

    public RedisCommand setCommandType(RedisCommandEnum command) {
        this.command = command;
        return this;
    }

    public String getKey() {
        return key;
    }

    public RedisCommand setKey(String key) {
        this.key = key;
        return this;
    }

    public String getField() {
        return field;
    }

    public RedisCommand setField(String field) {
        this.field = field;
        return this;
    }

    public String getValue() {
        return value;
    }

    public RedisCommand setValue(String value) {
        this.value = value;
        return this;
    }

    public long getOffset() {
        return offset;
    }

    public RedisCommand setOffset(long offset) {
        this.offset = offset;
        return this;
    }

    public boolean isFalg() {
        return falg;
    }

    public RedisCommand setFalg(boolean falg) {
        this.falg = falg;
        return this;
    }

    public String[] getKeys() {
        return keys;
    }

    public RedisCommand setKeys(String[] keys) {
        this.keys = keys;
        return this;
    }
}
