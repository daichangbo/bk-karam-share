package com.bk.karam.factory.cache.annotation;

import java.io.Serializable;

public interface ICacheKeyProvider {

	/**
     * 根据方法参数，产生缓存使用的key。
     * 如果返回{@code null}，缓存步骤将会被忽略。
     *
     * @param event 事件，内含捕获方法名和参数。
     * @return 用于缓存的key, 如果返回{@code null}，缓存步骤将会被忽略。
     */
    Serializable generate ( CacheKeyGenerationEvent event );

}
