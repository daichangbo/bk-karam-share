package com.bk.karam.lock.zk;

import com.bk.karam.lock.IInterProcessMutex;
import com.bk.karam.lock.IInterProcessSemaphoreMutex;
import com.bk.karam.lock.ILockFactory;
import com.bk.karam.lock.IReadWriteLock;
import java.util.concurrent.TimeUnit;

/**
 * @autor
 */
public class ZKLockFactory implements ILockFactory {

    private MyZooKeeperClient zooKeeperClient;

    public void setZooKeeperClient(MyZooKeeperClient zooKeeperClient) {
        this.zooKeeperClient = zooKeeperClient;
    }

    @Override
    public IInterProcessMutex newMutex( String key) {
        return newMutex(key, -1, null);
    }

    @Override
    public IInterProcessMutex newMutex(String key, long timeout, TimeUnit unit) {
        return zooKeeperClient.getInterProcessMutex(splitKey(key));
    }

    @Override
    public IInterProcessSemaphoreMutex newSemaphore( String key) {
        return newSemaphore(key, -1, null);
    }

    @Override
    public IInterProcessSemaphoreMutex newSemaphore(String key, long timeout, TimeUnit unit) {
        return zooKeeperClient.getSemaphore(splitKey(key));
    }

    @Override
    public IReadWriteLock newReadWriteLock( String key) {
        return newReadWriteLock(key, -1, null);
    }

    @Override
    public IReadWriteLock newReadWriteLock(String key, long timeout, TimeUnit unit) {
        return zooKeeperClient.getReadWriteLock(splitKey(key));
    }

    private static String[] splitKey(String key) {
        return key.split("/");
    }
}

