package com.bk.karam.lock.cachebase;

import com.bk.karam.factory.cache.redis.IRedisClient;
import com.bk.karam.lock.IInterProcessMutex;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @autor
 */
public class CacheBaseInterProcessMutex implements IInterProcessMutex {

    private static final String LOCK_NAME = "mutex-";
    private final CacheBaseLockInternals internals;
    private final String lockKey;
    private final ConcurrentMap<Thread, LockData> threadData = Maps.newConcurrentMap();

    /**
     * @param client  client
     * @param lockKey the path to lock
     * @param time    锁超时时间。到达超时时间后锁会自动解锁。
     * @param unit    超时时间单位
     */
    public CacheBaseInterProcessMutex( IRedisClient client,
                                       String lockKey,
                                       long time,
                                       TimeUnit unit) {
        this(client, lockKey, new CacheBaseLockInternalDriver(), time, unit);
    }

    /**
     * @param client  client
     * @param lockKey the path to lock
     * @param driver  lock driver
     * @param time    锁超时时间。到达超时时间后锁会自动解锁。
     * @param unit    超时时间单位
     */
    public CacheBaseInterProcessMutex(IRedisClient client,
                                      String lockKey,
                                      LockInternalsDriver driver,
                                      long time,
                                      TimeUnit unit) {
        this(client, lockKey, LOCK_NAME, 10, driver, time, unit);
    }

    /**
     * 创建一个互斥锁实例
     *
     * @param client        远端操作句柄
     * @param lockKey       要加锁的key
     * @param lockName      锁名称。一般而言用于指定是读锁还是写锁
     * @param maxRetryTimes 远端最大重试次数
     * @param driver        锁实现驱动
     * @param time          锁超时时间。到达超时时间后锁会自动解锁。
     * @param unit          超时时间单位
     */
    CacheBaseInterProcessMutex(IRedisClient client,
                               String lockKey,
                               String lockName,
                               int maxRetryTimes,
                               LockInternalsDriver driver,
                               long time,
                               TimeUnit unit) {
        this.lockKey = lockKey;
        internals = new CacheBaseLockInternals(client, driver, lockKey, lockName, maxRetryTimes, time, unit);
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
        /*
            并发时注意: 一个给定的 LockData 实例仅可能被单个线程操作，因此不需要锁定该实例。
         */
        Thread currentThread = Thread.currentThread();
        LockData lockData = threadData.get(currentThread);
        if (lockData == null) {
            throw new IllegalMonitorStateException("You do not own the lock: " + lockKey);
        }

        int newLockCount = lockData.lockCount.decrementAndGet();
        if (newLockCount > 0) {
            return;
        }
        if (newLockCount < 0) {
            throw new IllegalMonitorStateException("Lock count has gone negative for lock: " + lockKey);
        }
        try {
            internals.releaseLock();
        } finally {
            threadData.remove(currentThread);
        }
    }

    @Override
    public boolean isAcquiredInThisProcess() {
        return isOwnedByCurrentThread();
    }

    boolean isOwnedByCurrentThread() {
        LockData lockData = threadData.get(Thread.currentThread());
        return (lockData != null) && (lockData.lockCount.get() > 0);
    }

    protected byte[] getLockNodeBytes() {
        return (LOCK_NAME + Thread.currentThread().toString() + "_" + Thread.currentThread().getId()).getBytes();
    }

    protected String getLockKey() {
        LockData lockData = threadData.get(Thread.currentThread());
        return lockData != null ? lockData.lockKey : null;
    }

    private boolean internalLock(long time, TimeUnit unit) throws Exception {
        /*
           Note on concurrency: a given lockData instance
           can be only acted on by a single thread so locking isn't necessary
        */

        Thread currentThread = Thread.currentThread();

        LockData lockData = threadData.get(currentThread);
        if (lockData != null) {
            // re-entering
            lockData.lockCount.incrementAndGet();
            return true;
        }

        String realLockKey = internals.attemptLock(time, unit, getLockNodeBytes());
        if (realLockKey != null) {
            LockData newLockData = new LockData(currentThread, realLockKey);
            threadData.put(currentThread, newLockData);
            return true;
        }

        return false;
    }

    private static class LockData {
        final Thread owningThread;
        final String lockKey;
        final AtomicInteger lockCount = new AtomicInteger(1);

        private LockData(Thread owningThread, String lockKey) {
            this.owningThread = owningThread;
            this.lockKey = lockKey;
        }
    }
}
