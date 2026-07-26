package io.swiftcache.core.model;

import io.swiftcache.core.model.CachePersistAofEntry;

import java.util.Arrays;

public class PersistAofEntry implements CachePersistAofEntry {

    private Object[] params;

    private String methodName;

    public static PersistAofEntry newInstance() {
        return new PersistAofEntry();
    }

    @Override
    public Object[] params() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    @Override
    public String methodName() {
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
