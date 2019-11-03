package com.bk.karam.concurrent;

import java.io.Serializable;

/**
 * @autor
 */
@Deprecated
public interface IConcurrencyControlValueProvider {

    /**
     * 产生用于缓存的值
     *
     * @param event 事件对象，包含捕获的方法名和方法参数
     * @return 用于缓存的值
     */
    Serializable provide ( ConcurrencyControlValueGenerationEvent event );
}
