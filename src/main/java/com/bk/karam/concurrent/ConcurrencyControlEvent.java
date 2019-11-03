package com.bk.karam.concurrent;

import java.util.EventObject;

/**
 * @autor
 */
public abstract class ConcurrencyControlEvent extends EventObject {

    /**
     * 捕获的方法名
     */
    private final String name;

    /**
     * 调用方法的参数列表
     */
    private final Object[] args;

    public ConcurrencyControlEvent(Object source, String name, Object[] args) {
        super(source);
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public Object[] getArgs() {
        return args;
    }
}
