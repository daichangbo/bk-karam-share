package com.bk.karam.lock.cachebase;

import com.bk.karam.factory.cache.redis.IRedisClient;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @autor
 */
public interface LockInternalsDriver {
    /**
     * 尝试获取锁。这个方法仅应向对应存储空间查询对应锁是否存在，并检查锁对应的值和提供的值是否相同，以确定
     * 当前线程是否拥有对应的锁。
     * <p>
     * 注意：这个方法不会尝试创建锁
     *
     * @param client        cache 操作客户端
     * @param key           锁的key
     * @param lockNodeBytes 锁的值
     * @return 如果判断当前线程拥有锁，返回{@code true}，其他情况返回false。
     * @throws IOException 如果出现网络IO 异常
     */
    boolean getsTheLock ( IRedisClient client, String key, byte[] lockNodeBytes ) throws IOException;

    /**
     * 创建一个锁，然后返回锁的 key，最终加锁的 key 以传入的 {@code key} 相关，但不完全等同。后续如果要
     * 在当前实例之外对该锁的进行操作应当以返回的 key 为准，但如果铜鼓当前实例操作锁，仍然可以用传入的key
     * <p>
     * 如果超时时间不是 {@code -1} ，则锁会在指定时间之后自动解锁
     *
     * @param client        cache 操作客户端
     * @param key           锁的 key
     * @param lockNodeBytes 锁的值
     * @param time          锁超时时间
     * @param unit          时间单位
     * @return 不论加锁成功或失败，都会返回实际使用的锁的 key。
     * @throws IOException 如果出现 I/O 异常
     */
    String createsTheLock ( IRedisClient client,
                            String key,
                            byte[] lockNodeBytes,
                            long time,
                            TimeUnit unit ) throws IOException;
}
