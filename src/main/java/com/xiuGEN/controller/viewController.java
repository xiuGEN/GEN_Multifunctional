package com.xiuGEN.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.xiuGEN.context.SessionContext;
import com.xiuGEN.convert.UserToUserDto;
import com.xiuGEN.pojo.Capacity;
import com.xiuGEN.pojo.Roleuser;
import com.xiuGEN.pojo.User;
import com.xiuGEN.pojo.UserDto;
import com.xiuGEN.service.UserService;
import com.xiuGEN.utils.UUIDUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class viewController {
    @Autowired
    private UserService userService;

    @Value("${file.download}")
    private String downLocation;
    /*
    * 跳转页面
    * */
    @GetMapping("/login")
    public String toLogin(){ return "login";}

    @GetMapping("/register")
    public String toRegister(String id){
        if(id!=null){
            String sessionID=id.substring(32,(id.length()-64+32));
            SessionContext instance = SessionContext.getInstance();
            HttpSession session = instance.getSession(sessionID);
            User user = (User)session.getAttribute("user");
            instance.delSession(session);//设备号参数 默认值：1
            if(user==null){
                return "redirect:/errors404";
            }else{
                try{
                    //加密处理
                    user.setUpassword(DigestUtil.md5Hex(user.getUpassword()));
                    /*设置默认头像*/
                    user.setUpicture("static/headphone/4dd36dcbbc7b4d828525a2ae1920d5761.png");
                    /*保存数据库*/
                    userService.addUser(user);
                    User newUser = userService.existCheck("uname", user.getUname());
                    if(newUser ==null){
                        return "register";
                    }
                    /*用户授权*/
                    Roleuser roleuser = new Roleuser();
                    /*1.是admin   2是 user*/
                    roleuser.setRoleid(2);
                    roleuser.setUserid(newUser.getUid());
                    int i = userService.saveRole(roleuser);
                    if(i!=1){
                       return "register";
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    return "redirect:/errors404";
                }
                return "redirect:/login";
            }
        }else return "register";
    }
    @GetMapping("/forgot")
    public String toForgetpassoword(){return "forgot-password";}
    @GetMapping("/index")
    public String toIndex(){return "user-profile";}
    @GetMapping("/")
    public String toIndex2(){
        return "user-profile";
    }
    @GetMapping("/userprofile")
    public String toprofile(){return "user-profile";}
    @GetMapping("/uploadfile")
    public String toupload(){return "form-file-upload";}
    @GetMapping("/fileManager")
    public String tofileManager(){return "app-file-manager";}
    @GetMapping("/chat")
    public String toChat(){return "app-chat-box";}
    //resetpassword
    /*
    * 通过邮箱的地址访问,并且设置浏览器的cookies来判断改用户的合法性
    *
    * */
    @GetMapping("/resetpassword")
    public String toreset(HttpServletResponse response, String id){

        String sessionid= UUIDUtil.getContextFrom2RandomNumber(id);
        Cookie cookie = new Cookie("JSESSIONID",sessionid);
        response.addCookie(cookie);
        return "resetpassword";
    }
    /*
	* 跳转管理员页面
	* */
    @GetMapping("/manageuser")
    public String toManageUser(Model model){
        Subject subject = SecurityUtils.getSubject();
        try{
            subject.checkRole("admin");
        }catch (AuthorizationException auth){
            return "error/4xx";
        }
        List<User> users=userService.getAllUsers();
        List<UserDto> userDtos = new ArrayList<>();
        for(User user:users){
            UserDto userDto = UserToUserDto.INSTANCE.UserToUserDto(user);
            Capacity capacity = userService.getCapacity(userDto.getUid());
            if(capacity !=null){
                String capacitySize = capacity.getCapacitySize();
                Double aDouble = Double.valueOf(capacitySize);
                double v =aDouble.doubleValue() / (1024 * 1024 * 1024);
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                String format = decimalFormat.format(v);
                Double aDouble1 = Double.valueOf(format);
                userDto.setSize(aDouble1);
            }else {
                userDto.setSize(0.00);
            }
            userDtos.add(userDto);
        }
        model.addAttribute("users",userDtos);
        return "manageuser";
    }

    @GetMapping("/formTextEditor")
    public String getFormTextEditor(){
       /* return "form-text-editor";*/
        return "app-emailbox";
    }
   /* @GetMapping("/mailbox")
    public String getMailbox(){
        *//* return "form-text-editor";*//*
        return "mailbox";
    }
    @GetMapping("/mailbox-compose")
    public String getMailboxCompose(){
        *//* return "form-text-editor";*//*
        return "mailbox-compose";
    }
    @GetMapping("/mailbox-message")
    public String getMailboxMessage(){
        *//* return "form-text-editor";*//*
        return "mailbox-message";
    }*/

}

