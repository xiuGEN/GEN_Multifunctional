package com.xiuGEN.mapper;

import com.xiuGEN.pojo.Capacity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CapacityMapper {
    public Capacity selCapacityByUid(int uid);
    public int updateCapacityByUid(Capacity capacity);
    public int insertCapacityByUid(Capacity capacity);
}
