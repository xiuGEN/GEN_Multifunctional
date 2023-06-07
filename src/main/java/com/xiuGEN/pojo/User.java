package com.xiuGEN.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User implements Serializable {
    private Integer uid;
    private String uname;
    private String urealname;
    private String upassword;
    private String uemail;
    private String uphone;
    private String uaddress;
    private String upicture;
    private String ubirthdate;
    private String oldpassword;
    private String aliopenid;
}
