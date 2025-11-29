package io.swiftcache.core.support.interceptor.common;

import io.swiftcache.api.interceptor.CacheInterceptorContext;
import io.swiftcache.core.constant.enums.CacheInterceptorType;
import io.swiftcache.core.support.interceptor.AbstractCacheInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 耗时统计
 *
 * （1）耗时
 * （2）慢日志
 * @since 0.0.5
 * @param <K> key
 * @param <V> value
 */
public class CacheInterceptorCommonCost<K,V> extends AbstractCacheInterceptor<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheInterceptorCommonCost.class);

    @Override
    protected String getType() {
        return CacheInterceptorType.COMMON.code();
    }

    @Override
    public void before(CacheInterceptorContext<K,V> context) {
        log.debug("[Cache] Cost start, methodName: {}， params={}", context.methodName(), Arrays.toString(context.params()));
    }

    @Override
    public void after(CacheInterceptorContext<K,V> context) {
        long costMills = context.endMills()-context.startMills();
        final String methodName = context.methodName();
        log.debug("[Cache] Cost end, methodName: {}, cost: {}ms", methodName, costMills);
    }

}
