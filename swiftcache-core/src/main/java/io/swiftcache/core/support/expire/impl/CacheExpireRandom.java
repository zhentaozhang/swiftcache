package io.swiftcache.core.support.expire.impl;

import io.swiftcache.core.exception.CacheRuntimeException;
import io.swiftcache.core.support.expire.AbstractCacheExpire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 缓存过期-普通策略随机
 *
 * @since 0.0.16
 * @param <K> key
 * @param <V> value
 */
public class CacheExpireRandom<K,V> extends AbstractCacheExpire<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheExpireRandom.class);

    /**
     * 是否启用快模式
     * @since 0.0.16
     */
    private final AtomicBoolean fastMode = new AtomicBoolean(false);

    public CacheExpireRandom(boolean fastMode) {
        this.fastMode.set(fastMode);
    }

    public CacheExpireRandom() {
    }

    @Override
    protected void expireScheduleStart() {
        executorService.scheduleAtFixedRate(new ExpireThreadRandom(), 10, 10, TimeUnit.SECONDS);
    }

    /**
     * 定时执行任务
     * @since 0.0.16
     */
    private class ExpireThreadRandom implements Runnable {
        @Override
        public void run() {
            //1.判断是否为空
            if(expireMap == null || expireMap.isEmpty()) {
                log.info("expireMap 信息为空，直接跳过本次处理。");
                return;
            }

            //2. 是否启用快模式
            if(fastMode.get()) {
                expireKeys(10L);
            } else {
                expireKeys(100L);
            }
        }
    }


    /**
     * 过期信息
     * @param timeoutMills 超时时间
     * @since 0.0.16
     */
    private void expireKeys(final long timeoutMills) {
        final long timeLimit = System.currentTimeMillis() + timeoutMills;

        final int countLimit = getLimitSize();

        //2. 获取 key 进行处理
        int count = 0;
        while (true) {
            //2.1 返回判断
            if(count >= countLimit) {
                log.info("过期淘汰次数已经达到最大次数: {}，完成本次执行。", countLimit);
                return;
            }
            if(System.currentTimeMillis() >= timeLimit) {
                this.fastMode.set(true);
                log.info("过期淘汰已经达到限制时间，中断本次执行，设置 fastMode=true;");
                return;
            }

            //2.2 随机过期
            K key = getRandomKey2();
            Long expireAt = expireMap.get(key);
            boolean expireFlag = removeExpireKey(key, expireAt);
            log.debug("key: {} 过期执行结果 {}", key, expireFlag);

            //2.3 信息更新
            count++;
        }
    }


    private K getRandomKey2() {
        Random random = ThreadLocalRandom.current();
        int randomIndex = random.nextInt(expireMap.size());

        // 遍历 keys
        Iterator<K> iterator = expireMap.keySet().iterator();
        int count = 0;
        while (iterator.hasNext()) {
            K key = iterator.next();

            if(count == randomIndex) {
                return key;
            }
            count++;
        }

        // 正常逻辑不会到这里
        throw new CacheRuntimeException("对应信息不存在");
    }

}
