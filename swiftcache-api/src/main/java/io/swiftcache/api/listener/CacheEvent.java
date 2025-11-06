package io.swiftcache.api.listener;

public record CacheEvent<K, V>(
    CacheEventType type,
    K key,
    V value,
    long timestamp
) {
}
