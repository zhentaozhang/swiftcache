package io.swiftcache.core.support.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swiftcache.api.serializer.CacheSerializer;

public class JacksonSerializer implements CacheSerializer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public <T> String serialize(T object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
