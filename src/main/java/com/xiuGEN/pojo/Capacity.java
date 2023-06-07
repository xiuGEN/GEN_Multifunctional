package com.xiuGEN.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Capacity {
    private Integer cid;
    private Integer ownerId;
    private String capacitySize;
}
