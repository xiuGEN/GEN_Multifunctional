package com.xiuGEN.controller;

import cn.hutool.core.net.URLEncoder;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.xiuGEN.context.SessionContext;
import com.xiuGEN.pojo.Roleuser;
import com.xiuGEN.pojo.User;
import com.xiuGEN.service.UserService;
import com.xiuGEN.utils.UUIDUtil;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: GEN_Multifunctional
 * @description: 微信相关对接接口
 * @author: xiuGEN
 * @create: 2023-05-14 12:03
 **/
@Controller
@RequestMapping("/wechat")
public class WeChatController {
    @Value("${wechat.islogin}")
    String ISLOGIN;
    @Value("${wechat.appsecret}")
    String APPSECRET;
    @Value("${wechat.applicationid}")
    String APPLICATIONID;
    @Value("${wechat.callbackurl}")
    String CALLBACKURL;
    @Autowired
    UserService userService;
    private RestTemplate restTemplate = new RestTemplate();
    private static Logger logger = LoggerFactory.getLogger(WeChatController.class);
    /**
     * @author: GEN
     * @date: 2023/5/22
     * @Param: null
     * @return 跳转到支付宝扫码登入页码
     */
    @RequestMapping("/login")
    public void aliLogin(HttpServletRequest request, HttpServletResponse response){
        /*https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect */
        String url = "https://open.weixin.qq.com/connect/qrconnect"; // 指定要请求的 URL
        Map<String, String> varMap = new LinkedMap();
        varMap.put("appid",APPLICATIONID);
        varMap.put("scope","snsapi_login");
        String redirect_uri = new URLEncoder().encode(CALLBACKURL, Charset.forName("UTF-8"));
        varMap.put("redirect_uri",redirect_uri);
        varMap.put("response_type","code");
        String sessionid = request.getSession().getId();
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
    public void callBackUrl(String state,String code,HttpServletResponse servletResponse) throws IOException {
        if(StringUtils.hasLength(state) && StringUtils.hasLength(code)){
            /*https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code*/
            Map uriVariables = new HashMap();
            uriVariables.put("appid",APPLICATIONID);
            uriVariables.put("secret",APPSECRET);
            uriVariables.put("code",code);
            uriVariables.put("grant_type","authorization_code");
            String responeString = restTemplate.getForObject("https://api.weixin.qq.com/sns/oauth2/access_token", String.class, uriVariables);
            JSONObject tokenObj = JSONUtil.parseObj(responeString);
            String access_token =(String) tokenObj.get("access_token");
            String openid =(String) tokenObj.get("openid");
            if(StringUtils.hasLength(access_token) && StringUtils.hasLength(openid)){
                uriVariables.clear();
                uriVariables.put("access_token",access_token);
                uriVariables.put("openid",openid);
                String forObject = restTemplate.getForObject("https://api.weixin.qq.com/sns/userinfo", String.class, uriVariables);
                JSONObject jsonObject = JSONUtil.parseObj(forObject);
                jsonObject.get("");
            }
        }
       /* request.setCode(auth_code);
        request.setGrantType("authorization_code");
        try {
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
            AlipayUserInfoShareRequest requestUser = new AlipayUserInfoShareRequest();
            String token =oauthTokenResponse.getAccessToken();
            *//* 获取步骤3中的token *//*;
            AlipayUserInfoShareResponse response = alipayClient.execute(requestUser, token);
            if (response.isSuccess()) {
                String openId = response.getOpenId();
                String avatar = response.getAvatar();
                String nickName = response.getNickName();
                User existUser = userService.existCheck("aliopenid", openId);
                //合并账号
                if(StringUtils.hasLength(state)){
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
                    *//*设置默认头像*//*
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
                        *//*用户授权*//*
                        Roleuser roleuser = new Roleuser();
                        *//*1.是admin   2是 user*//*
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
                *//*
                 * 用户登录
                 *//*
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
        }*/
        servletResponse.sendRedirect("/register");
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
