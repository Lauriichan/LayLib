package me.lauriichan.laylib.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonByte implements IJsonNumber<Byte> {

    private static final JsonByte[] BYTES = new JsonByte[256];

    static {
        int index = 0;
        for (byte value = Byte.MIN_VALUE; value <= Byte.MAX_VALUE; value++) {
            BYTES[index++] = new JsonByte(value);
        }
    }

    public static JsonByte of(final byte value) {
        return BYTES[value + 128];
    }

    private final byte value;

    private JsonByte(final byte value) {
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
        if (obj instanceof Number) {
            return value == ((Number) obj).byteValue();
        }
        if (obj instanceof IJsonNumber) {
            return value == ((IJsonNumber<?>) obj).asByte();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value;
    }

    /*
     * IJson implementation
     */

    @Override
    public JsonType type() {
        return JsonType.BYTE;
    }

    @Override
    public Byte value() {
        return value;
    }

    /*
     * IJsonNumber implementation
     */

    @Override
    public byte asByte() {
        return value;
    }

    @Override
    public short asShort() {
        return value;
    }

    @Override
    public int asInt() {
        return value;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public BigInteger asBigInteger() {
        return BigInteger.valueOf(value);
    }

    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public BigDecimal asBigDecimal() {
        return BigDecimal.valueOf(value);
    }

}
