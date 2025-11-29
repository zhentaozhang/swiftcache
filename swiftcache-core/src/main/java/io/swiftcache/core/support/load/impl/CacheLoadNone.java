package io.swiftcache.core.support.load.impl;

import io.swiftcache.core.support.load.AbstractCacheLoad;

/**
 * 加载策略-无
 * @since 0.0.7
 */
public class CacheLoadNone<K,V> extends AbstractCacheLoad<K,V> {

    @Override
    public void doLoad() {

    }

}
