package com.bk.karam.concurrent;

/**
 * @autor
 */
public class ConcurrencyControlRejectionEvent extends ConcurrencyControlEvent {

    public ConcurrencyControlRejectionEvent(Object source, String name, Object[] args) {
        super(source, name, args);
    }

}
