package com.bk.karam.factory.email;

import com.bk.karam.dto.EmailDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import java.util.Date;

/**
 * @author daichangbo
 */
@Slf4j
public class EmailClientService implements EmailClient {

    private Message message ;

    private String sendMailBox;

    private Transport transport;

    public EmailClientService (Message msg) {
        this.message = msg;
    }

    public EmailClientService (Message msg,String sendMailBox) {
        this.message = msg;
        this.sendMailBox = sendMailBox;
    }

    public EmailClientService (Message msg,String sendMailBox,Transport transport) {
        this.message = msg;
        this.sendMailBox = sendMailBox;
        this.transport = transport;
    }

    @Override
    public void sendEmail( EmailDTO emailDTO) throws Exception {

        try {
            if (StringUtils.isNotEmpty(emailDTO.getSendType()) && emailDTO.getSendType().equals("2")) {
                sendGamil(emailDTO);
            } else {
                sendQQ(emailDTO);
            }
        } catch (Exception e) {
            log.error("sendEmail is error",e);
        }
        log.info("sendEmail is end ~***~");
    }

       private void sendGamil (EmailDTO emailDTO) throws Exception {
           iniMessage(emailDTO);
           message.setText(emailDTO.getContent());
           try {
               transport.send(message, message.getAllRecipients());
           } catch (MessagingException e) {
               log.error("sendGamil is error");
           } finally {
               transport.close();
           }
       }

    private void sendQQ (EmailDTO emailDTO) throws Exception {
        //发件人邮箱，显示的发件人(可以是任何内容)
        iniMessage(emailDTO);
        //设置内容
        message.setContent(emailDTO.getContent(),"text/html;charset=UTF-8");
        if (StringUtils.isNotEmpty(emailDTO.getSendContentType()) || "2".equals(emailDTO.getSendContentType())) {
            message.setText(emailDTO.getContent());
        }
        //发送
        try {
            transport.send(message, message.getAllRecipients());
        } catch (MessagingException e) {
            log.error("sendQQ is error");
        } finally {
            transport.close();
        }
    }

    private void iniMessage (EmailDTO emailDTO) throws Exception {
        message.setFrom(new InternetAddress(sendMailBox, emailDTO.getShowName()));
        //收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailDTO.getReceivingMailbox()));
        //设置主题
        message.setSubject(emailDTO.getTitle());
        message.setSentDate(new Date());
    }
}
