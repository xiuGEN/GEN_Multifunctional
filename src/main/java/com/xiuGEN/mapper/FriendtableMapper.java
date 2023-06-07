package com.xiuGEN.mapper;

import com.xiuGEN.pojo.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FriendtableMapper {
    /*添加好友*/
    public int insFriend(@Param("inviterid")int inviterid,@Param("invitedid") int invitedid);
    /*查询该用户所有好友*/
    public List<Friend> selFriends(@Param("userid") int userid);

    /*查询用户是否已经是好友*/
    public Friend selFriendsExist(@Param("inviterid")int inviterid,@Param("invitedid") int invitedid);
}
