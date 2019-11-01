package com.bk.karam.factory.cache.redis;

import redis.clients.jedis.SortingParams;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author daichangbo
 * @date 2019-11-01 12:06
 */
public interface IRedisClient {

    /**
     * 此方法将ArrayList集合直接存储为一个字符串
     * @param key  存储的名字
     * @param list 要存储的集合对象
     * @param expireTime 该对象的有效时间，单位为秒
     * @return
     */
    public <T> Boolean setList ( String key, List<T> list , Integer expireTime) ;

    /**
     * 此方法将会把存在redis中的数据取出来，并封装成相应的Arraylist集合
     * @param key 存储的名字
     * @param beanClass 要封装成为的javaBean
     * @return
     */
    public <T> List<T> getList (String key , Class<?> beanClass) ;

    /**
     * 将redis存放数据的方式做进一步封装，使其更加适合java数据的存放 此方法将javaBean直接存储为一个字符串
     * @param key  存储的名字
     * @param javaBean 要存储的对象
     * @param expireTime  该对象的有效时间，单位为秒
     * @return
     */
    public Boolean setBean (String key ,Object javaBean , Integer expireTime);

    /**
     * 此方法将会把存在redis中的数据取出来，并封装成相应的JavaBean
     * @param key 存储的名字
     * @param beanClass 要封装成为的javaBean
     * @return
     */
    public <T> T getBean(String key,Class<?> beanClass);

    /**
     * 此方法将Map集合直接存储为一个字符串
     * @param key         存储的名字
     * @param map         要存储的Map集合对象
     * @return            成功返回true,失败返回false
     */
    public Boolean setMap ( String key , Map<String,Object> map , Integer expireTime) ;

    /**
     * 此方法将会把存在redis中的数据取出来，并封装成相应的Map集合
     * @param key          存储的名字
     * @param beanClass    要封装成的对象
     * @return             返回封装后的map集合
     */
    public Map<String, Object> getMap(String key, Class<?> beanClass);


    /**
     * 此方法将String字符串放入缓存
     * @param key 存储的名字
     * @param value 存储的值
     * @return
     */
    public String put( @NotNull String key, @NotNull String value);

    /**
     * 读取缓存数据
     *
     * @param key 键
     * @param hashKey 项
     * @return 值
     */
    String hget(final String key, final String hashKey);

    /**
     * 取出缓存
     * @param key
     * @return
     */
    public String get(String key);

    /**
     * 获取原来key键对应的值并重新赋新值。
     *
     * @param key
     * @param value
     * @return
     */
    String getAndSet(final String key, final String value);

    /**
     * 键不存在则新增,键存在则不改变已经有的值。
     *
     * <pre>
     * 对应redisTemplate中setIfAbsent
     *
     * @param key
     * @param value
     * @return
     */
    Boolean setnx(final String key, final int expireTime, final String value);

    /**
     * 获取分布式锁
     *
     * @param lockKey
     * @param requestId 请求标识
     * @param expireTime (单位：秒)
     * @return
     * @throws Exception
     */
    boolean tryLock(final String lockKey, final String requestId, final long expireTime) throws Exception;

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
    boolean tryLock(final String lockKey, final String requestId, final long expireTime, final long retryTime)
            throws Exception;

    /**
     * 释放分布式锁
     *
     * @param lockKey
     * @param requestId 请求标识
     */
    boolean releaseLock(final String lockKey, final String requestId) throws Exception;

    /**
     * * 释放分布式锁
     *
     * @param lockKey
     * @param requestId 请求标识
     * @param retryTime 重试时间(单位：秒)
     * @return
     */
    boolean releaseLock(final String lockKey, final String requestId, final long retryTime) throws Exception;

    /**
     * 根据key获取过期时间
     * 1.   当前key没有设置过期时间，所以会返回-1.
     * 2.   当前key有设置过期时间，而且key已经过期，所以会返回-2.
     * 3.   当前key有设置过期时间，且key还没有过期，故会返回key的正常剩余时间.
     *
     * @param key 键
     * @return 时间(秒) 返回0代表为永久有效
     */
    long getExpire(final String key);

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    boolean exists(final String key);

    /**
     * 删除缓存
     * @param key
     * @return
     */
    public long delete(@NotNull String key);


    /**
     * 添加缓存数据
     *
     * @param key
     * @param hashKey
     * @param value
     * @param expireTime
     * @return
     */
    boolean hset(final String key, final String hashKey, final String value, long expireTime);

    /**
     * 像缓存中添加值
     * @param key
     * @param value
     * @param expireTime 有效期 单位秒
     * @return
     */
    public String put(String key, String value,int expireTime);

    /**
     * 向缓存中存储内容
     * @param key
     * @param value
     * @param keyIsExist   NX当缓存中不存在改key值时存储  XX 当缓存中存在key值时
     * @param timeType   expire time units: EX = seconds; PX = milliseconds
     * @param time
     * @return
     */
    String put(final String key, final String value, final String keyIsExist, final String timeType,final long time) ;

    /**
     * 批量删除缓存
     * @param keys
     * @return
     */
    Long del(final String... keys);

    /**
     * 从当前选择的DB返回随机选择的密钥
     * @return
     */
    String randomKey();

    /**
     * 替换缓存可key值
     * @param oldkey
     * @param newkey
     * @return
     */
    String rename(@NotNull String oldkey, @NotNull String newkey);

    /**
     *
     * @param key
     * @param value
     * @return
     */
    Long putCache(final String key, final String value);

    /**
     * 像当前缓存中追加
     * 将指定的成员添加到存储在key处的设置值。如果成员已经是
     * 设置不执行任何操作。如果键不存在，则指定成员为
     * 创建唯一的成员。如果键存在但未保存设置值，则返回错误
     * @param key
     * @param members
     * @return
     */
    Long sadd(final String key, final String... members);

    /**
     * 排序
     * @param key
     * @param sortingParameters
     * @return
     */
    List<String> sort(final String key, final SortingParams sortingParameters);

    /**
     * 加锁
     * @param lockKey 锁的key
     * @param acquireTimeout 获取超时时间
     * @param timeOut 锁的超时时间
     * @return
     */
    String lockWithTimeout (String lockKey,long acquireTimeout,long timeOut) ;

    /**
     * 存放本地缓存本地
     * 有效期为1天
     * @param key
     * @param value
     */
    public void putLocalCache(String key,Object value);

    /**
     * 获取本地缓存
     * @param key
     * @return
     */
    public  Object getLocalCache(String key) ;

    /**
     * 删除本地缓存
     * @param key
     */
    public void removeLocalCache(String key) ;

    /**
     * 批量删除本地缓存
     * @param keys
     */
    public void removeLocalCache(List<String> keys) ;

    /**
     * 给key设置失效时间
     * @param cacheKey
     * @param expireTime
     * @return
     */
    public void setExpire (String cacheKey,Integer expireTime) ;
}
