package com.bk.karam.constant;

import okhttp3.MediaType;

import java.util.Date;

/**
 * @author daichangbo
 * @date 2019-10-29 20:19
 * 基础数据
 */
public final class BaseConstant {

     private BaseConstant () {
          throw new AssertionError ( "can not instances" );
     }

     public static final String SYSTEM = "system";

     public static final Date CREATE_TIME = new Date ();

     public static final String DEFAULT_SECOND = "yyyy-MM-dd HH:mm:ss";
     /**
      * yyyy-MM-dd
      */
     public static final String DEFAULT = "yyyy-MM-dd";
     /**
      * yyyyMMdd
      */
     public static final String  SHORT = "yyyyMMdd";
     /**
      * yyyyMMddHHmmss
      */
     public static final String  SHORT_SECOND   = "yyyyMMddHHmmss";
     /**
      * yyyy年MM月dd日
      */
     public static final String  ZH_TO_DAY  = "yyyy年MM月dd日";

     /**
      * 请求方式
      * application/json
      */
     public static final MediaType APPLICATION_JSON_UTF8_VALUE = MediaType.parse("application/json; charset=utf-8");

     /**
      * 请求方式
      * application/xml
      */
     public static final MediaType APPLICATION_XML_VALUE = MediaType.parse("application/xml");
     /**
      * 请求地址
      */
     public static final String REQUEST_URL = "request url : {}";

     /**
      * 请求头
      */
     public static final String REQUEST_HEADER = "request header：{}";

     /**
      * 请求参数
      */
     public static final String REQUEST_PARAMETER = "The parameter ：{}";

     /**
      * 默认时间
      */
     public static final long DEFAULT_TIME = 3000;

     /**
      * 请求参数
      */
     public static final String REQUEST_PARAMETERS = "The parameter is empty：{}";

     /**
      * 必填字段为空
      */
     public static final String REQUIRED_PARAMETER = "The required field is blank";

     public static final String SESSION_ID = "sessionID is null";

     public static final String USER_INFO = "Please re-login" ;

     public static final String APP_KEY = "repair-web";

     public static final boolean ISONLINEWITHMULTIPLEEND = false;

     public static final String PASSWORD_REGEX = "^(?![0-9]*$)(?![a-zA-Z]*$)[a-zA-Z0-9]{6,16}$";

     public static final String KEY_NULL = "key为空" ;

     public static final String VALUE_NULL = "value 缓存数据为空" ;

     public static final String REDIS_CONNECT_ERROR = "redis contect link is null" ;

     public static final String REDIS_CLOSE_ERROR = "closeJedis is error" ;
}
