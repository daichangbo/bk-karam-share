package com.bk.karam.lock.cachebase;

import com.bk.karam.factory.cache.redis.IRedisClient;
import com.bk.karam.lock.IInterProcessMutex;
import com.bk.karam.lock.IInterProcessSemaphoreMutex;
import com.bk.karam.lock.ILockFactory;
import com.bk.karam.lock.IReadWriteLock;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @autor
 */
public class CacheBaseLockFactory implements ILockFactory {

    private LoadingCache<LockCacheKey, IInterProcessMutex> mutexLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .expireAfterAccess(3600, TimeUnit.SECONDS)
            .softValues()
            .build(new CacheLoader<LockCacheKey, IInterProcessMutex>() {
                @Override
                public IInterProcessMutex load( LockCacheKey cacheKey) throws Exception {
                    return getMutexInternal(cacheKey.getKey(), cacheKey.getTime(), cacheKey.getUnit());
                }
            });

    private IRedisClient cacheClient;

    public void setCacheClient(IRedisClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    @Override
    public IInterProcessMutex newMutex(String key) {
        return newMutex(key, 3000, TimeUnit.MILLISECONDS);
    }

    @Override
    public IInterProcessMutex newMutex(String key, long timeout, TimeUnit unit) {
        LockCacheKey cacheKey = new LockCacheKey(timeout, unit, key);
        try {
            return mutexLoadingCache.get(cacheKey);
        } catch (ExecutionException e) {
            return getMutexInternal(key, timeout, unit);
        }
    }

    private IInterProcessMutex getMutexInternal(String key, long timeout, TimeUnit unit) {
        return new CacheBaseInterProcessMutex(cacheClient, key, timeout, unit);
    }

    @Override
    public IInterProcessSemaphoreMutex newSemaphore( String key) {
        return newSemaphore(key, 3000, TimeUnit.MILLISECONDS);
    }

    @Override
    public IInterProcessSemaphoreMutex newSemaphore(String key, long timeout, TimeUnit unit) {
        return getSemaphoreInternal(key, timeout, unit);
    }

    private IInterProcessSemaphoreMutex getSemaphoreInternal(String key, long timeout, TimeUnit unit) {
        return new CacheBaseSemaphore(cacheClient, key, timeout, unit);
    }

    @Override
    public IReadWriteLock newReadWriteLock( String key) {
        return newReadWriteLock(key, -1, null);
    }

    @Override
    public IReadWriteLock newReadWriteLock(String key, long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException("newReadWriteLock");
    }

    private class LockCacheKey implements Serializable {

        private final long time;

        private final TimeUnit unit;

        private final String key;

        LockCacheKey(long time, TimeUnit unit, String key) {
            this.time = time;
            this.unit = unit;
            this.key = key;
        }

        public long getTime() {
            return time;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("time", time)
                    .append("unit", unit)
                    .append("key", key)
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (!(o instanceof LockCacheKey)) return false;

            LockCacheKey that = (LockCacheKey) o;

            return new EqualsBuilder()
                    .append(getKey(), that.getKey())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return getKey().hashCode();
        }
    }
}
