package io.swiftcache.core.support.expire.impl;

import io.swiftcache.core.support.expire.AbstractCacheExpire;

/**
 * 缓存过期-空策略
 *
 * @since 1.0.0
 */
public class CacheExpireNone<K,V> extends AbstractCacheExpire<K,V> {

    @Override
    protected void initExecutorService() {
        // nothing
    }

    @Override
    protected void expireScheduleStart() {

    }

}
