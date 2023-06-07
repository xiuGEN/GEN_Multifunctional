package com.xiuGEN.config.shiro;

import cn.hutool.crypto.digest.DigestUtil;
import com.xiuGEN.pojo.Role;
import com.xiuGEN.pojo.User;
import com.xiuGEN.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    /*认证
    *
    *       对从前端传入的值进行认证处理
    *
    *
    * */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {
        UsernamePasswordToken token =(UsernamePasswordToken)authenticationToken;
        User user = userService.login(token.getUsername());
        if(user==null){return null;}
        //通过 User user = (User) subject.getPrincipal(); //对认证时传递给的用户获取
        //已经向当前subject传递了user
        return new SimpleAuthenticationInfo(user,user.getUpassword(),"");
    }
    /*
    *
    * 密码进行验证
    *
    * */
    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        super.setCredentialsMatcher((token,info)->{
                UsernamePasswordToken token1 =(UsernamePasswordToken) token;
                String frontPassword=new String(token1.getPassword());
                //解密处理
                String md5password= DigestUtil.md5Hex(frontPassword);
                String sqldatapassword = (String)info.getCredentials();
                /*
                * 满足两个条件之一即可
                * 1.加密后的密码匹配相同则通过
                * 2.对已经加密的匹配相同则通过
                * */
                return md5password.equals(sqldatapassword) || sqldatapassword.equals(frontPassword);
        });
    }
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) throws AuthenticationException {
         //获取当前用户
        Subject subject = SecurityUtils.getSubject();  //subject是三大对象,获取当前用户对象
        User user = (User) subject.getPrincipal(); //对认证时传递给的用户获取
        SimpleAuthorizationInfo info =new SimpleAuthorizationInfo(); //创建授权类
        info.addStringPermission(user.getUname());//对从数据库得到的值进行授权
        List<Role> roles = userService.getRoleById(user.getUid());
        for(Role role:roles){
            info.addRole(role.getRolename());
        }
        return info;
    }
}
