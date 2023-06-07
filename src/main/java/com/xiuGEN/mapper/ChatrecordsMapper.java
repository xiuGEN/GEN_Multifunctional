package com.xiuGEN.mapper;

import com.xiuGEN.pojo.Capacity;
import com.xiuGEN.pojo.Chatrecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ChatrecordsMapper {
    /*将聊天记录插入在数据库中*/
    public int saveMessage(Chatrecord chatrecord);
    /*查询未读信息用户消息*/
    public List<Chatrecord> selUnreadMessage(@Param("touserid") int touseridid , @Param("state") int state);
    /*查询与某个用户聊天所有信息*/
    public List<Chatrecord> selAllMessageToOnePerson(@Param("userid")int userid,@Param("friendid")int friendid);
    /*查询与某个用户聊天所有信息一条*/
    public Chatrecord selOneMessageToOnePerson(@Param("userid")int userid,@Param("friendid")int friendid);
    /*<!--查询用户最新记录-->*/
    public List<Chatrecord> selOneMessageToOne(@Param("userid") int userid);
    /*修改聊天状态*/
    public int updateChatrecordsState(@Param("state")int state,@Param("ids")String ids);
}
