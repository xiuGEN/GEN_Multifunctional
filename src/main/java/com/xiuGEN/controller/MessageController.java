package com.xiuGEN.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiuGEN.pojo.*;
import com.xiuGEN.service.MessageService;
import com.xiuGEN.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Value("${wesocket.url}")
    private String websocketip;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    /*
    * 获取未读信息
    *
    * */
    @PostMapping("/unreadMessage")
    public String getUnreadMessage(){
        Subject subject = SecurityUtils.getSubject();
        User current = (User) subject.getPrincipal();
        JSONArray jsonArray = new JSONArray();
        /*
        * 0代表得到未读信息
        * 查询各个用户的第一条信息
        * */
        List<Chatrecord> chatrecords = messageService.getMessages(current.getUid(), 0);
        for(Chatrecord chatrecord:chatrecords){
            //获取发送方信息
            User uid = userService.existCheck("uid", String.valueOf(chatrecord.getFromuserid()));
            MessageContent message= new MessageContent();
            message.setSenderId(uid.getUid());
            message.setMessage(chatrecord.getMessage());
            message.setSenderName(uid.getUname());
            message.setSenderPhoto(uid.getUpicture());
            message.setSendtime(chatrecord.getSendtime());
            jsonArray.add(message);
        }
        return jsonArray.toString();
    }

    /*
    * 获取通知
    *
    * */
    @PostMapping("/unreadNotification")
    public String getNotifications(){
        Subject subject = SecurityUtils.getSubject();
        User current = (User) subject.getPrincipal();
        JSONArray jsonArray = new JSONArray();

        List<Notification> notifications = messageService.getNotifications(current.getUid(), 0);
        for(Notification notification:notifications){
            MessageContent message= new MessageContent();
            User uid = userService.existCheck("uid", String.valueOf(notification.getFromuserid()));
            message.setId(notification.getId());
            message.setSenderId(uid.getUid());
            message.setMessage(notification.getText());
            message.setTitle(notification.getTitle());
            message.setSendtime(notification.getTime());
            message.setSenderPhoto(uid.getUpicture());
            message.setType(notification.getType());
            message.setSenderName(uid.getUname());
            jsonArray.add(message);
        }
        return jsonArray.toString();
    }
    @PostMapping("/agree")
    public String addFriend(String senderid){
        Subject subject = SecurityUtils.getSubject();
        User current = (User) subject.getPrincipal();
        Integer sender = Integer.valueOf(senderid);
        int i = messageService.addFriend(sender, current.getUid());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state","invalid");
        if(i>0){
            jsonObject.put("state","success");
        }
        return jsonObject.toString();
    }
    /*从消息中跳转页面到chat中*/
    @GetMapping("/tochat/{sendid}")
    public void forward(HttpServletRequest request, HttpServletResponse response, @PathVariable(name="sendid") String sendid) throws IOException {
        request.getSession().setAttribute("activePosition",sendid);
        response.sendRedirect("/chat");
    }
    @PostMapping("/chat/info")
    public String getChatAllInfo(HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        //个人信息
        Subject subject = SecurityUtils.getSubject();
        User current = (User) subject.getPrincipal();
        jsonObject.put("User",current);
        //消息信息
        List<Friend> friends = messageService.getFriends(current.getUid());
        JSONArray jsonArray  =new JSONArray();
        Chatrecord chatrecord = null;
        for(Friend friend:friends){
            int friendid = friend.getInvitedid();
            if(friendid == current.getUid()){
                friendid = friend.getInviterid();
            }
            chatrecord =messageService.getOneMessagesToOnePerson(current.getUid(),friendid);
            if(chatrecord !=null){
                MessageContent message= new MessageContent();
                User uid = userService.existCheck("uid", String.valueOf(friendid));
                message.setSenderId(uid.getUid());
                message.setMessage(chatrecord.getMessage());
                message.setSendtime(chatrecord.getSendtime());
                message.setSenderPhoto(uid.getUpicture());
                message.setSenderName(uid.getUname());
                jsonArray.add(message);
            }
        }
        jsonArray.sort((o1, o2) -> {
            MessageContent m1 =(MessageContent)o1;
            MessageContent m2 =(MessageContent)o2;
            long l = m1.getSendtime().getTime() - m2.getSendtime().getTime();
            return l>0 ? -1:1;
        });
        jsonObject.put("friensList",jsonArray);

        //单个聊天记录
        Object activePosition = request.getSession().getAttribute("activePosition");
        List<Chatrecord> allmessagesToOnePerson = null;
        if(activePosition !=null){
            allmessagesToOnePerson = messageService.getMessagesToOnePerson(current.getUid(), Integer.valueOf((String)activePosition));
            request.getSession().removeAttribute("activePosition");
        }else{
            if(jsonArray.size()>0){
                MessageContent messageContent = (MessageContent) jsonArray.get(0);
                int friendid = messageContent.getSenderId();
                allmessagesToOnePerson = messageService.getMessagesToOnePerson(current.getUid(),friendid);
            }
        }
        jsonObject.put("allmessage",allmessagesToOnePerson);
        /*将通信地址传递*/
        String tmepWebsocketip = websocketip;
        websocketip=websocketip+"/"+current.getUid();
        jsonObject.put("websocketip",websocketip);
        websocketip=tmepWebsocketip;
        return  jsonObject.toString();
    }

    @PostMapping("/chat/chang/{id}")
    public String getChatAllInfoByUid(@PathVariable(name="id")String changid){
        JSONObject jsonObject = new JSONObject();
        Subject subject = SecurityUtils.getSubject();
        User current = (User) subject.getPrincipal();
        User uid = userService.existCheck("uid", changid);
        jsonObject.put("uid",uid.getUid());
        jsonObject.put("uname",uid.getUname());
        jsonObject.put("upicture",uid.getUpicture());
        List<Chatrecord> messagesToOnePerson = messageService.getMessagesToOnePerson(current.getUid(), Integer.valueOf(changid));
        jsonObject.put("allmessage",messagesToOnePerson);
        return jsonObject.toString();
    }


    /*搜索用户*/
    @PostMapping("/search/{userName}")
    public String isexist(@PathVariable(name = "userName") String checkValue) {
        JSONObject exist = new JSONObject();
        User user = userService.existCheck("uname", checkValue);
        exist.put("user",user);
        return exist.toString();
    }

    /*好友申请*/
    @PostMapping("/addfriend")
    public String addfriendRequest(String targetid){
        JSONObject jsonObject = new JSONObject();
        Subject subject = SecurityUtils.getSubject();
        User current = (User) subject.getPrincipal();
        if(targetid !=null){
            /*是否已经添加*/
            Friend friend = messageService.existFriend(current.getUid(), Integer.valueOf(targetid));
            if(friend ==null){
                int i = messageService.addFriendRequest( Integer.valueOf(targetid),0,current.getUid());
                if(i>0){
                    jsonObject.put("state","success");
                }else{
                    jsonObject.put("state","invalid");
                }
            }else{
                jsonObject.put("state","exist");
            }
        }else{
            jsonObject.put("state","invalid");
        }
        return jsonObject.toString();
    }

    /*查询所有好友*/
    @PostMapping("/getAllFriends")
    public String getAllFriends(){
        JSONObject json  = new JSONObject();
        Subject subject = SecurityUtils.getSubject();
        User current = (User) subject.getPrincipal();
        List<Friend> friends = messageService.getFriends(current.getUid());
        String uids = "";
        for(Friend friend:friends){
            int friendid = friend.getInviterid();
            if(current.getUid() == friendid){
                friendid=friend.getInvitedid();
            }
            uids=uids+friendid+",";
        }
        if(!uids.equals("")){
            uids =uids.substring(0, uids.lastIndexOf(","));
            List<User> usersByids = userService.getUsersByids(uids);
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(usersByids);
            json.put("friendlist",jsonArray);
        }
        return json.toString();
    }

    @GetMapping("/readMessage")
    public String readMessage(String id){
        /*1为已读*/
        JSONObject jsonObject  =new JSONObject();
        if(id!=null&&!id.equals("")){
            int i = messageService.changeNotificationState(Integer.parseInt(id), 1);
            if(i>0){
                jsonObject.put("state","success");
            }
        }
        return jsonObject.toString();
    }
}
