package com.xiuGEN.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.alipay.api.*;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.xiuGEN.context.SessionContext;
import com.xiuGEN.pojo.Roleuser;
import com.xiuGEN.pojo.User;
import com.xiuGEN.service.UserService;
import com.xiuGEN.utils.UUIDUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: GEN_Multifunctional
 * @description:
 * @author: xiuGEN
 * @create: 2023-05-15 21:26
 **/
@Controller
@RequestMapping("/alipay")
public class AlipayController {
    @Value("${alipay.islogin}")
    String ISLOGIN;
    @Value("${alipay.gateway}")
    String GATEWAYURL;
    @Value("${alipay.applicationid}")
    String APPLICATIONID;
    @Value("${alipay.callbackurl}")
    String CALLBACKURL;
    @Autowired
    AlipayClient alipayClient;
    @Autowired
    UserService userService;
    private static Logger logger = LoggerFactory.getLogger(AlipayController.class);
    /**
     * @author: GEN
     * @date: 2023/5/22
     * @Param: null
     * @return 跳转到支付宝扫码登入页码
     */
    @RequestMapping("/login")
    public void aliLogin(HttpServletRequest request, HttpServletResponse response){
        String url = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm"; // 指定要请求的 URL
        //https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=商户的APPID&scope=auth_user&redirect_uri=ENCODED_URL&state=init
        HashMap<String, String> varMap = new HashMap<>();
        varMap.put("app_id",APPLICATIONID);
        varMap.put("scope","auth_user");
        varMap.put("redirect_uri",CALLBACKURL);
        String sessionid = request.getSession().getId();
        //用于判断是否需要合并账号
        if(request.getSession().getAttribute("user") != null)
            varMap.put("state",sessionid);
        try {
            response.sendRedirect(jointRequestUrl(url,varMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @author: GEN
     * @date: 2023/5/22
     * @Param: auth_code 用于换取token
     * @return 支付宝回调函数
     */
    @RequestMapping("/callbackurl")
    public void callBackUrl(String state,String auth_code,HttpServletResponse servletResponse) throws IOException {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(auth_code);
        request.setGrantType("authorization_code");
        try {
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
            AlipayUserInfoShareRequest requestUser = new AlipayUserInfoShareRequest();
            String token =oauthTokenResponse.getAccessToken();
            /* 获取步骤3中的token */;
            AlipayUserInfoShareResponse response = alipayClient.execute(requestUser, token);
            if (response.isSuccess()) {
                String openId = response.getOpenId();
                String avatar = response.getAvatar();
                String nickName = response.getNickName();
                User existUser = userService.existCheck("aliopenid", openId);
                //合并账号 通过传入的sessionid
                if(StringUtils.hasLength(state) && existUser == null){
                    HttpSession session = SessionContext.getInstance().getSession(state);
                    User mergeUser = (User)session.getAttribute("user");
                    mergeUser.setAliopenid(openId);
                    userService.changeUserInfo(mergeUser);
                    existUser=mergeUser;
                }
                if(existUser == null){
                    User user = new User();
                    //加密处理
                    String password = UUIDUtil.randomPwd(8);
                    user.setUpassword(DigestUtil.md5Hex(password));
                    /*设置默认头像*/
                    user.setUpicture(avatar);
                    //设置默认用户名
                    String username= nickName+"_"+System.currentTimeMillis();
                    user.setUname(username);
                    //设置uid
                    user.setAliopenid(openId);
                    //保存到数据库
                   userService.addUser(user);
                    User newUser = userService.existCheck("aliopenid", user.getAliopenid());
                    if(newUser !=null) {
                        /*用户授权*/
                        Roleuser roleuser = new Roleuser();
                        /*1.是admin   2是 user*/
                        roleuser.setRoleid(2);
                        roleuser.setUserid(newUser.getUid());
                        int i = userService.saveRole(roleuser);
                        if (i == 1) {
                            existUser.setUpassword(password);
                            existUser = newUser;
                        }
                    }
                }

                //创建登入用户
                    /*
                     * 用户登录
                     */
                    Subject subject = SecurityUtils.getSubject();

                    UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(existUser.getUname(), existUser.getUpassword());
                    usernamePasswordToken.setRememberMe(Boolean.parseBoolean("true"));
                    subject.login(usernamePasswordToken); //进行密码的判断，密码错误则抛出异常
                    try {
                        subject.checkRole("user");
                        servletResponse.sendRedirect("/index");
                        return;
                    }catch (AuthorizationException authorizationException){
                        logger.error("用户无角色登入" + authorizationException.getMessage());
                    }
                }else {
                logger.error("支付宝第三方登入调用失败");
                logger.error(response.getSubCode() + ":" + response.getSubMsg());
            }
        }catch (AlipayApiException alipayApiException){
            logger.error("支付宝第三方登入调用失败");
            logger.error(alipayApiException.getErrMsg());
        }
        servletResponse.sendRedirect("/register");
    }
    /**
     * @author: GEN
     * @date: 2023/5/22
     * @Param: null
     * @return  初始化支付宝请求基础配置
     */
    @Bean
    private AlipayClient initConfig() throws AlipayApiException {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(GATEWAYURL);
        alipayConfig.setAppId(APPLICATIONID);
        try {
            InputStream privateKey = this.getClass().getResourceAsStream("/aliconfig/application_alipay_privateKey.txt");
            InputStream publicKey = this.getClass().getResourceAsStream("/aliconfig/alipayPublicKey_RSA2.txt");
            byte[] privateBytes = privateKey.readAllBytes();
            byte[] publicBytes = publicKey.readAllBytes();
            alipayConfig.setAlipayPublicKey(new String(publicBytes));
            alipayConfig.setPrivateKey(new String(privateBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        alipayConfig.setFormat("json");
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
//构造client
        return new DefaultAlipayClient(alipayConfig);
    }
    /**
     * @author: GEN
     * @date: 2023/5/21
     * @Param: null
     * @return 拼接请求路径
     */
    private String jointRequestUrl(String url, Map<String,String> parameter){
        StringBuilder stringBuilder = new StringBuilder(url);
        stringBuilder.append("?");
        for (Map.Entry<String,String> entry:parameter.entrySet()){
            stringBuilder.append(entry.getKey()+"="+entry.getValue()+"&");
        }
        int lastIndexOf = stringBuilder.lastIndexOf("&");
        if (lastIndexOf!= -1 ){
            stringBuilder.deleteCharAt(lastIndexOf);
        }
        return stringBuilder.toString();
    }
    /**
     * @author: GEN
     * @date: 2023/5/21
     * @Param: null
     * @return 第三方用户登入
     */
}
