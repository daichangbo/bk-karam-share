package com.bk.karam.concurrent;

import java.io.Serializable;

/**
 * @autor
 */
public interface IConcurrencyControlRejectMessageProvider {
    /**
     * 当被并发控制拦截时，产生告警信息
     *
     * @param event 事件对象，内含捕获的方法名和参数。
     * @return 告警信息。应当与方法返回值类型兼容。
     */
    Serializable provide ( ConcurrencyControlRejectionEvent event );

}
