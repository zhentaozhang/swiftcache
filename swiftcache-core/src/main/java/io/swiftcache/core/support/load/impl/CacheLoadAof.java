package io.swiftcache.core.support.load.impl;

import io.swiftcache.api.expire.CacheExpire;
import io.swiftcache.api.serializer.CacheSerializer;
import io.swiftcache.core.model.PersistAofEntry;
import io.swiftcache.core.support.load.AbstractCacheLoad;
import io.swiftcache.core.support.serializer.JacksonSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加载策略-AOF文件模式
 * @since 0.0.10
 */
public class CacheLoadAof<K,V> extends AbstractCacheLoad<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheLoadAof.class);
    private static final CacheSerializer SERIALIZER = new JacksonSerializer();

    /**
     * 文件路径
     * @since 0.0.8
     */
    private final String dbPath;

    public CacheLoadAof(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void doLoad() {
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(dbPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("[load] 开始处理 path: {}", dbPath);
        if(lines == null || lines.isEmpty()) {
            log.info("[load] path: {} 文件内容为空，直接返回", dbPath);
            return;
        }

        final var map = this.map;
        final CacheExpire<K,V> expire = this.expire;

        for(String line : lines) {
            if(line == null || line.isEmpty()) {
                continue;
            }

            PersistAofEntry entry = SERIALIZER.deserialize(line, PersistAofEntry.class);

            final String methodName = entry.getMethodName();
            Object[] params = entry.getParams();

            switch (methodName) {
                case "put" -> map.put((K) params[0], (V) params[1]);
                case "remove" -> map.remove((K) params[0]);
                case "expireAt" -> expire.expireAt((K) params[0], (Long) params[1]);
                default -> log.warn("Unknown method: {}", methodName);
            }
        }
    }


}
