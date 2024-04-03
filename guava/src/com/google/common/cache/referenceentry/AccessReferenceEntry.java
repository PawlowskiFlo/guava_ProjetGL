package com.google.common.cache.referenceentry;

import com.google.common.cache.ReferenceEntry;
import com.google.j2objc.annotations.Weak;

public class AccessReferenceEntry<K,V> extends AbstractReferenceEntry{

    @Override
    public long getAccessTime() {
        return Long.MAX_VALUE;
    }

    @Override
    public void setAccessTime(long time) {}

    @Weak
    ReferenceEntry<K, V> nextAccess = this;

    @Override
    public ReferenceEntry<K, V> getNextInAccessQueue() {
        return nextAccess;
    }

    @Override
    public void setNextInAccessQueue(ReferenceEntry next) {
        this.nextAccess = next;

    }

    @Weak ReferenceEntry<K, V> previousAccess = this;

    @Override
    public ReferenceEntry<K, V> getPreviousInAccessQueue() {
        return previousAccess;
    }

    @Override
    public void setPreviousInAccessQueue(ReferenceEntry previous) {
        this.previousAccess = previous;
    }
}
