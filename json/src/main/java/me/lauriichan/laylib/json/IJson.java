package me.lauriichan.laylib.json;

import java.math.BigDecimal;
import java.math.BigInteger;

import me.lauriichan.laylib.json.util.PrimitiveMapper;

import java.lang.IllegalArgumentException;

public interface IJson<V> {
    
    public static final JsonNull NULL = JsonNull.NULL;
    
    public static final JsonBoolean TRUE = JsonBoolean.TRUE;
    public static final JsonBoolean FALSE = JsonBoolean.FALSE;
    
    public static JsonBoolean of(boolean state) {
        return state ? TRUE : FALSE;
    }
    
    public static JsonString of(String string) {
        return new JsonString(string);
    }
    
    public static JsonByte of(byte value) {
        return JsonByte.of(value);
    }
    
    public static JsonShort of(short value) {
        return new JsonShort(value);
    }
    
    public static JsonInteger of(int value) {
        return new JsonInteger(value);
    }
    
    public static JsonLong of(long value) {
        return new JsonLong(value);
    }
    
    public static JsonBigInteger of(BigInteger value) {
        return new JsonBigInteger(value);
    }
    
    public static JsonFloat of(float value) {
        return new JsonFloat(value);
    }
    
    public static JsonDouble of(double value) {
        return new JsonDouble(value);
    }
    
    public static JsonBigDecimal of(BigDecimal value) {
        return new JsonBigDecimal(value);
    }

    public static IJson<?> of(Object object) throws IllegalArgumentException {
        if (object == null) {
            return JsonNull.NULL;
        }
        Class<?> type = PrimitiveMapper.fromPrimitive(object.getClass());
        if (object == Boolean.class) {
            return of((Boolean) object);
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
        throw new IllegalArgumentException("Unsupported object of type %s: %s".formatted(type, object));
    }

    JsonType type();

    V value();

    public default boolean isArray() {
        return type() == JsonType.ARRAY;
    }

    public default JsonArray asJsonArray() {
        return (JsonArray) this;
    }

    public default boolean isObject() {
        return type() == JsonType.OBJECT;
    }

    public default JsonObject asJsonObject() {
        return (JsonObject) this;
    }

    public default boolean isNull() {
        return type() == JsonType.NULL;
    }

    public default JsonNull asJsonNull() {
        return (JsonNull) this;
    }

    public default boolean isString() {
        return type() == JsonType.STRING;
    }

    public default JsonString asJsonString() {
        return (JsonString) this;
    }

    public default boolean isNumber() {
        return type() == JsonType.NUMBER;
    }

    public default IJsonNumber<?> asJsonNumber() {
        return (IJsonNumber<?>) this;
    }

    public default boolean isBoolean() {
        return type() == JsonType.BOOLEAN;
    }

    public default JsonBoolean asJsonBoolean() {
        return (JsonBoolean) this;
    }

}
