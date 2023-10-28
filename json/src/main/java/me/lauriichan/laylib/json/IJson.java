package me.lauriichan.laylib.json;

import java.math.BigDecimal;
import java.math.BigInteger;

import me.lauriichan.laylib.json.util.PrimitiveMapper;

public interface IJson<V> {

    JsonNull NULL = JsonNull.NULL;

    JsonBoolean TRUE = JsonBoolean.TRUE;
    JsonBoolean FALSE = JsonBoolean.FALSE;

    static JsonBoolean of(final boolean state) {
        return state ? TRUE : FALSE;
    }

    static JsonString of(final String string) {
        return new JsonString(string);
    }

    static JsonByte of(final byte value) {
        return JsonByte.of(value);
    }

    static JsonShort of(final short value) {
        return new JsonShort(value);
    }

    static JsonInteger of(final int value) {
        return new JsonInteger(value);
    }

    static JsonLong of(final long value) {
        return new JsonLong(value);
    }

    static JsonBigInteger of(final BigInteger value) {
        return new JsonBigInteger(value);
    }

    static JsonFloat of(final float value) {
        return new JsonFloat(value);
    }

    static JsonDouble of(final double value) {
        return new JsonDouble(value);
    }

    static JsonBigDecimal of(final BigDecimal value) {
        return new JsonBigDecimal(value);
    }

    static IJson<?> of(final Object object) throws IllegalArgumentException {
        if (object == null) {
            return JsonNull.NULL;
        }
        final Class<?> type = PrimitiveMapper.fromPrimitive(object.getClass());
        if (object == Boolean.class) {
            return of(object);
        }
        if (object == Byte.class) {
            return JsonByte.of((Byte) object);
        }
        if (type == String.class) {
            return new JsonString((String) object);
        }
        if (type == Short.class) {
            return new JsonShort((Short) object);
        }
        if (type == Integer.class) {
            return new JsonInteger((Integer) object);
        }
        if (type == Long.class) {
            return new JsonLong((Long) object);
        }
        if (type == BigInteger.class) {
            return new JsonBigInteger((BigInteger) object);
        }
        if (type == Float.class) {
            return new JsonFloat((Float) object);
        }
        if (type == Double.class) {
            return new JsonDouble((Double) object);
        }
        if (type == BigDecimal.class) {
            return new JsonBigDecimal((BigDecimal) object);
        }
        throw new IllegalArgumentException(String.format("Unsupported object of type %s: %s", type, object));
    }

    JsonType type();

    V value();

    default boolean isArray() {
        return type() == JsonType.ARRAY;
    }

    default JsonArray asJsonArray() {
        return (JsonArray) this;
    }

    default boolean isObject() {
        return type() == JsonType.OBJECT;
    }

    default JsonObject asJsonObject() {
        return (JsonObject) this;
    }

    default boolean isNull() {
        return type() == JsonType.NULL;
    }

    default JsonNull asJsonNull() {
        return (JsonNull) this;
    }

    default boolean isString() {
        return type() == JsonType.STRING;
    }

    default JsonString asJsonString() {
        return (JsonString) this;
    }

    default boolean isNumber() {
        return type() == JsonType.NUMBER;
    }

    default IJsonNumber<?> asJsonNumber() {
        return (IJsonNumber<?>) this;
    }

    default boolean isBoolean() {
        return type() == JsonType.BOOLEAN;
    }

    default JsonBoolean asJsonBoolean() {
        return (JsonBoolean) this;
    }

}
