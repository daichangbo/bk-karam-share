package com.bk.karam.result;

import java.io.Serializable;
import java.util.List;

/**
 * @author daichangbo
 */
public interface IResults<T> extends IResultBuilder<T> , Serializable {

    /**
     * 每页限制数
     * @param limit
     * @return
     */
    IResults<T> setLimit ( int limit );

    /**
     * 开始页数
     */
    IResults<T> setStart ( int start );

    /**
     * 总数
     * @param count
     * @return
     */
    IResults<T> setCount ( long count );

    /**
     * size
     * @param size
     * @return
     */
    IResults<T> setSize ( int size );

    /**
     * 结果集
     * @param obj
     * @return
     */
    IResults<T> setObj ( List<T> obj );
}
