package io.swiftcache.core.support.evict.impl;

import io.swiftcache.api.CacheEntry;
import io.swiftcache.api.context.CacheContext;
import io.swiftcache.core.model.DefaultCacheEntry;
import io.swiftcache.core.support.evict.AbstractCacheEvict;

import java.util.*;

/**
 * 丢弃策略-先进先出
 * @since 0.0.2
 */
public class CacheEvictFifo<K,V> extends AbstractCacheEvict<K,V> {

    /**
     * queue 信息
     * @since 0.0.2
     */
    private final Set<K> accessOrder = new LinkedHashSet<>();;

    @Override
    public synchronized CacheEntry<K,V> evict(CacheContext<K, V> context, final K newKey) {
        CacheEntry<K,V> result = null;

        if(isNeedEvict(context)) {
            Iterator<K> iterator = accessOrder.iterator();
            K evictKey = iterator.next();
            V evictValue = doEvictRemove(context, evictKey);
            iterator.remove();

            result = new DefaultCacheEntry<>(evictKey, evictValue);
        }

        return result;
    }

    @Override
    public synchronized void updateKey(CacheContext<K, V> context, K key) {
        accessOrder.remove(key);
        accessOrder.add(key);
    }

}
