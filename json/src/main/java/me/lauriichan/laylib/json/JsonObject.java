package me.lauriichan.laylib.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

public final class JsonObject
    implements IJson<Map<String, IJson<?>>>, Map<String, IJson<?>>, Iterable<Object2ObjectMap.Entry<String, IJson<?>>> {

    private final Object2ObjectLinkedOpenHashMap<String, IJson<?>> map = new Object2ObjectLinkedOpenHashMap<>();

    public boolean has(final String key) {
        return map.containsKey(key);
    }

    public boolean has(final String key, final JsonType type) {
        IJson<?> value = map.get(key);
        return value != null && value.isType(type);
    }

    public boolean isNull(final String key) {
        IJson<?> value = map.get(key);
        return value == null || value.isNull();
    }

    public <J extends IJson<?>> J getAs(final String key, final Class<J> jsonType) {
        return getAs(key, jsonType, null);
    }

    public <J extends IJson<?>> J getAs(final String key, final Class<J> jsonType, final J fallback) {
        return getAsOptional(key, jsonType).orElse(fallback);
    }

    public <J extends IJson<?>> Optional<J> getAsOptional(final String key, final Class<J> jsonType) {
        IJson<?> json = map.get(key);
        if (json == null) {
            return Optional.empty();
        }
        return json.as(jsonType);
    }

    public JsonObject getAsObject(final String key) {
        IJson<?> json = map.get(key);
        if (json == null || !json.isObject()) {
            return null;
        }
        return json.asJsonObject();
    }

    public JsonArray getAsArray(final String key) {
        IJson<?> json = map.get(key);
        if (json == null || !json.isArray()) {
            return null;
        }
        return json.asJsonArray();
    }

    public boolean getAsBoolean(final String key) {
        return getAsBoolean(key, false);
    }

    public boolean getAsBoolean(final String key, final boolean fallback) {
        IJson<?> json = map.get(key);
        if (json == null || !json.isBoolean()) {
            return fallback;
        }
        return json.asBoolean();
    }

    public boolean getAsNumericBoolean(final String key) {
        return getAsNumericBoolean(key, false);
    }

    public boolean getAsNumericBoolean(final String key, final boolean fallback) {
        IJson<?> json = map.get(key);
        if (json == null) {
            return fallback;
        }
        if (json.isBoolean()) {
            return json.asBoolean();
        }
        if (json.isNumber()) {
            return json.asNumber().byteValue() != 0;
        }
        return fallback;
    }

    public String getAsString(final String key) {
        return getAsString(key, null);
    }

    public String getAsString(final String key, final String fallback) {
        IJson<?> json = map.get(key);
        if (json == null || !json.isString()) {
            return fallback;
        }
        return json.asString();
    }

    public Number getAsNumber(final String key) {
        return getAsNumber(key, 0);
    }

    public Number getAsNumber(final String key, final Number fallback) {
        IJson<?> json = map.get(key);
        if (json == null || !json.isNumber()) {
            return fallback;
        }
        return json.asNumber();
    }

    public byte getAsByte(final String key) {
        return getAsNumber(key).byteValue();
    }

    public byte getAsByte(final String key, final byte fallback) {
        return getAsNumber(key, fallback).byteValue();
    }

    public short getAsShort(final String key) {
        return getAsNumber(key).shortValue();
    }

    public short getAsShort(final String key, final short fallback) {
        return getAsNumber(key, fallback).shortValue();
    }

    public int getAsInt(final String key) {
        return getAsNumber(key).intValue();
    }

    public int getAsInt(final String key, final int fallback) {
        return getAsNumber(key, fallback).intValue();
    }

    public long getAsLong(final String key) {
        return getAsNumber(key).longValue();
    }

    public long getAsLong(final String key, final long fallback) {
        return getAsNumber(key, fallback).longValue();
    }

    public float getAsFloat(final String key) {
        return getAsNumber(key).floatValue();
    }

    public float getAsFloat(final String key, final float fallback) {
        return getAsNumber(key, fallback).floatValue();
    }

    public double getAsDouble(final String key) {
        return getAsNumber(key).doubleValue();
    }

    public double getAsDouble(final String key, final double fallback) {
        return getAsNumber(key, fallback).doubleValue();
    }

    public BigInteger getAsBigInt(final String key) {
        return getAsBigInt(key, BigInteger.ZERO);
    }

    public BigInteger getAsBigInt(final String key, final Number fallback) {
        return getAsBigInt(key, fallback == null ? null : BigInteger.valueOf(fallback.longValue()));
    }

    public BigInteger getAsBigInt(final String key, final BigInteger fallback) {
        IJson<?> json = map.get(key);
        if (json == null || !json.isNumber()) {
            return fallback;
        }
        return json.asJsonNumber().asBigInteger();
    }

    public BigDecimal getAsBigDecimal(final String key) {
        return getAsBigDecimal(key, BigDecimal.ZERO);
    }

    public BigDecimal getAsBigDecimal(final String key, final Number fallback) {
        return getAsBigDecimal(key, fallback == null ? null : BigDecimal.valueOf(fallback.doubleValue()));
    }

    public BigDecimal getAsBigDecimal(final String key, final BigDecimal fallback) {
        IJson<?> json = map.get(key);
        if (json == null || !json.isNumber()) {
            return fallback;
        }
        return json.asJsonNumber().asBigDecimal();
    }

    public IJson<?> put(final String key, final Object value) {
        return map.put(key, IJson.of(value));
    }

    /*
     * IJson implementation
     */

    @Override
    public JsonType type() {
        return JsonType.OBJECT;
    }

    @Override
    public Map<String, IJson<?>> value() {
        return new Object2ObjectLinkedOpenHashMap<>(map);
    }

    /*
     * Object override
     */

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof JsonObject) {
            return map.equals(((JsonObject) obj).map);
        }
        if (obj instanceof Map) {
            return map.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    /*
     * Map implementation
     */

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return map.containsValue(value);
    }

    @Override
    public IJson<?> get(final Object key) {
        return map.get(key);
    }

    @Override
    public IJson<?> put(final String key, final IJson<?> value) {
        return map.put(key, value);
    }

    @Override
    public IJson<?> remove(final Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends IJson<?>> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<IJson<?>> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, IJson<?>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Iterator<Object2ObjectMap.Entry<String, IJson<?>>> iterator() {
        return map.object2ObjectEntrySet().iterator();
    }

}
