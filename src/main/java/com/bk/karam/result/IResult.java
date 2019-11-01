package com.bk.karam.result;

import java.io.Serializable;

/**
 * @author daichangbo
 */
public interface IResult<T> extends IResultBuilder ,Serializable {

    /**
     * 返回结果
     * @return
     */
    IResult<T> setObj ( T obj );

}
