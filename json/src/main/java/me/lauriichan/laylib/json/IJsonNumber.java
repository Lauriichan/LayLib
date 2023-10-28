package me.lauriichan.laylib.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface IJsonNumber<V extends Number> extends IJson<V> {

    public default JsonByte asJsonByte() {
        return (JsonByte) this;
    }

    public byte asByte();

    public default JsonShort asJsonShort() {
        return (JsonShort) this;
    }

    public short asShort();

    public default JsonInteger asJsonInteger() {
        return (JsonInteger) this;
    }

    public int asInt();

    public default JsonLong asJsonLong() {
        return (JsonLong) this;
    }

    public long asLong();

    public default JsonBigInteger asJsonBigInteger() {
        return (JsonBigInteger) this;
    }

    public BigInteger asBigInteger();

    public default JsonFloat asJsonFloat() {
        return (JsonFloat) this;
    }

    public float asFloat();

    public default JsonDouble asJsonDouble() {
        return (JsonDouble) this;
    }

    public double asDouble();

    public default JsonBigDecimal asJsonBigDecimal() {
        return (JsonBigDecimal) this;
    }

    public BigDecimal asBigDecimal();

    @Override
    public V value();

}
