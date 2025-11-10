package io.swiftcache.core.support.struct.lru;

import io.swiftcache.api.CacheEntry;

public interface LruMap<K,V> {

    CacheEntry<K, V> removeEldest();

    void updateKey(final K key);

    void removeKey(final K key);

    boolean isEmpty();

    boolean contains(final K key);

}
