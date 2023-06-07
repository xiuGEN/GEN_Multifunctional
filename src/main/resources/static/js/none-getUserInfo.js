/*
*
*加载页面启动所需信息
* */


$(function(){

       var $headphoto =$("#user-img");
       var $realname =$("#user-name");
       var $username =$("#designattion");

      /*获取用户头像名称信息*/
        $.ajax({
            url:     "/getUserInfo",
            method:  "GET",
            async:    false,
            dataType :"json",
            error :function(){
                alert("请检查网络");
            },
            success:function(respone){
                if(respone.user.upicture !=null && respone.user.upicture!="" && typeof(respone.user.upicture)!='undefined'){
                   if(respone.user.upicture.includes("http")){
                       $headphoto.prop("src",respone.user.upicture);
                   }else $headphoto.prop("src",'/'+respone.user.upicture);
                }
                if(respone.user.urealname !=null && respone.user.urealname!=""&& typeof(respone.user.urealname)!='undefined'){
                    $realname.text(respone.user.urealname);
                }
                if(respone.user.uname !=null && respone.user.uname!="" && typeof(respone.user.uname)!='undefined'){
                    $username.text(respone.user.uname);
                }
            }

        })


    /*
    * 获取用户未读信息
    *
    * */
    var $headerMessage=$("#header-message");
    $.ajax({
        url:"/message/unreadMessage",
        method:  "post",
        async:    false,
        dataType :"json",
        error :function(){
            alert("请检查网络");
        },
        success:function(datas) {
            if(datas.length >0){
                $("#UnReadNumber").text(datas.length);
                var nowtime = new Date();
                for(var i =0 ; i<datas.length;i++){
                    var temptime = new Date(datas[i].sendtime);
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
                    var uphoto = datas[i].senderPhoto;
                    if(uphoto == undefined){
                        uphoto = 'static/picture/logo_G.png'
                    }
                    var $usermessage =$("<a class='dropdown-item' href='/message/tochat/"+datas[i].senderId+"' onclick='readMessage(this)'>" +
                        "<div class=    'd-flex align-items-center'>" +
                        "<div>" +
                        "<img src='"+uphoto+"' class='msg-avatar' alt='user avatar'>" +
                        "</div>" +
                        "<div class='flex-grow-1'>" +
                        "<h6 class='msg-name'>"+datas[i].senderName+"<span class='msg-time float-end'>"+time+"</span></h6>" +
                        "<p class='msg-info'>"+datas[i].message.substr(0,12)+"</p>" +
                        "</div>" +
                        "</div>" +
                        "</a>");

                    $headerMessage.append($usermessage);
                }
            }else{
                var $nomessage=$("<div style='font-size: 80px;text-align: center;margin: auto'>无</div>");
                $headerMessage.html($nomessage);
            }

        }

    })


    /*
    * 获取通知
    * */
    var $headerNotifications = $("#header-notifications");
    $.ajax({
        url:"/message/unreadNotification",
        method:  "post",
        async:    false,
        dataType :"json",
        error :function(){
            alert("请检查网络");
        },
        success:function(datas) {
            if(datas.length >0){
                $("#UnreadNotificationNumber").text(datas.length);
                var nowtime = new Date();
                for(var i =0 ; i<datas.length;i++){
                    var temptime = new Date(datas[i].sendtime);
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
                    var uphoto = datas[i].senderPhoto;
                    if(typeof(uphoto) =='undefined'){
                        uphoto = '/static/picture/logo_G.png'
                    }
                    if(datas[i].type == 'addrequest'){
                        var $usermessage =$("<a class='dropdown-item' href='javascript:' data-bs-toggle='modal' data-bs-target='#myModal' onclick='addrequire(`"+datas[i].senderId+"`,`"+datas[i].senderName+"`,`"+datas[i].senderPhoto+"`,`"+time+"`),readNotification(this),readdeal(`"+datas[i].id+"`)'>" +
                            "<div class='d-flex align-items-center'>" +
                            "<div class='notify bg-light-primary text-primary'><i class='bx bx-group'></i></div>" +
                            "<div class='flex-grow-1'>" +
                            "<h6 class='msg-name'>"+datas[i].title+"<span class='msg-time float-end'>"+time+"</span></h6>" +
                            "<p class='msg-info'>"+datas[i].message.substr(0,12)+"</p>" +
                            "</div>" +
                            "</div>" +
                            "</a>");
                        $headerNotifications.append($usermessage);
                    }else{
                        var $usermessage =$("<a class='dropdown-item' href='javascript:' data-bs-toggle='modal' data-bs-target='#myModal' onclick='notification(`"+datas[i].senderName+"`),readNotification(this),readdeal(`"+datas[i].id+"`)'>" +
                            "<div class='d-flex align-items-center'>" +
                            "<div class='notify bg-light-danger text-danger'><i class='bx bx-message-detail'></i></div>" +
                            "<div class='flex-grow-1'>" +
                            "<h6 class='msg-name'>"+datas[i].title+"<span class='msg-time float-end'>"+time+"</span></h6>" +
                            "<p class='msg-info'>"+datas[i].message.substr(0,12)+"</p>" +
                            "</div>" +
                            "</div>" +
                            "</a>");
                        $headerNotifications.append($usermessage);
                        /*将信息存储在本地*/
                        localStorage.setItem(datas[i].senderName,datas[i].message);
                    }
                }
            }else{
                var $nomessage=$("<div style='font-size: 80px;text-align: center;margin: auto'>无</div>");
                $headerNotifications.html($nomessage);
            }

        }

    })
});

