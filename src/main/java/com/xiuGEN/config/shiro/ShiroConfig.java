package com.xiuGEN.config.shiro;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import cn.hutool.core.codec.Base64;
import com.xiuGEN.fliter.UserEffectFilter;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.InvalidRequestFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class ShiroConfig {


    //shiroFilterFactoryBean
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager")DefaultWebSecurityManager securityManager){
            CustomShiroFilterFactoryBean bean = new CustomShiroFilterFactoryBean();
            bean.setSecurityManager(securityManager);
            Map<String,String> filters = new LinkedHashMap<>();
            /*
            必加的条件,如果不对其进行放行着会传入两个相应,会登录两次才会成功登录
            /*会把login也拦截了,所以要登录了两次
            */
            Map<String, Filter> customfilters = new LinkedHashMap<>();
            // 判断用户是否有效
            customfilters.put("checkUser", userEffectFilter());
            bean.setFilters(customfilters);
            filters.put("/static/**","anon");
            //验证码
            filters.put("/sendVerificationCode","anon");
            filters.put("/verificationCode","anon");
            filters.put("/loginUrl","anon");

            //放通第三方登入支付宝
            filters.put("/alipay/callbackurl","anon");
            filters.put("/alipay/login","anon");
            //放通第三方登入微信
            filters.put("/wechat/callbackurl","anon");
            filters.put("/wechat/login","anon");
            //放通登录,注册,忘记密码请求
            filters.put("/login","anon");
            filters.put("/register","anon");
            filters.put("/forgot","anon");
            filters.put("/resetpassword","anon");
            filters.put("/test","anon");
            filters.put("/exist/*","anon");
            //放通静态资源
            //放通error资源
            filters.put("/error/**","anon");
            /*放通webscoket*/
            filters.put("/webSocket/*","anon");
            //对所有请求拦截
            filters.put("/logout","logout");
            filters.put("/**","user,checkUser");
        bean.setFilterChainDefinitionMap(filters);
            bean.setLoginUrl("/login");
            return bean;
    }

    private Filter userEffectFilter() {
        UserEffectFilter userEffectFilter = new UserEffectFilter();
        return userEffectFilter;
    }

    //DefaultWebSecurityManager
    @Bean
    public DefaultWebSecurityManager securityManager(@Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager securityManager =new DefaultWebSecurityManager();
        securityManager.setRememberMeManager(cookieRememberMeManager());
        securityManager.setRealm(userRealm);
        return securityManager;
    }
    //创建realm
    @Bean
    public UserRealm userRealm(){
        return new UserRealm();
    }


    // 实现记住我，所需要的配置
    @Bean
    public SimpleCookie simpleCookie() {
        // 这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setHttpOnly(true);
        // 记住我cookie生效时间一个月，单位秒
        simpleCookie.setMaxAge(60 * 60 * 24 * 3);
        return simpleCookie;
    }

    // 实现记住我，所需要的配置
    @Bean
    public CookieRememberMeManager cookieRememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        /*设置相同的密钥不然每次解密不一致*/
        cookieRememberMeManager.setCipherKey(Base64.decode("6ZmI6I2j3Y+R1aSn5BOlAA=="));
        cookieRememberMeManager.setCookie(simpleCookie());
        return cookieRememberMeManager;
    }

    @Bean
    public InvalidRequestFilter invalidRequestFilter(){
        InvalidRequestFilter invalidRequestFilter = new InvalidRequestFilter();
        invalidRequestFilter.setBlockNonAscii(false);
        return invalidRequestFilter;
    }



  /*  *//**
     * cookie对象;
     * @return
     *//*
    @Bean
    public SimpleCookie rememberMeCookie(){
        System.out.println("ShiroConfiguration.rememberMeCookie()");
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
        simpleCookie.setMaxAge(259200);
        return simpleCookie;
    }


    *//**
     * cookie管理对象;
     * @return
     *//*
    @Bean
    public CookieRememberMeManager rememberMeManager(@Qualifier("rememberMeCookie") SimpleCookie simpleCookie){
        System.out.println("ShiroConfiguration.rememberMeManager()");
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(simpleCookie);
        return cookieRememberMeManager;
    }*/





    //将shiro和thymeleaf中的整合配置交给spring管理
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }










}
