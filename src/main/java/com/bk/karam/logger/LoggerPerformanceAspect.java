package com.bk.karam.logger;

import com.bk.karam.util.ReflectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import java.lang.reflect.Method;

/**
 * @autor daichangbo
 */
@Configuration
@Aspect
public class LoggerPerformanceAspect implements ApplicationContextAware {

    private boolean enable = true;

    private ApplicationContext applicationContext;

    public void setEnable(boolean enable) {
        this.enable = enable;
    }



    @Pointcut("execution(public * *.*(..))")
    private void methodInvoke() {
    }

    /**
     * 统计被 {@linkplain StatisticAnnotation StatisticAnnotation } 注解的方法的运行时间
     *
     * @param pjp join point
     * @return anything.
     * @throws Throwable if exceptions being throw.
     */
    @Around("methodInvoke() && @annotation(com.jk.base.logger.StatisticAnnotation)")
    public Object aroundInvoke(ProceedingJoinPoint pjp) throws Throwable {
        if (!enable) {
            return pjp.proceed();
        }
        return new StatisticImpl(pjp).run();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private class StatisticImpl {

        private final ProceedingJoinPoint pjp;

        private final  Method target;

        private final StatisticAnnotation statisticAnnotation;

        private StatisticImpl(ProceedingJoinPoint pjp) {
            this.pjp = pjp;
            this.target = ReflectionUtils.findMethod(pjp);
            this.statisticAnnotation = this.target.getAnnotation(StatisticAnnotation.class);
        }

        Object run() throws Throwable {
            return logAndInvoke(pjp,statisticAnnotation);
        }

    }

    private Object logAndInvoke(final ProceedingJoinPoint pjp,final StatisticAnnotation statisticAnnotation) throws Throwable {
        Object[] args = pjp.getArgs();
        long useTime = statisticAnnotation.useTime();
        return ProfilingUtils.invokeAndLog(new ProfilingUtils.Invoke() {
            @Override
            public Object invoke(Object[] args) throws Throwable {
                return pjp.proceed();
            }
        }, pjp.getSignature().toString(), args,useTime);
    }
}
