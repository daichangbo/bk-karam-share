package com.bk.karam.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author daichangbo
 */
@Data
public class EmailDTO implements Serializable {

    private static final long serialVersionUID = 2543032413975837886L;

    /**
     * 发送邮箱
     */
    private String sendMailBox ;

    /**
     * 邮箱密码
     */
    private String password ;

    /**
     * 收件邮箱
     */
    private String receivingMailbox ;

    /**
     * 1：或空使用谷歌邮件服务器
     * 2：使用qq邮箱发送
     */
    private String sendType ;

    /**
     * 发送内容
     */
    private String content ;

    /**
     * 发送主题
     */
    private String title;

    /**
     * 验证码
     */
    private String validNo ;

    /**
     * 显示名称
     */
    private String showName;

    /**
     * 发送内容类型
     * 默认html
     * 2：文本
     */
    private String sendContentType;

    /**
     * 邮件类别
     * 1或空： 注册邮件
     * 2：密码重置
     */
    private Integer emailMode;


}
