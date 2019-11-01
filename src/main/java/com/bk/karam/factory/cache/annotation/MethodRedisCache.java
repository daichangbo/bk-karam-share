package com.bk.karam.factory.cache.annotation;

import java.lang.annotation.*;

/**
 * @author daichangbo
 * redis 注解封装
 * 适用于方法上
 * 该注解使用时需先初始化IRedisClient
 * 默认缓存时间是2分钟，调用者可以根据需求而定
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodRedisCache {

    /**
     * 产生缓存key的 provider<br>
     * 缓存系统会通过 spring 的 DI 系统获得该类型 bean，因此如果没有向spring注册bean，可能会导致失败。
     */
    Class<? extends ICacheKeyProvider> cacheKeyProvider () default HashCodeCacheKeyProvider.class;

    /**
     * 用于处理方法结果，最后决定是否缓存方法执行结果<br>
     * 缓存系统会通过 spring 的 DI 系统获得该类型 bean，因此如果没有向spring注册bean，可能会导致失败。
     */
    Class<? extends ICacheValueHandler> cacheValueHandler () default ICacheValueHandler.class;

    /**
     * 缓存过期时间，单位秒。使用“0”使用永不过期。
     */
    long expireTime () default 120;

    /**
     * 是否跳过缓存获取阶段。默认{@code false}。
     * 如果设置为{@code true}，缓存系统不会尝试获取缓存，但是仍然会根据{@link #modify()}决定如何处理已经存在的缓存结果。
     */
    boolean ignoreCache () default false;

    /**
     * 所注解的方法是否会修改缓存值。默认{@code false}。
     * 如果设置为{@code true},缓存系统会使已经缓存的结果失效。强制使下次缓存结果更新。
     */
    boolean modify () default false;

    /**
     * 发现已经有缓存锁占用当前方法时处理措施
     */
    Policy policy () default Policy.IGNORE;

    /**
     * 锁超时时间，毫秒
     */
    long lockTimeout () default 1500;

    /**
     * 方法缓存锁被占用时处理措施
     */
    enum Policy {
        /**
         * 忽略锁占用
         */
        IGNORE,
        /**
         * 等待锁释放
         */
        WAIT
    }

}
