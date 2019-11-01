package com.bk.karam.factory.cache.local;

import java.lang.annotation.*;

/**
 * @author daichangbo
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisAnnotation {

    /**
     * 缓存数据类型
     */
    CacheType type ();

    /**
     * 缓存key
     * 自定义缓存key值
     */
    String key ();

    /**
     * 存储方式
     * 默认是本地存储
     * @return
     */
    CacheMode mode () default CacheMode.LOCAL;

    /**
     * 失效时间
     */
    int expiredTime () default 3600;

    /**
     * 预留字段
     */
    boolean read () default true;
}
