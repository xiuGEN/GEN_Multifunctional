package com.xiuGEN.service.impl;

import com.github.pagehelper.PageHelper;
import com.xiuGEN.mapper.EventMapper;
import com.xiuGEN.mapper.LoggingEventExceptionMapper;
import com.xiuGEN.pojo.LoggingEvent;
import com.xiuGEN.pojo.LoggingEventException;
import com.xiuGEN.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    @Autowired
    EventMapper eventMapper;
    @Autowired
    LoggingEventExceptionMapper loggingEventExceptionMapper;

    @Override
    public List<LoggingEvent> getAllEvent() {
        return eventMapper.selectEventAll();
    }

    @Override
    public List<LoggingEvent> getEventlimit(int size) {
        return eventMapper.selectEventlimit(size);
    }

    @Override
    public LoggingEvent getLoggingEventById(Long eventid) {
        return eventMapper.selectEventsByid(eventid);
    }

    @Override
    public List<LoggingEvent> getPortEvent(Integer pageSize, Integer pageNum){
        PageHelper.startPage(pageNum,pageSize);
        List<LoggingEvent> loggingEvents = eventMapper.selectEventAll();
        return loggingEvents;
    }

    @Override
    public List<LoggingEventException> getloggingEventExceptions(Long eventid) {
        return loggingEventExceptionMapper.selLoggingException(eventid);
    }
}
