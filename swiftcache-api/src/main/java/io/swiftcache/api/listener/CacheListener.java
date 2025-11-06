package io.swiftcache.api.listener;

@FunctionalInterface
public interface CacheListener<K, V> {
    void onEvent(CacheEvent<K, V> event);
}
