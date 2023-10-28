package me.lauriichan.laylib.json;

public final class JsonString implements IJson<String> {

    private final String string;

    public JsonString(final String string) {
        this.string = string;
    }

    /*
     * Object override
     */

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof String) {
            return string.equals(obj);
        }
        if (obj instanceof JsonString) {
            return string == ((JsonString) obj).value();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

    /*
     * IJson implementation
     */

    @Override
    public JsonType type() {
        return JsonType.STRING;
    }

    @Override
    public String value() {
        return string;
    }

}
