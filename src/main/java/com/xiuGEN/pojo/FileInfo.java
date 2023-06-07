package com.xiuGEN.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class
FileInfo {
    private int fid;
    private String fileName;
    private String suffix;
    private String fileLocation;
    private Integer ownerId;
    private String size;
    private String typeName;
    private Date insertTime;
}
