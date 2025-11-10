package io.swiftcache.core.support.persist.impl;

import io.swiftcache.core.support.persist.AbstractCachePersist;

/**
 * 缓存持久化-无任何操作
 * @author binbin.hou
 * @since 0.0.8
 */
public class CachePersistNone<K,V> extends AbstractCachePersist<K,V> {

    @Override
    protected void doPersist() {

    }

    @Override
    protected void initExecutorService() {

    }

    @Override
    protected void persistScheduleStart() {

    }

}
