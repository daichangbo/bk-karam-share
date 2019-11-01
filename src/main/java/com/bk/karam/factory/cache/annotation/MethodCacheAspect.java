package com.bk.karam.factory.cache.annotation;

import com.alibaba.fastjson.JSON;
import com.bk.karam.factory.cache.redis.IRedisClient;
import com.bk.karam.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
@Slf4j
@Aspect
public class MethodCacheAspect implements ApplicationContextAware {

	private static final String METHOD_CACHE_KEY = "_@@_$$METHOD_CACHE_KEY_";
	private final DefaultCacheValueHandler defaultCacheValueHandler = new DefaultCacheValueHandler();
	@Resource
	private IRedisClient iRedisClient;
	private ApplicationContext applicationContext;
	private boolean enable = true;

	public void setCacheClient(IRedisClient iRedisClient) {
		this.iRedisClient = iRedisClient;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Around("execution(* *.*(..)) && @annotation(com.jk.base.cache.annotation.MethodRedisCache)")
	public Object aroundMethod(ProceedingJoinPoint pjp) throws Throwable {
		if (!enable)
			return pjp.proceed();
		if (log.isDebugEnabled())
			log.debug("Method cache match " + pjp.getSignature());
		return new MethodCacheImpl(pjp).run();
	}

	private static class DefaultCacheValueHandler implements ICacheValueHandler {

		@Override
		public CacheAction handle(CacheValueHandlerEvent event) {
			Serializable key = event.getKey();
			Object result = event.getValue();
			MethodRedisCache methodCache = event.getMethodCache();
			if (null != methodCache && null != key) {
				if (methodCache.modify()) {
					return CacheAction.CLEAN;
				} else if (null != result && result instanceof Serializable) {
					return CacheAction.CACHE;
				}
			}
			return CacheAction.IGNORE;
		}
	}

	private class MethodCacheImpl {

		private final ProceedingJoinPoint pjp;

		private final Method target;

		private final Logger targetLogger;

		private final MethodRedisCache methodRedisCache;

		private final String key;

		private MethodCacheImpl(ProceedingJoinPoint pjp) {
			this.pjp = pjp;
			this.targetLogger = LoggerFactory.getLogger(pjp.getThis().getClass());
			this.target = ReflectionUtils.findMethod(pjp);
			this.methodRedisCache = this.target.getAnnotation(MethodRedisCache.class);
			this.key = METHOD_CACHE_KEY + String.valueOf(getCacheKey(pjp, methodRedisCache));
		}

		Object run() throws Throwable {
			if (targetLogger.isDebugEnabled())
				targetLogger.debug("Preparing cache method " + target.getName() + " for key " + key);
			if (null == key)
				return pjp.proceed();

			Object result;
			/*
		            先尝试读取方法缓存，如果能读到直接返回，否则将进入方法体执行,接下来这几个步骤由于不可能是原子性操作，
		            如果多个线程差不多同时进入这里，很可能会因为某些时间差导致多个线程同时获得执行机会。如果对执行权
		            要求严格的场合，需要配合其他切面完成对这块区域的保护
			 */
			result = methodRedisCache.ignoreCache() ? null : getCachedValue(key, target, targetLogger);
			if (null != result)
				return result;

			if (targetLogger.isDebugEnabled())
				targetLogger.debug("Cache missed, proceeding method " + target.getName());
			result = pjp.proceed();

			ICacheValueHandler valueHandler = getValueHandler(methodRedisCache);
			CacheAction action = valueHandler.handle(new CacheValueHandlerEvent(pjp, pjp.getSignature().getName(), key, result, methodRedisCache));
			if (Objects.equals(CacheAction.CLEAN, action)) {
				iRedisClient.delete(key);
			} else if (Objects.equals(CacheAction.CACHE, action) && null != result) {
			    if (result.getClass().isAssignableFrom(List.class)) {
                    iRedisClient.setList(key, (List)result,(int) methodRedisCache.expireTime());
                } else if (result.getClass().isAssignableFrom(String.class)) {
                    iRedisClient.put(key, JSON.toJSONString(result),(int) methodRedisCache.expireTime());
                } else {
			        iRedisClient.setBean(key,result,(int) methodRedisCache.expireTime());
                }
			}
			return result;
		}
	}

	/**
	 * 生成key值
	 * @param pjp
	 * @param methodRedisCache
	 * @return
	 */
	private Serializable getCacheKey(ProceedingJoinPoint pjp, MethodRedisCache methodRedisCache) {
		Object[] args = pjp.getArgs();
		Class<? extends ICacheKeyProvider> providerClass = methodRedisCache.cacheKeyProvider();
		ICacheKeyProvider provider = applicationContext.getBean(providerClass);
		String methodName = pjp.getSignature().getName();
		return provider.generate(new CacheKeyGenerationEvent(pjp, methodName, args));
	}

	/**
	 * 获取缓存值
	 * @param key
	 * @param method
	 * @param targetLogger
	 * @return
	 */
	@Nullable
	private Object getCachedValue(Serializable key, Method method, Logger targetLogger) {
		Class<?> classType = method.getReturnType();
        Object result = null;
		if (classType.isAssignableFrom(List.class)) {
            result = iRedisClient.getList((String) key,Object.class);
        } else if (classType.isAssignableFrom(Map.class)) {
            result = iRedisClient.getMap((String) key,Object.class);
        } else if (classType instanceof Object) {
            result = iRedisClient.getBean((String) key,classType);
        }else {
            result = iRedisClient.get((String) key);
        }
		Class<?> returnType = method.getReturnType();
		if (null != result && returnType.isAssignableFrom(result.getClass())) {
			if (targetLogger.isDebugEnabled())
				targetLogger.debug("Get cached data " + result);
			return result;
		}
		return null;
	}

	private ICacheValueHandler getValueHandler(MethodRedisCache methodRedisCache) {
		Class<? extends ICacheValueHandler> clazz = methodRedisCache.cacheValueHandler();
		if (Objects.equals(ICacheValueHandler.class, clazz)) {
			return defaultCacheValueHandler;
		} else {
			return applicationContext.getBean(clazz);
		}
	}
}

