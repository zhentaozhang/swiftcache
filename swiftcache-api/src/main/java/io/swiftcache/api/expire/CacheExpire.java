package io.swiftcache.api.expire;

import java.util.Collection;
import java.util.Map;

public interface CacheExpire<K,V> {

    CacheExpire<K, V> init(final Map<K, V> map);

    void expireAt(final K key, final long expireAt);

    void refreshExpire(final Collection<K> keyList);

    Long expireTime(final K key);

}
