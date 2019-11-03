package com.bk.karam.concurrent;

import java.lang.annotation.*;

/**
 * 添加该注解表示方法接受并发控制.
 * <p>
 * <pre>
 *     {@code @ConcurrencyControl(keyProvider = KeyProvider.class,
 *                                valueProvider = ValueProvider.class
 *                                rejectProvider = RejectProvider.class)}
 *     Foo getFoo(Bar bar);
 * </pre>
 * <p>
 * 运行期，方法执行前会使用{@link #keyProvider()}所提供的 key 尝试从缓存中查找值，如果找到将会被并发控制器拦截，
 * 返回由{@link #rejectProvider()}提供的错误信息。<br>
 * 否则，将缓存{@link #valueProvider()}提供的值，并执行目标方法。<br>
 * 当方法执行完成，缓存将被清空。<br>
 * <p>
 * 缓存存续期间，产生相同 key 的请求都将被拦截，直到：<br>
 * <ol>
 * <li>超时，超时值由{@link #expiredTime()}获得，单位 秒</li>
 * <li>方法运行完成退出并清除缓存</li>
 * </ol>
 * @author
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConcurrencyControl {

    /**
     * 指定缓存用的key，如果为空，则使用{@link #keyProvider()}
     */
    String cacheKey () default "";

    /**
     * 缓存Key生成器
     */
    Class<? extends IConcurrencyControlKeyProvider> keyProvider () default DefaultHashCodeConcurrencyControlKeyProvider.class;

    /**
     * 指定缓存的值，如果为空，则使用 {@link #valueProvider()}
     */
    @Deprecated
    String cacheValue () default "";

    /**
     * 缓存value生成器
     */
    @Deprecated
    Class<? extends IConcurrencyControlValueProvider> valueProvider () default DefaultConcurrencyControllerValueProvider.class;


    /**
     * 当被缓存控制拦截时，产生告警信息
     */
    Class<? extends IConcurrencyControlRejectMessageProvider> rejectProvider ();

    /**
     * 缓存超时时间 / 锁获取过期时间，秒
     */
    int expiredTime () default 60;

    /**
     * 并发控制策略
     */
    Policy policy () default Policy.REJECT;

    /**
     * 并发控制策略
     */
    enum Policy {
        /**
         * 直接拒绝
         */
        REJECT,
        /**
         * 等待直至超时
         */
        WAIT
    }
}
