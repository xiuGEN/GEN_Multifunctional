package com.xiuGEN.mapper;

import com.xiuGEN.pojo.Role;
import com.xiuGEN.pojo.Roleuser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoleUserMapper {
    public int insertRoleUser(Roleuser roleuser);
    public List<Role> selRole(Integer id);
    public List<Role> selAllRoles();
    public int delUserRole(@Param("userid") Integer userid);
}
