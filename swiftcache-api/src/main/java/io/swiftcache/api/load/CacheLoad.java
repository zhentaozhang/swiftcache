package io.swiftcache.api.load;

import io.swiftcache.api.expire.CacheExpire;

import java.util.Map;

public interface CacheLoad<K, V> {

    void init(final Map<K, V> map, final CacheExpire<K, V> expire);

    void load();

}
