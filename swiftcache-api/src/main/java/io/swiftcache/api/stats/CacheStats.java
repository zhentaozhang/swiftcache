package io.swiftcache.api.stats;

public interface CacheStats {

    void recordHit();

    void recordMiss();

    void recordEvict();

    void recordPut();

    long hitCount();

    long missCount();

    long evictCount();

    long putCount();

    double hitRate();

}
