package com.xiuGEN.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TypeMapper {
    public String selTypeBySuffix(String suffix);
}
