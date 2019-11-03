package com.bk.karam.lock.zk;


import com.bk.karam.lock.IInterProcessMutex;
import com.bk.karam.lock.IInterProcessSemaphoreMutex;
import com.bk.karam.lock.IReadWriteLock;
import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @autor
 */
public class MyZooKeeperClientImpl implements MyZooKeeperClient {

    private static final Logger logger = LoggerFactory.getLogger(MyZooKeeperClientImpl.class);

    private final String ephemeralZNodeNamePrefix;

    private final CuratorFramework client;

    private LoadingCache<PathCacheKey, InternalInterProcessMutex> mutexLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(3600, TimeUnit.SECONDS)
            .softValues()
            .build(new CacheLoader<PathCacheKey, InternalInterProcessMutex>() {
                @Override
                public InternalInterProcessMutex load( PathCacheKey cacheKey) throws Exception {
                    return getMutexInternal(cacheKey.basePath);
                }
            });
    private LoadingCache<PathCacheKey, IReadWriteLock> readWriteLockLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(3600, TimeUnit.SECONDS)
            .softValues()
            .build(new CacheLoader<PathCacheKey, IReadWriteLock>() {
                @Override
                public IReadWriteLock load(PathCacheKey cacheKey) throws Exception {
                    return getReadWriteLockInternal(cacheKey.basePath);
                }
            });
    private LoadingCache<PathCacheKey, IInterProcessSemaphoreMutex> semaphoreLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(3600, TimeUnit.SECONDS)
            .softValues()
            .build(new CacheLoader<PathCacheKey, IInterProcessSemaphoreMutex>() {
                @Override
                public IInterProcessSemaphoreMutex load( PathCacheKey cacheKey) throws Exception {
                    return getSemaphoreInternal(cacheKey.basePath);
                }
            });

    MyZooKeeperClientImpl(String ephemeralZNodeNamePrefix, CuratorFramework client) {
        this.ephemeralZNodeNamePrefix = ephemeralZNodeNamePrefix;
        this.client = client;
    }

    @Override
    public IReadWriteLock getReadWriteLock(String... paths) {
        String basePath = joinPath("read-write-lock", paths);
        PathCacheKey cacheKey = getCacheKey(basePath);
        try {
            return readWriteLockLoadingCache.get(cacheKey);
        } catch (ExecutionException e) {
            logger.warn(e.getLocalizedMessage(), e);
            return getReadWriteLockInternal(cacheKey.basePath);
        }
    }

    private IReadWriteLock getReadWriteLockInternal(String basePath) {
        return new ReadWriteLockImpl(new InterProcessReadWriteLock(client, basePath));
    }

    @Override
    public IInterProcessMutex getInterProcessMutex( String... paths) {
        String basePath = joinPath("distributed-lock", paths);
        PathCacheKey cacheKey = getCacheKey(basePath);
        try {
            return mutexLoadingCache.get(cacheKey);
        } catch (ExecutionException e) {
            logger.error(e.getLocalizedMessage(), e);
            return getMutexInternal(cacheKey.basePath);
        }
    }

    private InternalInterProcessMutex getMutexInternal(String basePath) {
        return new InternalInterProcessMutex(new InterProcessMutex(client, basePath));
    }

    @Override
    public IInterProcessSemaphoreMutex getSemaphore(String... paths) {
        String basePath = joinPath("semaphore-lock", paths);
        PathCacheKey cacheKey = getCacheKey(basePath);
        try {
            return semaphoreLoadingCache.get(cacheKey);
        } catch (ExecutionException e) {
            logger.error(e.getLocalizedMessage(), e);
            return getSemaphoreInternal(cacheKey.basePath);
        }
    }

    private InternalSemaphore getSemaphoreInternal(String basePath) {
        return new InternalSemaphore(new InterProcessSemaphoreMutex(client, basePath));
    }

    private String joinPath(String prefix, String... paths) {
        LinkedList<String> list = Lists.newLinkedList(Arrays.asList(paths));
        list.addFirst(ephemeralZNodeNamePrefix);
        list.addFirst(prefix);
        list.addFirst("");
        return Joiner.on('/').join(list);
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }

    private PathCacheKey getCacheKey(String basePath) {
        Thread currentThread = Thread.currentThread();
        return new PathCacheKey(basePath, String.valueOf(currentThread.hashCode()) + currentThread.toString());
    }

    private class PathCacheKey implements Serializable {
        private final String basePath;

        private final String key;

        public PathCacheKey(String basePath, String suffix) {
            this.basePath = basePath;
            this.key = basePath + suffix;
        }

        @Override
        public String toString() {
            return key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (!(o instanceof PathCacheKey)) return false;

            PathCacheKey that = (PathCacheKey) o;

            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    private class InternalInterProcessMutex implements IInterProcessMutex {

        private final InterProcessLock lock;

        InternalInterProcessMutex(InterProcessLock lock) {
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

    private class InternalSemaphore implements IInterProcessSemaphoreMutex {
        private final InterProcessSemaphoreMutex lock;

        InternalSemaphore(InterProcessSemaphoreMutex lock) {
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
