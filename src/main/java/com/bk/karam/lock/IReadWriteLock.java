package com.bk.karam.lock;

/**
 * @autor
 */
public interface IReadWriteLock {
    /**
     * Returns the lock used for reading.
     *
     * @return read lock
     */
    IInterProcessMutex readLock ();

    /**
     * Returns the lock used for writing.
     *
     * @return write lock
     */
    IInterProcessMutex writeLock ();
}
