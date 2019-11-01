package com.bk.karam.factory.cache.local;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author daichangbo
 * 本地缓存
 * 默认缓存时间为10分钟
 */
@Slf4j
public class LocalCacheClient {

    //本地缓存为防止项目过大缓存过多导致内存溢出，特此本地缓存保留一天时间(仅针对部分方法设置本地缓存)
    private static Cache<String, Object> localCache
            // CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
            = CacheBuilder.newBuilder()
            // 设置并发级别为20，并发级别是指可以同时写缓存的线程数
            .concurrencyLevel(20)
            // 设置写缓存后
            .expireAfterWrite(10, TimeUnit.MINUTES)
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

    public static Object getLocalCache(String key){
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
    public static void putLocalCache(String key,Object value){
        try {
            localCache.put(key,value);
        } catch (Exception e) {
            log.info("putLocalCache is error",e);
        }
    }

    public static void removeLocalCache(String key){
        if(StringUtils.isNotEmpty(key)){
            //单个清除
            localCache.invalidate(key);
        }
    }

    /**
     *
     * @param keys
     */
    public static void removeLocalCache(List<String> keys){
        if(CollectionUtils.isNotEmpty(keys)){
            //批量清楚
            localCache.invalidateAll(keys);
        }
    }

    public static boolean exist (String key) {
       Object obj = getLocalCache(key);
       if (obj == null)
           return Boolean.FALSE;
       return Boolean.TRUE;
    }
}