/*
*   通知
* */
function notification(key){
    const message =  localStorage.getItem(key);
    var text  = $("<p>"+message+"</p>");
    $("#modal_title").text("通知");
    $("#model_body").html(text);
    $("#modal_footer").html("<button type='button' class='btn btn-danger' data-bs-dismiss='modal' onclick='closenotification(`"+key+"`)'>关闭</button>")
}
/*
* 好友请求
*
* */
function addrequire(senderid,senderName,img,time,id){
    var divRequire  =$("<div class='d-flex align-items-center'>" +
                            "<div><img src='"+img+"' class='msg-avatar' alt='user avatar' style='width: 45px;height: 45px;border-radius: 50%;margin-right: 15px;'></div>" +
                            "<div class='flex-grow-1'>" +
                                "<h6 class='msg-name'>"+senderName+"<span class='msg-time float-end'>"+time+"</span></h6>" +
                                "<p class='msg-info'>请求添加您为好友</p></div>" +
                             "</div>");
    $("#modal_title").text("好友申请");
    $("#model_body").html(divRequire);
    $("#modal_footer").html("<button type='button' class='btn btn-success' data-bs-dismiss='modal' onclick='agree(`"+senderid+"`,`"+img+"`,`"+senderName+"`)'>同意</button>" +
                                  "<button type='button' class='btn btn-danger' data-bs-dismiss='modal'>拒绝</button>")
}


/*已读信息处理*/
function readNotification(node) {
    var number = $("#UnreadNotificationNumber").text();
    $("#UnreadNotificationNumber").text(number - 1);
    $(node).remove();
}
function readMessage(node) {
    var number = $("#UnReadNumber").text();
    $("#UnReadNumber").text(number - 1);
    $(node).remove();
}
function agree(senderid,img,senderName){
    $.ajax({
        url:"/message/agree",
        method:  "post",
        async:    false,
        data:{senderid:senderid},
        dataType :"json",
        error :function(){
            round_error_noti();
        },
        success:function(resp){
            if(resp.state=='success'){
                img_success_noti(img,senderName);
            }else {
                round_error_noti();
            }
        }
    })


}
/*
* 同意提示
* */
function img_success_noti(img,senderName) {
    Lobibox.notify('success', {
        pauseDelayOnHover: true,
        continueDelayOnInactiveTab: false,
        position: 'top right',
        icon: 'bx bx-check-circle',
        img: img, //path to image
        msg: '你已经和'+senderName+'是好友,快去聊天吧'
    });
}
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
* 关闭通知处理
* */
function closenotification(key){
    localStorage.removeItem(key);
}
/*
* 将通知变已读
* */
function readdeal(id){
    $.ajax({
        url:"/message/readMessage",
        method:"GET",
        async:    false,
        data:{"id":id},
        dataType :"json",
        error :function(){
            alert("请检查网络");
        },
        success:function(datas) {

        }
    })
}
/*
* 同意提示
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