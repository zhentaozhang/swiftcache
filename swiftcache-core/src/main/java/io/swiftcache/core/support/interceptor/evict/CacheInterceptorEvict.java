package io.swiftcache.core.support.interceptor.evict;

import io.swiftcache.api.context.CacheContext;
import io.swiftcache.api.evict.CacheEvict;
import io.swiftcache.api.interceptor.CacheInterceptorContext;
import io.swiftcache.core.constant.enums.CacheInterceptorType;
import io.swiftcache.core.support.interceptor.AbstractCacheInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 驱除策略拦截器
 *
 * @since 0.0.11
 */
public class CacheInterceptorEvict<K,V> extends AbstractCacheInterceptor<K, V> {

    private static final Logger log = LoggerFactory.getLogger(CacheInterceptorEvict.class);

    @Override
    protected String getType() {
        return CacheInterceptorType.EVICT.code();
    }

    @Override
    public void before(CacheInterceptorContext<K,V> context) {
        final CacheContext<K, V> cacheContext = context.cacheContext();
        final CacheEvict<K,V> evict = cacheContext.evict();

        // 执行数据的淘汰
        Object[] params = context.params();
        K key = null;
        if(params != null && params.length > 0) {
            key = (K) params[0];
        }


        evict.evict(cacheContext, key);
    }

    @Override
    @SuppressWarnings("all")
    public void after(CacheInterceptorContext<K,V> context) {
    }

}
