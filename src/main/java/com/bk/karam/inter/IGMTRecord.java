package com.bk.karam.inter;

import java.util.Date;

/**
 * @author daichangbo
 */
public interface IGMTRecord {

    String getCreator ();

    void setCreator ( String creator );

    Date getGmtCreated ();

    void setGmtCreated ( Date gmtCreated );

    String getModifier ();

    void setModifier ( String modifier );

    Date getGmtModified ();

    void setGmtModified ( Date gmtModified );
}
