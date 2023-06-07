package com.xiuGEN.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Path {
    private String path;
    private LocalDateTime expirationTime;
    // getter/setter方法
}