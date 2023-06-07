package com.xiuGEN.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Role {
    private  int roleid;
    private String rolename;

    /*是否是用户自身权限*/
    private boolean flag;
}
