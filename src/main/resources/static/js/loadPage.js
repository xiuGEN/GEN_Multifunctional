/*
* 聊天页面中加载页面相关信息
* */
/*当前用户聊天对象id*/
var currentToUserid;
/*当前用户聊天对象头像*/
var currentToUserimg;

/*当前用户id*/
var currentUserid
/*通信*/
var socket;
/*通信地址*/
var websocketip
/*通信状态*/
var isClosewebsocket =false;
/*
* 添加失败
* */
function round_error_noti(msg) {
    Lobibox.notify('error', {
        pauseDelayOnHover: true,
        size: 'mini',
        rounded: true,
        delayIndicator: false,
        icon: 'bx bx-x-circle',
        continueDelayOnInactiveTab: false,
        position: 'top right',
        msg: msg || '添加失败,请检查网络'
    });
}

/*
* 成功
* */
function round_success_noti(msg) {
    Lobibox.notify('success', {
        pauseDelayOnHover: true,
        size: 'mini',
        rounded: true,
        icon: 'bx bx-check-circle',
        delayIndicator: false,
        continueDelayOnInactiveTab: false,
        position: 'top right',
        msg: msg ||'执行成功'
    });
}


$(function(){

    $.ajax({
        url: "/message/chat/info",
        method: "post",
        async: false,
        dataType: "json",
        error: function () {
        },
        success: function (respone) {
            $("#user_upicture").children("img").prop("src",respone.User.upicture);
            $("#user_uname").text(respone.User.uname);
            currentUserid = respone.User.uid;
            /*显示聊天列表*/
            showchatlist(respone);
            /*
            * 内容加载
            * */
            chatWindowsShow($("#chatWindows_content"),currentToUserimg,respone.allmessage);
            /*打开通信*/
            websocketip =respone.websocketip;
            openSocket(websocketip);
        }
    })
});

/*
* 显示聊天列表
*
* */
function showchatlist(respone){
    $("#friendsList").html("");
    for(var i=0;i<respone.friensList.length;i++){
        var $afriend = $("<a href='javascript:;' class='list-group-item' onclick='switchChatWindos(`"+respone.friensList[i].senderId+"`,this)'>")
        if(respone.allmessage[0].fromuserid ==respone.friensList[i].senderId || respone.allmessage[0].touserid == respone.friensList[i].senderId){
            $afriend.addClass("active");
            $("#chatwindowName").text(respone.friensList[i].senderName);
            /*获取当前聊天对象id和头像*/
            currentToUserimg = respone.friensList[i].senderPhoto;
            currentToUserid = respone.friensList[i].senderId;
        }
        var $friend = $("<div class='d-flex'>" +
            "<div class='chat-user-online'>" +
            "<img src='"+respone.friensList[i].senderPhoto+"' width='42' height='42' class='rounded-circle' >" +
            "</div>" +
            "<div class='flex-grow-1 ms-2'>" +
            "<h6 class='mb-0 chat-title'>"+respone.friensList[i].senderName+"</h6>" +
            "<p class='mb-0 chat-msg'>"+respone.friensList[i].message.substr(0,10)+"</p>" +
            "</div>" +
            "<div class='chat-time'>"+timeshow(respone.friensList[i].sendtime)+"</div>" +
            "</div>");
        $afriend.html($friend)
        $("#friendsList").append($afriend);
    }
}




/*
* 发送处理
*
* */
function sendMessage(){
    if (isClosewebsocket) {
        openSocket(websocketip);
    }
    var message = $("#messageframe").val();
    var time = new Date().getTime();
    if(message != ''){
         var servermsg = '{"touserid":"'+currentToUserid+'","message":"'+message+'","sendtime":"'+time+'"}';
        socket.send(servermsg);
        var rightcontent =$("<div class='chat-content-rightside'>" +
            "<div class='d-flex ms-auto'>" +
            "<div class='flex-grow-1 me-2'>" +
            "<p class='mb-0 chat-time text-end'>"+timeshow2(time)+"</p>" +
            "<p class='chat-right-msg' style='white-space: pre-wrap;'>"+message+"</p>" +
            "</div>" +
            "</div>" +
            "</div>");
        $("#chatWindows_content").append(rightcontent);
        $(".ps__rail-x").animate({top:"437px"},"normal")
        /*将数据清空*/
        $("#messageframe").val("");
        /*将滚动条移动至底部(不保险)*/
        document.querySelector('#chatWindows_content').scrollTop =1000000;
    }
}



