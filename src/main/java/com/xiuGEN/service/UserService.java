package com.xiuGEN.service;

import com.xiuGEN.pojo.Capacity;
import com.xiuGEN.pojo.Role;
import com.xiuGEN.pojo.Roleuser;
import com.xiuGEN.pojo.User;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;

public interface UserService {
    public User login(String username,String password);
    public User login(String username);
    public User checkUserByEmail(String email);
    public int addUser(User user);
    public User existCheck(String existName, String existValue);
    public int changeUserInfo(User user);
    public Capacity getCapacity(int uid);
    public List<User> getUsersByids(String uids);
    public List<User> getAllUsers();
    public int saveCapacity(Capacity capacity);
    public int changeCapacity(Capacity capacity);
    /*添加用户角色*/
    public int saveRole(Roleuser roleuser);
    /*查询用户角色*/
    public List<Role> getRoleById(Integer userid);

    /*获取所有角色*/
    public List<Role> getAllRoles();

    /*删除角色*/
    public int delRolesById(Integer uid);

}
