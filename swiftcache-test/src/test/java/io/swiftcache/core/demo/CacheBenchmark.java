package io.swiftcache.core.demo;

import io.swiftcache.api.Cache;
import io.swiftcache.core.bs.CacheBs;
import io.swiftcache.core.support.evict.CacheEvicts;
import io.swiftcache.core.support.expire.CacheExpires;
import io.swiftcache.core.support.persist.CachePersists;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class CacheBenchmark {

    private static final int WARMUP_OPS = 50_000;
    private static final long NANOS_PER_MS = 1_000_000L;
    private static final long NANOS_PER_SEC = 1_000_000_000L;
    private static final Random RND = new Random(42);

    record Result(long nanos, long ops, double hitRate) {
        double opsPerSec() { return ops / (nanos / (double) NANOS_PER_SEC); }
    }

    public static void main(String[] args) {
        System.out.println("=== SwiftCache Benchmark Baseline ===\n");
        System.out.println("JVM:   " + Runtime.version());
        System.out.println("CPUs:  " + Runtime.getRuntime().availableProcessors());
        System.out.println("Date:  " + new Date());
        System.out.println();

        warmup();

        System.out.println("--- 写入吞吐 ---");
        run("put-10K",   () -> benchPut(10_000));
        run("put-100K",  () -> benchPut(100_000));
        run("put-500K",  () -> benchPut(500_000));

        System.out.println("\n--- 读取吞吐 ---");
        run("get-hit-100K",  () -> benchGetHit(100_000));
        run("get-miss-100K", () -> benchGetMiss(100_000));

        System.out.println("\n--- 淘汰策略吞吐 ---");
        for (var p : List.of("fifo", "lru", "lfu", "clock", "lru2q", "lru2", "lru-double"))
            run("evict-" + p, () -> benchEvict(p, 100_000, 50_000));

        System.out.println("\n--- 过期策略吞吐 ---");
        for (var p : List.of("random", "sort", "seq"))
            run("expire-" + p, () -> benchExpire(p, 50_000));

        System.out.println("\n--- 持久化开销 ---");
        run("aof-100K", () -> benchPersist("aof", 100_000));
        run("rdb-10K",  () -> benchPersist("rdb", 10_000));
        run("rdb-100K", () -> benchPersist("rdb", 100_000));

        System.out.println("\n--- 并发吞吐 ---");
        run("concurrent-read-4t",  () -> benchConcurrent(4, 100_000, false));
        run("concurrent-write-4t", () -> benchConcurrent(4, 50_000, true));

        System.out.println("\n--- 命中率 (Zipf, size=10K, ops=100K) ---");
        for (var p : List.of("fifo", "lru", "lfu", "clock", "lru2q", "lru2"))
            run("hitrate-" + p, () -> benchHitRate(p, 10_000, 100_000));

        System.out.println("\n====================================");
        System.out.println("All scenarios completed");
    }

    // ── Warmup ──

    static void warmup() {
        Cache<String, String> c = CacheBs.<String, String>newInstance().size(1000).build();
        for (int i = 0; i < WARMUP_OPS; i++) {
            String k = "k" + (i & 0xFFFF);
            c.put(k, "v");
            c.get(k);
        }
    }

    // ── Runner ──

    static void run(String name, Supplier<Result> scenario) {
        long bestNanos = Long.MAX_VALUE;
        Result best = null;
        for (int i = 0; i < 3; i++) {
            Result r = scenario.get();
            if (r.nanos() < bestNanos) {
                bestNanos = r.nanos();
                best = r;
            }
        }
        String line = String.format("  [%-18s]  %8.0f ms  %9.0f ops/s",
                name, best.nanos() / (double) NANOS_PER_MS, best.opsPerSec());
        if (best.hitRate() >= 0)
            System.out.println(line + String.format("  hit_rate=%.1f%%", best.hitRate() * 100));
        else
            System.out.println(line);
    }

    // ── Create helpers ──

    static Cache<String, String> createEvict(String policy, int size) {
        return switch (policy) {
            case "fifo"       -> CacheBs.<String, String>newInstance().size(size).evict(CacheEvicts.fifo()).build();
            case "lru"        -> CacheBs.<String, String>newInstance().size(size).evict(CacheEvicts.lru()).build();
            case "lfu"        -> CacheBs.<String, String>newInstance().size(size).evict(CacheEvicts.lfu()).build();
            case "clock"      -> CacheBs.<String, String>newInstance().size(size).evict(CacheEvicts.clock()).build();
            case "lru2q"      -> CacheBs.<String, String>newInstance().size(size).evict(CacheEvicts.lru2Q()).build();
            case "lru2"       -> CacheBs.<String, String>newInstance().size(size).evict(CacheEvicts.lru2()).build();
            case "lru-double" -> CacheBs.<String, String>newInstance().size(size).evict(CacheEvicts.lruDoubleListMap()).build();
            default -> throw new IllegalArgumentException(policy);
        };
    }

    static Cache<String, String> createExpire(String policy, int size) {
        return switch (policy) {
            case "random" -> CacheBs.<String, String>newInstance().size(size).expire(CacheExpires.random()).build();
            case "sort"   -> CacheBs.<String, String>newInstance().size(size).expire(CacheExpires.sort()).build();
            case "seq"    -> CacheBs.<String, String>newInstance().size(size).expire(CacheExpires.sequence()).build();
            default -> throw new IllegalArgumentException(policy);
        };
    }

    // ── Scenario: Put ──

    static Result benchPut(int count) {
        Cache<String, String> c = CacheBs.<String, String>newInstance().size(count + 1).build();
        long start = System.nanoTime();
        for (int i = 0; i < count; i++)
            c.put("k" + i, "v" + i);
        long nanos = System.nanoTime() - start;
        return new Result(nanos, count, -1);
    }

    // ── Scenario: Get hit ──

    static Result benchGetHit(int count) {
        Cache<String, String> c = CacheBs.<String, String>newInstance().size(count + 1).build();
        for (int i = 0; i < count; i++) c.put("k" + i, "v" + i);
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) c.get("k" + i);
        long nanos = System.nanoTime() - start;
        return new Result(nanos, count, -1);
    }

    // ── Scenario: Get miss ──

    static Result benchGetMiss(int count) {
        Cache<String, String> c = CacheBs.<String, String>newInstance().size(count + 1).build();
        for (int i = 0; i < count; i++) c.put("k" + i, "v" + i);
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) c.get("miss-" + i);
        long nanos = System.nanoTime() - start;
        return new Result(nanos, count, 0);
    }

    // ── Scenario: Evict throughput ──

    static Result benchEvict(String policy, int count, int capacity) {
        Cache<String, String> c = createEvict(policy, capacity);
        long start = System.nanoTime();
        for (int i = 0; i < count; i++)
            c.put("k" + i, "v" + i);
        long nanos = System.nanoTime() - start;
        return new Result(nanos, count, -1);
    }

    // ── Scenario: Expire throughput ──

    static Result benchExpire(String policy, int count) {
        Cache<String, String> c = createExpire(policy, count);
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            c.put("k" + i, "v" + i);
            c.expireAt("k" + i, System.currentTimeMillis() + 86_400_000);
        }
        long nanos = System.nanoTime() - start;
        return new Result(nanos, count, -1);
    }

    // ── Scenario: Persist overhead ──

    static Result benchPersist(String type, int count) {
        Cache<String, String> c;
        if ("aof".equals(type))
            c = CacheBs.<String, String>newInstance()
                    .size(count + 1)
                    .persist(CachePersists.aof("bench.aof"))
                    .build();
        else
            c = CacheBs.<String, String>newInstance()
                    .size(count + 1)
                    .persist(CachePersists.dbJson("bench.rdb"))
                    .build();

        long start = System.nanoTime();
        for (int i = 0; i < count; i++)
            c.put("k" + i, "v" + i);
        long nanos = System.nanoTime() - start;
        // Let the async persist thread flush before exit
        sleep(200);
        return new Result(nanos, count, -1);
    }

    // ── Scenario: Concurrent ──

    static Result benchConcurrent(int threads, int opsPerThread, boolean write) {
        Cache<String, String> c = CacheBs.<String, String>newInstance()
                .size(100_000)
                .evict(CacheEvicts.lru())
                .build();

        if (!write)
            for (int i = 0; i < 50_000; i++) c.put("k" + i, "v" + i);

        var latch = new CountDownLatch(threads);
        var totalOps = new java.util.concurrent.atomic.AtomicLong(0);
        long start = System.nanoTime();
        for (int t = 0; t < threads; t++) {
            int tid = t;
            new Thread(() -> {
                for (int i = 0; i < opsPerThread; i++) {
                    String k = "k" + ((tid * opsPerThread + i) % 100_000);
                    if (write) c.put(k, "v");
                    else c.get(k);
                    totalOps.incrementAndGet();
                }
                latch.countDown();
            });
        }
        try { latch.await(30, TimeUnit.SECONDS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        long nanos = System.nanoTime() - start;
        return new Result(nanos, totalOps.get(), -1);
    }

    // ── Scenario: Hit rate ──
    // Working set = 5x cache size, Zipf-skewed access.
    // On miss: put the key. This triggers eviction.

    static Result benchHitRate(String policy, int cacheSize, int accesses) {
        assert cacheSize <= 100_000;
        Cache<String, String> c = createEvict(policy, cacheSize);
        int workingSet = cacheSize * 5;

        int[] zipf = new int[accesses];
        for (int i = 0; i < accesses; i++) {
            double x = RND.nextDouble();
            zipf[i] = (int) (workingSet * x * x);
            if (zipf[i] >= workingSet) zipf[i] = workingSet - 1;
        }

        int hits = 0;
        long start = System.nanoTime();
        for (int idx : zipf) {
            String k = "k" + idx;
            if (c.get(k) != null) hits++;
            else c.put(k, "v");
        }
        long nanos = System.nanoTime() - start;
        return new Result(nanos, accesses, hits / (double) accesses);
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
