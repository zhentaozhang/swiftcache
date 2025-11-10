package io.swiftcache.core.support.load;

import io.swiftcache.api.expire.CacheExpire;
import io.swiftcache.api.load.CacheLoad;

import java.util.Map;

/**
 * 抽象父类
 *
 * @param <K> 键
 * @param <V> 值
 * @since 1.0.0
 */
public abstract class AbstractCacheLoad<K,V> implements CacheLoad<K,V> {

    protected Map<K, V> map;
    protected CacheExpire<K, V> expire;

    @Override
    public void init(Map<K, V> map, CacheExpire<K, V> expire) {
        this.map = map;
        this.expire = expire;
    }

    public abstract void doLoad();

    @Override
    public void load() {
        doLoad();
    }

}
