package com.bk.karam.lock;

import java.util.concurrent.TimeUnit;

/**
 * @autor
 */
public interface ILockFactory {

    /**
     * 构造一个可重入的互斥锁
     *
     * @param key 锁 key
     * @return 可重入互斥锁
     */
    IInterProcessMutex newMutex ( String key );

    /**
     * 构造一个会自动超时的可重入互斥锁
     *
     * @param key     锁 key
     * @param timeout 超时时间，值是{@code -1}时表示永不过期
     * @param unit    时间单位
     * @return 会自动超时的可重入互斥锁
     */
    IInterProcessMutex newMutex ( String key, long timeout, TimeUnit unit );

    /**
     * 构造一个不可重入的互斥锁
     *
     * @param key 锁 key
     * @return 不可重入的互斥锁
     */
    IInterProcessSemaphoreMutex newSemaphore ( String key );

    /**
     * 构造一个会自动超时的不可重入的互斥锁
     *
     * @param key     锁 key
     * @param timeout 超时时间，值是{@code -1}时表示永不过期
     * @param unit    时间单位
     * @return 会自动超时的不可重入互斥锁
     */
    IInterProcessSemaphoreMutex newSemaphore ( String key, long timeout, TimeUnit unit );

    /**
     * 构造一个读写锁
     *
     * @param key 锁 key
     * @return 读写锁
     */
    IReadWriteLock newReadWriteLock ( String key );

    /**
     * 构造一个会自动超市的读写锁
     *
     * @param key     锁 key
     * @param timeout 超时时间，值是{@code -1}时表示永不过期
     * @param unit    时间单位
     * @return 会自动超时的读写锁
     */
    IReadWriteLock newReadWriteLock ( String key, long timeout, TimeUnit unit );
}
