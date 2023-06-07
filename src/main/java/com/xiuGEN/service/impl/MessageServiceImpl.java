package com.xiuGEN.service.impl;

import com.xiuGEN.mapper.ChatrecordsMapper;
import com.xiuGEN.mapper.FriendtableMapper;
import com.xiuGEN.mapper.NotificationMapping;
import com.xiuGEN.pojo.Chatrecord;
import com.xiuGEN.pojo.Friend;
import com.xiuGEN.pojo.Notification;
import com.xiuGEN.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private ChatrecordsMapper chatrecordsMapper;
    @Autowired
    private NotificationMapping notificationMapping;
    @Autowired
    private FriendtableMapper friendtableMapper;

    //好友申请标题
    private  final static String TITLE="添加好友";
    //好友申请信息
    private final static String TEXT="请求添加好友;";
    //添加好友类型
    private final static String ADDREQUESTTYPE = "addrequest";
    @Override
    public int saveChatRecords(Chatrecord chatrecord) {
        return chatrecordsMapper.saveMessage(chatrecord);
    }

    @Override
    public List<Chatrecord> getMessages(int toUserid, int state) {
        return chatrecordsMapper.selUnreadMessage(toUserid,state);
    }

    @Override
    public List<Notification> getNotifications(int toUsserid, int state) {
        return notificationMapping.selNoreadNotification(toUsserid,state);
    }

    @Override
    public int addFriend(int inviterid, int invitedid) {
        return friendtableMapper.insFriend(inviterid,invitedid);
    }

    @Override
    public List<Friend> getFriends(int userId) {
        return friendtableMapper.selFriends(userId);
    }

    @Override
    public List<Chatrecord> getMessagesToOnePerson(int userid, int friendid) {
        List<Chatrecord> chatrecords = chatrecordsMapper.selAllMessageToOnePerson(userid, friendid);
        changeChatrecordsState(chatrecords);
        return chatrecords;
    }

    @Override
    public Chatrecord getOneMessagesToOnePerson(int userid, int friendid) {
        return chatrecordsMapper.selOneMessageToOnePerson(userid, friendid);
    }

    @Override
    public List<Chatrecord> getMessagesToOne(int userid) {
        return chatrecordsMapper.selOneMessageToOne(userid);
    }

    @Override
    public Friend existFriend(int inviterid, int invitedid) {
        return friendtableMapper.selFriendsExist(inviterid, invitedid);
    }

    @Override
    public int addFriendRequest(String title, String text, String type, int touserid, int state, int fromuserid) {
        return  notificationMapping.insFriendRequest(title,text,type,touserid,state,fromuserid);
    }
    @Override
    public int addFriendRequest(int touserid, int state, int fromuserid) {
        return addFriendRequest(TITLE,TEXT,ADDREQUESTTYPE,touserid,state,fromuserid);
    }

    @Override
    public int changeNotificationState(int id,int state) {
        return notificationMapping.updataState(id,state);
    }

    @Override
    public int changeChatrecordsState(int state, String ids) {
        return chatrecordsMapper.updateChatrecordsState(state,ids);
    }
    /*
    * 将信息标记为已读
    * */
    public int changeChatrecordsState(List<Chatrecord> chatrecords){
        String ids="";
        if(chatrecords !=null && chatrecords.size()>0){
            for(Chatrecord chatrecord:chatrecords){
                ids=ids+chatrecord.getRid()+",";
            }
            ids=ids.substring(0,ids.lastIndexOf(","));
            return changeChatrecordsState(1,ids);
        }
        return 0;
    }
}
