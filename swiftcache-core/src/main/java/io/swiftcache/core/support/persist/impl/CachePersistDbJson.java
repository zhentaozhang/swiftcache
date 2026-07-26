package io.swiftcache.core.support.persist.impl;

import io.swiftcache.api.expire.CacheExpire;
import io.swiftcache.api.serializer.CacheSerializer;
import io.swiftcache.core.model.PersistRdbEntry;
import io.swiftcache.core.support.persist.AbstractCachePersist;
import io.swiftcache.core.support.serializer.JacksonSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存持久化-db-基于 JSON
 * @since 0.0.8
 */
public class CachePersistDbJson<K,V> extends AbstractCachePersist<K,V> {

    private static final CacheSerializer SERIALIZER = new JacksonSerializer();

    private final String dbPath;

    private final long delay;

    private final long period;

    private final TimeUnit timeUnit;

    public CachePersistDbJson(String dbPath) {
        this(dbPath, 5, 5, TimeUnit.MINUTES);
    }

    public CachePersistDbJson(String dbPath, long delay, long period, TimeUnit timeUnit) {
        this.dbPath = dbPath;
        this.delay = delay;
        this.period = period;
        this.timeUnit = timeUnit;
    }

    /**
     * 持久化
     * key长度 key+value
     * 第一个空格，获取 key 的长度，然后截取
     */
    @Override
    public void doPersist() {
        var entrySet = this.map.keySet();
        final CacheExpire<K,V> expire = this.expire;

        List<String> lines = new ArrayList<>(entrySet.size());
        for(K key : entrySet) {
            Long expireTime = expire.expireTime(key);
            PersistRdbEntry<K,V> persistRdbEntry = new PersistRdbEntry<>();
            persistRdbEntry.setKey(key);
            persistRdbEntry.setValue(this.map.get(key));
            persistRdbEntry.setExpire(expireTime);

            lines.add(SERIALIZER.serialize(persistRdbEntry));
        }

        try {
            Files.write(Path.of(dbPath), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long delay() {
        return delay;
    }

    @Override
    public long period() {
        return period;
    }

    @Override
    public TimeUnit timeUnit() {
        return timeUnit;
    }

}
