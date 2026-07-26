package io.swiftcache.core.support.persist.impl;

import io.swiftcache.api.expire.CacheExpire;
import io.swiftcache.api.persist.CachePersist;
import io.swiftcache.core.model.CachePersistAofEntry;
import io.swiftcache.api.serializer.CacheSerializer;
import io.swiftcache.core.support.persist.AbstractCachePersistAof;
import io.swiftcache.core.support.serializer.JacksonSerializer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachePersistAof<K,V> extends AbstractCachePersistAof<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CachePersistAof.class);
    private static final CacheSerializer SERIALIZER = new JacksonSerializer();

    private final ConcurrentLinkedQueue<CachePersistAofEntry> bufferList = new ConcurrentLinkedQueue<>();

    private final String dbPath;

    private BufferedWriter fileWriter;

    public CachePersistAof(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public boolean append(final CachePersistAofEntry aofEntry) {
        if(aofEntry != null) {
            bufferList.add(aofEntry);
            return true;
        }
        return false;
    }

    @Override
    protected synchronized void doPersist() {
        if (fileWriter == null) {
            return;
        }

        List<CachePersistAofEntry> snapshot = new ArrayList<>();
        while (true) {
            CachePersistAofEntry entry = bufferList.poll();
            if (entry == null) break;
            snapshot.add(entry);
        }
        if (snapshot.isEmpty()) {
            return;
        }

        try {
            for (CachePersistAofEntry entry : snapshot) {
                fileWriter.write(SERIALIZER.serialize(entry));
                fileWriter.newLine();
            }
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CachePersist<K, V> init(Map<K, V> map, CacheExpire<K, V> expire) {
        try {
            Path path = Path.of(dbPath);
            Path parent = path.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
            fileWriter = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.init(map, expire);
        return this;
    }

    @Override
    protected void cleanup() {
        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                log.error("关闭 AOF 文件失败", e);
            }
            fileWriter = null;
        }
    }

}
