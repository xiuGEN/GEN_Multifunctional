package com.xiuGEN.service;


import com.xiuGEN.pojo.LoggingEvent;
import com.xiuGEN.pojo.LoggingEventException;

import java.util.List;

public interface EventService {
    public List<LoggingEvent> getAllEvent();

    public List<LoggingEvent> getEventlimit(int size);
    public LoggingEvent getLoggingEventById(Long eventid);

    public List<LoggingEvent> getPortEvent(Integer pageSize,Integer pageNum);

    public List<LoggingEventException> getloggingEventExceptions(Long eventid);
}
