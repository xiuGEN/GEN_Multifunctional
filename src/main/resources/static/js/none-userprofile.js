/*
*
*user-file相关交互
* */
    $(function(){
        var $profile_headphone=$("#profile-headphone");
        var $profile_realname=$("#profile-realname");
        var $profile_uname=$("#profile-uname");
        var $profile_realname2=$("#profile-realname2");
        var $profile_uname2=$("#profile-uname2");
        var $profile_upassword=$("#profile-upassword");
        var $profile_uemail=$("#profile-uemail");
        var $profile_uphone=$("#profile-uphone");
        var $profile_address=$("#profile-address");
        var $profile_ubirthdate=$("#profile-ubirthdata");
        var $user_profile=$("#user-profile");
        var $headphoto =$("#user-img");
        var userInfo;
        /*
        * 对头像图片上传
        * */

        $("input[type='file']").on("change",function(){
            var formData = new FormData();
            var file=document.getElementById("imageFile").files[0];
            formData.append("imageFile",file);
           $.ajax({
               url:"/image/headphoto",
               data:formData,
               type:"post",
               async:false,
               processData:false,
               contentType:false,
               dataType:'json',
               error:function(e){
                   alert("更换头像失败,请检查网络");
               },
               success:function(response){
                   if(response.status=='success')
                           if(response.upicture !=null && response.upicture!="" && typeof(response.upicture)!='undefined'){
                               $profile_headphone.prop("src",'/'+response.upicture);
                               $headphoto.prop("src","/"+response.upicture);
                           }
                   else if(response.status=='invalid'){
                            alert("请检查文件名或者文件格式");
                       }
               },



           })
        });


        /*
        * 获取数据
        * */
        $.ajax({
            url:     "/getUserInfo",
            method:  "GET",
            async:    false,
            dataType :"json",
            error :function(){
                alert("请检查网络");
            },
            success:function(respone){
                userInfo=respone.user;
                if(respone.user.upicture !=null && respone.user.upicture!="" && typeof(respone.user.upicture)!='undefined'){
                    if(respone.user.upicture.includes("http")){
                        $profile_headphone.prop("src",respone.user.upicture);
                    }else
                    $profile_headphone.prop("src",'/'+respone.user.upicture);
                }
                if(respone.user.urealname !=null && respone.user.urealname!="" && typeof(respone.user.urealname)!='undefined'){
                    $profile_realname.text(respone.user.urealname);
                    $profile_realname2.prop("value",respone.user.urealname);
                }
                if(respone.user.uname !=null && respone.user.uname!="" && typeof(respone.user.uname)!='undefined'){
                    $profile_uname.text(respone.user.uname);
                    $profile_uname2.prop("value",respone.user.uname);
                }
                if(respone.user.upassword!=null && respone.user.upassword!="" && typeof(respone.user.upassword)!='undefined'){
                    $profile_upassword.prop("value",respone.user.upassword);
                }
                if(respone.user.uemail!=null && respone.user.uemail!="" && typeof(respone.user.uemail)!='undefined'){
                    $profile_uemail.prop("value",respone.user.uemail);
                }
                if(respone.user.uphone!=null && respone.user.uphone!="" && typeof(respone.user.uphone)!='undefined'){
                    $profile_uphone.prop("value",respone.user.uphone);
                }
                if(respone.user.uaddress!=null && respone.user.uaddress!="" && typeof(respone.user.uaddress)!='undefined'){
                    $profile_address.prop("value",respone.user.uaddress);
                }
                if(respone.user.ubirthdate!=null && respone.user.ubirthdate!="" && typeof(respone.user.ubirthdate)!='undefined'){
                    $profile_ubirthdate.prop("value",respone.user.ubirthdate);
                }
            }

        });


        /*
        * 保存的点击事件
        *
        * */

        //自定义添加电话号码的校验
        jQuery.validator.addMethod("phone",function (value,element){
            var phonenumber=value;
            var phoneRegExp=/^(((\(\d{3,4}\)|\d{3,4})?\d{7,8})|(1[3-9][0-9]{9}))$/;
            return this.optional(element) || phoneRegExp.test(phonenumber);
        },jQuery.validator.format("请输入正确的电话号码"));
        //对手机请求
        jQuery.validator.addMethod("existphone", function(value, element, param) {
            var exist;
            $.ajax({
                url: "/exist2/uphone",
                method: 'POST',
                async:false,
                dataType: 'json',
                data: {checkValue: value},
                success :function(respone){
                    if(respone.exists == 'exists')
                        exist=false;
                    else if(respone.exists == 'noexists')
                        exist=true;
                }
            });
            return exist;
        }, $.validator.format("手机号已注册"));

        //添加日期的校验
        jQuery.validator.addMethod("checkdate", function(value, element, param) {
            var data=value;
            var dateReEx=/^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))$/;
            return  dateReEx.test(data);
        }, jQuery.validator.format("请输入正确的格式"));

        jQuery.validator.addMethod("checkdate", function(value, element, param) {
            var data=value;
            var dateReEx=/^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))$/;
            return  dateReEx.test(data);
        }, jQuery.validator.format("请输入正确的格式"));

        //对邮箱请求
        jQuery.validator.addMethod("existemail", function(value, element, param) {
            var exist;
            $.ajax({
                url: "/exist2/uemail",
                method: 'POST',
                async:false,
                dataType: 'json',
                data: {checkValue: value},
                success :function(respone){
                    if(respone.exists == 'exists')
                        exist = false;
                    else if(respone.exists == 'noexists')
                        exist = true;
                }
            })
            return exist;
        }, jQuery.validator.format("邮箱已注册"));

        //对用户名请求校验
        jQuery.validator.addMethod("existeusname", function(value, element, param) {
            var exist;
            $.ajax({
                url: "/exist2/uname",
                method: 'POST',
                async:false,
                dataType: 'json',
                data: {
                    checkValue: value,
                },
                success :function(respone){
                    if(respone.exists == 'exists')
                        exist = false;
                    else if(respone.exists == 'noexists')
                        exist = true;
                }
            })
            return exist;
        }, jQuery.validator.format("用户名已注册"));
        $.extend($.validator.messages, {
            email:"无效的邮箱格式",
        });
        $user_profile.validate({
            onfocusout: function(element) { $(element).valid(); },
            rules:{
                urealname:{
                    required:true,
                    rangelength:[2,7],

                },
                uphone: {
                    required: true,
                    phone: true,
                    existphone: true
                },
                uemail: {
                    required: true,
                    email: true,
                    existemail: true
                },

                uname: {
                    required: true,
                    rangelength:[3,7],
                    existeusname:true
                },

                upassword: {
                    required: true,
                    rangelength:[8,15],
                },

                ubirthdate:{
                    required: true,
                    checkdate: true
                },
                uaddress:{
                    rangelength:[3,30],
                }

            },
            messages:{
                urealname:{
                    rangelength:"请输入长度在2到7之间的字符"
                },
                uname:{
                    rangelength:"请输入长度在3到7之间的字符"
                }
                ,
                upassword:{
                    rangelength:"请输入长度在8到15之间的字符"
                },
                uaddress: {
                    rangelength:"请输入长度在3到30之间的字符"
                }
            },
            /*触发验证方式*/
            onkeyup:false,
            highlight: function(element){ /*输入不正确的*/
                $(element).closest('.input-group').addClass('validate-has-error');
            },


            unhighlight: function(element) /*输入正确的*/
            {
                $(element).closest('.input-group').removeClass('validate-has-error');
            },
            submitHandler: function(ev)
            {
                if( $("input#profile-realname2").val() !=userInfo.urealname   ||
                    $("input#profile-uphone").val()    !=userInfo.uphone         ||
                    $("input#profile-ubirthdata").val()!=userInfo.ubirthdate ||
                    $("input#profile-uname2").val()    !=userInfo.uname          ||
                    $("input#profile-uemail").val()    !=userInfo.uemail         ||
                    $("input#profile-upassword").val() !=userInfo.upassword   ||
                    $("input#profile-address").val()   !=userInfo.uaddress      &&
                    $("input#profile-address").val()   !=""                     &&
                    typeof($("input#profile-address").val())!='undefined'
                ){
                    userInfo.urealname=$("input#profile-realname2").val();
                    userInfo.uphone=$("input#profile-uphone").val();
                    userInfo.ubirthdate=$("input#profile-ubirthdata").val();
                    userInfo.uname=$("input#profile-uname2").val();
                    userInfo.uemail=$("input#profile-uemail").val();
                    userInfo.upassword=$("input#profile-upassword").val() ;
                    userInfo.uaddress=$("input#profile-address").val() ;

                    $.ajax({
                        url:"/save",
                        method:'POST',
                        dataType:'json',
                        data:{
                            urealname: 		$("input#profile-realname2").val(),
                            uphone: 		$("input#profile-uphone").val(),
                            ubirthdate: 	$("input#profile-ubirthdata").val(),
                            uname: 			$("input#profile-uname2").val(),
                            uemail: 		$("input#profile-uemail").val(),
                            upassword:		$("input#profile-upassword").val(),
                            uaddress:       $("input#profile-address").val()
                        },
                        error: function()
                        {
                            alert("保存失败");
                        },
                        success: function(response)
                        {
                            if(response.register_status =='invalid'){
                                alert("保存失败");
                            }else if(response.register_status =='success'){
                                alert("保存成功");
                                $profile_realname.text($("input#profile-realname2").val());
                                $profile_uname.text($("input#profile-uname2").val());
                                $("#user-name").text($("input#profile-realname2").val());
                                $("#designattion").text($("input#profile-uname2").val());
                            }

                        }

                    });


                }else{
                }




            }
        });

    });




