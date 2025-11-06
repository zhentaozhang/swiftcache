package io.swiftcache.api.evict;

import io.swiftcache.api.CacheEntry;
import io.swiftcache.api.context.CacheContext;

public interface CacheEvict<K, V> {

    CacheEntry<K,V> evict(final CacheContext<K,V> cacheContext, final K newKey);

    void updateKey(final CacheContext<K, V> cacheContext, final K evictKey);

    void removeKey(final CacheContext<K, V> cacheContext, final K evictKey);

}
