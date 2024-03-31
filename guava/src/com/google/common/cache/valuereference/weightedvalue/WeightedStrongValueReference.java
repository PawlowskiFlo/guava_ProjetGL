package com.google.common.cache.valuereference.weightedvalue;


import com.google.common.cache.valuereference.StrongValueReference;

/** References a strong value. */
public final class WeightedStrongValueReference<K, V> extends StrongValueReference<K, V> {
    final int weight;

    public WeightedStrongValueReference(V referent, int weight) {
        super(referent);
        this.weight = weight;
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
