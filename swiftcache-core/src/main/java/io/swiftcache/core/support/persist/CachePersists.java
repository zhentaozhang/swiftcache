package io.swiftcache.core.support.persist;

import io.swiftcache.api.persist.CachePersist;
import io.swiftcache.core.support.persist.impl.CachePersistAof;
import io.swiftcache.core.support.persist.impl.CachePersistDbJson;
import io.swiftcache.core.support.persist.impl.CachePersistNone;

/**
 * 缓存持久化工具类
 * @author binbin.hou
 * @since 0.0.8
 */
public final class CachePersists {

    private CachePersists(){}

    /**
     * 默认操作
     * @param <K> key
     * @param <V> value
     * @return 结果
     * @since 1.0.0
     */
    public static <K,V> CachePersist<K,V> defaultStrategy() {
        return none();
    }

    /**
     * 无操作
     * @param <K> key
     * @param <V> value
     * @return 结果
     * @since 0.0.8
     */
    public static <K,V> CachePersist<K,V> none() {
        return new CachePersistNone<>();
    }

    /**
     * DB json 操作
     * @param <K> key
     * @param <V> value
     * @param path 文件路径
     * @return 结果
     * @since 0.0.8
     */
    public static <K,V> CachePersist<K,V> dbJson(final String path) {
        return new CachePersistDbJson<>(path);
    }

    /**
     * AOF 持久化
     * @param <K> key
     * @param <V> value
     * @param path 文件路径
     * @return 结果
     * @since 0.0.10
     */
    public static <K,V> CachePersist<K,V> aof(final String path) {
        return new CachePersistAof<>(path);
    }

}
