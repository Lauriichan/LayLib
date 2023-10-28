package me.lauriichan.laylib.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonInteger implements IJsonNumber<Integer> {

    private final int value;

    public JsonInteger(final int value) {
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
            return value == ((Number) obj).intValue();
        }
        if (obj instanceof IJsonNumber) {
            return value == ((IJsonNumber<?>) obj).asInt();
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
        return JsonType.INTEGER;
    }

    @Override
    public Integer value() {
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
