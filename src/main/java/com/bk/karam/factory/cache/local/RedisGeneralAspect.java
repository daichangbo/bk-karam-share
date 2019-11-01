package com.bk.karam.factory.cache.local;

import com.bk.karam.factory.cache.redis.IRedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author daichangbo
 */
@Slf4j
@Aspect
@Component
public class RedisGeneralAspect {

    @Resource
    private IRedisClient iRedisClient;

    /**
     * 定义切入点，使用了 @redisServicePoint 的方法
     */
    @Pointcut("@annotation(com.jk.base.cache.local.RedisAnnotation)")
    public void redisServicePoint(){

    }

    @Around("redisServicePoint()")
    public Object redisPoint (ProceedingJoinPoint pdj) {

     try {
         Object obj = null;
         Class<?> classTarget = pdj.getTarget().getClass();
         Class<?>[] pts = ((MethodSignature)pdj.getSignature()).getParameterTypes();
         Method objMethod = classTarget.getDeclaredMethod(pdj.getSignature().getName(),pts);
         Class<?> returnType = objMethod.getReturnType();
         RedisAnnotation redisGeneralAnnotation = pdj.getTarget().getClass().getDeclaredMethod(pdj.getSignature().getName(),pts).getAnnotation(RedisAnnotation.class);
         if(redisGeneralAnnotation != null && redisGeneralAnnotation.read()){
             //先组装完整KEY 缓存注解的前缀key + 传参串
             StringBuilder sb = new StringBuilder();
             String key = redisGeneralAnnotation.key().toString();
             if (StringUtils.isEmpty(key)) {
                 //key值为空暂不抛异常
                 return null;
             }
             //进入缓存判断
             if (redisGeneralAnnotation.mode().equals(CacheMode.REDIS)) {
                 obj = this.obtainRedis(key,redisGeneralAnnotation,pdj);
             } else {
                 obj = obtainLocal(key,pdj);
             }
             return obj;
         }else{
             //方法没加缓存注解直接返回原查询方法
             return pdj.proceed();
         }
     } catch (Throwable e){
         log.info("RedisGeneralAspect is error",e);
     }
     return null;
    }

    private Object obtainRedis(String key, RedisAnnotation redisGeneralAnnotation, ProceedingJoinPoint pdj) {
        Object obj = null;
        try {
            if (!iRedisClient.exists(key)) {
                // Redis 中不存在，则从数据库中查找，并保存到 Redis
                obj = pdj.proceed();
                if (obj != null) {
                    //查询有数据存入缓存
                    if (redisGeneralAnnotation.type().equals(CacheType.LIST)) {
                        iRedisClient.setList(key, (List) obj, redisGeneralAnnotation.expiredTime());
                    } else if (redisGeneralAnnotation.type().equals(CacheType.OBJECT)) {
                        iRedisClient.setBean(key, obj, redisGeneralAnnotation.expiredTime());
                    } else if (redisGeneralAnnotation.type().equals(CacheType.MAP)) {
                        iRedisClient.setMap(key, (Map<String, Object>) obj, redisGeneralAnnotation.expiredTime());
                    } else {
                        iRedisClient.put(key, (String) obj, redisGeneralAnnotation.expiredTime());
                    }
                }
            } else {
                //从Redis中获取
                //查询有数据存入缓存
                if (redisGeneralAnnotation.type().equals(CacheType.LIST)) {
                    obj = iRedisClient.getList(key, Object.class);
                } else if (redisGeneralAnnotation.type().equals(CacheType.OBJECT)) {
                    obj = iRedisClient.getBean(key, Object.class);
                } else if (redisGeneralAnnotation.type().equals(CacheType.MAP)) {
                    obj = iRedisClient.getMap(key, Object.class);
                } else {
                    obj = iRedisClient.get(key);
                }
            }
        } catch (Throwable e) {
            log.info("obtainRedis is error", e);
        }
        return obj;
    }

    private Object obtainLocal(String key, ProceedingJoinPoint pdj) {
        Object obj = null;
        try {
            if (LocalCacheClient.exist(key)) {
                obj = LocalCacheClient.getLocalCache(key);
            } else {
                obj = pdj.proceed();
                if (obj != null) {
                    LocalCacheClient.putLocalCache(key, obj);
                }
            }
        } catch (Throwable e) {
            log.info("obtainLocal is error", e);
        }
        return obj;
    }
}
