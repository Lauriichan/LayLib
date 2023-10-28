package me.lauriichan.laylib.json.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;

public final class PrimitiveMapper {

    private static final Object2ObjectMap<Class<?>, Class<?>> primitiveMapping, complexMapping;

    static {
        Object2ObjectArrayMap<Class<?>, Class<?>> map = new Object2ObjectArrayMap<>(8);
        map.put(void.class, Void.class);
        map.put(boolean.class, Boolean.class);
        map.put(byte.class, Byte.class);
        map.put(short.class, Short.class);
        map.put(int.class, Integer.class);
        map.put(long.class, Long.class);
        map.put(float.class, Float.class);
        map.put(double.class, Double.class);
        primitiveMapping = Object2ObjectMaps.unmodifiable(map);
        map = new Object2ObjectArrayMap<>(8);
        for (final Entry<Class<?>, Class<?>> entry : primitiveMapping.object2ObjectEntrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        complexMapping = Object2ObjectMaps.unmodifiable(map);
    }

    public static Class<?> fromPrimitive(final Class<?> type) {
        return primitiveMapping.getOrDefault(type, type);
    }

    public static Class<?> toPrimitive(final Class<?> type) {
        return complexMapping.getOrDefault(type, type);
    }

}
