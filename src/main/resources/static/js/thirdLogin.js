var countdown;
var remainingTime = 60; //验证码冻结时间秒
var sessionid;

// 发送验证码
$('#sendCode').on('click', function() {

    // 这里可以添加实际的发送验证码代码
    /*获取用户头像名称信息*/
    sessionid = "";
    let email = $("#email").val();
    debugger;
    if(email){
        $.ajax({
            url:     "/sendVerificationCode",
            method:  "POST",
            async:    false,
            data:{
                mail:email
            },
            dataType :"json",
            error :function(){
                alert("发送验证码失败");
            },
            success:function(respone){
                debugger;
                if(respone.code == 200){
                    sessionid = respone.msg;
                    // 显示倒计时和禁用发送按钮
                    $('#sendCode').prop('disabled', true);

                    countdown = setInterval(function() {
                        remainingTime--;
                        $('#sendCode').text(remainingTime);
                        if (remainingTime <= 0) {
                            clearInterval(countdown);
                            $('#sendCode').text("发送验证码");
                            $('#sendCode').prop('disabled', false);
                            remainingTime = 60;
                        }
                    }, 1000);
                }else{
                    alert(respone.msg)
                }
            }

        })
    }else alert("邮箱为空")
});

// 合并按钮点击事件
$('#merge').on('click', function() {
    let code = $("#code").val();
    if(code){
        $.ajax({
            url:     "/verificationCode",
            method:  "POST",
            async:    false,
            data:{
                code :code
            },
            dataType :"json",
            error :function(){
                alert("发送验证码失败");
            },
            success:function(respone){
                if(respone.code == 200){
                    window.location.href = respone.url
                }else{
                    alert(respone.msg)
                }
            }
        })
    } else alert("验证码未空")

});

//无账号登入
$("#noaccount").on("click",function (){
    $.ajax({
        url:"/loginUrl",
        method:"get",
        data:{
            sign:"1"
        },
        dataType:"json",
        error:function (){
            alert("登入失败");
        },
        success:function (res){
            if(res.code == 200){
                window.location.href=res.msg
            }else alert(res.msg)
        }
    })

})

//微信登入
$(".wechat-login-btn").on("click",function (){
    alert("money不足，功能未开发");
})
