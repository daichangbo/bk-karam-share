package com.bk.karam.factory.cache.annotation;

import java.io.Serializable;
import java.util.EventObject;

public class CacheValueHandlerEvent extends EventObject {


    private static final long serialVersionUID = -6909523873222856945L;

    /**
     * 捕获的方法名
     */
    private final String name;

    /**
     * 缓存 key
     */
    private final Serializable key;

    /**
     * 调用方法的参数列表
     */
    private final Object value;

    /**
     * 配置项
     */
    private final MethodRedisCache methodRedisCache;

    /**
     * Constructs a prototypical Event.
     *
     * @param source      The object on which the Event initially occurred.
     * @param name
     * @param key
     * @param value
     */
    public CacheValueHandlerEvent(Object source,
                                  String name,
                                  Serializable key,
                                  Object value,
                                  MethodRedisCache methodRedisCache) {
        super(source);
        this.name = name;
        this.key = key;
        this.value = value;
        this.methodRedisCache = methodRedisCache;
    }

    public String getName() {
        return name;
    }

    public Serializable getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public MethodRedisCache getMethodCache() {
        return methodRedisCache;
    }


}
