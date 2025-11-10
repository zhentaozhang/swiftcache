package io.swiftcache.core.support.persist.impl;

import io.swiftcache.api.persist.CachePersistAofEntry;
import io.swiftcache.api.serializer.CacheSerializer;
import io.swiftcache.core.support.persist.AbstractCachePersistAof;
import io.swiftcache.core.support.serializer.JacksonSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存持久化-AOF 持久化模式
 * @author binbin.hou
 * @since 0.0.10
 */
public class CachePersistAof<K,V> extends AbstractCachePersistAof<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CachePersistAof.class);
    private static final CacheSerializer SERIALIZER = new JacksonSerializer();

    /**
     * 缓存列表
     * @since 0.0.10
     */
    private final ConcurrentLinkedQueue<String> bufferList = new ConcurrentLinkedQueue<>();

    /**
     * 数据持久化路径
     * @since 0.0.10
     */
    private final String dbPath;

    public CachePersistAof(String dbPath) {
        this.dbPath = dbPath;
    }

    /**
     * 添加文件内容到 buffer 列表中
     * @param aofEntry entry 信息
     * @since 0.0.10
     */
    @Override
    public boolean append(final CachePersistAofEntry aofEntry) {
        if(aofEntry != null) {
            String json = SERIALIZER.serialize(aofEntry);
            log.debug("[Cache] AOF append json={}", json);
            bufferList.add(json);

            return true;
        }

        return false;
    }

    @Override
    protected void doPersist() {
        log.info("[Cache] 开始 AOF 持久化到文件");
        // 1. 创建文件
        try {
            if(!Files.exists(Path.of(dbPath))) {
                Files.createFile(Path.of(dbPath));
            }
            // 2. 持久化追加到文件中
            List<String> snapshot = List.copyOf(bufferList);
            Files.write(Path.of(dbPath), snapshot, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        bufferList.removeIf(s -> true);
        log.info("[Cache] 完成 AOF 持久化到文件");
    }

}
