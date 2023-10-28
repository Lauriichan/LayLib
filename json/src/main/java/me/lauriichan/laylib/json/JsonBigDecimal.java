package me.lauriichan.laylib.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonBigDecimal implements IJsonNumber<BigDecimal> {

    private final BigDecimal value;

    public JsonBigDecimal(final double value) {
        this.value = BigDecimal.valueOf(value);
    }

    public JsonBigDecimal(final BigDecimal value) {
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
            if (obj instanceof BigDecimal) {
                return value.equals(obj);
            } else if (obj instanceof BigInteger) {
                return value.equals(new BigDecimal((BigInteger) obj));
            }
            return value.equals(BigDecimal.valueOf(((Number) obj).doubleValue()));
        }
        if (obj instanceof IJsonNumber) {
            return value.equals(((IJsonNumber<?>) obj).asBigDecimal());
        }
        return false;
    }

    /*
     * IJson implementation
     */

    @Override
    public JsonType type() {
        return JsonType.BIG_DECIMAL;
    }

    @Override
    public BigDecimal value() {
        return value;
    }

    /*
     * IJsonNumber implementation
     */

    @Override
    public byte asByte() {
        return value.byteValue();
    }

    @Override
    public short asShort() {
        return value.shortValue();
    }

    @Override
    public int asInt() {
        return value.intValue();
    }

    @Override
    public long asLong() {
        return value.longValue();
    }

    @Override
    public BigInteger asBigInteger() {
        return value.toBigInteger();
    }

    @Override
    public float asFloat() {
        return value.floatValue();
    }

    @Override
    public double asDouble() {
        return value.doubleValue();
    }

    @Override
    public BigDecimal asBigDecimal() {
        return value;
    }

}
