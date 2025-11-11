package io.swiftcache.core.support.interceptor.stats;

import io.swiftcache.api.context.CacheContext;
import io.swiftcache.api.interceptor.CacheInterceptorContext;
import io.swiftcache.core.constant.enums.CacheInterceptorType;
import io.swiftcache.core.support.interceptor.AbstractCacheInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsInterceptor<K, V> extends AbstractCacheInterceptor<K, V> {

    private static final Logger log = LoggerFactory.getLogger(StatsInterceptor.class);

    @Override
    protected String getType() {
        return CacheInterceptorType.COMMON.code();
    }

    @Override
    public boolean match(CacheInterceptorContext<K, V> context) {
        return context.typeList().contains(CacheInterceptorType.COMMON.code());
    }

    @Override
    public void before(CacheInterceptorContext<K, V> context) {
        log.debug("[Stats] before method={}", context.methodName());
    }

    @Override
    @SuppressWarnings("all")
    public void after(CacheInterceptorContext<K, V> context) {
        final CacheContext<K, V> cacheContext = context.cacheContext();
        final String method = context.methodName();
        final Object result = context.result();

        switch (method) {
            case "get" -> {
                if (result == null) {
                    cacheContext.stats().recordMiss();
                } else {
                    cacheContext.stats().recordHit();
                }
            }
            case "put" -> cacheContext.stats().recordPut();
            case "remove" -> cacheContext.stats().recordEvict();
        }
    }
}
