package com.bk.karam.concurrent;

import java.io.Serializable;

/**
 * @autor
 */
public interface IConcurrencyControlKeyProvider {

    /**
     * 根据方法参数，产生key
     *
     * @param event 缓存key生成事件。内含捕获的方法名和参数。
     * @return 用于缓存的key
     */
    Serializable provide ( ConcurrencyControlKeyGenerationEvent event );
}
