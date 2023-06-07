package com.xiuGEN.service.websocket;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiuGEN.pojo.Chatrecord;
import com.xiuGEN.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/webSocket/{sid}")
@Component
public class WebSocketServer {
    private static MessageService chatService;
    @Autowired
    public void setMessageService(MessageService messageService){
        WebSocketServer.chatService = messageService;
    }
    private  int currentUserid;
    //记录当前在线连接数。AtomicInteger可实现线程安全的自增。
    private static AtomicInteger onlineNum = new AtomicInteger();
    //JUC包的线程安全ConcurrentHashMap，用来存放所有WebSocketServer对象Session。
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();
    //建立连接
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "sid") String sid) {
        currentUserid =Integer.valueOf(sid);
        Session issucces = sessionPools.put(sid, session);
        if(issucces ==null) addOnlineCount();
    }
    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "sid") String sid) {
        sessionPools.remove(sid);
        subOnlineCount();
    }
    //收到客户端信息
    @OnMessage
    public void onMessage(String messageStr) throws IOException {
        //json转对象
        Chatrecord chatrecord = JSON.parseObject(messageStr, Chatrecord.class);
        chatrecord.setFromuserid(currentUserid);
        String toUserid = String.valueOf(chatrecord.getTouserid());
        Session target = sessionPools.get(toUserid);
        if (target!=null) {
            try {
                //向目标用户发送信息
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message",chatrecord);
                sendMessage(target, jsonObject.toString());
                //将消息标记为已读
                chatrecord.setState(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            //将消息标记为未读
            chatrecord.setState(0);
        }
        /*将数据存入数据库中*/
        chatService.saveChatRecords(chatrecord);
    }
    //发生错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }
    //实际发送消息
    public void sendMessage(Session session, String message) throws IOException {
        if (session != null) {
            synchronized (session) {
                session.getBasicRemote().sendText(message);
            }
        }
    }
    //在线人数+1
    public static void addOnlineCount() {
        onlineNum.incrementAndGet();
    }
    //在线人数-1
    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }

}