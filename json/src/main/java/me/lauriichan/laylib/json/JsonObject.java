package me.lauriichan.laylib.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

public final class JsonObject
    implements IJson<Map<String, IJson<?>>>, Map<String, IJson<?>>, Iterable<Object2ObjectMap.Entry<String, IJson<?>>> {

    private final Object2ObjectLinkedOpenHashMap<String, IJson<?>> map = new Object2ObjectLinkedOpenHashMap<>();

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
