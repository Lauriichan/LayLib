package me.lauriichan.laylib.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonBigInteger implements IJsonNumber<BigInteger> {

    private final BigInteger value;

    public JsonBigInteger(final long value) {
        this.value = BigInteger.valueOf(value);
    }

    public JsonBigInteger(final BigInteger value) {
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
            if (obj instanceof BigInteger) {
                return value.equals(obj);
            } else if (obj instanceof BigDecimal) {
                try {
                    return value.equals(((BigDecimal) obj).toBigIntegerExact());
                } catch (final ArithmeticException ignore) {
                    return false;
                }
            }
            return value.equals(BigInteger.valueOf(((Number) obj).longValue()));
        }
        if (obj instanceof IJsonNumber) {
            return value.equals(((IJsonNumber<?>) obj).asBigInteger());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /*
     * IJson implementation
     */

    @Override
    public JsonType type() {
        return JsonType.BIG_INTEGER;
    }

    @Override
    public BigInteger value() {
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
        return value;
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
        return new BigDecimal(value);
    }

}
