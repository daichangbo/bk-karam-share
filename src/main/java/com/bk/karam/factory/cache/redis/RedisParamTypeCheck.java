package com.bk.karam.factory.cache.redis;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

/**
 * @author daichangbo
 * @date 2019-11-01 12:07
 */
public class RedisParamTypeCheck {

    public Boolean isBaseType (Class<?> beanClass) {
        if (beanClass.equals(java.lang.Integer.class) ||
                beanClass.equals(java.lang.Byte.class) ||
                beanClass.equals(java.lang.Long.class) ||
                beanClass.equals(java.lang.Double.class) ||
                beanClass.equals(java.lang.Float.class) ||
                beanClass.equals(java.lang.Character.class) ||
                beanClass.equals(java.lang.Short.class) ||
                beanClass.equals(java.lang.Boolean.class) ||
                beanClass.equals(java.lang.String.class)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Object getPackingType ( Jedis jedis, String key, Class<?> beanClass) {
        String value = jedis.get(key) ;
        try {
            if (StringUtils.isEmpty(value) || "null".equals(value)) {
                return null;
            }
            if (beanClass.equals(java.lang.Integer.class))
                return Integer.valueOf(value.trim());
            if (beanClass.equals(java.lang.Long.class))
                return Long.valueOf(value.trim());
            if (beanClass.equals(java.lang.Boolean.class))
                return Boolean.valueOf(value.trim());
            if (beanClass.equals(java.lang.Short.class))
                return Short.valueOf(value.trim());
            if (beanClass.equals(java.lang.Double.class))
                return Double.valueOf(value.trim());
            if (beanClass.equals(java.lang.Float.class))
                return Float.valueOf(value.trim());
        } catch (Exception e) {
            return value;
        }
        return value;
    }

    public Object getPackingType (String value,Class<?> beanClass) {
        if (beanClass.equals(java.lang.Integer.class))
            return Integer.valueOf(value);
        if (beanClass.equals(java.lang.Long.class))
            return Long.valueOf(value);
        if (beanClass.equals(java.lang.Boolean.class))
            return Boolean.valueOf(value);
        if (beanClass.equals(java.lang.Short.class))
            return Short.valueOf(value);
        if (beanClass.equals(java.lang.Double.class))
            return Double.valueOf(value);
        if (beanClass.equals(java.lang.Float.class))
            return Float.valueOf(value);
        return value;
    }
}
