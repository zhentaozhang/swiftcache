package io.swiftcache.api.serializer;

public interface CacheSerializer {
    <T> String serialize(T object);
    <T> T deserialize(String json, Class<T> type);
}
