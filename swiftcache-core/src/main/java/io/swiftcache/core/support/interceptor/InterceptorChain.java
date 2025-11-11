package io.swiftcache.core.support.interceptor;

import io.swiftcache.api.interceptor.CacheInterceptor;
import io.swiftcache.core.support.interceptor.aof.CacheInterceptorAof;
import io.swiftcache.core.support.interceptor.common.CacheInterceptorCommonCost;
import io.swiftcache.core.support.interceptor.evict.CacheInterceptorEvict;
import io.swiftcache.core.support.interceptor.evict.EvictTrackingInterceptor;
import io.swiftcache.core.support.interceptor.refresh.CacheInterceptorRefresh;
import io.swiftcache.core.support.interceptor.stats.StatsInterceptor;

import java.util.ArrayList;
import java.util.List;

public class InterceptorChain<K, V> {

    private final List<CacheInterceptor<K, V>> list;

    private InterceptorChain(List<CacheInterceptor<K, V>> list) {
        this.list = list;
    }

    public List<CacheInterceptor<K, V>> interceptors() {
        return list;
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    @SuppressWarnings("all")
    public static <K, V> List<CacheInterceptor<K, V>> defaultStrategy() {
        return InterceptorChain.<K, V>builder()
                .commonCost()
                .evict()
                .evictTracking()
                .aof()
                .refresh()
                .stats()
                .build()
                .interceptors();
    }

    public static class Builder<K, V> {

        private final List<CacheInterceptor<K, V>> list = new ArrayList<>();

        public Builder<K, V> commonCost() {
            list.add(new CacheInterceptorCommonCost<>());
            return this;
        }

        public Builder<K, V> evict() {
            list.add(new CacheInterceptorEvict<>());
            return this;
        }

        public Builder<K, V> evictTracking() {
            list.add(new EvictTrackingInterceptor<>());
            return this;
        }

        public Builder<K, V> aof() {
            list.add(new CacheInterceptorAof<>());
            return this;
        }

        public Builder<K, V> refresh() {
            list.add(new CacheInterceptorRefresh<>());
            return this;
        }

        public Builder<K, V> stats() {
            list.add(new StatsInterceptor<>());
            return this;
        }

        public Builder<K, V> add(CacheInterceptor<K, V> interceptor) {
            list.add(interceptor);
            return this;
        }

        public InterceptorChain<K, V> build() {
            return new InterceptorChain<>(list);
        }

    }

}
