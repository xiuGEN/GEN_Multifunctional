package com.xiuGEN.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
* 发送方信息
* */
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageContent {
    private int id;
    private int senderId;
    private String senderName;
    private String senderPhoto;
    private String message;
    private String title;
    private String type;
    private Date sendtime;
}
