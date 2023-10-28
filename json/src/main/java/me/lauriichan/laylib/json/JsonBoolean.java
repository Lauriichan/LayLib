package me.lauriichan.laylib.json;

public final class JsonBoolean implements IJson<Boolean> {

    public static JsonBoolean TRUE = new JsonBoolean(true);
    public static JsonBoolean FALSE = new JsonBoolean(false);

    public static JsonBoolean of(final boolean state) {
        return state ? JsonBoolean.TRUE : JsonBoolean.FALSE;
    }

    private final boolean value;

    private JsonBoolean(final boolean value) {
        this.value = value;
    }

    /*
     * Object override
     */

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Boolean) {
            return value == (Boolean) obj;
        }
        if (obj instanceof JsonBoolean) {
            return value == ((JsonBoolean) obj).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    /*
     * IJson implementation
     */

    @Override
    public JsonType type() {
        return JsonType.BOOLEAN;
    }

    @Override
    public Boolean value() {
        return value;
    }

}
