package io.swiftcache.core.load;

import io.swiftcache.core.support.load.AbstractCacheLoad;

/**
 * @since 0.0.7
 */
public class MyCacheLoad extends AbstractCacheLoad<String,String> {

    @Override
    public void doLoad() {
        super.map.put("1", "1");
        super.map.put("2", "2");
    }


}
