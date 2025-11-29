package io.swiftcache.core.bs;

import io.swiftcache.api.Cache;
import io.swiftcache.api.listener.CacheEvent;
import io.swiftcache.api.listener.CacheEventType;
import io.swiftcache.core.load.MyCacheLoad;
import io.swiftcache.core.support.evict.CacheEvicts;
import io.swiftcache.core.support.expire.CacheExpires;
import io.swiftcache.core.support.interceptor.CacheInterceptors;
import io.swiftcache.core.support.load.CacheLoads;
import io.swiftcache.core.support.persist.CachePersists;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存引导类测试
 * @since 0.0.2
 */
class CacheBsTest {

    /**
     * 大小指定测试
     * @since 0.0.2
     */
    @Test
    void helloTest() {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(2)
                .build();

        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.put("4", "4");

        assertThat(cache.size()).isEqualTo(2);
        System.out.println(cache.keySet());
    }

    /**
     * 大小指定测试
     * @since 0.0.2
     */
    @Test
    void fifoTest() {
        Cache<String, String> cache =
                CacheBs.<String,String>newInstance()
                        .evict(CacheEvicts.<String, String>fifo())
                .size(2)
                .build();

        cache.put("1", "1");
        cache.put("2", "2");
        cache.get("1");
        cache.put("3", "3");
        cache.get("1");
        cache.put("4", "4");

        assertThat(cache.size()).isEqualTo(2);
        assertThat(cache.keySet().toString()).isEqualTo("[1, 4]");
        System.out.println(cache.keySet());
    }

    /**
     * 配置指定测试
     * @since 0.0.2
     */
    @Test
    void configTest() {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .evict(CacheEvicts.<String, String>defaultStrategy())
                .expire(CacheExpires.<String, String>defaultStrategy())
                .interceptorList(CacheInterceptors.<String, String>defaultStrategy())
                .load(CacheLoads.<String, String>defaultStrategy())
                .persist(CachePersists.<String, String>defaultStrategy())
                .size(2)
                .build();

        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.put("4", "4");

        assertThat(cache.size()).isEqualTo(2);
        System.out.println(cache.keySet());
    }

    /**
     * 过期测试
     * @since 0.0.3
     */
    @Test
    void expireTest() throws InterruptedException {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .build();

        cache.put("1", "1");
        cache.put("2", "2");

        long now = System.currentTimeMillis();
        cache.expireAt("1", now+40);
        assertThat(cache.size()).isEqualTo(2);

        // 沉睡 50S，让其过期
        TimeUnit.MILLISECONDS.sleep(50);
        assertThat(cache.size()).isEqualTo(1);
        System.out.println(cache.keySet());
    }

    /**
     * 缓存删除监听器
     * @since 0.0.6
     */
    @Test
    void cacheRemoveListenerTest() {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(1)
                .build();

        cache.put("1", "1");
        cache.put("2", "2");
    }

    /**
     * 加载接口测试
     * @since 0.0.7
     */
    @Test
    void loadTest() {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .load(new MyCacheLoad())
                .build();

        assertThat(cache.size()).isEqualTo(2);
    }

    /**
     * 持久化接口测试
     * @since 0.0.7
     */
    @Test
    void persistRdbTest() throws InterruptedException {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .load(new MyCacheLoad())
                .persist(CachePersists.<String, String>dbJson("1.rdb"))
                .build();

        assertThat(cache.size()).isEqualTo(2);
        TimeUnit.SECONDS.sleep(2);
    }

    /**
     * 加载接口测试
     * @since 0.0.8
     */
    @Test
    void loadDbJsonTest() {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .load(CacheLoads.<String, String>dbJson("1.rdb"))
                .build();

        assertThat(cache.size()).isEqualTo(2);
    }

    /**
     * 慢日志接口测试
     * @since 0.0.9
     */
    @Test
    void slowLogTest() {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .build();

        cache.put("1", "2");
        cache.get("1");
    }


    /**
     * 持久化 AOF 接口测试
     * @since 0.0.10
     */
    @Test
    void persistAofTest() throws InterruptedException {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .persist(CachePersists.<String, String>aof("1.aof"))
                .build();

        cache.put("1", "1");
        cache.expireAt("1", 10);
        cache.remove("2");

        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * 加载 AOF 接口测试
     * @since 0.0.10
     */
    @Test
    void loadAofTest() throws InterruptedException {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .load(CacheLoads.<String, String>aof("default.aof"))
                .build();

        assertThat(cache.size()).isEqualTo(1);
        System.out.println(cache.keySet());
    }


    /**
     * LRU 驱除策略测试
     * @since 0.0.10
     */
    @Test
    void lruEvictTest() throws InterruptedException {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>lru())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        // 访问一次A
        cache.get("A");
        cache.put("D", "LRU");

        assertThat(cache.size()).isEqualTo(3);
        System.out.println(cache.keySet());
    }

    @Test
    void lruDoubleListMapTest() throws InterruptedException {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>lruDoubleListMap())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        // 访问一次A
        cache.get("A");
        cache.put("D", "LRU");

        assertThat(cache.size()).isEqualTo(3);
        System.out.println(cache.keySet());
    }


    /**
     * 基于 LRU 2Q 实现
     * @since 0.0.13
     */
    @Test
    void lruQ2Test()  {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>lru2Q())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        // 访问一次A
        cache.get("A");
        cache.put("D", "LRU");

        assertThat(cache.size()).isEqualTo(3);
        System.out.println(cache.keySet());
    }

    /**
     * 基于 LRU-2 实现
     * @since 0.0.13
     */
    @Test
    void lru2Test()  {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>lru2())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        // 访问一次A
        cache.get("A");
        cache.put("D", "LRU");

        assertThat(cache.size()).isEqualTo(3);
        System.out.println(cache.keySet());
    }

    /**
     * 基于 LFU 实现
     * @since 0.0.14
     */
    @Test
    void lfuTest()  {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>lfu())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        // 访问一次A
        cache.get("A");
        cache.put("D", "LRU");

        assertThat(cache.size()).isEqualTo(3);
        System.out.println(cache.keySet());
    }


    /**
     * 基于 clock 算法 实现
     * @since 0.0.15
     */
    @Test
    void clockTest()  {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>clock())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        cache.get("A");
        cache.put("D", "LRU");

        assertThat(cache.size()).isEqualTo(3);
        System.out.println(cache.keySet());
    }

    @Test
    void statsTest() {
        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .build();

        var stats = cache.cacheContext().stats();
        assertThat(stats.hitRate()).isEqualTo(0.0);

        cache.put("A", "1");
        assertThat(stats.putCount()).isEqualTo(1);

        cache.get("A");
        assertThat(stats.hitCount()).isEqualTo(1);

        cache.get("NONEXIST");
        assertThat(stats.missCount()).isEqualTo(1);
        assertThat(stats.hitRate()).isGreaterThan(0.0);
    }

    @Test
    void listenerTest() {
        List<CacheEvent<String, String>> events = new ArrayList<>();

        Cache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .listener(events::add)
                .build();

        cache.put("A", "1");
        cache.put("B", "2");
        cache.get("A");
        cache.remove("A");

        assertThat(events).extracting(CacheEvent::type)
                .containsExactly(CacheEventType.PUT, CacheEventType.PUT, CacheEventType.REMOVE);
    }

}
