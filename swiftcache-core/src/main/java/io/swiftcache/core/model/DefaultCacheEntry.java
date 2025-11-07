package io.swiftcache.core.model;

import io.swiftcache.api.CacheEntry;

public class DefaultCacheEntry<K,V> implements CacheEntry<K,V> {

    private final K key;

    private final V value;

    public static <K,V> DefaultCacheEntry<K,V> of(final K key,
                                                   final V value) {
        return new DefaultCacheEntry<>(key, value);
    }

    public DefaultCacheEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K key() {
        return key;
    }

    @Override
    public V value() {
        return value;
    }

    @Override
    public String toString() {
        return "EvictEntry{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }

}
