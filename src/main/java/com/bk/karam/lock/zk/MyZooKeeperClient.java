package com.bk.karam.lock.zk;

import com.bk.karam.lock.IInterProcessMutex;
import com.bk.karam.lock.IInterProcessSemaphoreMutex;
import com.bk.karam.lock.IReadWriteLock;
import java.io.Closeable;

/**
 * @autor
 */
public interface MyZooKeeperClient extends Closeable {

    /**
     * 获取读写锁
     *
     * @param paths 锁路径
     * @return 读写锁
     */
    IReadWriteLock getReadWriteLock ( String... paths );

    /**
     * 获取分布式锁
     *
     * @param paths 锁路径
     * @return 分布式锁
     */
    IInterProcessMutex getInterProcessMutex ( String... paths );

    IInterProcessSemaphoreMutex getSemaphore ( String... paths );
}
