package io.swiftcache.core.support.expire;

import io.swiftcache.api.expire.CacheExpire;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 缓存过期-统一父类策略
 *
 * @since 1.0.0
 */
public abstract class AbstractCacheExpire<K,V> implements CacheExpire<K,V> {

    protected Map<K, V> cacheMap;

    protected final Map<K, Long> expireMap = new HashMap<>();

    protected ScheduledExecutorService executorService;

    protected void initExecutorService() {
        ThreadFactory tf = r -> {
            Thread t = new Thread(r, "cache-expire-" + System.nanoTime());
            t.setDaemon(true);
            return t;
        };
        this.executorService = Executors.newSingleThreadScheduledExecutor(tf);
    }

    protected abstract void expireScheduleStart();

    protected int getLimitSize() {
        return 1000;
    }

    @Override
    public CacheExpire<K, V> init(Map<K, V> map) {
        this.cacheMap = map;
        this.initExecutorService();
        this.expireScheduleStart();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (executorService != null) executorService.shutdownNow();
        }));
        return this;
    }

    @Override
    public void expireAt(K key, long expireAt) {
        expireMap.put(key, expireAt);
    }

    @Override
    public void refreshExpire(Collection<K> keyList) {
        if(keyList == null || keyList.isEmpty()) {
            return;
        }

        // 判断大小，小的作为外循环。一般都是过期的 keys 比较小。
        if(keyList.size() <= expireMap.size()) {
            for(K key : keyList) {
                Long expireAt = expireMap.get(key);
                removeExpireKey(key, expireAt);
            }
        } else {
            for(Map.Entry<K, Long> entry : expireMap.entrySet()) {
                this.removeExpireKey(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }


    /**
     * 过期处理 key
     * @param key key
     * @param expireAt 过期时间
     * @since 0.0.16
     * @return 是否执行过期
     */
    protected boolean removeExpireKey(final K key, final Long expireAt) {
        if(expireAt == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if(currentTime >= expireAt) {
            expireMap.remove(key);
            V removeValue = cacheMap.remove(key);

            return true;
        }

        return false;
    }

}
