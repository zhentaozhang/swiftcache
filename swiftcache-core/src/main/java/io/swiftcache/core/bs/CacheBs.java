package io.swiftcache.core.bs;

import io.swiftcache.api.Cache;
import io.swiftcache.api.context.CacheContext;
import io.swiftcache.api.evict.CacheEvict;
import io.swiftcache.api.expire.CacheExpire;
import io.swiftcache.api.interceptor.CacheInterceptor;
import io.swiftcache.api.listener.CacheListener;
import io.swiftcache.api.load.CacheLoad;
import io.swiftcache.api.persist.CachePersist;
import io.swiftcache.core.core.Caches;
import io.swiftcache.core.core.DefaultCacheContext;
import io.swiftcache.core.support.evict.CacheEvicts;
import io.swiftcache.core.support.expire.CacheExpires;
import io.swiftcache.core.support.interceptor.CacheInterceptors;
import io.swiftcache.core.support.load.CacheLoads;
import io.swiftcache.core.support.persist.CachePersists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CacheBs<K,V> {

    private CacheBs(){}

    public static <K,V> CacheBs<K,V> newInstance() {
        return new CacheBs<>();
    }

    private Cache<K,V> cache = Caches.defaultStrategy();

    private int size = Integer.MAX_VALUE;

    private CacheEvict<K,V> evict = CacheEvicts.defaultStrategy();

    private CacheLoad<K,V> load = CacheLoads.defaultStrategy();

    private CachePersist<K,V> persist = CachePersists.defaultStrategy();

    private CacheExpire<K, V> expire = CacheExpires.defaultStrategy();

    private List<CacheInterceptor<K,V>> interceptorList = CacheInterceptors.defaultStrategy();

    private List<CacheListener<K,V>> listeners = new ArrayList<>();

    public CacheBs<K, V> listener(CacheListener<K, V> listener) {
        Objects.requireNonNull(listener, "listener");
        this.listeners.add(listener);
        return this;
    }

    public CacheBs<K, V> size(int size) {
        if (size < 0) throw new IllegalArgumentException("size must be >= 0");
        this.size = size;
        return this;
    }

    public CacheBs<K, V> evict(CacheEvict<K, V> evict) {
        Objects.requireNonNull(evict, "evict");
        this.evict = evict;
        return this;
    }

    public CacheBs<K, V> load(CacheLoad<K, V> load) {
        Objects.requireNonNull(load, "load");
        this.load = load;
        return this;
    }

    public CacheBs<K, V> persist(CachePersist<K, V> persist) {
        Objects.requireNonNull(persist, "persist");
        this.persist = persist;
        return this;
    }

    public CacheBs<K, V> expire(CacheExpire<K, V> expire) {
        Objects.requireNonNull(expire, "expire");
        this.expire = expire;
        return this;
    }

    public CacheBs<K, V> interceptorList(List<CacheInterceptor<K,V>> interceptorList) {
        this.interceptorList = interceptorList;
        return this;
    }

    public CacheBs<K, V> cache(Cache<K, V> cache) {
        Objects.requireNonNull(cache, "cache");
        this.cache = cache;
        return this;
    }

    public Cache<K,V> build() {
        DefaultCacheContext<K,V> cacheContext = new DefaultCacheContext<>();

        Collections.sort(interceptorList, (o1, o2) -> o1.order() - o2.order());

        cacheContext.evict(evict);
        cacheContext.size(size);
        cacheContext.load(load);
        cacheContext.persist(persist);
        cacheContext.interceptorList(interceptorList);
        cacheContext.listeners(listeners);

        Map<K,V> map = cacheContext.map();

        this.expire.init(map);
        this.persist.init(map, expire);
        this.load.init(map, expire);

        cacheContext.expire(expire);
        cacheContext.persist(persist);

        this.cache.init(cacheContext);

        this.load.load();

        return cache;
    }

}
