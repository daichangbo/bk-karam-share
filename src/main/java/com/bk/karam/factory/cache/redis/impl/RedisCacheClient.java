package com.bk.karam.factory.cache.redis.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bk.karam.constant.BaseConstant;
import com.bk.karam.exception.BaseException;
import com.bk.karam.factory.cache.redis.IRedisClient;
import com.bk.karam.factory.cache.redis.IRedisFactory;
import com.bk.karam.factory.cache.redis.RedisParamTypeCheck;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * dcb
 */
@Slf4j
@Service
public class RedisCacheClient extends RedisParamTypeCheck implements IRedisClient {

    private IRedisFactory iRedisFactory;


    public void setiRedisFactory(IRedisFactory iRedisFactory) {
        this.iRedisFactory = iRedisFactory;
    }

    public RedisCacheClient () {
        /**
         * 初始化构造方法
         */
    }

    //本地缓存为防止项目过大缓存过多导致内存溢出，特此本地缓存保留一天时间(仅针对部分方法设置本地缓存)
    private Cache<String, Object> localCache
            // CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
            = CacheBuilder.newBuilder()
            // 设置并发级别为20，并发级别是指可以同时写缓存的线程数
            .concurrencyLevel(20)
            // 设置写缓存后
            .expireAfterWrite(1, TimeUnit.DAYS)
            //当缓存项上一次更新操作之后的多久会被刷新。
            //.refreshAfterWrite(60, TimeUnit.SECONDS)
            // 设置缓存容器的初始容量为10 不易设置过大以免造成内存浪费
            .initialCapacity(10)
            // 设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
            .maximumSize(10000)
            // 设置要统计缓存的命中率
            .recordStats()
            // 设置缓存的移除通知
            .removalListener(new RemovalListener<Object, Object>() {
                public void onRemoval(RemovalNotification<Object, Object> notification) {
                    log.info(notification.getKey() + " was removed, cause is " + notification.getCause());
                }
            }).build();

    public Jedis getJedis(Jedis jedis) throws Exception {
        synchronized (this) {
            if(!isConnected(jedis)){
                return iRedisFactory.getJedisFactory().getResource();
            }
        }
        throw new BaseException ( BaseConstant.REDIS_CONNECT_ERROR);
    }

    protected void closeJedis(Jedis jedis) {
        try {
            if (jedis == null || !jedis.isConnected()) {
                return;
            }
            jedis.close();
        } catch (Exception e) {
            log.info(BaseConstant.REDIS_CLOSE_ERROR,e);
        }
    }

    protected void setExpireTime (Jedis jedis,String key,Integer expireTime) throws Exception{
        if (expireTime != null && expireTime > 0 && isConnected(jedis)) {
            jedis.expire(key, expireTime);
        }
    }

    protected boolean isConnected(Jedis jedis) {
        if (jedis == null || !jedis.isConnected()) {
            return false;
        }
        return true;
    }

