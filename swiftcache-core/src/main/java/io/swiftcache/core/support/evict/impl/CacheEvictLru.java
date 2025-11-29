package io.swiftcache.core.support.evict.impl;

import io.swiftcache.api.CacheEntry;
import io.swiftcache.api.context.CacheContext;
import io.swiftcache.core.model.DefaultCacheEntry;
import io.swiftcache.core.support.evict.AbstractCacheEvict;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 丢弃策略-LRU 最近最少使用
 * @since 0.0.11
 */
public class CacheEvictLru<K,V> extends AbstractCacheEvict<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheEvictLru.class);

    private final LinkedHashMap<K, K> accessOrder = new LinkedHashMap<>(16, 0.75f, true);

    @Override
    public CacheEntry<K, V> evict(CacheContext<K, V> context, final K newKey) {
        CacheEntry<K, V> result = null;
        if(isNeedEvict(context)) {
            K evictKey = accessOrder.keySet().iterator().next();
            V evictValue = doEvictRemove(context, evictKey);
            accessOrder.remove(evictKey);
            result = new DefaultCacheEntry<>(evictKey, evictValue);
        }

        return result;
    }

    @Override
    public void updateKey(CacheContext<K, V> context, final K key) {
        accessOrder.put(key, key);
    }

    @Override
    public void removeKey(CacheContext<K, V> context, final K key) {
        accessOrder.remove(key);
    }

}
