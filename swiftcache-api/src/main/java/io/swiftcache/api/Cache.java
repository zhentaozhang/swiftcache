package io.swiftcache.api;

import io.swiftcache.api.context.CacheContext;

import java.util.Set;

public interface Cache<K, V> {

    Cache<K, V> init(final CacheContext<K, V> context);

    CacheContext<K, V> cacheContext();

    Cache<K, V> expireAt(final K key, final long unixTime);

    int size();

    boolean isEmpty();

    boolean containsKey(K key);

    V get(K key);

    V put(K key, V value);

    V remove(K key);

    Set<K> keySet();

}
