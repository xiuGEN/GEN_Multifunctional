package com.xiuGEN.config.webmvc;

import com.xiuGEN.listener.SessionListener;
import com.xiuGEN.utils.SendmailUtil;
import com.xiuGEN.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpSessionListener;
import java.nio.charset.StandardCharsets;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${headphoto.Handlerposition}")
    private String Handlerfilepath;
    @Value("${file.location}")
    private String uploadFile;
    @Autowired
    private JavaMailSenderImpl javaMailSender;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(UUIDUtil.isMessyCode(Handlerfilepath)|| UUIDUtil.isMessyCode(uploadFile)){
            Handlerfilepath = new String(Handlerfilepath.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
            uploadFile = new String(uploadFile.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
        }
        registry.addResourceHandler("/static/headphone/**").addResourceLocations(Handlerfilepath);
        registry.addResourceHandler("/uploadFile/**").addResourceLocations(uploadFile);
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");

    }
    

    /*
    *
    * 配置session监听器,管理全局session
    * */
    @Bean
    public ServletListenerRegistrationBean servletListenerRegistrationBean(){
        ServletListenerRegistrationBean listener  = new ServletListenerRegistrationBean();
        listener.setListener(new SessionListener());
        return listener;
    }

    /*
    * 将工具类交由spring管理
    * */
    @Bean
    public SendmailUtil sendmailUtil(){
        return new SendmailUtil(javaMailSender);
    }
}
