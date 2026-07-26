package io.swiftcache.core.core;

import io.swiftcache.api.context.CacheContext;
import io.swiftcache.api.evict.CacheEvict;
import io.swiftcache.api.expire.CacheExpire;
import io.swiftcache.api.interceptor.CacheInterceptor;
import io.swiftcache.api.listener.CacheListener;
import io.swiftcache.api.load.CacheLoad;
import io.swiftcache.api.persist.CachePersist;
import io.swiftcache.api.stats.CacheStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCacheContext<K,V> implements CacheContext<K, V> {

    private Map<K, V> map = new ConcurrentHashMap<>();

    private int size;

    private CacheEvict<K,V> evict;

    private CacheExpire<K,V> expire;

    private CacheLoad<K,V> load;

    private CachePersist<K,V> persist;

    private List<CacheInterceptor<K,V>> interceptorList;

    private final DefaultCacheStats stats = new DefaultCacheStats();

    private List<CacheListener<K, V>> listeners = new ArrayList<>();

    @Override
    public Map<K, V> map() {
        return map;
    }

    @Override
    public int size() {
        return size;
    }

    public DefaultCacheContext<K, V> size(int size) {
        this.size = size;
        return this;
    }

    @Override
    public CacheEvict<K, V> evict() {
        return evict;
    }

    public DefaultCacheContext<K, V> evict(CacheEvict<K, V> evict) {
        this.evict = evict;
        return this;
    }

    @Override
    public CacheExpire<K, V> expire() {
        return expire;
    }

    public DefaultCacheContext<K, V> expire(CacheExpire<K, V> expire) {
        this.expire = expire;
        return this;
    }

    @Override
    public CacheLoad<K, V> load() {
        return load;
    }

    public DefaultCacheContext<K, V> load(CacheLoad<K, V> load) {
        this.load = load;
        return this;
    }

    @Override
    public CachePersist<K, V> persist() {
        return persist;
    }

    public DefaultCacheContext<K, V> persist(CachePersist<K, V> persist) {
        this.persist = persist;
        return this;
    }

    @Override
    public List<CacheInterceptor<K, V>> interceptorList() {
        return interceptorList;
    }

    public DefaultCacheContext<K, V> interceptorList(List<CacheInterceptor<K, V>> interceptorList) {
        this.interceptorList = interceptorList;
        return this;
    }

    @Override
    public CacheStats stats() {
        return stats;
    }

    @Override
    public List<CacheListener<K, V>> listeners() {
        return listeners;
    }

    public DefaultCacheContext<K, V> listeners(List<CacheListener<K, V>> listeners) {
        this.listeners = listeners;
        return this;
    }

}
