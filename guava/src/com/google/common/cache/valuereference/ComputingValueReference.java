package com.google.common.cache.valuereference;

public class ComputingValueReference<K, V> extends LoadingValueReference<K, V> {
    public ComputingValueReference(ValueReference<K, V> oldValue) {
        super(oldValue);
    }

    @Override
    public boolean isLoading() {
        return false;
    }
}

