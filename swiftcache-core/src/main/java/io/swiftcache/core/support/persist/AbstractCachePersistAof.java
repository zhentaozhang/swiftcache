package io.swiftcache.core.support.persist;

import io.swiftcache.api.persist.CachePersistAof;

/**
 * 缓存持久化-适配器模式
 * @since 1.0.0
 */
public abstract class AbstractCachePersistAof<K,V> extends AbstractCachePersist<K,V>
        implements CachePersistAof<K,V> {

}
