package com.bk.karam.lock.cachebase;

import com.bk.karam.factory.cache.redis.IRedisClient;
import org.apache.curator.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class CacheBaseLockInternals {

    private static final Logger logger = LoggerFactory.getLogger(CacheBaseLockInternals.class);

    private final IRedisClient client;
    private final String lockKey;
    private final LockInternalsDriver driver;
    private final String lockName;
    private final long time;
    private final TimeUnit unit;

    private final int maxRetryTimes;

    private String realLockKey;

    /**
     * 创建锁操作类
     *
     * @param client        cache 操作客户端
     * @param driver        锁驱动
     * @param lockKey       锁 key
     * @param lockName      锁名称
     * @param maxRetryTimes 最大重试次数
     * @param time          锁超时时间
     * @param unit          锁超时时间单位
     */
    CacheBaseLockInternals(IRedisClient client,
                           LockInternalsDriver driver,
                           String lockKey,
                           String lockName,
                           int maxRetryTimes,
                           long time,
                           TimeUnit unit) {
        this.driver = driver;
        this.lockName = lockName;
        this.maxRetryTimes = maxRetryTimes;
        this.time = time;
        this.unit = unit;

        this.client = client;
        this.lockKey = lockKey + "/" + lockName;
    }

    IRedisClient getClient() {
        return client;
    }

    public LockInternalsDriver getDriver() {
        return driver;
    }

    public String getLockName() {
        return lockName;
    }

    void releaseLock() throws Exception {
        deleteOurPath(realLockKey);
    }

    /**
     * 尝试进行加锁
     *
     * @param time          加锁操作超时时间
     * @param unit          加锁操作超时时间单位
     * @param lockNodeBytes 锁的值
     * @return 如果加锁成功，将返回真正的锁 key，否则返回{@code null}
     * @throws Exception 任意不可预料的异常，如线程中断等。
     */
    String attemptLock(long time, TimeUnit unit, byte[] lockNodeBytes) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Attempt lock " + logger + " time " + time + " unit " + unit.name() + " data " + new String(lockNodeBytes));
        }
        final long startMillis = System.currentTimeMillis();
        final Long millisToWait = (unit != null) ? unit.toMillis(time) : null;
        int retryCount = 0;

        boolean hasTheLock = false;
        boolean isDone = false;
        while (!isDone) {
            isDone = true;
            try {
                // 加锁操作会遵循先加锁，后检查这样的逻辑，以尽最大可能获得锁
                realLockKey = driver.createsTheLock(client, lockKey, lockNodeBytes, this.time, this.unit);
                hasTheLock = internalLockLoop(realLockKey, lockNodeBytes);
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.error(e.getLocalizedMessage(), e);
                }
                // 可能需要重试的情况，发生任何网络IO异常/竞争异常等等
                // 如果检查通过，考虑到以上操作耗费了不少时间，不需要暂停线程且立刻重试
                if (allowRetry(millisToWait, retryCount++, System.currentTimeMillis() - startMillis)) {
                    isDone = false;
                    continue;
                } else {
                    throw e;
                }
            }

            if (!hasTheLock) {
                synchronized (this) {
                    long elapsedTimeMs = System.currentTimeMillis() - startMillis;
                    if (millisToWait != null && elapsedTimeMs >= millisToWait) {
                        throw new TimeoutException();
                    } else if (allowRetry(millisToWait, retryCount++, elapsedTimeMs)) {
                        Thread.sleep(75 * retryCount);
                        isDone = false;
                    } else {
                        // 要么超时，要么重试
                        // 到达这个分支表明重试次数已经超过上限
                        // 重试次数太多而没有超时，一般是重试次数太少，超时时间太长导致
                        // 为了节省系统资源，直接跳出循环
                        break;
                    }
                }
            }
        }

        if (hasTheLock) {
            return realLockKey;
        }

        return null;
    }

    private boolean internalLockLoop(String realLockKey, byte[] lockNodeBytes) throws Exception {
        boolean haveTheLock;
        try {
            haveTheLock = driver.getsTheLock(client, realLockKey, lockNodeBytes);
        } catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            throw e;
        }
        return haveTheLock;
    }

    private void deleteOurPath(String ourPath) throws Exception {
        try {
            client.delete(ourPath);
        } catch (Exception e) {
        }
    }

    private boolean allowRetry(Long millisToWait, int retryCount, long elapsedTimeMs) {
        return retryCount < maxRetryTimes
                && (null == millisToWait || elapsedTimeMs < millisToWait);
    }
}
