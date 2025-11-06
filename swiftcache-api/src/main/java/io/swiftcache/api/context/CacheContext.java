package io.swiftcache.api.context;

import io.swiftcache.api.evict.CacheEvict;
import io.swiftcache.api.expire.CacheExpire;
import io.swiftcache.api.listener.CacheListener;
import io.swiftcache.api.load.CacheLoad;
import io.swiftcache.api.persist.CachePersist;
import io.swiftcache.api.interceptor.CacheInterceptor;
import io.swiftcache.api.stats.CacheStats;

import java.util.List;
import java.util.Map;

public interface CacheContext<K, V> {

    Map<K, V> map();

    int size();

    CacheEvict<K,V> evict();

    CacheExpire<K,V> expire();

    CacheLoad<K,V> load();

    CachePersist<K,V> persist();

    List<CacheInterceptor<K,V>> interceptorList();

    CacheStats stats();

    List<CacheListener<K, V>> listeners();

}
