package io.swiftcache.core.support.interceptor.aof;

import io.swiftcache.api.interceptor.CacheInterceptorContext;
import io.swiftcache.api.persist.CachePersist;
import io.swiftcache.core.support.interceptor.CacheInterceptorType;
import io.swiftcache.core.model.PersistAofEntry;
import io.swiftcache.core.support.interceptor.AbstractCacheInterceptor;
import io.swiftcache.core.support.persist.AbstractCachePersistAof;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 顺序追加模式
 *
 * AOF 持久化到文件，暂时不考虑 buffer 等特性。
 * @since 0.0.10
 */
public class CacheInterceptorAof<K,V> extends AbstractCacheInterceptor<K, V> {

    private static final Logger log = LoggerFactory.getLogger(CacheInterceptorAof.class);

    @Override
    protected String getType() {
        return CacheInterceptorType.AOF.code();
    }

    @Override
    public void before(CacheInterceptorContext<K,V> context) {
    }

    @Override
    public void after(CacheInterceptorContext<K,V> context) {
        // 持久化类
        CachePersist<K,V> persist = context.cacheContext().persist();

        final String methodName = context.methodName();
        final Object[] params = context.params();

        // 这里要求必须是 aof 的子类，是否不太好？
        if(persist instanceof AbstractCachePersistAof) {
            AbstractCachePersistAof<K,V> cachePersistAof = (AbstractCachePersistAof<K,V>) persist;

            PersistAofEntry aofEntry = PersistAofEntry.newInstance();
            aofEntry.setMethodName(methodName);
            aofEntry.setParams(params);

            // 直接持久化
            log.debug("[Cache] AOF 开始追加文件内容：{}", aofEntry);
            cachePersistAof.append(aofEntry);
            log.debug("[Cache] AOF 完成追加文件内容：{}", aofEntry);
        }
    }

}
