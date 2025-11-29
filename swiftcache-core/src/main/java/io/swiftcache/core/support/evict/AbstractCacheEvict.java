package io.swiftcache.core.support.evict;

import io.swiftcache.api.CacheEntry;
import io.swiftcache.api.context.CacheContext;
import io.swiftcache.api.evict.CacheEvict;

/**
 * 丢弃策略-抽象实现类
 * @since 0.0.11
 */
public abstract class AbstractCacheEvict<K,V> implements CacheEvict<K,V> {


    /**
     * 是否需要驱逐
     * @param context 上下文
     * @return 结果
     */
    protected boolean isNeedEvict(CacheContext<K, V> context) {
        return context.map().size() >= context.size();
    }

    /**
     * 执行驱逐删除
     * @param context 上下文
     * @param key 键
     * @return 结果
     */
    protected V doEvictRemove(final CacheContext<K, V> context,
                              final K key) {
        return context.map().remove(key);
    }


    @Override
    public void updateKey(CacheContext<K, V> context, K key) {

    }

    @Override
    public void removeKey(CacheContext<K, V> context, K key) {

    }

}
