package io.swiftcache.core.core.impl;

import io.swiftcache.api.Cache;
import io.swiftcache.api.context.CacheContext;
import io.swiftcache.api.interceptor.CacheInterceptor;
import io.swiftcache.api.listener.CacheEvent;
import io.swiftcache.api.listener.CacheEventType;
import io.swiftcache.core.support.interceptor.CacheInterceptorType;
import io.swiftcache.core.support.interceptor.DefaultCacheInterceptorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultCache<K,V> implements Cache<K,V> {

    protected CacheContext<K, V> cacheContext;

    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static final List<String> TYPES_EXPIRE = List.of(
            CacheInterceptorType.COMMON.code(),
            CacheInterceptorType.EVICT_UPDATE.code(),
            CacheInterceptorType.AOF.code()
    );
    private static final List<String> TYPES_SIZE = List.of(
            CacheInterceptorType.COMMON.code(),
            CacheInterceptorType.REFRESH.code()
    );
    private static final List<String> TYPES_IS_EMPTY = List.of(
            CacheInterceptorType.COMMON.code()
    );
    private static final List<String> TYPES_CONTAINS_KEY = List.of(
            CacheInterceptorType.COMMON.code(),
            CacheInterceptorType.REFRESH.code(),
            CacheInterceptorType.EVICT_UPDATE.code()
    );
    private static final List<String> TYPES_GET = List.of(
            CacheInterceptorType.COMMON.code(),
            CacheInterceptorType.REFRESH.code(),
            CacheInterceptorType.EVICT_UPDATE.code()
    );
    private static final List<String> TYPES_PUT = List.of(
            CacheInterceptorType.COMMON.code(),
            CacheInterceptorType.REFRESH.code(),
            CacheInterceptorType.EVICT.code(),
            CacheInterceptorType.EVICT_UPDATE.code(),
            CacheInterceptorType.AOF.code()
    );
    private static final List<String> TYPES_REMOVE = List.of(
            CacheInterceptorType.COMMON.code(),
            CacheInterceptorType.REFRESH.code(),
            CacheInterceptorType.EVICT_REMOVE.code(),
            CacheInterceptorType.AOF.code()
    );
    private static final List<String> TYPES_KEY_SET = List.of(
            CacheInterceptorType.COMMON.code(),
            CacheInterceptorType.REFRESH.code()
    );

    @Override
    public Cache<K, V> init(CacheContext<K, V> context) {
        this.cacheContext = context;
        return this;
    }

    @Override
    public CacheContext<K, V> cacheContext() {
        return cacheContext;
    }


    protected DefaultCacheInterceptorContext<K,V> doFilterBefore(final String methodName,
                                                          final List<String> typeList,
                                                          Object...params) {
        DefaultCacheInterceptorContext<K,V> interceptorContext = DefaultCacheInterceptorContext.newInstance();
        interceptorContext.methodName(methodName);
        interceptorContext.params(params);
        interceptorContext.cacheContext(cacheContext);
        interceptorContext.startMills(System.currentTimeMillis());
        interceptorContext.typeList(typeList);

        final List<CacheInterceptor<K,V>> cacheInterceptorList = cacheContext.interceptorList();
        for(CacheInterceptor<K,V> interceptor : cacheInterceptorList) {
            if(interceptor.match(interceptorContext)) {
                interceptor.before(interceptorContext);
            }
        }
        return interceptorContext;
    }


    protected void doFilterAfter(final DefaultCacheInterceptorContext<K,V> interceptorContext,
                                 final Object result) {
        interceptorContext.endMills(System.currentTimeMillis());
        interceptorContext.result(result);

        final List<CacheInterceptor<K,V>> cacheInterceptorList = cacheContext.interceptorList();
        for(int i = cacheInterceptorList.size()-1; i >=0 ; i--) {
            CacheInterceptor<K,V> interceptor = cacheInterceptorList.get(i);
            if(interceptor.match(interceptorContext)) {
                interceptor.after(interceptorContext);
            }
        }
    }

    private void fireEvent(CacheEventType type, K key, V value) {
        var event = new CacheEvent<>(type, key, value, System.currentTimeMillis());
        for (var listener : cacheContext.listeners()) {
            listener.onEvent(event);
        }
    }

    @Override
    public Cache<K, V> expireAt(K key, long unixTime) {
        Objects.requireNonNull(key, "key");
        lock.writeLock().lock();
        try {
            var context = doFilterBefore("expireAt", TYPES_EXPIRE, key, unixTime);
            cacheContext.expire().expireAt(key, unixTime);
            doFilterAfter(context, null);
        } finally {
            lock.writeLock().unlock();
        }
        fireEvent(CacheEventType.EXPIRE, key, null);
        return this;
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            var context = doFilterBefore("size", TYPES_SIZE);

            int result = cacheContext.map().size();

            doFilterAfter(context, result);

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            var context = doFilterBefore("isEmpty", TYPES_IS_EMPTY);

            boolean result = cacheContext.map().isEmpty();

            doFilterAfter(context, result);

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsKey(K key) {
        lock.readLock().lock();
        try {
            Objects.requireNonNull(key, "key");

            var context = doFilterBefore("containsKey", TYPES_CONTAINS_KEY, key);

            boolean result = cacheContext.map().containsKey(key);

            doFilterAfter(context, result);

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public V get(K key) {
        lock.readLock().lock();
        try {
            Objects.requireNonNull(key, "key");

            var context = doFilterBefore("get", TYPES_GET, key);

            V result = cacheContext.map().get(key);

            doFilterAfter(context, result);

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        Objects.requireNonNull(key, "key");
        V result;
        lock.writeLock().lock();
        try {
            var context = doFilterBefore("put", TYPES_PUT, key, value);
            result = cacheContext.map().put(key, value);
            doFilterAfter(context, result);
        } finally {
            lock.writeLock().unlock();
        }
        fireEvent(CacheEventType.PUT, key, value);
        return result;
    }

    @Override
    public V remove(K key) {
        Objects.requireNonNull(key, "key");
        V result;
        V oldValue;
        lock.writeLock().lock();
        try {
            var context = doFilterBefore("remove", TYPES_REMOVE, key);
            oldValue = cacheContext.map().get(key);
            result = cacheContext.map().remove(key);
            doFilterAfter(context, result);
        } finally {
            lock.writeLock().unlock();
        }
        fireEvent(CacheEventType.REMOVE, key, oldValue);
        return result;
    }

    @Override
    public Set<K> keySet() {
        lock.readLock().lock();
        try {
            var context = doFilterBefore("keySet", TYPES_KEY_SET);

            Set<K> result = new HashSet<>(cacheContext.map().keySet());

            doFilterAfter(context, result);

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

}
