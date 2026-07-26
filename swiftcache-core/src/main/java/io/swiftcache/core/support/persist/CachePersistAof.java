package io.swiftcache.core.support.persist;

import io.swiftcache.api.persist.CachePersist;
import io.swiftcache.core.model.CachePersistAofEntry;

public interface CachePersistAof<K, V> extends CachePersist<K,V> {

    boolean append(final CachePersistAofEntry aofEntry);

}
