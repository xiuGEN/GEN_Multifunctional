package com.xiuGEN.service;

import com.xiuGEN.pojo.Chatrecord;
import com.xiuGEN.pojo.Friend;
import com.xiuGEN.pojo.Notification;

import java.util.List;


public interface MessageService {
    /*保存聊天记录*/
    public int saveChatRecords(Chatrecord chatrecord);
    /*获取根据state信息*/
    public List<Chatrecord> getMessages(int toUserid,int state);
    /*获取通知*/
    public List<Notification> getNotifications(int toUsserid,int state);
    /*添加好友*/
    public int addFriend(int inviterid,int invitedid);
    /*查询该用户好友*/
    public List<Friend> getFriends(int userId);
    /*查询与某个用户的所有记录*/
    public List<Chatrecord> getMessagesToOnePerson(int userid,int friendid);
    /*查询与某个用户的单条记录*/
    public Chatrecord getOneMessagesToOnePerson(int userid,int friendid);
    /*查询用户与好友之间最新记录的一条*/
    public List<Chatrecord> getMessagesToOne(int userid);
    /*好友是否是重复添加*/
    public Friend existFriend(int inviterid,int invitedid);
    /*添加好友申请*/
    public int addFriendRequest(String title,String text,String type,int touserid,int state ,int fromuserid);
    public int addFriendRequest( int touserid, int state, int fromuserid);
    /*更新通知状态通知*/
    public int changeNotificationState(int id,int state);
    /*修改聊天记录状态*/
    public int changeChatrecordsState(int state,String ids);
}
