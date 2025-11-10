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
import java.util.concurrent.TimeUnit;

/**
 * 缓存持久化-db-基于 JSON
 * @author binbin.hou
 * @since 0.0.8
 */
public class CachePersistDbJson<K,V> extends AbstractCachePersist<K,V> {

    private static final CacheSerializer SERIALIZER = new JacksonSerializer();

    /**
     * 数据库路径
     * @since 0.0.8
     */
    private final String dbPath;

    public CachePersistDbJson(String dbPath) {
        this.dbPath = dbPath;
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

        // 创建并清空文件
        try {
            Files.write(Path.of(dbPath), new byte[0], StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(K key : entrySet) {
            Long expireTime = expire.expireTime(key);
            PersistRdbEntry<K,V> persistRdbEntry = new PersistRdbEntry<>();
            persistRdbEntry.setKey(key);
            persistRdbEntry.setValue(this.map.get(key));
            persistRdbEntry.setExpire(expireTime);

            String line = SERIALIZER.serialize(persistRdbEntry);
            try {
                Files.writeString(Path.of(dbPath), line, StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public long delay() {
        return 5;
    }

    @Override
    public long period() {
        return 5;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.MINUTES;
    }

}
