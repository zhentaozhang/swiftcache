package io.swiftcache.core.demo;

import io.swiftcache.api.Cache;
import io.swiftcache.core.bs.CacheBs;
import io.swiftcache.core.support.evict.CacheEvicts;

public class CacheDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== SwiftCache 基础演示 ===\n");

        // 1. FIFO 淘汰
        Cache<String, String> cache = CacheBs.<String, String>newInstance()
                .size(3)
                .evict(CacheEvicts.fifo())
                .build();

        cache.put("A", "alpha");
        cache.put("B", "beta");
        cache.put("C", "charlie");
        System.out.println("put A, B, C  size=" + cache.size() + " keys=" + cache.keySet());

        cache.put("D", "delta");
        System.out.println("put D（淘汰 A）size=" + cache.size() + " keys=" + cache.keySet());
        System.out.println("get(A)=" + cache.get("A") + " (预期 null)");
        System.out.println("get(B)=" + cache.get("B"));

        // 2. LRU
        Cache<String, String> lru = CacheBs.<String, String>newInstance()
                .size(3)
                .evict(CacheEvicts.lru())
                .build();
        lru.put("A", "1"); lru.put("B", "2"); lru.put("C", "3");
        lru.get("A");
        lru.put("D", "4");
        System.out.println("\nLRU（访问 A 后淘汰 B）keys=" + lru.keySet());

        // 3. LFU
        Cache<String, String> lfu = CacheBs.<String, String>newInstance()
                .size(3)
                .evict(CacheEvicts.lfu())
                .build();
        lfu.put("A", "1"); lfu.put("B", "2"); lfu.put("C", "3");
        lfu.get("A"); lfu.get("A"); lfu.get("B");
        lfu.put("D", "4");
        System.out.println("\nLFU（A:2 B:1 C:0，淘汰 C）keys=" + lfu.keySet());

        // 4. Clock
        Cache<String, String> clock = CacheBs.<String, String>newInstance()
                .size(3)
                .evict(CacheEvicts.clock())
                .build();
        clock.put("A", "1"); clock.put("B", "2"); clock.put("C", "3");
        clock.get("A"); clock.get("B");
        clock.put("D", "4");
        System.out.println("\nClock（A/B 置标志位，淘汰 C）keys=" + clock.keySet());

        // 5. 过期
        Cache<String, String> expired = CacheBs.<String, String>newInstance()
                .size(10)
                .build();
        expired.put("X", "1s 后过期");
        expired.expireAt("X", System.currentTimeMillis() + 1000);
        System.out.println("\n过期：put X，等待 1.5s...");
        Thread.sleep(1500);
        System.out.println("get(X)=" + expired.get("X") + " (预期 null)  size=" + expired.size());

        // 6. 事件监听
        System.out.println("\n事件监听：");
        Cache<String, String> listener = CacheBs.<String, String>newInstance()
                .listener(e -> System.out.println("  → " + e.type() + " key=" + e.key()))
                .build();
        listener.put("K", "v");
        listener.remove("K");

        // 7. 统计数据
        Cache<String, String> stats = CacheBs.<String, String>newInstance().build();
        stats.put("X", "1");
        stats.get("X");
        stats.get("MISS");
        System.out.println("\n统计：hit=" + stats.cacheContext().stats().hitCount()
                + " miss=" + stats.cacheContext().stats().missCount()
                + " rate=" + stats.cacheContext().stats().hitRate());

        System.out.println("\n=== 演示结束 ===");
    }
}
