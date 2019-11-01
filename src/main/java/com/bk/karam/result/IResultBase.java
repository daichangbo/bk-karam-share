package com.bk.karam.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author daichangbo
 */
@Data
public class IResultBase implements Serializable {

    /**
     * 返回码
     */
    private int returnCode ;

    /**
     * 返回ji
     */
    private String returnMsg;

    /**
     * 描述字段
     */
    private String attributes;

    /**
     * 成功标识
     */
    private boolean isSuccess;
}
