package com.google.common.cache.referenceentry;

import com.google.common.cache.ReferenceEntry;
import com.google.common.cache.valuereference.ValueReference;

import javax.annotation.CheckForNull;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import static com.google.common.cache.LocalCache.unset;

/** Used for weakly-referenced keys. */
public class WeakEntry<K, V> extends WeakReference<K> implements ReferenceEntry<K, V> {
    public WeakEntry(ReferenceQueue<K> queue, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
        super(key, queue);
        this.hash = hash;
        this.next = next;
    }

    @Override
    public K getKey() {
        return get();
    }

    /*
     * It'd be nice to get these for free from AbstractReferenceEntry, but we're already extending
     * WeakReference<K>.
     */

    // null access

    @Override
    public long getAccessTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAccessTime(long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReferenceEntry<K, V> getNextInAccessQueue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNextInAccessQueue(ReferenceEntry<K, V> next) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReferenceEntry<K, V> getPreviousInAccessQueue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous) {
        throw new UnsupportedOperationException();
    }

    // null write

    @Override
    public long getWriteTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWriteTime(long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReferenceEntry<K, V> getNextInWriteQueue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNextInWriteQueue(ReferenceEntry<K, V> next) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReferenceEntry<K, V> getPreviousInWriteQueue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous) {
        throw new UnsupportedOperationException();
    }

    // The code below is exactly the same for each entry type.

    final int hash;
    @CheckForNull final ReferenceEntry<K, V> next;
    volatile ValueReference<K, V> valueReference = unset();

    @Override
    public ValueReference<K, V> getValueReference() {
        return valueReference;
    }

    @Override
    public void setValueReference(ValueReference<K, V> valueReference) {
        this.valueReference = valueReference;
    }

    @Override
    public int getHash() {
        return hash;
    }

    @Override
    public ReferenceEntry<K, V> getNext() {
        return next;
    }
}

