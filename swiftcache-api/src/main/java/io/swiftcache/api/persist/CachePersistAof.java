package io.swiftcache.api.persist;

public interface CachePersistAof<K, V> extends CachePersist<K,V> {

    boolean append(final CachePersistAofEntry aofEntry);

}
