package com.bk.karam.lock.cachebase;

import com.bk.karam.factory.cache.redis.IRedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @autor
 */
public class CacheBaseLockInternalDriver implements LockInternalsDriver{
    private static final Logger logger = LoggerFactory.getLogger(CacheBaseLockInternalDriver.class);

    private static String convert(byte[] bytes) {
        return new String(bytes);
    }

    @Override
    public boolean getsTheLock( IRedisClient client, String key, byte[] lockNodeBytes) throws IOException {
        String value = convert(lockNodeBytes);
        String _value = client.get(key);
        if (logger.isDebugEnabled()) {
            logger.debug("Get cache value " + _value + " compare to " + value);
        }
        return Objects.equals(value, _value);
    }

    @Override
    public String createsTheLock(IRedisClient client,
                                 String key,
                                 byte[] lockNodeBytes,
                                 long time,
                                 TimeUnit unit) throws IOException {
        long timeToWait = null == unit ? -1 : unit.toSeconds(time);
        String value = convert(lockNodeBytes);
        CacheEntryWrapper<Serializable, String> wrapper = null;//client.getWrapper(key);
        if (logger.isDebugEnabled()) {
            logger.debug("Get cache value " + wrapper);
        }
        if (null == wrapper || null == wrapper.get()) {
            boolean result;
            if (-1 == timeToWait) {
//                result = client.putWithVersion(key, value, 2);
            } else {
//                result = client.putInternal(key, value, 2, timeToWait);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Put cache " + key + " result "  + " expire time " + timeToWait);
            }
        }
        return key;
    }

}
