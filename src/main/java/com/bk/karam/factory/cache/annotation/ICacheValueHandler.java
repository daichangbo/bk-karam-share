package com.bk.karam.factory.cache.annotation;

public interface ICacheValueHandler {

	 /**
     * 返回的值会指示缓存系统如何处理方法执行结果
     *
     * @param event 事件执行参数
     * @return 缓存指令
     */
    CacheAction handle ( CacheValueHandlerEvent event );
}
