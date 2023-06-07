package com.xiuGEN.mapper;

import com.xiuGEN.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    public List<User> selUsers();
    public User selUserByLogin(@Param("uname")String username,@Param("upassword")String userpassword);
    public User selUserByUname(@Param("uname")String username);
    public List<User> selUserByuids(@Param("uids") String  uids);
    public User selUserByEmail(@Param("uemail")String userEmail);
    public User  selExist(@Param("exist")String exist,@Param("existValue")String existValue);
    public int  insUser(User user);
    public int  updUser(User user);
    public List<User> selAllUser();

}
