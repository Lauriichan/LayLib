package me.lauriichan.laylib.command.util;

import java.util.Map.Entry;

public final class MapUtil {

    private MapUtil() {
        throw new UnsupportedOperationException();
    }

    public static <K, V> Entry<K, V> entry(K key, V value) {
        return new EntryImpl<>(key, value);
    }

    private static class EntryImpl<K, V> implements Entry<K, V> {

        private final K key;
        private V value;

        public EntryImpl(final K key, final V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V tmp = this.value;
            this.value = value;
            return tmp;
        }

    }

}
