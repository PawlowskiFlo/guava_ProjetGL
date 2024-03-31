package com.google.common.cache.valuereference;


import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.ReferenceEntry;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.CheckForNull;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

import static com.google.common.cache.LocalCache.unset;
import static com.google.common.util.concurrent.Futures.transform;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static com.google.common.util.concurrent.Uninterruptibles.getUninterruptibly;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class LoadingValueReference<K, V> implements ValueReference<K, V> {
    volatile ValueReference<K, V> oldValue;

    // TODO(fry): rename get, then extend AbstractFuture instead of containing SettableFuture
    final SettableFuture<V> futureValue = SettableFuture.create();
    final Stopwatch stopwatch = Stopwatch.createUnstarted();

    final Thread loadingThread;

    public LoadingValueReference() {
        this(null);
    }

    public LoadingValueReference(@CheckForNull ValueReference<K, V> oldValue) {
        this.oldValue = (oldValue == null) ? unset() : oldValue;
        this.loadingThread = Thread.currentThread();
    }

    @Override
    public boolean isLoading() {
        return true;
    }

    @Override
    public boolean isActive() {
        return oldValue.isActive();
    }

    @Override
    public int getWeight() {
        return oldValue.getWeight();
    }


    /**
     * getter for future Value used in tests
     * @return the futureValue created
     */
    public SettableFuture<V> getFutureValue(){
        return this.futureValue;
    }

    @CanIgnoreReturnValue
    public boolean set(@CheckForNull V newValue) {
        return futureValue.set(newValue);
    }

    @CanIgnoreReturnValue
    public boolean setException(Throwable t) {
        return futureValue.setException(t);
    }

    private ListenableFuture<V> fullyFailedFuture(Throwable t) {
        return Futures.immediateFailedFuture(t);
    }

    @Override
    public void notifyNewValue(@CheckForNull V newValue) {
        if (newValue != null) {
            // The pending load was clobbered by a manual write.
            // Unblock all pending gets, and have them return the new value.
            set(newValue);
        } else {
            // The pending load was removed. Delay notifications until loading completes.
            oldValue = unset();
        }

        // TODO(fry): could also cancel loading if we had a handle on its future
    }

    public ListenableFuture<V> loadFuture(K key, CacheLoader<? super K, V> loader) {
        try {
            stopwatch.start();
            V previousValue = oldValue.get();
            if (previousValue == null) {
                V newValue = loader.load(key);
                return set(newValue) ? futureValue : Futures.immediateFuture(newValue);
            }
            ListenableFuture<V> newValue = loader.reload(key, previousValue);
            if (newValue == null) {
                return Futures.immediateFuture(null);
            }
            // To avoid a race, make sure the refreshed value is set into loadingValueReference
            // *before* returning newValue from the cache query.
            return transform(
                    newValue,
                    newResult -> {
                        LoadingValueReference.this.set(newResult);
                        return newResult;
                    },
                    directExecutor());
        } catch (Throwable t) {
            ListenableFuture<V> result = setException(t) ? futureValue : fullyFailedFuture(t);
            if (t instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return result;
        }
    }

    @CheckForNull
    public V compute(
            K key, BiFunction<? super K, ? super @Nullable V, ? extends @Nullable V> function) {
        stopwatch.start();
        V previousValue;
        try {
            previousValue = oldValue.waitForValue();
        } catch (ExecutionException e) {
            previousValue = null;
        }
        V newValue;
        try {
            newValue = function.apply(key, previousValue);
        } catch (Throwable th) {
            this.setException(th);
            throw th;
        }
        this.set(newValue);
        return newValue;
    }

    public long elapsedNanos() {
        return stopwatch.elapsed(NANOSECONDS);
    }

    @Override
    public V waitForValue() throws ExecutionException {
        return getUninterruptibly(futureValue);
    }

    @Override
    public V get() {
        return oldValue.get();
    }

    public ValueReference<K, V> getOldValue() {
        return oldValue;
    }

    @Override
    public ReferenceEntry<K, V> getEntry() {
        return null;
    }

    @Override
    public ValueReference<K, V> copyFor(
            ReferenceQueue<V> queue, @CheckForNull V value, ReferenceEntry<K, V> entry) {
        return this;
    }

    public Thread getLoadingThread() {
        return this.loadingThread;
    }
}
