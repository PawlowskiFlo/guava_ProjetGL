package com.google.common.cache.referenceentry;

import com.google.common.cache.ReferenceEntry;
import com.google.j2objc.annotations.Weak;

public class WriteReferenceEntry<K,V> extends AbstractReferenceEntry{
    @Override
    public long getWriteTime() {
        return Long.MAX_VALUE;
    }

    @Override
    public void setWriteTime(long time) {}

    @Weak
    ReferenceEntry<K, V> nextWrite = this;

    @Override
    public ReferenceEntry<K, V> getNextInWriteQueue() {
        return nextWrite;
    }

    @Override
    public void setNextInWriteQueue(ReferenceEntry next) {
        this.nextWrite = next;
    }

    @Weak ReferenceEntry<K, V> previousWrite = this;

    @Override
    public ReferenceEntry<K, V> getPreviousInWriteQueue() {
        return previousWrite;
    }

    @Override
    public void setPreviousInWriteQueue(ReferenceEntry previous) {
        this.previousWrite = previous;
    }

}
