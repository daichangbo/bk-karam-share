package com.bk.karam.lock.zk;

import com.bk.karam.lock.IInterProcessMutex;
import com.bk.karam.lock.IReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import java.util.concurrent.TimeUnit;

/**
 * @autor
 */
public class ReadWriteLockImpl implements IReadWriteLock {

    private final InterProcessReadWriteLock readWriteLock;

    ReadWriteLockImpl(InterProcessReadWriteLock readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    /**
     * Returns the lock used for reading.
     *
     * @return read lock
     */
    @Override
    public IInterProcessMutex readLock() {
        return new InternalInterProcessMutex(readWriteLock.readLock());
    }

    /**
     * Returns the lock used for writing.
     *
     * @return write lock
     */
    @Override
    public IInterProcessMutex writeLock() {
        return new InternalInterProcessMutex(readWriteLock.writeLock());
    }

    private static class InternalInterProcessMutex implements IInterProcessMutex {

        private final InterProcessMutex lock;

        public InternalInterProcessMutex(InterProcessMutex lock) {
            this.lock = lock;
        }

        @Override
        public void acquire() throws Exception {
            lock.acquire();
        }

        @Override
        public boolean acquire(long time, TimeUnit unit) throws Exception {
            return lock.acquire(time, unit);
        }

        @Override
        public void release() throws Exception {
            lock.release();
        }

        @Override
        public boolean isAcquiredInThisProcess() {
            return lock.isAcquiredInThisProcess();
        }
    }
}

