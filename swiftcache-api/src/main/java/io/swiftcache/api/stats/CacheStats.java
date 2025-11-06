package io.swiftcache.api.stats;

import java.util.concurrent.atomic.AtomicLong;

public class CacheStats {

    private final AtomicLong hitCount = new AtomicLong();
    private final AtomicLong missCount = new AtomicLong();
    private final AtomicLong evictCount = new AtomicLong();
    private final AtomicLong putCount = new AtomicLong();

    public void recordHit() {
        hitCount.incrementAndGet();
    }

    public void recordMiss() {
        missCount.incrementAndGet();
    }

    public void recordEvict() {
        evictCount.incrementAndGet();
    }

    public void recordPut() {
        putCount.incrementAndGet();
    }

    public long hitCount() { return hitCount.get(); }
    public long missCount() { return missCount.get(); }
    public long evictCount() { return evictCount.get(); }
    public long putCount() { return putCount.get(); }

    public double hitRate() {
        long total = hitCount() + missCount();
        return total == 0 ? 0.0 : (double) hitCount() / total;
    }
}
