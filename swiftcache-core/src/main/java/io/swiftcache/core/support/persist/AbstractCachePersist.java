package io.swiftcache.core.support.persist;

import io.swiftcache.api.expire.CacheExpire;
import io.swiftcache.api.persist.CachePersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 缓存持久化-适配器模式
 * @since 0.0.10
 */
public abstract class AbstractCachePersist<K,V> implements CachePersist<K,V> {

    private static final Logger log = LoggerFactory.getLogger(AbstractCachePersist.class);

    protected Map<K, V> map;
    protected CacheExpire<K, V> expire;

    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger(1);

    protected ScheduledExecutorService executorService;

    protected abstract void doPersist();

    protected void initExecutorService() {
        ThreadFactory tf = r -> {
            Thread t = new Thread(r, "cache-persist-" + THREAD_COUNTER.getAndIncrement());
            t.setDaemon(true);
            return t;
        };
        this.executorService = Executors.newSingleThreadScheduledExecutor(tf);
    }

    protected void persistScheduleStart() {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("[Cache] 开始持久化缓存信息");
                    doPersist();
                    log.info("[Cache] 完成持久化缓存信息");
                } catch (Exception exception) {
                    log.error("[Cache] 文件持久化异常", exception);
                }
            }
        }, delay(), period(), timeUnit());
    }

    protected void cleanup() {
    }

    @Override
    public CachePersist<K, V> init(Map<K, V> map, CacheExpire<K, V> expire) {
        this.map = map;
        this.expire = expire;

        initExecutorService();
        this.persistScheduleStart();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (executorService != null) executorService.shutdownNow();
            cleanup();
        }));

        return this;
    }

    protected long delay() {
        return 1;
    }

    protected long period() {
        return 1;
    }

    protected TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }

}
