package com.bk.karam.factory.email;

import com.sun.mail.util.MailSSLSocketFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


/**
 * @author daichangbo
 */
@Data
@Slf4j
public class EmailFactoryBean implements FactoryBean<EmailClient> {

    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    /**
     * 发送邮箱
     */
    private String sendMailBox;

    /**
     * 邮箱密码
     */
    private String password;

    /**
     * 收件邮箱
     */
    private String receivingMailbox;

    /**
     * 1：或空使用谷歌邮件服务器
     * 2：使用qq邮箱发送
     */
    private String sendType;

    /**
     * 是否有附件
     */
    private boolean isAnnex = false;

    private String sendUserName;

    private Message message;

    private Transport transport ;


    public void init() {
        try {
            getObject();
        } catch (Exception e) {
            log.info("init EmailClient is error", e);
        }
    }

    @Override
    public EmailClient getObject() throws Exception {

        // Get a Properties object
        String[] emails = null;
        Properties props = new Properties();
        // -- Create a new message --
        Session session = null;
        if (null == message) {
            synchronized (EmailClient.class) {

                if (StringUtils.isNotEmpty(sendType) && "2".equals(sendType)) {
                    //选择ssl方式
                    gmailssl(props);

                } else {
                    qqemail(props);
                }
                session = Session.getDefaultInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(sendMailBox, password);
                            }
                        });
                message = new MimeMessage(session);
                if (transport == null) {
                    transport = session.getTransport();
                    transport.connect(sendMailBox, password);
                }
            }
        }
        return new EmailClientService(message, sendMailBox,transport);
    }

    @Override
    public Class<?> getObjectType() {
        return EmailClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * gamil ssl方式
     *
     * @param props
     */
    private void gmailssl(Properties props) {
        props.put("mail.debug", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
    }

    private void qqemail(Properties prop) {
        //协议
        prop.setProperty("mail.transport.protocol", "smtp");
        //服务器
        prop.setProperty("mail.smtp.host", "smtp.exmail.qq.com");
        //端口
        prop.setProperty("mail.smtp.port", "465");
        //使用smtp身份验证
        prop.setProperty("mail.smtp.auth", "true");
        //使用SSL，企业邮箱必需！
        //开启安全协议
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);
    }
}
