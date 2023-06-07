package com.xiuGEN.mapper;


import com.xiuGEN.pojo.LoggingEventException;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
/**
* @author GEN
* @description 针对表【logging_event_exception】的数据库操作Mapper
* @createDate 2022-07-10 13:55:11
* @Entity person.domain.LoggingEventException
*/
public interface LoggingEventExceptionMapper {
    public List<LoggingEventException> selLoggingException(Long eventid);

}
