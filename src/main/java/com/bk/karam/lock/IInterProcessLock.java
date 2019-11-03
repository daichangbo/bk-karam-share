package com.bk.karam.lock;

import java.util.concurrent.TimeUnit;

/**
 * @autor
 */
public interface IInterProcessLock {

    /**
     * Acquire the mutex - blocking until it's available. Each call to acquire must be balanced by a call
     * to {@link #release()}
     *
     * @throws Exception ZK errors, connection interruptions
     */
    void acquire () throws Exception;

    /**
     * Acquire the mutex - blocks until it's available or the given time expires. Each call to acquire that returns true must be balanced by a call
     * to {@link #release()}
     *
     * @param time time to wait
     * @param unit time unit
     * @return true if the mutex was acquired, false if not
     * @throws Exception ZK errors, connection interruptions
     */
    boolean acquire ( long time, TimeUnit unit ) throws Exception;

    /**
     * Perform one release of the mutex.
     *
     * @throws Exception ZK errors, interruptions, current thread does not own the lock
     */
    void release () throws Exception;

    /**
     * Returns true if the mutex is acquired by a thread in this JVM
     *
     * @return true/false
     */
    boolean isAcquiredInThisProcess ();
}
