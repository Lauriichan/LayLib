package me.lauriichan.laylib.json;

public enum JsonType {

    // Json types
    JSON,
    ARRAY(JSON),
    OBJECT(JSON),

    // Primitive types
    PRIMITIVE,
    NULL(PRIMITIVE),
    STRING(PRIMITIVE),
    NUMBER(PRIMITIVE),
    BOOLEAN(PRIMITIVE),
    BYTE(NUMBER),
    SHORT(NUMBER),
    INTEGER(NUMBER),
    LONG(NUMBER),
    BIG_INTEGER(NUMBER),
    FLOAT(NUMBER),
    DOUBLE(NUMBER),
    BIG_DECIMAL(NUMBER);

    private final JsonType parent;

    JsonType() {
        this(null);
    }

    JsonType(final JsonType parent) {
        this.parent = parent;
    }

    public JsonType parent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean isType(final JsonType type) {
        return this == type || parent != null && parent.isType(type);
    }

    public boolean isPrimitive() {
        return isType(JsonType.PRIMITIVE);
    }

    public boolean hasType(final IJson<?> json) {
        return json != null && json.type().isType(this);
    }

}
