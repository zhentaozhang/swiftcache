package io.swiftcache.api.interceptor;

import io.swiftcache.api.CacheOrder;

public interface CacheInterceptor<K,V> extends CacheOrder{

    boolean match(final CacheInterceptorContext<K,V> context);

    void before(CacheInterceptorContext<K,V> context);

    void after(CacheInterceptorContext<K,V> context);

    void exception(final CacheInterceptorContext context, final Exception exception);

}
