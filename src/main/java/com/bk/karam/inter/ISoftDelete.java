package com.bk.karam.inter;

/**
 * @author daichangbo
 * @date 2019-05-09 20:09
 */
public interface ISoftDelete {

    /**
     * 软删除标记 - 未被删除
     */
    String IS_NOT_DELETED = "N";
    /**
     * 软删除标记 - 已删除
     */
    String HAS_BEEN_DELETED = "Y";

    /**
     * getter for is_deleted
     *
     * @return "N" if available, "Y" if deleted.
     */
    String getIsDeleted ();

    /**
     * setter for is_deleted
     *
     * @param isDeleted "N" if available, "Y" if deleted.
     */
    void setIsDeleted ( String isDeleted );
}
