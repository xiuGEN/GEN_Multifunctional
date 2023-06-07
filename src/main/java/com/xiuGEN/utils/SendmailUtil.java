package com.xiuGEN.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
public class SendmailUtil{
    private JavaMailSenderImpl javaMailSender;
    private String sendTo;
    public  SendmailUtil(JavaMailSenderImpl javaMailSender){
        this.javaMailSender=javaMailSender;
    }
    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }
    public void sendText(String title, String message, boolean isHtml,File image) throws Exception{
        MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMailMessage,true);
        helper.setSubject(title);
        helper.setText(message,isHtml);
        if(image!=null){
            String name =image.getName();
            helper.addAttachment(name,image);
        }
        helper.setTo(sendTo);
        helper.setFrom("870329165@qq.com");
        javaMailSender.send(mimeMailMessage);
    }
}
