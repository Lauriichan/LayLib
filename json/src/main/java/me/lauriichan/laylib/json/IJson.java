package me.lauriichan.laylib.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import me.lauriichan.laylib.json.util.PrimitiveMapper;

public interface IJson<V> {

    JsonNull NULL = JsonNull.NULL;

    JsonBoolean TRUE = JsonBoolean.TRUE;
    JsonBoolean FALSE = JsonBoolean.FALSE;

    static JsonBoolean of(final boolean state) {
        return state ? TRUE : FALSE;
    }

    static JsonBoolean of(final Boolean state) {
        Objects.requireNonNull(state, "String can't be null");
        return state ? TRUE : FALSE;
    }

    static JsonString of(final String string) {
        Objects.requireNonNull(string, "String can't be null");
        return new JsonString(string);
    }

    static JsonByte of(final byte value) {
        return JsonByte.of(value);
    }

    static JsonByte of(final Byte value) {
        Objects.requireNonNull(value, "Byte can't be null");
        return JsonByte.of(value);
    }

    static JsonShort of(final short value) {
        return new JsonShort(value);
    }

    static JsonShort of(final Short value) {
        Objects.requireNonNull(value, "Short can't be null");
        return new JsonShort(value);
    }

    static JsonInteger of(final int value) {
        return new JsonInteger(value);
    }

    static JsonInteger of(final Integer value) {
        Objects.requireNonNull(value, "Integer can't be null");
        return new JsonInteger(value);
    }

    static JsonLong of(final long value) {
        return new JsonLong(value);
    }

    static JsonLong of(final Long value) {
        Objects.requireNonNull(value, "Long can't be null");
        return new JsonLong(value);
    }

    static JsonBigInteger of(final BigInteger value) {
        Objects.requireNonNull(value, "BigInteger can't be null");
        return new JsonBigInteger(value);
    }

    static JsonFloat of(final float value) {
        return new JsonFloat(value);
    }

    static JsonFloat of(final Float value) {
        Objects.requireNonNull(value, "Float can't be null");
        return new JsonFloat(value);
    }

    static JsonDouble of(final double value) {
        return new JsonDouble(value);
    }

    static JsonDouble of(final Double value) {
        Objects.requireNonNull(value, "Double can't be null");
        return new JsonDouble(value);
    }

    static JsonBigDecimal of(final BigDecimal value) {
        Objects.requireNonNull(value, "BigDecimal can't be null");
        return new JsonBigDecimal(value);
    }

    static IJson<?> of(final Object object) throws IllegalArgumentException {
        if (object == null) {
            return JsonNull.NULL;
        }
        final Class<?> type = PrimitiveMapper.fromPrimitive(object.getClass());
        if (type == Boolean.class) {
            return of((Boolean) object);
        }
        if (type == String.class) {
            return new JsonString((String) object);
        }
        if (type == Byte.class) {
            return JsonByte.of((Byte) object);
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
    
    default boolean isType(JsonType type) {
        return type().isType(type);
    }

    default boolean isJson() {
        return type().isType(JsonType.JSON);
    }

    default boolean isPrimitive() {
        return type().isType(JsonType.PRIMITIVE);
    }

    default boolean isArray() {
        return type().isType(JsonType.ARRAY);
    }

    default JsonArray asJsonArray() {
        return (JsonArray) this;
    }

    default boolean isObject() {
        return type().isType(JsonType.OBJECT);
    }

    default JsonObject asJsonObject() {
        return (JsonObject) this;
    }

    default boolean isNull() {
        return type().isType(JsonType.NULL);
    }

    default JsonNull asJsonNull() {
        return (JsonNull) this;
    }

    default boolean isString() {
        return type().isType(JsonType.STRING);
    }
    
    default String asString() {
        return (String) value();
    }

    default JsonString asJsonString() {
        return (JsonString) this;
    }

    default boolean isNumber() {
        return type().isType(JsonType.NUMBER);
    }
    
    default Number asNumber() {
        return (Number) value();
    }

    default IJsonNumber<?> asJsonNumber() {
        return (IJsonNumber<?>) this;
    }

    default boolean isBoolean() {
        return type().isType(JsonType.BOOLEAN);
    }
    
    default boolean asBoolean() {
        return ((Boolean) value()).booleanValue();
    }

    default JsonBoolean asJsonBoolean() {
        return (JsonBoolean) this;
    }

    @SuppressWarnings("unchecked")
    default <J extends IJson<?>> Optional<J> as(final Class<J> jsonType) {
        if (jsonType.isAssignableFrom(getClass())) {
            return Optional.of((J) this);
        }
        return Optional.empty();
    }

}
