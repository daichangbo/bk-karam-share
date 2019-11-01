package com.bk.karam.result;

/**
 * @author daichangbo
 */
public interface IResultBuilder<T> {

    /**
     * 返回码
     * @return
     */
    IResultBuilder<T> setReturnCode ( int returnCode );

    /**
     * 结果描述
     * @return
     */
    IResultBuilder<T> setReturnMsg ( String returnMsg );

    /**
     * 成功标识
     * @return
     */
    IResultBuilder<T> setSuccess ( boolean success );

    /**
     * 扩展字段
     * @return
     */
    IResultBuilder<T> setAttributes ( String attributes );

}
