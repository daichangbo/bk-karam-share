package com.bk.karam.lock.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @autor
 */
public class MyZooKeeperClientFactoryBean implements FactoryBean<MyZooKeeperClient>, InitializingBean, DisposableBean {

    private String connectionString;

    private Integer sessionTimeout;

    private Integer maxRetries;

    private String ephemeralZNodeNamePrefix;

    private MyZooKeeperClient client;

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public void setEphemeralZNodeNamePrefix(String ephemeralZNodeNamePrefix) {
        this.ephemeralZNodeNamePrefix = ephemeralZNodeNamePrefix;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //操作失败重试机制 1000毫秒间隔 重试3次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(sessionTimeout, maxRetries);
        //创建Curator客户端
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
        //开始
        curatorFramework.start();

        this.client = new MyZooKeeperClientImpl(ephemeralZNodeNamePrefix, curatorFramework);
    }

    @Override
    public MyZooKeeperClient getObject() throws Exception {
        return this.client;
    }

    @Override
    public Class<MyZooKeeperClient> getObjectType() {
        return MyZooKeeperClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        client.close();
    }
}
