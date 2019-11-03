package com.bk.karam.lock.cachebase;

import com.bk.karam.factory.cache.redis.IRedisClient;
import com.bk.karam.lock.IInterProcessSemaphoreMutex;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @autor
 */
public class CacheBaseSemaphore implements IInterProcessSemaphoreMutex {

    private static final String LOCK_NAME = "semaphore-";
    private final CacheBaseLockInternals internals;
    private final String lockKey;
    private final byte[] lockValue;

    private volatile boolean hasTheLock;

    public CacheBaseSemaphore( IRedisClient client,
                               String lockKey,
                               long time,
                               TimeUnit unit) {
        this(client, lockKey, new CacheBaseLockInternalDriver(), time, unit);
    }

    public CacheBaseSemaphore(IRedisClient client,
                              String lockKey,
                              LockInternalsDriver driver,
                              long time,
                              TimeUnit unit) {
        this(client, lockKey, LOCK_NAME, 10, driver, time, unit);
    }

    CacheBaseSemaphore(IRedisClient client,
                       String lockKey,
                       String lockName,
                       int maxRetryTimes,
                       LockInternalsDriver driver,
                       long time,
                       TimeUnit unit) {
        this.lockKey = lockKey;
        internals = new CacheBaseLockInternals(client, driver, lockKey, lockName, maxRetryTimes, time, unit);
        lockValue = (LOCK_NAME + Thread.currentThread().toString() + "_" + UUID.randomUUID().toString() + "_" + Thread.currentThread().getId()).getBytes();
    }

    @Override
    public void acquire() throws Exception {
        if (!internalLock(-1, null)) {
            throw new IOException("Lost connection while trying to acquire lock: " + lockKey);
        }
    }

    @Override
    public boolean acquire(long time, TimeUnit unit) throws Exception {
        return internalLock(time, unit);
    }

    @Override
    public void release() throws Exception {
        try {
            internals.releaseLock();
        } finally {
            hasTheLock = false;
        }
    }

    @Override
    public boolean isAcquiredInThisProcess() {
        return hasTheLock;
    }

    protected byte[] getLockNodeBytes() {
        return lockValue;
    }

    private boolean internalLock(long time, TimeUnit unit) throws Exception {
        String realLockKey = internals.attemptLock(time, unit, getLockNodeBytes());
        hasTheLock = null != realLockKey;
        return hasTheLock;
    }
}
