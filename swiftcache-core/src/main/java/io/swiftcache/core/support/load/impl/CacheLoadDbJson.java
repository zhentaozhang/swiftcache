package io.swiftcache.core.support.load.impl;

import io.swiftcache.api.expire.CacheExpire;
import io.swiftcache.api.serializer.CacheSerializer;
import io.swiftcache.core.model.PersistRdbEntry;
import io.swiftcache.core.support.load.AbstractCacheLoad;
import io.swiftcache.core.support.serializer.JacksonSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加载策略-文件路径
 * @since 0.0.8
 */
public class CacheLoadDbJson<K,V> extends AbstractCacheLoad<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheLoadDbJson.class);
    private static final CacheSerializer SERIALIZER = new JacksonSerializer();

    /**
     * 文件路径
     * @since 0.0.8
     */
    private final String dbPath;

    public CacheLoadDbJson(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void doLoad() {
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(dbPath));
        } catch (NoSuchFileException e) {
            log.warn("[load] path: {} 不存在，跳过加载", dbPath);
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("[load] 开始处理 path: {}", dbPath);
        if(lines == null || lines.isEmpty()) {
            log.info("[load] path: {} 文件内容为空，直接返回", dbPath);
            return;
        }

        final var cacheMap = this.map;
        final CacheExpire<K,V> cacheExpire = this.expire;
        for(String line : lines) {
            if(line == null || line.isEmpty()) {
                continue;
            }

            PersistRdbEntry<K,V> entry = SERIALIZER.deserialize(line, PersistRdbEntry.class);

            K key = entry.getKey();
            V value = entry.getValue();
            Long expire = entry.getExpire();

            cacheMap.put(key, value);
            if(expire != null) {
                cacheExpire.expireAt(key, expire);
            }
        }
        //nothing...
    }
}
