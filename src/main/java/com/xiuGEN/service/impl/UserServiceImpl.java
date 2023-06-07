package com.xiuGEN.service.impl;

import com.xiuGEN.mapper.CapacityMapper;
import com.xiuGEN.mapper.FileInfoMapper;
import com.xiuGEN.mapper.RoleUserMapper;
import com.xiuGEN.mapper.UserMapper;
import com.xiuGEN.pojo.*;
import com.xiuGEN.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CapacityMapper capacityMapper;
    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private RoleUserMapper roleUserMapper;

    @Override
    public User login(String username, String password) {
        return userMapper.selUserByLogin(username,password);
    }
    @Override
    public User login(String username) {
        return userMapper.selUserByUname(username);
    }

    @Override
    public User checkUserByEmail(String email) {
        return userMapper.selUserByEmail(email);
    }

    @Override
    public int addUser(User user) {
        return userMapper.insUser(user);
    }

    @Override
    public User existCheck(String existName, String existValue) {
        return userMapper.selExist(existName,existValue);
    }

    @Override
    public int changeUserInfo(User user) {
        return userMapper.updUser(user);
    }

    @Override
    public Capacity getCapacity(int uid) {
        return capacityMapper.selCapacityByUid(uid);
    }

    @Override
    public List<User> getUsersByids(String uids) {
        return userMapper.selUserByuids(uids);
    }

    @Override
    public List<User> getAllUsers(){
        return  userMapper.selUsers();
    }

    @Override
    public int saveCapacity(Capacity capacity){
       return capacityMapper.insertCapacityByUid(capacity);
    }

    @Override
    public int changeCapacity(Capacity capacity){
        return capacityMapper.updateCapacityByUid(capacity);
    }

    @Override
    public int saveRole(Roleuser roleuser) {
        return roleUserMapper.insertRoleUser(roleuser);
    }

    @Override
    public List<Role> getRoleById(Integer userid){
        return roleUserMapper.selRole(userid);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleUserMapper.selAllRoles();
    }

    @Override
    public int delRolesById(Integer uid) {
        return roleUserMapper.delUserRole(uid);
    }

}
