package com.xiuGEN.mapper;

import com.xiuGEN.pojo.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NotificationMapping {

    public List<Notification> selNoreadNotification(@Param("touserid") int touserid, @Param("state") int state);
    /*插入数据*/
    public int insFriendRequest(@Param("title") String title ,@Param("text") String text, @Param("type") String type,@Param("touserid")int touserid,@Param("state") int state,@Param("fromuserid")int fromuesrid);
    /*更新状态*/
    public int updataState(@Param("id")int id,@Param("state")int state);
}
