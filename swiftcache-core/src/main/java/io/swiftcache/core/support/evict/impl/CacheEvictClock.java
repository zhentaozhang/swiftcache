package io.swiftcache.core.support.evict.impl;

import io.swiftcache.api.CacheEntry;
import io.swiftcache.api.context.CacheContext;
import io.swiftcache.core.model.DefaultCacheEntry;
import io.swiftcache.core.support.evict.AbstractCacheEvict;
import io.swiftcache.core.support.struct.lru.LruMap;
import io.swiftcache.core.support.struct.lru.impl.LruMapCircleList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 淘汰策略-clock 算法
 *
 * @author binbin.hou
 * @since 0.0.15
 */
public class CacheEvictClock<K,V> extends AbstractCacheEvict<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheEvictClock.class);

    /**
     * 循环链表
     * @since 0.0.15
     */
    private final LruMap<K,V> circleList;

    public CacheEvictClock() {
        this.circleList = new LruMapCircleList<>();
    }

    @Override
    public CacheEntry<K, V> evict(CacheContext<K, V> context, final K newKey) {
        CacheEntry<K, V> result = null;

        // 超过限制，移除队尾的元素
        if(isNeedEvict(context)) {
            CacheEntry<K,V>  evictEntry = circleList.removeEldest();;
            // 执行缓存移除操作
            final K evictKey = evictEntry.key();
            V evictValue = doEvictRemove(context, evictKey);

            log.debug("基于 clock 算法淘汰 key：{}, value: {}", evictKey, evictValue);
            result = new DefaultCacheEntry<>(evictKey, evictValue);
        }

        return result;
    }


    /**
     * 更新信息
     * @param key 元素
     * @since 0.0.15
     */
    @Override
    public void updateKey(CacheContext<K, V> context, final K key) {
        this.circleList.updateKey(key);
    }

    /**
     * 移除元素
     *
     * @param key 元素
     * @since 0.0.15
     */
    @Override
    public void removeKey(CacheContext<K, V> context, final K key) {
        this.circleList.removeKey(key);
    }

}
