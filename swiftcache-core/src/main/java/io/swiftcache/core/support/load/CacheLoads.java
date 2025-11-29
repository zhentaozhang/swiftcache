package io.swiftcache.core.support.load;

import io.swiftcache.api.load.CacheLoad;
import io.swiftcache.core.support.load.impl.CacheLoadAof;
import io.swiftcache.core.support.load.impl.CacheLoadDbJson;
import io.swiftcache.core.support.load.impl.CacheLoadNone;

/**
 *
 * 加载策略工具类
 * @since 0.0.7
 */
public final class CacheLoads {

    private CacheLoads(){}

    /**
     * 默认加载
     * @param <K> key
     * @param <V> value
     * @return 值
     * @since 1.0.0
     */
    public static <K,V> CacheLoad<K,V> defaultStrategy() {
        return none();
    }

    /**
     * 无加载
     * @param <K> key
     * @param <V> value
     * @return 值
     * @since 0.0.7
     */
    public static <K,V> CacheLoad<K,V> none() {
        return new CacheLoadNone<>();
    }

    /**
     * 文件 JSON
     * @param dbPath 文件路径
     * @param <K> key
     * @param <V> value
     * @return 值
     * @since 0.0.8
     */
    public static <K,V> CacheLoad<K,V> dbJson(final String dbPath) {
        return new CacheLoadDbJson<>(dbPath);
    }

    /**
     * AOF 文件加载模式
     * @param dbPath 文件路径
     * @param <K> key
     * @param <V> value
     * @return 值
     * @since 0.0.10
     */
    public static <K,V> CacheLoad<K,V> aof(final String dbPath) {
        return new CacheLoadAof<>(dbPath);
    }

}