    /**
     * 存储字符串的值
     * @param key
     * @param value
     */
    @Override
    public String put(@NotNull String key, @NotNull String value) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            Preconditions.checkNotNull(value,BaseConstant.VALUE_NULL);
            jedis = getJedis(jedis);
            return jedis.set(key, value);
        } catch (Exception e) {
            log.error("putRedisCache is error",e);
        } finally {
            closeJedis(jedis);
        }
        return null;
    }

    /**Hash
     * 为哈希表 key 中的域 field 的值加上增量 value
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hincrBy(String key, String field, long value) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            Preconditions.checkNotNull(value,BaseConstant.VALUE_NULL);
            log.info("hincrBy  cache " + key + " = " + value);
            jedis = getJedis(jedis);
            return jedis.hincrBy(key, field, value);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            closeJedis(jedis);
        }
        return 0L;
    }

    /**
     * 删除缓存内容
     */
    @Override
    public long delete(@NotNull String key) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            jedis = getJedis(jedis);
            return jedis.del(key);
        }catch (Exception e) {
            log.error(e.getMessage(),e);
        }finally {
            closeJedis(jedis);
        }
        return 0;
    }

    /**
     * 此方法将ArrayList集合直接存储为一个字符串
     * @param key  存储的名字
     * @param list 要存储的集合对象
     * @param expireTime 该对象的有效时间，单位为秒
     * @return
     */
    @Override
    public <T> Boolean setList(String key, List<T> list, Integer expireTime) {
        if (CollectionUtils.isEmpty(list) && StringUtils.isEmpty(key)) {
            return Boolean.FALSE;
        }
        Jedis jedis = null;
        try {
            jedis = getJedis(jedis);
            jedis.set(key, JSONObject.toJSONString(list));
            setExpireTime(jedis,key,expireTime);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.info("setList is error",e);
        }  finally {
            closeJedis(jedis);
        }
        return Boolean.FALSE;
    }

    /**
     * 此方法将会把存在redis中的数据取出来，并封装成相应的Arraylist集合
     * @param key
     *            存储的名字
     * @param beanClass
     *            要封装成为的javaBean
     * @return List
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getList(String key, Class<?> beanClass) {
        if (StringUtils.isEmpty(key) && beanClass == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = getJedis(jedis);
            JSONArray jsonArray = JSONArray.parseArray(jedis.get(key)) ;
            if(jsonArray == null) {
                return null;
            }
            List<Object> objList = new ArrayList<>();
            for (int i = 0 ; i < jsonArray.size() ; i++) {
                Object object = JSONObject.toJavaObject((JSONObject) jsonArray.get(i), beanClass);
                objList.add(object);
            }
            return (List<T>) objList;
        } catch (Exception e) {
            log.error("getList is error" , e);
        }  finally {
            closeJedis(jedis);
        }
        return null;
    }

    @Override
    public boolean hset(String key, String hashKey, String value, long expireTime) {
        if (StringUtils.isEmpty(key) && StringUtils.isEmpty(value)) {
            return Boolean.FALSE;
        }
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            Preconditions.checkNotNull(value,BaseConstant.VALUE_NULL);
            jedis = getJedis(jedis);
            jedis.hset(key, hashKey, value);
            setExpireTime(jedis,key,(int)expireTime);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.info("setBean is error " ,e);
        }  finally {
            closeJedis(jedis);
        }
        return Boolean.FALSE;
    }

    /**
     * 将redis存放数据的方式做进一步封装，使其更加适合java数据的存放 此方法将javaBean直接存储为一个字符串
     *
     * @param key
     *            存储的名字
     * @param javaBean
     *            要存储的对象
     */
    @Override
    public Boolean setBean(String key, Object javaBean, Integer expireTime) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            Preconditions.checkNotNull(javaBean,BaseConstant.VALUE_NULL);
            String value = JSON.toJSONString(javaBean);
            if (javaBean instanceof String) {
                value = (String) javaBean;
            } else {
                value = JSON.toJSONString(javaBean);
            }
            jedis = getJedis(jedis);
            jedis.setex(key, expireTime.intValue(), value);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.info("setBean is error " ,e);
        } finally {
            closeJedis(jedis);
        }
        return Boolean.FALSE;
    }

    /**
     * 此方法将会把存在redis中的数据取出来，并封装成相应的JavaBean
     *
     * @param key
     *            存储的名字
     * @param beanClass
     *            要封装成为的javaBean
     * @return javaBean
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String key, Class<?> beanClass) {
        if (StringUtils.isEmpty(key) && beanClass == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = getJedis(jedis);
            Object object = getClassBean (jedis, key,  beanClass);
            return (T) object;
        } catch (Exception e) {
            log.info("getBean is error " ,e);
        } finally {
            closeJedis(jedis);
        }
        return null;
    }

    protected Object getClassBean (Jedis jedis,String key, Class<?> beanClass) {
        try {
            if (isBaseType(beanClass)) {
                return getPackingType(jedis,key,beanClass);
            }
            return getResult(jedis,key, beanClass);
        } catch (Exception e) {
            log.info("getClassBean is error " ,e);
        }
        return null;
    }

    private Object getResult (Jedis jedis,String key,Class<?> beanClass) throws Exception{
        String cacheValue = jedis.get(key);
        if (StringUtils.isNotEmpty(cacheValue)) {
            JSONObject jsonObj = JSONObject.parseObject(cacheValue);
            return JSONObject.toJavaObject(jsonObj, beanClass);
        }
        return null;
    }

    /**
     * 此方法将Map集合直接存储为一个字符串
     * @param key         存储的名字
     * @param map         要存储的Map集合对象
     * @return            成功返回true,失败返回false
     */
    @Override
    public Boolean setMap(String key, Map<String, Object> map, Integer expireTime) {
        if (StringUtils.isEmpty(key) && map == null) {
            return Boolean.FALSE;
        }
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            Preconditions.checkNotNull(map,BaseConstant.VALUE_NULL);
            jedis = getJedis(jedis);
            jedis.set(key, JSONObject.toJSONString(map));
            setExpireTime(jedis,key,expireTime);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.info("setMap is error " ,e);
        } finally {
            closeJedis(jedis);
        }
        return Boolean.FALSE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getMap(String key, Class<?> beanClass) {
        if (StringUtils.isEmpty(key) && beanClass == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = getJedis(jedis);
            Map<Object,Object> map = (Map<Object,Object>) JSONObject.toJavaObject(JSONObject.parseObject(jedis.get(key)), Map.class);
            if (map == null) {
                return new HashMap<>();
            }
            Set<Object> set = map.keySet();
            Iterator<Object> iterator = set.iterator();
            Map<String,Object> maps = new HashMap<>();
            while (iterator.hasNext()) {
                String mapKey = (String)  iterator.next();
                Object object = JSON.parseObject(JSON.toJSONString(map.get(mapKey)),beanClass);
                maps.put(mapKey, object) ;
            }
            return maps;
        } catch (Exception e) {
            log.info("setMap is error " ,e);
        } finally {
            closeJedis(jedis);
        }
        return null;
    }

    /**
     * 根据key获取缓存的值
     */
    @Override
    public String get(String key) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            jedis = getJedis(jedis);
            return jedis.get(key);
        } catch (Exception e) {
            log.info("get is error",e);
        } finally {
            closeJedis(jedis);
        }
        return null;
    }

    /**
     * 读取缓存数据
     *
     * @param key 键
     * @param hashKey 项
     * @return 值
     */
    @Override
    public String hget(String key, String hashKey) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            Preconditions.checkNotNull(hashKey,BaseConstant.KEY_NULL);
            jedis = getJedis(jedis);
            return jedis.hget(key, hashKey);
        } catch (Exception e) {
            log.info("hget is error",e);
        } finally {
            closeJedis(jedis);
        }
        return null;
    }

    /**
     * 获取原来key键对应的值并重新赋新值。
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public String getAndSet(String key, String value) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            jedis = getJedis(jedis);
            return jedis.getSet(key, value);
        } catch (Exception e) {
            log.info("getAndSet is error",e);
        } finally {
            closeJedis(jedis);
        }
        return null;
    }

    @Override
    public Boolean setnx(String key, int expireTime, String value) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            Preconditions.checkNotNull(value,BaseConstant.VALUE_NULL);
            jedis = getJedis(jedis);
            String result = jedis.set(key, value, "NX", "EX", expireTime);
            return "OK".equalsIgnoreCase(result);
        }catch (Exception e) {
            log.info("setnx is error",e);
        } finally {
            closeJedis(jedis);
        }
        return false;
    }

    /**
     * 获取分布式锁
     *
     * @param lockKey
     * @param requestId 请求标识
     * @param expireTime
     * @return
     * @throws Exception
     */
    @Override
    public boolean tryLock(final String lockKey, final String requestId, final long expireTime) throws Exception {
        try {
            return tryLock(lockKey, requestId, expireTime, 0);
        } catch (Exception e) {
            throw new BaseException ("tryLock is error");
        }
    }

    /**
     * 获取分布式锁
     *
     * @param lockKey
     * @param requestId 请求标识
     * @param expireTime (单位：秒)
     * @param retryTime 重试时间(单位：秒)
     * @return
     * @throws Exception
     */
    @Override
    public boolean tryLock(final String lockKey, final String requestId, final long expireTime, final long retryTime)
            throws Exception {
        try {
            //当前时间
            Long currentTimeMillis = System.currentTimeMillis();
            // 释放锁时间
            long releaseLockTime = currentTimeMillis + expireTime * 1000;
            // 重试获取锁时间
            long retryLockTime = currentTimeMillis + retryTime * 1000;
            if (System.currentTimeMillis() - releaseLockTime <= 0) {
                do {
                    // 获取原子锁，设置锁过期时间，以防止程序异常，成为永久锁
                    if (setnx(lockKey, (int)expireTime, requestId)) {
                        // 上锁成功结束请求
                        return true;
                    } else if (retryTime == 0) {
                        return false;
                    }
                    // 每次请求等待一段时间
                    TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(100, 1000));
                } while (System.currentTimeMillis() - retryLockTime <= 0);
            }
            return false;
        } catch (Exception e) {
            log.error("获取分布式锁异常，lockKey：{}, requestId：{}", lockKey, requestId);
            throw new BaseException("tryLock is error"+e);
        }
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey
     * @param requestId 请求标识
     */
    @Override
    public boolean releaseLock(final String lockKey, final String requestId) throws Exception {
        try {
            return releaseLock(lockKey, requestId, 0);
        } catch (Exception e) {
            throw new BaseException("releaseLock is error"+e);
        }
    }

    /**
     * 释放分布式锁
     *
     * <pre>
     * REDIS通过使用WATCH, MULTI, and EXEC组成的事务来实现乐观锁(注意没有用DISCARD),REDIS事务没有回滚操作。
     * WATCH命令可以监控一个或多个键，一旦其中有一个键被修改（或删除），之后的事务就不会执行。
     * </pre>
     *
     * @param lockKey
     * @param requestId 请求标识
     * @param retryTime 重试时间(单位：秒)
     * @throws Exception
     */
    @Override
    public boolean releaseLock(final String lockKey, final String requestId, final long retryTime) throws Exception {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(lockKey,BaseConstant.KEY_NULL);
            jedis = getJedis(jedis);
            jedis.watch(lockKey);
            String value = jedis.get(lockKey);
            if (StringUtils.isEmpty(value)) {
                return false;
            }
            if (!value.equals(requestId)) {
                return false;
            }
            // 重试获取锁时间
            long currentTimeMillis = System.currentTimeMillis();
            long retryLockTime = currentTimeMillis + retryTime * 1000;
            do {
                Transaction transaction = jedis.multi();
                transaction.del(lockKey);
                List<Object> result = transaction.exec();
                if (CollectionUtils.isNotEmpty(result)) {
                    return true;
                }
                // 每次请求等待一段时间
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(100, 1000));
            } while (System.currentTimeMillis() - retryLockTime <= 0);
            return false;
        } catch (Exception e) {
            log.error("释放分布式锁异常，lockKey：{}, requestId：{}", lockKey, requestId);
            throw new BaseException("releaseLock is error"+e);
        } finally {
            unwatch(jedis);
            closeJedis(jedis);
        }
    }

    /**
     * 释放watch
     *
     * @param jedis
     */
    private void unwatch(Jedis jedis) {
        try {
            if (jedis == null) {
                return;
            }
            jedis.unwatch();
        } catch (Exception e) {
            log.info("unwatch is error" ,e);
        }
    }

    /**
     * 查询key的过期时间 以秒为单位的时间表示返回的是指定key的剩余的生存时间
     *
     * @param key
     * @return
     */
    @Override
    public long getExpire(String key) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            jedis = getJedis(jedis);
            return jedis.ttl(key);
        } catch (Exception e) {
            log.info("getExpire is error" ,e);
        } finally {
            closeJedis(jedis);
        }
        return 0;
    }

    @Override
    public boolean exists(String key) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            jedis = getJedis(jedis);
            return jedis.exists(key);
        } catch (Exception e) {
            log.info("exists is error" ,e);
        } finally {
            closeJedis(jedis);
        }
        return false;
    }

    @Override
    public String put(String key, String value, int expireTime) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            Preconditions.checkNotNull(value,BaseConstant.VALUE_NULL);
            jedis = getJedis(jedis);
            if (expireTime == 0) {
                return jedis.set(key, value);
            }
            return jedis.setex(key, expireTime, value);
        } catch (Exception e) {
            log.error("put is error",e);
        } finally {
            closeJedis(jedis);
        }
        return null;
    }

    /**
     *
     */
    @Override
    public String put(String key, String value,final String keyIsExist, final String timeType, long time) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            Preconditions.checkNotNull(value,BaseConstant.VALUE_NULL);
            jedis = getJedis(jedis);
            return jedis.set(key, value, keyIsExist, timeType, time);
        } catch (Exception e) {
            log.error("put is error",e);
        } finally {
            closeJedis(jedis);
        }
        return null;
    }

    @Override
    public Long del(String... keys) {
        Jedis jedis = null;
        try {
            if (keys.length == 0) {
                return 0L;
            }
            jedis = getJedis(jedis);
            return jedis.del(keys);
        } catch (Exception e) {
            log.error("del keys is error",e);
        } finally {
            closeJedis(jedis);
        }
        return 0L;
    }

    @Override
    public String randomKey() {
        Jedis jedis = null;
        try {
            jedis = getJedis(jedis);
            return jedis.randomKey();
        } catch (Exception e) {
            log.error("randomRedisKey is error",e);
        } finally {
            closeJedis(jedis);
        }
        return null;
    }

    @Override
    public String rename(@NotNull String oldkey,@NotNull String newkey) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(oldkey,"原key为空");
            Preconditions.checkNotNull(newkey,"新key为空");
            jedis = getJedis(jedis);
            return jedis.rename(oldkey, newkey);
        } catch (Exception e) {
            log.error("rename redis is error",e);
        } finally {
            closeJedis(jedis);
        }
        return null;
    }

    @Override
    public Long putCache(String key, String value) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            Preconditions.checkNotNull(value,BaseConstant.VALUE_NULL);
            jedis = getJedis(jedis);
            return jedis.setnx(key, value);
        } catch (Exception e) {
            log.error("randomKey is error",e);
        } finally {
            closeJedis(jedis);
        }
        return 0L;
    }

    @Override
    public Long sadd(String key, String... members) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            jedis = getJedis(jedis);
            return jedis.sadd(key, members);
        } catch (Exception e) {
            log.error("sadd is error",e);
        } finally {
            closeJedis(jedis);
        }
        return 0L;
    }


    @Override
    public List<String> sort(String key, SortingParams sortingParameters) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(key,BaseConstant.KEY_NULL);
            jedis = getJedis(jedis);
            return jedis.sort(key, sortingParameters);
        } catch (Exception e) {
            log.error("sort is error",e);
        } finally {
            closeJedis(jedis);
        }
        return new ArrayList<>();
    }

    @Override
    public String lockWithTimeout(String lockKey, long acquireTimeout, long timeOut) {
        String retIdentifier = null;
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(lockKey,BaseConstant.KEY_NULL);
            jedis = getJedis(jedis);
            String identifier = UUID.randomUUID().toString();
            // 超时时间，上锁后超过此时间则自动释放锁
            int lockExprie = (int) (timeOut / 1000 );
            // 获取锁的超时时间，超过这个时间则放弃获取锁
            long end = System.currentTimeMillis() + acquireTimeout;
            while (System.currentTimeMillis() < end) {
                if ( jedis.setnx(lockKey, identifier) == 1) {
                    jedis.expire(lockKey, lockExprie) ;
                    // 返回value值，用于释放锁时间确认
                    retIdentifier = identifier;
                    return retIdentifier;
                }
                // 返回-1代表key没有设置超时时间，为key设置一个超时时间
                if (jedis.ttl(lockKey) == -1) {
                    jedis.expire(lockKey, lockExprie) ;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            log.error("lockWithTimeout is error",e);
        } finally {
            closeJedis(jedis);
        }
        return null;
    }

    @Override
    public  Object getLocalCache(String key){
        try {
            return StringUtils.isNotEmpty(key)? localCache.getIfPresent(key):null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param key
     * @param value
     */
    @Override
    public void putLocalCache(String key,Object value){
        try {
            localCache.put(key,value);
        } catch (Exception e) {
            log.info("putLocalCache is error",e);
        }
    }

    @Override
    public void removeLocalCache(String key){
        if(StringUtils.isNotEmpty(key)){
            //单个清除
            localCache.invalidate(key);
        }
    }

    /**
     *
     * @param keys
     */
    @Override
    public void removeLocalCache(List<String> keys){
        if(CollectionUtils.isNotEmpty(keys)){
            //批量清楚
            localCache.invalidateAll(keys);
        }
    }

    @Override
    public void setExpire(String cacheKey, Integer expireTime) {
        Jedis jedis = null;
        try {
            Preconditions.checkNotNull(cacheKey,BaseConstant.KEY_NULL);
            jedis = getJedis(jedis);
            setExpireTime(jedis, cacheKey, expireTime);
        } catch (Exception e) {
            log.error("setExpire is error",e);
        } finally {
            closeJedis(jedis);
        }
    }
}
