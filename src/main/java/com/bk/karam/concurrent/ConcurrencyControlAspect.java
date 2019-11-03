package com.bk.karam.concurrent;

import com.bk.karam.lock.IInterProcessSemaphoreMutex;
import com.bk.karam.lock.ILockFactory;
import com.bk.karam.util.ReflectionUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @autor
 */
@Slf4j
@Aspect
public class ConcurrencyControlAspect implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ConcurrencyControlAspect.class);

    private static final String CONCURRENCY_CONTROL_LOCK_KEY = "my_concurrency_ctrl-";

    private ApplicationContext applicationContext;
    private ILockFactory lockFactory;
    private boolean enable = true;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setLockFactory(ILockFactory lockFactory) {
        this.lockFactory = lockFactory;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    private Method getMethod(ProceedingJoinPoint pjp) {
        return ReflectionUtils.findMethod(pjp);
    }

    @Around("execution(* *.*(..)) && @annotation(com.jk.base.concurrent.ConcurrencyControl)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        if (!enable) {
            return pjp.proceed();
        }
        if (logger.isDebugEnabled())
            logger.debug("Concurrency control match " + pjp.getSignature());
        return new ConcurrencyControlImpl(pjp).call();
    }

    private final class ConcurrencyControlImpl {

        private final ProceedingJoinPoint pjp;

        private final Object[] args;

        private final Method method;

        private final ConcurrencyControl concurrencyControl;

        private final Logger targetLogger;

        private final String lockKey;

        private final long timeToWait;

        private IInterProcessSemaphoreMutex semaphore;

        ConcurrencyControlImpl(ProceedingJoinPoint pjp) {
            this.pjp = pjp;
            this.args = pjp.getArgs();
            this.method = getMethod(pjp);
            this.concurrencyControl = this.method.getAnnotation(ConcurrencyControl.class);
            this.targetLogger = LoggerFactory.getLogger(pjp.getThis().getClass());
            this.lockKey = CONCURRENCY_CONTROL_LOCK_KEY + String.valueOf(getLockKey());
            this.timeToWait = ConcurrencyControl.Policy.REJECT == concurrencyControl.policy() ? 0 : concurrencyControl.expiredTime();
            if (null != lockFactory) {
                // 锁超时时间和请求锁超时时间设置一致
                // 平均来讲，这样并没有什么特别的好处，大概能方便定时和实现
                long lockTimeout = Math.max(3, concurrencyControl.expiredTime());
                this.semaphore = lockFactory.newSemaphore(lockKey, lockTimeout, TimeUnit.SECONDS);
            }
        }

        private Serializable getLockKey() {
            Serializable key;
            String _key = concurrencyControl.cacheKey();
            if (!Strings.isNullOrEmpty(_key)) {
                key = _key;
            } else {
                IConcurrencyControlKeyProvider provider = applicationContext.getBean(concurrencyControl.keyProvider());
                key = provider.provide(new ConcurrencyControlKeyGenerationEvent(pjp, pjp.getSignature().getName(), args));
            }
            return key;
        }

        private boolean lock() throws Exception {
            return null == semaphore || semaphore.acquire(timeToWait, TimeUnit.SECONDS);
        }

        private void release() throws Exception {
            if (null != semaphore) {
                if (semaphore.isAcquiredInThisProcess())
                    semaphore.release();
            }
        }

        private Serializable onReject() {
            IConcurrencyControlRejectMessageProvider provider = applicationContext.getBean(concurrencyControl.rejectProvider());
            return provider.provide(new ConcurrencyControlRejectionEvent(pjp, pjp.getSignature().getName(), args));
        }

        Object call() throws Throwable {

            if (targetLogger.isDebugEnabled())
                targetLogger.debug("Concurrency control key - [" + lockKey + "] - " + pjp.getSignature() + "");

            // 先尝试加锁
            try {
                if (lock()) {
                    if (targetLogger.isDebugEnabled())
                        targetLogger.debug("Lock " + lockKey + " successful.");
                    return pjp.proceed();
                } else {
                    // 需要进行并发控制时直接拒绝
                    return onReject();
                }
            } finally {
                if (targetLogger.isDebugEnabled()) {
                    targetLogger.debug("Release " + lockKey);
                }
                release();
            }
        }
    }
}
