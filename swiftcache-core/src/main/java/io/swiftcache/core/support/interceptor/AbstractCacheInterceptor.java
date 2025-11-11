package io.swiftcache.core.support.interceptor;

import io.swiftcache.api.interceptor.CacheInterceptor;
import io.swiftcache.api.interceptor.CacheInterceptorContext;

public abstract class AbstractCacheInterceptor<K,V> implements CacheInterceptor<K,V> {

    /**
     * 当前的类别
     * @return 结果
     */
    protected abstract String getType();

    @Override
    public boolean match(CacheInterceptorContext<K, V> context) {
        String type = getType();
        return context.typeList().contains(type);
    }

    @Override
    public void before(CacheInterceptorContext<K, V> context) {

    }

    @Override
    public void after(CacheInterceptorContext<K, V> context) {

    }

    @Override
    public void exception(CacheInterceptorContext context, Exception exception) {

    }

    @Override
    public int order() {
        return 0;
    }

}
