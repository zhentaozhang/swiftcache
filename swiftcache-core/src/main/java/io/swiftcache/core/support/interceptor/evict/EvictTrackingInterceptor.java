package io.swiftcache.core.support.interceptor.evict;

import io.swiftcache.api.context.CacheContext;
import io.swiftcache.api.evict.CacheEvict;
import io.swiftcache.api.interceptor.CacheInterceptorContext;
import io.swiftcache.core.constant.enums.CacheInterceptorType;
import io.swiftcache.core.support.interceptor.AbstractCacheInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvictTrackingInterceptor<K,V> extends AbstractCacheInterceptor<K, V> {

    private static final Logger log = LoggerFactory.getLogger(EvictTrackingInterceptor.class);

    @Override
    protected String getType() {
        return null;
    }

    @Override
    public boolean match(CacheInterceptorContext<K, V> context) {
        return context.typeList().contains(CacheInterceptorType.EVICT_UPDATE.code())
            || context.typeList().contains(CacheInterceptorType.EVICT_REMOVE.code());
    }

    @Override
    public void before(CacheInterceptorContext<K,V> context) {
    }

    @Override
    @SuppressWarnings("all")
    public void after(CacheInterceptorContext<K,V> context) {
        final CacheContext<K, V> cacheContext = context.cacheContext();
        CacheEvict<K,V> evict = cacheContext.evict();

        Object[] params = context.params();
        final K key = (K) params[0];

        if (context.typeList().contains(CacheInterceptorType.EVICT_REMOVE.code())) {
            evict.removeKey(cacheContext, key);
        } else {
            evict.updateKey(cacheContext, key);
        }
    }

}