/*时间 ..以前*/
function timeshow(temptime){
    temptime = new Date(temptime)
    var nowtime = new Date();
        if(temptime.getFullYear() == nowtime.getFullYear()){
            time = (temptime.getMonth()+1)+"-"+temptime.getDay();
            if(temptime.getMonth()==nowtime.getMonth()){
                time = nowtime.getDate() - temptime.getDate() +"天前";
                if(temptime.getDate()==nowtime.getDate()){
                    time = (nowtime.getHours()+1)-(temptime.getHours()+1)+"小时前";
                    if(temptime.getHours()==nowtime.getHours()){
                        time=(nowtime.getMinutes()+1)-(temptime.getMinutes()+1)+"分钟前";
                        if(temptime.getMinutes()==nowtime.getMinutes()){
                            time = (nowtime.getSeconds()+1)-(temptime.getSeconds()+1)+"秒以前"
                        }
                    }
                }
            }
        }else{
            time = temptime.getFullYear()+"-"+(temptime.getMonth()+1)+"-"+temptime.getDay();
        }
        return time;
}
/*时间 日期展示*/
function timeshow2(temptime){
    temptime = new Date(temptime)
    var nowtime = new Date();
    if(temptime.getFullYear() == nowtime.getFullYear()){
        time = (temptime.getMonth()+1)+"/"+(temptime.getDay()+1)+" "+(temptime.getHours()+1)+":"+(temptime.getMinutes()+1);
            if(temptime.getDate()==nowtime.getDate()){
                if(temptime.getMinutes()<10){
                    time = (temptime.getHours())+":0"+(temptime.getMinutes());
                }else
                    time = (temptime.getHours())+":"+(temptime.getMinutes());
        }
    }else{
        time = temptime.getFullYear()+"-"+(temptime.getMonth()+1)+"-"+(temptime.getDay()+1);
    }
    return time;
}

/*切换窗口设置*/
function switchChatWindos(sendid,changeA){
    var url ="/message/chat/chang/"+sendid
    //获取该用户信息
    $.ajax({
        url: url,
        method: "post",
        async: false,
        dataType: "json",
        error: function () {
            round_error_noti("请检查网络")
        },
        success: function (respone) {
            currentToUserid = respone.uid;
            currentToUserimg = respone.upicture;
            //1.将姓名改变
            $("#chatwindowName").text(respone.uname);
            //将聊天框清空
            chatWindowsShow($("#chatWindows_content"),currentToUserimg,respone.allmessage);
            //2将active变化
            $("#friendsList .active").removeClass("active");
            $(changeA).addClass("active");
            //改变currentToUserid
            //改变currentToImg


        }
    })
}

/*聊天窗口操作*/
function chatWindowsShow(position,img,records){
    position.html("");
    if(typeof (records) !='undefined')
        for(var i=0;i<records.length;i++){
            var time=timeshow2(records[i].sendtime);
            if(records[i].fromuserid == currentUserid){
                var rightcontent =$("<div class='chat-content-rightside'>" +
                    "<div class='d-flex ms-auto'>" +
                    "<div class='flex-grow-1 me-2'>" +
                    "<p class='mb-0 chat-time text-end'>"+time+"</p>" +
                    "<p class='chat-right-msg'>"+records[i].message+"</p>" +
                    "</div>" +
                    "</div>" +
                    "</div>");
                position.append(rightcontent);
            }else {
                var leftcontent = $("<div class='chat-content-leftside'>" +
                    "<div class='d-flex'>" +
                    "<img src=" + img + " width='48' height='48' class='rounded-circle'  />" +
                    "<div class='flex-grow-1 ms-2'>" +
                    "<p class='mb-0 chat-time'>" + time + "</p>" +
                    "<p class='chat-left-msg'>" + records[i].message + "</p>" +
                    "</div>" +
                    "</div>" +
                    "</div>");
                position.append(leftcontent);
            }
        }
    /*将滚动条移动至底部(不保险)*/

    document.querySelector('#chatWindows_content').scrollTop =1000000;
}

/*通信设备打开*/
function openSocket(url) {

    if (typeof (WebSocket) == "undefined") {
        console.log("您的浏览器不支持WebSocket");
    } else {
        /*为发送框添加发送事件*/
        $("#messageframe").bind("keydown",function(event){
            if(event.keyCode=='13'){
                sendMessage();
            }
        })
        console.log("您的浏览器支持WebSocket");
        //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
        if(url =='' || typeof (url) == 'undefined'){
            round_error_noti("无法进行通信,请检查网络");
        }else{
            var socketUrl = url;
            // var socketUrl = "ws://127.0.0.1.150:12345/webSocket/" + userId;
            if (socket != null) {
                socket.close();
                socket = null;
            }
            socket = new WebSocket(socketUrl);


            //打开事件
            socket.onopen = function () {
                isClosewebsocket = false;
                console.log("websocket已打开")
            };

            //获得消息事件
            socket.onmessage = function (msg) {
                //用户获取的数据
                var serverMsg = msg.data;
                serverMsg = JSON.parse(serverMsg);
                var leftcontent = $("<div class='chat-content-leftside'>" +
                    "<div class='d-flex'>" +
                    "<img src=" + currentToUserimg+ " width='48' height='48' class='rounded-circle'  />" +
                    "<div class='flex-grow-1 ms-2'>" +
                    "<p class='mb-0 chat-time'>" + timeshow2(serverMsg.message.sendtime) + "</p>" +
                    "<p class='chat-left-msg'>" + serverMsg.message.message + "</p>" +
                    "</div>" +
                    "</div>" +
                    "</div>");
                $("#chatWindows_content").append(leftcontent);
                /*将滚动条移动至底部(不保险)*/
                document.querySelector('#chatWindows_content').scrollTop =1000000;
            };


            //关闭事件
            socket.onclose = function () {
                isClosewebsocket = true;
                console.log("websocket已关闭");
            };

            //发生了错误事件
            socket.onerror = function () {

                console.log("websocket发生了错误");
            }
        }

        }

}

