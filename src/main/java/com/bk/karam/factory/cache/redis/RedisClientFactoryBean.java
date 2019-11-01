package com.bk.karam.factory.cache.redis;

import com.bk.karam.factory.cache.redis.impl.RedisCacheClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis客户端bean加载
 * @Bean(value = "iRedisClient",initMethod = "init")
 *     public RedisClientFactoryBean cacheClientFactoryBean() {
 *         RedisClientFactoryBean redisClientFactoryBean = new RedisClientFactoryBean();
 *         redisClientFactoryBean.setHost("localhost");
 *         redisClientFactoryBean.setMaxIdle(20);
 *         redisClientFactoryBean.setMaxWaitMillis(100);
 *         redisClientFactoryBean.setPort(6379);
 *         redisClientFactoryBean.setTimeOut(10000);
 *         redisClientFactoryBean.setPassword(null);
 *         return redisClientFactoryBean;
 *     }
 *     也可以使用xml的方式实现注入 ........
 *
 */
@Slf4j
@Data
public class RedisClientFactoryBean implements InitializingBean, FactoryBean<IRedisClient> {

    private JedisPool jedisPool;

    private static int MAX_TOTAL = 100;

    private static int MAX_IDLE = 10;

    private static int TIME_OUT = 3000; //ms

    /**
     * 最大链接数,由于链接共用，不宜过大。（测试预发配置30，生产建议配置100）
     */
    private int maxTotal;

    /**
     * 最大空闲连接数
     */
    private int maxIdle ;

    /**
     * 连接接redis超时时间ms
     */
    private int timeOut;

    private String host;

    private int port;

    private long maxWaitMillis;

    private String password;

    private String userName;

    private boolean blockWhenExhausted;


    public void init () {
        try {
            getObject();
        } catch (Exception e) {
        }
    }

    @Override
    public IRedisClient getObject() throws Exception {
        jedisPool = new JedisPool();

        log.info("JedisPool注入成功！！");
        log.info("redis地址：" + host + ":" + port);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle != 0 ? maxIdle : MAX_IDLE);
        jedisPoolConfig.setMaxWaitMillis(maxIdle != 0 ? maxIdle : MAX_IDLE);
        // 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
        // 是否启用pool的jmx管理功能, 默认true
        jedisPoolConfig.setJmxEnabled(true);
        jedisPool = new JedisPool(jedisPoolConfig, host, port, timeOut, StringUtils.isEmpty(password) ? null : password);
        return getIRedisClient();
    }

    public IRedisClient getIRedisClient() {
        RedisCacheClient redisCacheClient = new RedisCacheClient ();
        redisCacheClient.setiRedisFactory(getRedisFactory());
        return redisCacheClient;
    }

    public IRedisFactory getRedisFactory() {
        return new IRedisFactory() {
            @Override
            public JedisPool getJedisFactory() {
                return jedisPool;
            }
        };
    }

    @Override
    public Class<?> getObjectType() {
        return IRedisClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(host)) {
            throw new IllegalArgumentException("host cannot be null.");
        }
        if (port == 0) {
            throw new IllegalArgumentException("port incorrect");
        }
    }
}
