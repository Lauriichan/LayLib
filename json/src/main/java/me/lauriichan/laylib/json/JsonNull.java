package me.lauriichan.laylib.json;

public final class JsonNull implements IJson<Void> {

    public static final JsonNull NULL = new JsonNull();

    private JsonNull() {}

    /*
     * Object override
     */

    @Override
    public boolean equals(final Object obj) {
        return obj == null || obj == this;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /*
     * IJson implementation
     */

    @Override
    public JsonType type() {
        return JsonType.NULL;
    }

    @Override
    public Void value() {
        return null;
    }

}
