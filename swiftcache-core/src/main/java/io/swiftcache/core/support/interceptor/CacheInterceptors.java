package io.swiftcache.core.support.interceptor;

import io.swiftcache.api.interceptor.CacheInterceptor;
import io.swiftcache.core.support.interceptor.aof.CacheInterceptorAof;
import io.swiftcache.core.support.interceptor.common.CacheInterceptorCommonCost;
import io.swiftcache.core.support.interceptor.evict.CacheInterceptorEvict;
import io.swiftcache.core.support.interceptor.evict.EvictTrackingInterceptor;
import io.swiftcache.core.support.interceptor.refresh.CacheInterceptorRefresh;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存拦截器工具类
 * @since 0.0.5
 */
public final class CacheInterceptors {

    /**
     * 默认通用
     * @return 结果
     * @since 0.0.5
     * @param <K> key
     * @param <V> value
     */
    @SuppressWarnings("all")
    public static <K,V> List<CacheInterceptor<K,V>> defaultStrategy() {
        return InterceptorChain.defaultStrategy();
    }

    public static <K,V> CacheInterceptor<K,V> commonCost() {
        return new CacheInterceptorCommonCost<>();
    }

    public static <K,V> CacheInterceptor<K,V> evict() {
        return new CacheInterceptorEvict<>();
    }

    public static <K,V> CacheInterceptor<K,V> evictTracking() {
        return new EvictTrackingInterceptor<>();
    }

    public static <K,V> CacheInterceptor<K,V> aof() {
        return new CacheInterceptorAof<>();
    }

    public static <K,V> CacheInterceptor<K,V> refresh() {
        return new CacheInterceptorRefresh<>();
    }


}
