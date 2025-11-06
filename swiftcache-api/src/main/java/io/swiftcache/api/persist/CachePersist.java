package io.swiftcache.api.persist;

import io.swiftcache.api.expire.CacheExpire;

import java.util.Map;

public interface CachePersist<K, V> {

    CachePersist<K, V> init(final Map<K, V> map, final CacheExpire<K, V> expire);

}
