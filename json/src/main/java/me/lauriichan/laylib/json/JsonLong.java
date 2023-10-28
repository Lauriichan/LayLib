package me.lauriichan.laylib.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonLong implements IJsonNumber<Long> {

    private final long value;

    public JsonLong(final long value) {
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
            return value == ((Number) obj).longValue();
        }
        if (obj instanceof IJsonNumber) {
            return value == ((IJsonNumber<?>) obj).asLong();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

    /*
     * IJson implementation
     */

    @Override
    public JsonType type() {
        return JsonType.LONG;
    }

    @Override
    public Long value() {
        return value;
    }

    /*
     * IJsonNumber implementation
     */

    @Override
    public byte asByte() {
        return (byte) value;
    }

    @Override
    public short asShort() {
        return (short) value;
    }

    @Override
    public int asInt() {
        return (int) value;
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
