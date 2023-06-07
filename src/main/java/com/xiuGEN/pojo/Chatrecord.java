package com.xiuGEN.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chatrecord {
    private int rid;
    private int fromuserid;
    private int touserid;
    private String message;
    private int state;//0为未读 1为已读
    private Date sendtime;
}
