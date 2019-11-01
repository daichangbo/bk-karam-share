package com.bk.karam.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author daichangbo
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ResultsBase<T> extends IResultBase implements Serializable {

    private static final long serialVersionUID = -7769002552982569958L;

    /**
     * 用于构造返回结果集
     */
    private List<T> obj;

    /**
     * limit
     */
    private int limit;

    /**
     * start
     */
    private int start;

    /**
     * count
     */
    private long count;

    /**
     * size
     */
    private int size;
}
