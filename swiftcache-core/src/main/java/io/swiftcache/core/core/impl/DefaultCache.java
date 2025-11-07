package io.swiftcache.core.core.impl;

import io.swiftcache.api.Cache;
import io.swiftcache.api.context.CacheContext;
import io.swiftcache.api.interceptor.CacheInterceptor;
import io.swiftcache.api.listener.CacheEvent;
import io.swiftcache.api.listener.CacheEventType;
import io.swiftcache.core.constant.enums.CacheInterceptorType;
import io.swiftcache.core.support.interceptor.DefaultCacheInterceptorContext;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultCache<K,V> implements Cache<K,V> {

    protected CacheContext<K, V> cacheContext;

    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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
        lock.writeLock().lock();
        try {
            Objects.requireNonNull(key, "key");

            var context = doFilterBefore("expireAt",
                    Arrays.asList(CacheInterceptorType.COMMON.code(),
                            CacheInterceptorType.EVICT_UPDATE.code(),
                            CacheInterceptorType.AOF.code()
                    ),
                    key, unixTime);

            cacheContext.expire().expireAt(key, unixTime);

            doFilterAfter(context, null);

            fireEvent(CacheEventType.EXPIRE, key, null);

            return this;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            var context = doFilterBefore("size",
                    Arrays.asList(CacheInterceptorType.COMMON.code(),
                            CacheInterceptorType.REFRESH.code()));

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
            var context = doFilterBefore("isEmpty",
                    Arrays.asList(CacheInterceptorType.COMMON.code(),
                            CacheInterceptorType.REFRESH.code()));

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

            var context = doFilterBefore("containsKey",
                    Arrays.asList(CacheInterceptorType.COMMON.code(),
                            CacheInterceptorType.REFRESH.code(),
                            CacheInterceptorType.EVICT_UPDATE.code()
                    ),
                    key);

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

            var context = doFilterBefore("get",
                    Arrays.asList(CacheInterceptorType.COMMON.code(),
                            CacheInterceptorType.REFRESH.code(),
                            CacheInterceptorType.EVICT_UPDATE.code()
                    ),
                    key);

            V result = cacheContext.map().get(key);

            doFilterAfter(context, result);

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        lock.writeLock().lock();
        try {
            Objects.requireNonNull(key, "key");

            var context = doFilterBefore("put",
                    Arrays.asList(CacheInterceptorType.COMMON.code(),
                            CacheInterceptorType.REFRESH.code(),
                            CacheInterceptorType.EVICT.code(),
                            CacheInterceptorType.EVICT_UPDATE.code(),
                            CacheInterceptorType.AOF.code()
                    ),
                    key, value);

            V result = cacheContext.map().put(key, value);

            doFilterAfter(context, result);

            fireEvent(CacheEventType.PUT, key, value);

            return result;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V remove(K key) {
        lock.writeLock().lock();
        try {
            Objects.requireNonNull(key, "key");

            var context = doFilterBefore("remove",
                    Arrays.asList(CacheInterceptorType.COMMON.code(),
                            CacheInterceptorType.REFRESH.code(),
                            CacheInterceptorType.EVICT_REMOVE.code(),
                            CacheInterceptorType.AOF.code()
                    ),
                    key);

            V oldValue = cacheContext.map().get(key);
            V result = cacheContext.map().remove(key);

            doFilterAfter(context, result);

            fireEvent(CacheEventType.REMOVE, key, oldValue);

            return result;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        lock.readLock().lock();
        try {
            var context = doFilterBefore("keySet",
                    Arrays.asList(CacheInterceptorType.COMMON.code(),
                            CacheInterceptorType.REFRESH.code()
                    ));

            Set<K> result = cacheContext.map().keySet();

            doFilterAfter(context, result);

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

}
