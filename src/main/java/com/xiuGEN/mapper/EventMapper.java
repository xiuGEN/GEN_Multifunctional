package com.xiuGEN.mapper;


import com.xiuGEN.pojo.LoggingEvent;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EventMapper {
    public List<LoggingEvent>selectEventlimit(int size);
    public List<LoggingEvent> selectEventAll();
    public LoggingEvent selectEventsByid(Long eventid);
}
