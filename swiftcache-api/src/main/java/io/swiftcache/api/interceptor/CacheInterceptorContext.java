package io.swiftcache.api.interceptor;

import io.swiftcache.api.context.CacheContext;

import java.lang.reflect.Method;
import java.util.List;

public interface CacheInterceptorContext<K,V> {

    List<String> typeList();

    CacheContext<K, V> cacheContext();

    String methodName();

    Object[] params();

    long startMills();

    Object result();

    long endMills();

}
