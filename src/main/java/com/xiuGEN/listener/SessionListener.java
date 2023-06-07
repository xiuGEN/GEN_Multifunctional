package com.xiuGEN.listener;

import com.xiuGEN.context.SessionContext;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
          
        private SessionContext myc = SessionContext.getInstance();
          
        public void sessionCreated(HttpSessionEvent httpSessionEvent) {
            HttpSession session = httpSessionEvent.getSession();
            myc.addSession(session);  
        }  
      
        public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {  
            HttpSession session = httpSessionEvent.getSession();  
            myc.delSession(session);  
        }  
      
    }