package com.xiuGEN.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiuGEN.pojo.LoggingEvent;
import com.xiuGEN.pojo.LoggingEventException;
import com.xiuGEN.service.EventService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/logging")
public class LoggingController {
    @Value("${logging.initCount}")
    String count;
    @Autowired
    EventService eventService;

    @RequestMapping("/showAllEvent")
    public String showEvents(Model model,Integer flag){
        Subject subject = SecurityUtils.getSubject();
        try{
            subject.checkRole("admin");
        }catch (AuthorizationException auth){
            return "error/4xx";
        }
        List<LoggingEvent> allEvent = null;
        if(flag!= null && flag == 0)
            allEvent = eventService.getAllEvent();
        else
            allEvent = eventService.getEventlimit(Integer.valueOf(count));
        for(LoggingEvent loggingEvent:allEvent){
            Long timestmp = loggingEvent.getTimestmp();
            SimpleDateFormat simpleDateFormat  =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(timestmp);
            loggingEvent.setLoggingTime(format);
        }
        model.addAttribute("loggings",allEvent);
        return "managelogging";
    }

    @RequestMapping("/detailInfo")
    @ResponseBody
    public String getLoggingEvent(Long eventid){
        JSONObject jsonObject = new JSONObject();
        LoggingEvent loggingEventById = eventService.getLoggingEventById(eventid);
        if(loggingEventById ==null){
            jsonObject.put("status","invalid");
        }else{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(loggingEventById.getTimestmp());
            loggingEventById.setLoggingTime(format);
            jsonObject.put("status","success");
            jsonObject.put("event",loggingEventById);
        }
        return jsonObject.toString();
    }

    @RequestMapping("/errorInfo")
    @ResponseBody
    public String getErrorLoggingEvent(Long eventid){
        JSONObject jsonObject= new JSONObject();
        List<LoggingEventException> loggingEventExceptions = eventService.getloggingEventExceptions(eventid);
        if(loggingEventExceptions!=null){
            jsonObject.put("status","success");
            JSONArray jsonArray = new JSONArray();
            if(loggingEventExceptions.size()==0){
                LoggingEvent loggingEventById = eventService.getLoggingEventById(eventid);
                LoggingEventException loggingEventException = new LoggingEventException();
                loggingEventException.setI(1);
                loggingEventException.setTrace_line(loggingEventById.getFormatted_message());
                loggingEventExceptions.add(loggingEventException);
            }
                jsonArray.addAll(loggingEventExceptions);
                jsonObject.put("loggingEventExceptions",jsonArray);
        }else{
            jsonObject.put("status","invalid");
        }
        return jsonObject.toString();
    }

}
