package io.swiftcache.core.support.interceptor;

import io.swiftcache.api.context.CacheContext;
import io.swiftcache.api.interceptor.CacheInterceptorContext;

import java.util.List;

/**
 * 耗时统计
 *
 * （1）耗时
 * （2）慢日志
 * @since 0.0.5
 * @param <K> key
 * @param <V> value
 */
public class DefaultCacheInterceptorContext<K,V> implements CacheInterceptorContext<K,V> {

    /**
     * 类型列表
     * @since 1.0.0
     */
    private List<String> typeList;

    /**
     * 缓存上下文
     * @since 1.0.0
     */
    private CacheContext<K, V> cacheContext;

    /**
     * 执行的方法信息
     * @since 0.0.5
     */
    private String methodName;

    /**
     * 执行的参数
     * @since 0.0.5
     */
    private Object[] params;

    /**
     * 方法执行的结果
     * @since 0.0.5
     */
    private Object result;

    /**
     * 开始时间
     * @since 0.0.5
     */
    private long startMills;

    /**
     * 结束时间
     * @since 0.0.5
     */
    private long endMills;

    public static <K,V> DefaultCacheInterceptorContext<K,V> newInstance() {
        return new DefaultCacheInterceptorContext<>();
    }

    @Override
    public List<String> typeList() {
        return typeList;
    }

    public DefaultCacheInterceptorContext<K, V> typeList(List<String> typeList) {
        this.typeList = typeList;
        return this;
    }

    @Override
    public CacheContext<K, V> cacheContext() {
        return cacheContext;
    }

    public DefaultCacheInterceptorContext<K, V> cacheContext(CacheContext<K, V> cacheContext) {
        this.cacheContext = cacheContext;
        return this;
    }

    @Override
    public String methodName() {
        return methodName;
    }

    public DefaultCacheInterceptorContext<K, V> methodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    @Override
    public Object[] params() {
        return params;
    }

    public DefaultCacheInterceptorContext<K, V> params(Object[] params) {
        this.params = params;
        return this;
    }

    @Override
    public Object result() {
        return result;
    }

    public DefaultCacheInterceptorContext<K, V> result(Object result) {
        this.result = result;
        return this;
    }

    @Override
    public long startMills() {
        return startMills;
    }

    public DefaultCacheInterceptorContext<K, V> startMills(long startMills) {
        this.startMills = startMills;
        return this;
    }

    @Override
    public long endMills() {
        return endMills;
    }

    public DefaultCacheInterceptorContext<K, V> endMills(long endMills) {
        this.endMills = endMills;
        return this;
    }
}