/*用户搜索*/
function searchUser(e){
    var uname=$(e).prev("input").val();
    if(uname != '' && typeof(uname) != 'undefined'){
       var url="/message/search/"+uname;
        $.ajax({
            url: url,
            method: "post",
            async: false,
            dataType: "json",
            error: function () {
                round_error_noti("请检查网络")
            },
            success:function (respone){
                if(respone.user !=null && typeof (respone.user) != 'undefined'  ){
                    $(e).parent().parent().children(":not(.input-group)").remove();
                   var $searchUser= $("<div targetid='"+respone.user.uid+"' class='d-flex align-items-center userpeople'>" +
                            "<div>" +
                                "<img src='"+respone.user.upicture+"' class='msg-avatar' alt='user avatar' style='width: 45px;height: 45px;border-radius: 50%;margin-right: 15px;'>" +
                            "</div>" +
                            "<div class='flex-grow-1'>" +
                                "<h6 class='msg-name'>"+respone.user.uname+"</h6>" +
                            "</div>" +
                        "</div>")
                    $(e).parent().parent().append($searchUser);
                }else{
                    $(e).parent().parent().children(":not(.input-group)").remove();
                    $(e).parent().parent().append("<div class='flex-grow-1'><h6 class='msg-name'>没有此人</h6></div>")
                }
            }
        })
    }else {
        round_error_noti("请输入要查找的账号")
    }

}

/*添加好友*/
function addfriend(e){
    var hasSearcheruser=$("div").find(".userpeople")
    var targetid =hasSearcheruser.attr("targetid");
    if(hasSearcheruser.length>0){
        $.ajax({
            url: "/message/addfriend",
            method: "post",
            async: false,
            data:{"targetid":targetid},
            dataType: "json",
            error: function () {
                round_error_noti("请检查网络")
            },
            success:function (respone) {
                if(respone.state == 'success'){
                    round_success_noti("好友添加请求已发送")
                    $(hasSearcheruser).remove();
                }else if(respone.state == 'exist'){
                    round_error_noti("好友已添加");
                } else {
                    round_error_noti("添加失败")
                }
            }
        })
    }else{
        round_error_noti("无好友添加")
    }

}

function showFriendList(e){
    var list = $("<div className='dropdown'>" +
        "        <div className='cursor-pointer font-24 dropdown-toggle dropdown-toggle-nocaret' data-bs-toggle='dropdown'>" +
        "            <i className='bx bx-dots-horizontal-rounded'></i>" +
        "        </div>" +
        "        <div className='dropdown-menu dropdown-menu-end'>" +
        "            <a className='dropdown-item' href='javascript:;'>聊天</a>" +
        "            <a className='dropdown-item' href='javascript:;'>删除</a>" +
        "        </div>" +
        "    </div>");
    $.ajax({
        url: "/message/getAllFriends",
        method: "post",
        async: false,
        dataType: "json",
        error: function () {
            round_error_noti("请检查网络")
        },
        success:function (respone) {
            $("#friendsList").html("");
            console.log(respone);
            if(respone.friendlist != null)
            for(var i=0;i<respone.friendlist.length;i++){
                var $afriend = $("<a href='javascript:;' class='list-group-item' onclick='switchChatWindos(`"+respone.friendlist[i].uid+"`,this)'>")
                var $friend = $("<div class='d-flex'>" +
                    "<div class='chat-user-online'>" +
                    "<img src='"+respone.friendlist[i].upicture+"' width='42' height='42' class='rounded-circle' >" +
                    "</div>" +
                    "<div class='flex-grow-1 ms-2'>" +
                    "<h6 class='mb-0 chat-title'>"+respone.friendlist[i].uname+"</h6>" +
                    "</div>" +
                    "</div>");
                $afriend.html($friend)
                $("#friendsList").append($afriend);
            }
        }
    })

}
/*切换到聊天*/
function switchChat(){
    $.ajax({
        url: "/message/chat/info",
        method: "post",
        async: false,
        dataType: "json",
        error: function () {
        },
        success: function (respone) {
            /*显示聊天列表*/
            showchatlist(respone);
            /*
            * 内容加载
            * */
            chatWindowsShow($("#chatWindows_content"),currentToUserimg,respone.allmessage);
        }
    })
}

/*删除好友*/

function deleteFriends(uid){

}