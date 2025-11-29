package io.swiftcache.core.support.evict.impl;

import io.swiftcache.api.CacheEntry;
import io.swiftcache.api.context.CacheContext;
import io.swiftcache.core.support.evict.AbstractCacheEvict;

/**
 * 丢弃策略
 * @since 0.0.2
 */
public class CacheEvictNone<K,V> extends AbstractCacheEvict<K,V> {

    @Override
    public CacheEntry<K, V> evict(CacheContext<K, V> context, K evictKey) {
        return null;
    }

}
