package com.xiuGEN.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private int id;
    private String title;
    private String text;
    private String type;
    private int touserid;
    private int state;
    private int fromuserid;
    private Date time;
}
