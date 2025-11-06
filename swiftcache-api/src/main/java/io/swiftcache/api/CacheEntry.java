package io.swiftcache.api;

public interface CacheEntry<K, V> {

    K key();

    V value();

}
