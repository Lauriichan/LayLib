package me.lauriichan.laylib.json.io;

enum JsonToken {

    EOF,
    KEY,
    NULL,
    STRING,
    NUMBER,
    BOOLEAN,
    END_ARRAY,
    END_OBJECT,
    START_ARRAY,
    START_OBJECT,

    // Numbers
    BYTE(NUMBER),
    SHORT(NUMBER),
    INTEGER(NUMBER),
    LONG(NUMBER),
    BIG_INTEGER(NUMBER),
    FLOAT(NUMBER),
    DOUBLE(NUMBER),
    BIG_DECIMAL(NUMBER);

    private final JsonToken parent;

    JsonToken() {
        this.parent = null;
    }

    JsonToken(final JsonToken parent) {
        this.parent = parent;
    }

    public JsonToken getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public JsonToken actualToken() {
        return parent != null ? parent : this;
    }

}
