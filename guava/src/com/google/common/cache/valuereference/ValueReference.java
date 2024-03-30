package com.google.common.cache.valuereference;

import com.google.common.cache.ReferenceEntry;
import com.google.common.util.concurrent.ExecutionError;

import javax.annotation.CheckForNull;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ExecutionException;
/** A reference to a value. */
public interface ValueReference <K, V>{
    /** Returns the value. Does not block or throw exceptions. */
    @CheckForNull
    V get();

    /**
     * Waits for a value that may still be loading. Unlike get(), this method can block (in the case
     * of FutureValueReference).
     *
     * @throws ExecutionException if the loading thread throws an exception
     * @throws ExecutionError if the loading thread throws an error
     */
    V waitForValue() throws ExecutionException;

    /** Returns the weight of this entry. This is assumed to be static between calls to setValue. */
    int getWeight();

    /**
     * Returns the entry associated with this value reference, or {@code null} if this value
     * reference is independent of any entry.
     */
    @CheckForNull
    ReferenceEntry<K, V> getEntry();

    /**
     * Creates a copy of this reference for the given entry.
     *
     * <p>{@code value} may be null only for a loading reference.
     */
    ValueReference<K, V> copyFor(
            ReferenceQueue<V> queue, @CheckForNull V value, ReferenceEntry<K, V> entry);

    /**
     * Notify pending loads that a new value was set. This is only relevant to loading value
     * references.
     */
    void notifyNewValue(@CheckForNull V newValue);

    /**
     * Returns true if a new value is currently loading, regardless of whether there is an existing
     * value. It is assumed that the return value of this method is constant for any given
     * ValueReference instance.
     */
    boolean isLoading();

    /**
     * Returns true if this reference contains an active value, meaning one that is still considered
     * present in the cache. Active values consist of live values, which are returned by cache
     * lookups, and dead values, which have been evicted but awaiting removal. Non-active values
     * consist strictly of loading values, though during refresh a value may be both active and
     * loading.
     */
    boolean isActive();
}
