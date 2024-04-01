package com.google.common.cache.referenceentry.weaktype;


import com.google.common.cache.ReferenceEntry;
import com.google.common.cache.referenceentry.WeakEntry;
import com.google.j2objc.annotations.Weak;

import javax.annotation.CheckForNull;
import java.lang.ref.ReferenceQueue;

import static com.google.common.cache.LocalCache.nullEntry;

public class WeakWriteEntry<K, V> extends WeakEntry<K, V> {
    public WeakWriteEntry(
            ReferenceQueue<K> queue, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
        super(queue, key, hash, next);
    }

    // The code below is exactly the same for each write entry type.

    volatile long writeTime = Long.MAX_VALUE;

    @Override
    public long getWriteTime() {
        return writeTime;
    }

    @Override
    public void setWriteTime(long time) {
        this.writeTime = time;
    }

    // Guarded By Segment.this
    @Weak
    ReferenceEntry<K, V> nextWrite = nullEntry();

    @Override
    public ReferenceEntry<K, V> getNextInWriteQueue() {
        return nextWrite;
    }

    @Override
    public void setNextInWriteQueue(ReferenceEntry<K, V> next) {
        this.nextWrite = next;
    }

    // Guarded By Segment.this
    @Weak ReferenceEntry<K, V> previousWrite = nullEntry();

    @Override
    public ReferenceEntry<K, V> getPreviousInWriteQueue() {
        return previousWrite;
    }

    @Override
    public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous) {
        this.previousWrite = previous;
    }
}
