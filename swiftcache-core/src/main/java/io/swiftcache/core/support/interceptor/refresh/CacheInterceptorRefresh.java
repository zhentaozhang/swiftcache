package io.swiftcache.core.support.interceptor.refresh;

import io.swiftcache.api.context.CacheContext;
import io.swiftcache.api.interceptor.CacheInterceptorContext;
import io.swiftcache.core.constant.enums.CacheInterceptorType;
import io.swiftcache.core.support.interceptor.AbstractCacheInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 刷新
 *
 * @since 0.0.5
 */
public class CacheInterceptorRefresh<K,V> extends AbstractCacheInterceptor<K, V> {

    private static final Logger log = LoggerFactory.getLogger(CacheInterceptorRefresh.class);

    @Override
    protected String getType() {
        return CacheInterceptorType.REFRESH.code();
    }

    @Override
    public void before(CacheInterceptorContext<K,V> context) {
        log.debug("[Cache] refresh before start");
        final CacheContext<K,V> cacheContext = context.cacheContext();

        // 刷新指定的 Key
        Object[] params = context.params();
        if(params != null && params.length > 0) {
            K key = (K) params[0];
            cacheContext.expire().refreshExpire(Arrays.asList(key));
            return;
        }

        // 刷新全部
        final var cacheMap = cacheContext.map();
        cacheContext.expire().refreshExpire(cacheMap.keySet());
    }


    @Override
    public void after(CacheInterceptorContext<K,V> context) {
    }

}
