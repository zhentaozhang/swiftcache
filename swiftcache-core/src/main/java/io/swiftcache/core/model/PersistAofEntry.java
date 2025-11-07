package io.swiftcache.core.model;

import io.swiftcache.api.persist.CachePersistAofEntry;

import java.util.Arrays;

public class PersistAofEntry implements CachePersistAofEntry {

    private Object[] params;

    private String methodName;

    public static PersistAofEntry newInstance() {
        return new PersistAofEntry();
    }

    @Override
    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "PersistAofEntry{" +
                "params=" + Arrays.toString(params) +
                ", methodName='" + methodName + '\'' +
                '}';
    }

}
