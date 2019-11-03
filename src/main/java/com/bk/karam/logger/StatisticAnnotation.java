package com.bk.karam.logger;

import java.lang.annotation.*;

/**
 * @autor daichangbo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface StatisticAnnotation {

    /**
     * 系统默认接口响应时间为5s
     * @return
     */
    long useTime () default  5000;
}
