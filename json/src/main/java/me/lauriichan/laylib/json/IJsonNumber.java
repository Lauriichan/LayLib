package me.lauriichan.laylib.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface IJsonNumber<V extends Number> extends IJson<V> {

    default JsonByte asJsonByte() {
        return (JsonByte) this;
    }

    byte asByte();

    default JsonShort asJsonShort() {
        return (JsonShort) this;
    }

    short asShort();

    default JsonInteger asJsonInteger() {
        return (JsonInteger) this;
    }

    int asInt();

    default JsonLong asJsonLong() {
        return (JsonLong) this;
    }

    long asLong();

    default JsonBigInteger asJsonBigInteger() {
        return (JsonBigInteger) this;
    }

    BigInteger asBigInteger();

    default JsonFloat asJsonFloat() {
        return (JsonFloat) this;
    }

    float asFloat();

    default JsonDouble asJsonDouble() {
        return (JsonDouble) this;
    }

    double asDouble();

    default JsonBigDecimal asJsonBigDecimal() {
        return (JsonBigDecimal) this;
    }

    BigDecimal asBigDecimal();

    @Override
    V value();

}
