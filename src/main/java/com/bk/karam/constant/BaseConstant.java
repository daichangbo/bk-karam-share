package com.bk.karam.constant;

import java.util.Date;

/**
 * @author daichangbo
 * @date 2019-10-29 20:19
 * 基础数据
 */
public interface BaseConstant {

     String SYSTEM = "system";

     Date CREATE_TIME = new Date ();

     String DEFAULT_SECOND = "yyyy-MM-dd HH:mm:ss";
     /**
      * yyyy-MM-dd
      */
     String DEFAULT = "yyyy-MM-dd";
     /**
      * yyyyMMdd
      */
     String  SHORT = "yyyyMMdd";
     /**
      * yyyyMMddHHmmss
      */
     String  SHORT_SECOND   = "yyyyMMddHHmmss";
     /**
      * yyyy年MM月dd日
      */
     String  ZH_TO_DAY  = "yyyy年MM月dd日";
}
