package io.swiftcache.core.support.expire;

import io.swiftcache.api.expire.CacheExpire;
import io.swiftcache.core.support.expire.impl.CacheExpireNone;
import io.swiftcache.core.support.expire.impl.CacheExpireRandom;
import io.swiftcache.core.support.expire.impl.CacheExpireSequence;
import io.swiftcache.core.support.expire.impl.CacheExpireSort;

/**
 * 缓存淘汰策略工具类
 *
 * @since 1.0.0
 */
public class CacheExpires {

    /**
     * 默认策略
     * @return 实现
     * @param <K> 泛型 key
     * @param <V> 泛型 value
     */
    public static <K,V> CacheExpire<K,V> defaultStrategy() {
        return random();
    }

    /**
     * 排序策略
     *
     * 缺点：内存占用
     * @return 实现
     * @param <K> 泛型 key
     * @param <V> 泛型 value
     */
    public static <K,V> CacheExpire<K,V> sort() {
        return new CacheExpireSort<>();
    }

    /**
     * 随机策略
     * @return 实现
     * @param <K> 泛型 key
     * @param <V> 泛型 value
     */
    public static <K,V> CacheExpire<K,V> random() {
        return new CacheExpireRandom<>();
    }

    /**
     * 顺序策略
     * @return 实现
     * @param <K> 泛型 key
     * @param <V> 泛型 value
     */
    public static <K,V> CacheExpire<K,V> sequence() {
        return new CacheExpireSequence<>();
    }

    /**
     * 空策略
     * @return 实现
     * @param <K> 泛型 key
     * @param <V> 泛型 value
     */
    public static <K,V> CacheExpire<K,V> none() {
        return new CacheExpireNone<>();
    }

}
