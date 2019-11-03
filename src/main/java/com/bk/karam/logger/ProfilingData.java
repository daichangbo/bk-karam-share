package com.bk.karam.logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @autor daichangbo
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfilingData {

    /**
     * 日志记录时间
     */
    private String time;
    /**
     * 接口签名
     */
    private String signature;
    /**
     * 调用参数 JSON 字符串
     */
    private String arguments;
    /**
     * 接口用时
     */
    private long useTime;
    /**
     * 接口返回结果
     */
    @JsonIgnore
    private Object invokeResults;
    /**
     * 是否成功，{@code true}表示没有异常，{@code false}表示有异常
     */
    private boolean success;
    /**
     * 如果调用发生异常，这里将记录异常
     */
    private String exception;

    private String thread;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
