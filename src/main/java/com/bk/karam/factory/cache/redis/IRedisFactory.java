package com.bk.karam.factory.cache.redis;

import redis.clients.jedis.JedisPool;

/**
 * dcb
 */
@FunctionalInterface
public interface IRedisFactory {

    public JedisPool getJedisFactory () ;
}
