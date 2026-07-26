package io.swiftcache.core.core;

import io.swiftcache.api.stats.CacheStats;

import java.util.concurrent.atomic.AtomicLong;

public class DefaultCacheStats implements CacheStats {

    private final AtomicLong hitCount = new AtomicLong();
    private final AtomicLong missCount = new AtomicLong();
    private final AtomicLong evictCount = new AtomicLong();
    private final AtomicLong putCount = new AtomicLong();

    @Override
    public void recordHit() {
        hitCount.incrementAndGet();
    }

    @Override
    public void recordMiss() {
        missCount.incrementAndGet();
    }

    @Override
    public void recordEvict() {
        evictCount.incrementAndGet();
    }

    @Override
    public void recordPut() {
        putCount.incrementAndGet();
    }

    @Override
    public long hitCount() { return hitCount.get(); }

    @Override
    public long missCount() { return missCount.get(); }

    @Override
    public long evictCount() { return evictCount.get(); }

    @Override
    public long putCount() { return putCount.get(); }

    @Override
    public double hitRate() {
        long total = hitCount() + missCount();
        return total == 0 ? 0.0 : (double) hitCount() / total;
    }

}
