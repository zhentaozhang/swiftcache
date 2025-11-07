package io.swiftcache.core.core;

import io.swiftcache.api.Cache;
import io.swiftcache.core.core.impl.DefaultCache;

public class Caches {

    public static <K,V> Cache<K,V> defaultStrategy() {
        return new DefaultCache<>();
    }

}
