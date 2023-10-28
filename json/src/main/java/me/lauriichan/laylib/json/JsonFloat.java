package me.lauriichan.laylib.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonFloat implements IJsonNumber<Float> {

    private final float value;

    public JsonFloat(final float value) {
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
            return value == ((Number) obj).floatValue();
        }
        if (obj instanceof IJsonNumber) {
            return value == ((IJsonNumber<?>) obj).asFloat();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Float.hashCode(value);
    }

    /*
     * IJson implementation
     */

    @Override
    public JsonType type() {
        return JsonType.FLOAT;
    }

    @Override
    public Float value() {
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
        return (long) value;
    }

    @Override
    public BigInteger asBigInteger() {
        return BigInteger.valueOf(asLong());
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
