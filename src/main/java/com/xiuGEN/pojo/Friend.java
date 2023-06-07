package com.xiuGEN.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friend {
    private int fid;
    private int inviterid;
    private int invitedid;
    private Date time;
}
