package com.bk.karam.exception;

import lombok.Data;

/**
 * @author daichangbo
 * @date 2019-10-29 20:21
 * 基础异常封装
 */
@Data
public class BaseException extends Exception{

    private static final long serialVersionUID = 8540595639814874244L;

    private int code;

    private  String msg;

    public BaseException () {

    }

    public BaseException ( int code ) {
        super();
        this.code = code;
    }

    public BaseException ( String msg ) {
        super();
        this.msg = msg;
    }

    public BaseException ( int code ,String msg ) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public BaseException (String message,int code ,String msg) {
        super(message);
        this.code = code;
        this.msg = msg;
    }

    public BaseException (Throwable throwable,int code,String msg) {
        super(throwable);
        this.code = code;
        this.msg = msg;
    }
}
