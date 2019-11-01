package com.bk.karam.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author daichangbo
 * 构建返回结果集
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ResultBase<T> extends IResultBase implements Serializable {

    private static final long serialVersionUID = -8826523040149110654L;

    /**
     * 返回结果
     */
    private T obj;


}
