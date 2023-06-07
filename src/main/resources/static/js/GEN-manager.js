
/*
* 搜索框中的函数
* */
function onclick02(index){
    var elementsByClassName = document.getElementsByClassName("tip-list");
    var searchinput = document.getElementById("search-input")
    var tiplist = ["图片:","视频:","音频:","压缩文件:","文档:"]
    for (var i=0;i<elementsByClassName.length;i++){
        if (index===i){
            searchinput.value=tiplist[i];
            searchinput.focus()
            document.getElementById("tip-frame").style.display = "none";
            document.getElementById("search-win").style.height = "80px";
            break;
        }
    }
}

function oninput01(x){
    x.value = x.value.trim()
    var searchinput = document.getElementById("search-input")
    var frame = document.getElementById("tip-frame")
    var win = document.getElementById("search-win")
    if (searchinput.value.length>0){
        frame.style.display = "none";
        win.style.height = "40px";
    } else{
        frame.style.display = "block";
        win.style.height = "180px";
    }
}

var downpath;
/*
*
* 分享函数操作
*
* */
function share(info,type){
    const path=downpath || 'http://localhost:23213/download/';
    if(type=='image'){
        $("#avatar2").prop("src",info)
    }else if(type=='audio'){
        $("#avatar2").prop("src","/static/images/audioicon.jpg")
    }else if(type=='video'){
        $("#avatar2").prop("src","/static/images/videoicon.jpeg")
    }else{
        $("#avatar2").prop("src","/static/images/fileicon.jpg")
    }
    info = encodeURIComponent(info);
    info = encodeBase64(info);
    $("#linkid").html(path+info);
    console.log(path+"----"+info)
    $("#shutout").show();
    $("#sharefiles").fadeIn();
}
function encodeBase64(str) {
    const encodedStr = encodeURIComponent(str).replace(/%([A-F\d]{2})/g, (match, p1) => String.fromCharCode(parseInt(p1, 16)));
    const base64Str = btoa(encodedStr);
    return base64Str;
}

/*
* 显示详细信息
* */
function detailInfo(type,fileName,size,fileLocation,insertTime){
    $("#lookinfo").fadeIn();
    $("#shutout").show();
    if(type=='image'){
        avatar=fileLocation+"/"+fileName;
    }else if(type=='audio'){
        avatar="/static/images/audioicon.jpg";
    }else if(type=='video'){
        avatar="/static/images/videoicon.jpeg";
    }else{
        avatar="/static/images/fileicon.jpg";
    }
    detailsdata(avatar,fileName,size,fileLocation,insertTime);
}

// 关闭详细信息弹窗
$("#layui-icon-close02").click(function () {
    $("#curtain").hide()
    $("#lookinfo").hide()
    $("#shutout").hide();
});

//关闭分享信息框架
$("#layui-icon-close03").click(function(){
    $("#sharefiles").hide()
    $("#shutout").hide();
})


/*
* 详细信息弹窗
*
* avatar:显示文件图片
* name:文件名
* size:文件大小
* path:下载路径
* ctime:创建时间
*
* */
function detailsdata(avatar,name,size,path,ctime) {
    document.getElementById("avatar1").src = avatar;
    document.getElementById("filename").innerText = name;
    name=name.substring(0,27);
    document.getElementById("filename1").innerText = name;
    size=(size/1024);
    if (size<1024){
        document.getElementById("filesize1").innerText = size.toFixed(2)+"KB";
    }else if (size>=1024&&size<1048576){
        document.getElementById("filesize1").innerText = (size/1024).toFixed(2)+"MB";
    }else{
        document.getElementById("filesize1").innerText = (size/1024/1024).toFixed(2)+"GB";
    }
    document.getElementById("filepath1").innerText = path;
    document.getElementById("uploadtime1").innerText = ctime;

    var elementsByClassName = document.getElementsByClassName("rightsub-list");
    for (let i = 0; i < elementsByClassName.length; i++) {
        elementsByClassName[i].style.display = "none"
    }
    document.getElementById("lookinfo").style.display = "block";
}

// 复制连接到粘贴板
function copylink() {
    var link = document.getElementById("linkid").innerText;
    var hidetext = document.getElementById("hidetext");
    hidetext.value = link;
    hidetext.select();
    document.execCommand("copy")
    var layer = layui.layer;
    // 使用正则表达式提取请求路径
    /*var regex = /https?:\/\/.*?(\/[^?]+)/;
    var match = link.match(regex);
    if (match && match.length > 1) {
        var path = match[1];
        console.log("提取的请求路径：" + path);
    } else {
        console.log("无法提取请求路径");
    }*/
    // 创建 URL 对象
    var url = new URL(link);

// 提取剩余部分
    var path = url.pathname + url.search ;

    console.log("剩余部分:", path);
    $.ajax({
        url:"/share/urlsetvalid",
        data:{
            urlpath:path
        },
        dataType:"json",
        method:"GET",
        error:function(){
            layer.msg("分享失败");
        },
        success:function(res){
            if(res.code == 200){
                layer.msg(res.msg);
            }
        }
    })
}


;(function($){
        /*
        * 对各个节点进行获取
        * */
        var $data = new Date();
        var $_showfiles = $("#showfiles");
        var $_showRecentlyfile=$("#showRecentlyfile");
        var $afterbutton=$("#afterbutton");
        var $prebutton =$("#prebutton");
        var $firstbutton=$("#firstbutton");
        var currentpage;
        var currentfilecount=5;
        var pages;
        var typeName;
        /*
        * 显示文件封装
        *
        * */
        function showPagesInfo(respone) {

            respone=respone.pageInfo;
            pages=respone.pages; /*获取总页数*/
            currentpage =respone.pageNum;
            for (var i=0;i<respone.size;i++){
                var $divfile=$("<div class='file'></div>");
                var $afile=$("<a></a>");
                if(respone.list[i].typeName=='image'){
                    var $image=$("<div class='image'> " +
                       /* "<i class='entypo-down download' info='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"' style='font-size: 30px;position: absolute;top: 25px;'></i> "+*/
                        "<img alt='image' class='img-responsive file-image' src='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"'>"+
                        "<i class='fadeIn animated bx bx-list-ul' style='font-size: 30px;position: absolute;top: 30px;left: 10px; cursor: pointer;'></i>"+
                        "<i style='font-size: 30px;position: absolute;top: 30px;left: 130px ;cursor: pointer;'><input class='form-check-input' type='checkbox' id='inlineCheckbox1' value='option1' style='cursor: pointer;'/></i>"+
                        /*"<i class='entypo-cancel delete' info='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"' style='font-size: 30px;position: absolute;top: 25px;left: 130px'></i>"+*/
                        "</div>");

                    var $fileName=$("<div class='file-name'>"+
                        "<div class='file-name2'>"+respone.list[i].fileName+"</div>"+
                        "<span>"+respone.list[i].insertTime+"</span>"+
                        "</div>");
                    $afile.append($image);
                    $afile.append($fileName);
                }else if(respone.list[i].typeName == 'audio'){
                    var $audio=$("<div class='icon'> " +
                      /*  "<i class='entypo-down download' info='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"' ></i> "+*/
                        "<i class='fa fa-music'></i>"+
                        "<i class='fadeIn animated bx bx-list-ul' style='font-size: 30px;position: absolute;top: 35px;left: 10px; cursor: pointer;'></i>"+
                        "<i style='font-size: 30px;position: absolute;top: 30px;left: 130px ;cursor: pointer;'><input class='form-check-input' type='checkbox' id='inlineCheckbox1' value='option1' style='cursor: pointer;'/></i>"+
                        /*"<i class='entypo-cancel delete' info='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"'  ></i>"+*/
                        "</div>");
                    var $fileName=$("<div class='file-name'>"+
                        "<div class='file-name2'>"+respone.list[i].fileName+"</div>"+
                        "<span>"+respone.list[i].insertTime+"</span>"+
                        "</div>");
                    $afile.append($audio);
                    $afile.append($fileName);
                }else if(respone.list[i].typeName == 'video'){
                    var $video=$("<div class='icon'> " +
                       /*"<i class='entypo-down download' info='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"' ></i> "+*/
                        "<i class='img-responsive fa fa-film'></i>"+
                        "<i class='fadeIn animated bx bx-list-ul' style='font-size: 30px;position: absolute;top: 35px;left: 10px; cursor: pointer;'></i>"+
                        "<i style='font-size: 30px;position: absolute;top: 30px;left: 130px ;cursor: pointer;'><input class='form-check-input' type='checkbox' id='inlineCheckbox1' value='option1' style='cursor: pointer;'/></i>"+
                       /* "<i class='entypo-cancel delete' info='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"' ></i>"+*/
                        "</div>");
                    var $fileName=$("<div class='file-name'>"+
                        "<div class='file-name2'>"+respone.list[i].fileName+"</div>"+
                        "<span>"+respone.list[i].insertTime+"</span>"+
                        "</div>");
                    $afile.append($video);
                    $afile.append($fileName);

                }else{
                    var $file=$("<div class='icon'> " +
                        /*"<i class='entypo-down download' info='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"' ></i> "+*/
                        "<i class='fa fa-file'></i>"+
                        "<i class='fadeIn animated bx bx-list-ul' style='font-size: 30px;position: absolute;top: 35px;left: 10px; cursor: pointer;'></i>"+
                        "<i style='font-size: 30px;position: absolute;top: 30px;left: 130px ;cursor: pointer;'><input class='form-check-input' type='checkbox' id='inlineCheckbox1' value='option1' style='cursor: pointer;'/></i>"+
                        /* "<i class='entypo-cancel delete' info='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"'></i>"+*/
                        "</div>");
                    var $fileName=$("<div class='file-name'>"+
                        "<div class='file-name2'>"+respone.list[i].fileName+"</div>"+
                        "<span>"+respone.list[i].insertTime+"</span>"+
                        "</div>");
                    $afile.append($file);
                    $afile.append($fileName);
                }
                var $tip="<div class='rightsub-list' style='display: none'>" +
                    "                <ul style='margin: 0;padding: 0;'>" +
                    "                    <li style='margin-top: 3px;'class='download' info='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"'>下载</li>" +
                    "                    <li onclick='share(`"+respone.list[i].fileLocation+'/'+respone.list[i].fileName+"`,`"+respone.list[i].typeName+"`)'><a>分享</a></li>" +
                    "                    <li class='delete' info='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"'>删除</li>" +
                    "                    <li onclick='detailInfo(`"+respone.list[i].typeName+"`,`"+respone.list[i].fileName+"`,`"+respone.list[i].size+"`,`"+respone.list[i].fileLocation+"`,`"+respone.list[i].insertTime+"`)'><a>查看详细信息</a></li>" +
                    "                    <input type='hidden' id='"+respone.list[i].fid+"'>"+
                    "                </ul>" +
                    "            </div>";
                $divfile.append($afile);
                $divfile.append($tip);
                $_showfiles.append($divfile);
                $(".fadeIn").css("display","none");
                $("#showfiles .form-check-input").css("display","none");
            }
        }
        /*
        * 显示最近文件封装
        * */
        function showCurrentInfo(respone){

            respone=respone.pageInfo;
            pages=respone.pages;
            if(respone.size<5){
                currentfilecount=respone.size;
            }
            for(var i=0;i<currentfilecount;i++){
                var $tr=$("<tr>" +
                            "<td>" +
                                "<div class='d-flex align-items-center'>" +
                                "<div><i class='bx bxs-file me-2 font-24 text-primary'></i></div>" +
                                "<div class='font-weight-bold text-primary' >"+respone.list[i].fileName.substring(0,45)+"</div></div>" +
                            "</td>" +
                            "<td>"+(respone.list[i].size/1048576).toFixed(2)+"M</td>" +
                            "<td>"+respone.list[i].insertTime+"</td>" +
                            "<td>" +
                                "<div class='col'>" +
                                    "<div class='btn-group'>" +
                                        "<button type='button' class='btn btn-secondary'>操作</button>" +
                                        "<button type='button' class='btn btn-secondary split-bg-secondary dropdown-toggle dropdown-toggle-split' data-bs-toggle='dropdown' aria-expanded='false'></button>" +
                                        "<ul class='dropdown-menu'>" +
                                            "<li><a class='dropdown-item download' info='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"'>下载</a></li>" +
                                            "<li><hr class='dropdown-divider'></li>" +
                                            "<li><a class='dropdown-item delete' info='"+respone.list[i].fileLocation+"/"+respone.list[i].fileName+"'  >删除</a></li>" +
                                        "</ul>" +
                                    "</div>" +
                                "</div>" +
                            "</td>" +
                    "</tr>");
                $_showRecentlyfile.append($tr);
            }
                currentfilecount=5;
        }
     /*
     * 显示页面所有文件
     *
     * */

        $.ajax({
               url:"/filesInfo",
                method:"post",
                async:false,
                dataType:"json",
                error:function(){
                      alert("请检查网络情况,或者联系管理员");
                },
                success:function(respone){

                  /*  var $fileName=$("<div class='file-name'>"+
                        "<div class='file-name2'>Monica's birthday.mpg4</div>"+
                        "<span>Added: Fab 18, 2016</span>" +
                        "</div>");*/
                    downpath = respone.sharedownlocaltion;
                    showPagesInfo(respone);
                    showCurrentInfo(respone);

                    $("#showfiles").on("mouseover","img,.fa",function(){
                        $(".fadeIn").stop(false,true);
                        $(".form-check-input").stop(false,true);
                        $(this).siblings(".fadeIn").fadeIn("slow",function(){});
                        $(this).siblings().children(".form-check-input").fadeIn("slow",function(){});
                        $(this).css("opacity","0.4");
                        return false;
                    });

                    $("#showfiles").on("mouseleave",".icon,.image",function(){
                        $(this).children(".fadeIn").fadeOut("slow",function(){});
                        $(this).children("i").children(".form-check-input").fadeOut("slow",function(){});
                        $(".fadeIn").stop(false,true);
                        $(".form-check-input").stop(false,true);
                        $(this).children("img,.fa").css("opacity","1");
                    });
                /*
                * 下列该代码是给选中的一个效果
                * */
                    $("#showfiles").on("click",".form-check-input",function(){
                        if($(this).prop("checked")){
                            var $this =$(this).parents(".file").find("input[type=hidden]").prop("id");
                            if($(this).parents(".icon").length>0){
                                $(this).parents(".icon").prop("id",$this);
                            }else{
                                $(this).parents(".image").prop("id",$this);
                            }

                            $(this).parents("#showfiles").off("mouseleave",".icon,.image");
                            $("#showfiles").on("mouseleave",".icon:not([id]),.image:not([id])",function(){
                                $(this).children(".fadeIn").fadeOut("slow",function(){});
                                $(this).children("i").children(".form-check-input").fadeOut("slow",function(){});
                                $(".fadeIn").stop(false,true);
                                $(".form-check-input").stop(false,true);
                                $(this).children("img,.fa").css("opacity","1");
                            });
                        }else{
                            $(this).parents("#showfiles").off("mouseleave",".icon:not([id]),.image:not([id])");
                            if($(this).parents(".icon").length>0){
                                $(this).parents(".icon").removeAttr("id");
                            }else{
                                $(this).parents(".image").removeAttr("id");
                            }
                            $("#showfiles").on("mouseleave",".icon:not([id]),.image:not([id])",function(){
                                $(this).children(".fadeIn").fadeOut("slow",function(){});
                                $(this).children("i").children(".form-check-input").fadeOut("slow",function(){});
                                $(".fadeIn").stop(false,true);
                                $(".form-check-input").stop(false,true);
                                $(this).children("img,.fa").css("opacity","1");
                            });
                        }

                    });

                    /*对分页的按钮生成*/
                    if(pages<3){
                        for(var i=1;i<=pages;i++){
                            var lione=$("<li id='pagebutton"+i+"' class='page-item'><a class='page-link' href='javascript:;'>"+i+"</a></li>");
                            if(i==1){
                                $prebutton.after(lione);
                            }else{
                                $afterbutton.before(lione);
                            }
                        }

                    }else{
                        for(var i=1;i<=3;i++){
                            var lione=$("<li id='pagebutton"+i+"' class='page-item'><a class='page-link' href='javascript:;'>"+i+"</a></li>");
                            if(i==1){
                                $prebutton.after(lione);
                            }else{
                                $afterbutton.before(lione);
                            }

                        }

                    }

                }


        });

        /*
        *
        * 图片
        **/
        var imageSize=0;
        $.ajax({
            url:"/filesInfo2/image",
            method:"post",
            dataType:"json",
            async:false,
            error:function(){
                alert("请检查网络情况,或者联系管理员");
            },
            success:function(respone){
                for(var i=0;i<respone.files.length;i++){
                   eval(imageSize+=parseFloat(respone.files[i].size));
                }
                var $imageCounts=$("#imageCounts");
                var $imageSize=$("#imageSize");
                $imageSize.text((imageSize/(1024*1024*1024)).toFixed(3)+"GB");
                $imageCounts.text(respone.files.length+" files");
            }


        });

        /*
        * 音频
        *
        * */
        var audioSize=0;
        $.ajax({
            url:"/filesInfo2/audio",
            method:"post",
            dataType:"json",
            async:false,
            error:function(){
                alert("请检查网络情况,或者联系管理员");
            },
            success:function(respone){
                for(var i=0;i<respone.files.length;i++){
                        eval(audioSize+=parseFloat(respone.files[i].size));
                }
                var $audioCounts=$("#audioCounts");
                var $audioSize=$("#audioSize");
                $audioSize.text((audioSize/(1024*1024*1024)).toFixed(3)+"GB");
                $audioCounts.text(respone.files.length+" files");
            }


    });
           /*
            * 视频
            *
            * */
         var videoSize=0;
         $.ajax({
                    url:"/filesInfo2/video",
                    method:"post",
                    dataType:"json",
                    async:false,
                    error:function(){
                        alert("请检查网络情况,或者联系管理员");
                    },
                    success:function(respone){
                        for(var i=0;i<respone.files.length;i++){
                            eval(videoSize+=parseFloat(respone.files[i].size));

                        }
                        var $videoCounts=$("#videoCounts");
                        var $videoSize=$("#videoSize");
                        $videoSize.text((videoSize/(1024*1024*1024)).toFixed(3)+"GB");
                        $videoCounts.text(respone.files.length+" files");
                    }


                });
                /*
                 * 其他文件
                 *
                 * */
         var otherSize=0;
         $.ajax({
                    url:"/filesInfo2/file",
                    method:"post",
                    dataType:"json",
                    async:false,
                    error:function(){
                        alert("请检查网络情况,或者联系管理员");
                    },
                    success:function(respone){
                        for(var i=0;i<respone.files.length;i++){
                            eval(otherSize+=parseFloat(respone.files[i].size));
                        }
                        var count=0;
                        /*
                        * 其他未定义的文件
                        * */
                        $.ajax({
                           url:"/filesInfo2/other",
                            method:"post",
                            dataType:"json",
                            async:false,
                            error:function(){
                                alert("请检查网络情况,或者联系管理员");
                            },
                            success:function (respones) {
                                for(var i=0;i<respones.files.length;i++){
                                    eval(otherSize+=parseFloat(respones.files[i].size));
                                }
                                count=respones.files.length;
                            }
                        });
                        var $fileCounts=$("#fileCounts");
                        var $fileSize=$("#fileSize");
                        $fileSize.text((otherSize/(1024*1024*1024)).toFixed(3)+"GB");
                        $fileCounts.text((respone.files.length+count)+" files");
                    }


                });

        /*
        * 总容量
        * */
        $.ajax({
            url:"/filesInfo2/capacity",
            method:"post",
            async:false,
            dataType:"json",
            error:function(){
                alert("请检查网络情况,或者联系管理员");
            },
            success:function(respone){
                var test=(1024*1024*1024*1024);

                var allSize=parseInt(respone.capacity.capacitySize);
                var $Capacity=$("#Capacity");
                var test2=eval((audioSize/allSize).toFixed(2)*100);
                $("#imagebar").css("width",eval((imageSize/allSize).toFixed(2)*100)+"%");
                $("#audiobar").css("width",eval((audioSize/allSize).toFixed(2)*100)+"%");
                $("#videobar").css("width",eval((videoSize/allSize).toFixed(2)*100)+"%");
                $("#otherbar").css("width",eval((otherSize/allSize).toFixed(2)*100)+"%");
                $Capacity.html(((imageSize+audioSize+videoSize+otherSize)/(1024*1024*1024)).toFixed(3)+"GB<span  class='float-end text-secondary'>"+(allSize/(1024*1024*1024)).toFixed(3)+"GB</span>");
            }

        });

        /*
        * 分页按钮处理
        *
        * */
        $prebutton.on("click",function(){
            var i=0;
            var prepage=parseInt($("#pagebutton1>a").text());
           if(prepage!=1){
                $(".pagebar li:hidden").show();
               $("#pagebutton1>a").text(prepage-3);
               $("#pagebutton2>a").text(prepage-2);
               $("#pagebutton3>a").text(prepage-1);
           }
        });
        /*
        * 点击按钮分页显示
        *
        * */

        $afterbutton.on("click",function() {
            var afterpage = parseInt($("#pagebutton3>a").text());
            if(typeof(afterpage) && afterpage<pages)
            for (var i = 1; i <= 3; i++) {
                if ((afterpage+i) <= pages) {
                    var id="#pagebutton"+i+">a";
                    $(id).text(afterpage+i);
                } else {
                    $("#pagebutton" + i).hide();
                }
            }

        });
        $(".pagebar li>a").off("click");
        $(".pagebar").on("click","li>a",function(){

            var pagenum=$(this).text();
            if(pagenum!="»" && pagenum!="«"){
                $.ajax({
                    url:"/filesInfo",
                    method:"post",
                    asyn:false,
                    data:{page:pagenum},
                    dataType:"json",
                    error:function(){
                        alert("请检查网络情况,或者联系管理员");
                    },
                    success:function(respone){
                        $("#showfiles>.file").remove();
                        showPagesInfo(respone);
                    }
                });

            }
        });

        /*
        * 添加下载和删除事件
        *
        * */
        $("#showfiles").on("mouseleave",".file",function(){
            $(".rightsub-list").slideUp("fast");
            $(this).stop(false,true);
            $(this).stop(false,true);
        });
        $("#showfiles").on("click",".fadeIn",function (){
           $(this).parents("a").siblings(".rightsub-list").slideDown("normal");
        });
        $("#showfiles").on("click",".download",function() {
            var infos = $(this).attr("info");
            var a = document.createElement('a');
            var url = 'download/?path='+infos;
            var filename = infos;
            a.href = url;
            a.download = filename;
            a.click();
        });
        $("#showfiles").on("click",".delete",function(){
           var flag =confirm("确定删除");
           var infos=$(this).attr("info");
            if(flag){
                $.ajax({
                    url:"/delete",
                    method:"post",
                    async:"false",
                    dataType:"json",
                    data:{path:infos},
                    error:function(){
                        alert("删除失败,请稍后在试");
                    },
                    success:function(respone){
                        if(respone.states == 'invalid'){
                            alert("删除失败,请稍后在试");

                        }else if(respone.states == 'success') {

                            $.ajax({
                                url: "/filesInfo",
                                method: "post",
                                async: false,
                                data: {page: currentpage},
                                dataType: "json",
                                error: function () {
                                    alert("请检查网络情况,或者联系管理员");
                                },
                                success: function (respone) {
                                    $("#showfiles>.file").remove();
                                    showPagesInfo(respone);
                                    $("#showRecentlyfile>tr").remove();
                                    showCurrentInfo(respone);
                                    /*
                                    * 分页按钮
                                    * */

                                    $(".pagebar .pagination>li").not("#prebutton,#afterbutton").remove();
                                    if(pages<3){
                                        for(var i=1;i<=pages;i++){
                                            var lione=$("<li id='pagebutton"+i+"' class='page-item'><a class='page-link' href='javascript:;'>"+i+"</a></li>");
                                            if(i==1){
                                                $prebutton.after(lione);
                                            }else{
                                                $afterbutton.before(lione);
                                            }
                                        }

                                    }else{
                                        for(var i=1;i<=3;i++){
                                            var lione=$("<li id='pagebutton"+i+"' class='page-item'><a class='page-link' href='javascript:;'>"+i+"</a></li>");
                                            if(i==1){
                                                $prebutton.after(lione);
                                            }else{
                                                $afterbutton.before(lione);
                                            }

                                        }

                                    }
                                    alert("删除成功");
                                }
                            });

                        }
                    }
                 })
            }

        });


        /*
        *
        * 对最近文件进行下载删除处理
        *
        *
        *
        * */
        $("#showRecentlyfile").on("click",".download",function(){
            var infos = $(this).attr("info");
            var a = document.createElement('a');
            var url = 'download/?path='+infos;
            var filename = infos;
            a.href = url;
            a.download = filename;
            a.click();
        });

        $("#showRecentlyfile").on("click",".delete",function(){
        var flag =confirm("确定删除");
        var infos=$(this).attr("info");
        var $tr=$(this).parents("tr");

        if(flag){
            $.ajax({
                url:"/delete",
                method:"post",
                async:"false",
                dataType:"json",
                data:{path:infos},
                error:function(){
                    alert("删除失败,请稍后在试");
                },
                success:function(respone){
                    if(respone.states == 'invalid'){
                        alert("删除失败,请稍后在试");

                    }else if(respone.states == 'success') {

                        $.ajax({
                            url: "/filesInfo",
                            method: "post",
                            async: false,
                            data: {page: currentpage},
                            dataType: "json",
                            error: function () {
                                alert("请检查网络情况,或者联系管理员");
                            },
                            success: function (respone) {
                                $("#showfiles>.file").remove();
                                showPagesInfo(respone);
                                $("#showRecentlyfile>tr").remove();
                                showCurrentInfo(respone);
                                /*
                                * 分页按钮
                                * */
                                $(".pagebar .pagination>li").not("#prebutton,#afterbutton").remove();
                                if(pages<3){
                                    for(var i=1;i<=pages;i++){
                                        var lione=$("<li id='pagebutton"+i+"' class='page-item'><a class='page-link' href='javascript:;'>"+i+"</a></li>");
                                        if(i==1){
                                            $prebutton.after(lione);
                                        }else{
                                            $afterbutton.before(lione);
                                        }
                                    }

                                }else{
                                    for(var i=1;i<=3;i++){
                                        var lione=$("<li id='pagebutton"+i+"' class='page-item'><a class='page-link' href='javascript:;'>"+i+"</a></li>");
                                        if(i==1){
                                            $prebutton.after(lione);
                                        }else{
                                            $afterbutton.before(lione);
                                        }

                                    }

                                }



                                alert("删除成功");
                            }
                        });
                    }
                }
            })
        }

    });
        /*
        *
        *不同类型显示
        *
        * */
            $("div.fm-menu>div>a").on("click",function(){
                typeName=$(this).attr("typeName");
                $.ajax({
                    url:"/filesInfoType",
                    method:"post",
                    async:false,
                    dataType:"json",
                    data:{typename:typeName},
                    error:function(){
                        alert("加载失败,请检查网络");
                        window.location.href='/fileManager'
                    },
                    success:function(respone){
                        $("#showfiles>.file").remove();
                        showPagesInfo(respone);
                        for(var i=1;i<=3;i++){
                            $("#pagebutton"+i).remove();
                        }
                        if(pages<3){
                            for(var i=1;i<=pages;i++){
                                var lione=$("<li id='pagebutton"+i+"' class='page-item'><a class='page-link' href='javascript:;'>"+i+"</a></li>");
                                if(i==1){
                                    $prebutton.after(lione);
                                }else{
                                    $afterbutton.before(lione);
                                }
                            }
                        }else{
                            for(var i=1;i<=3;i++){
                                var lione=$("<li id='pagebutton"+i+"' class='page-item'><a class='page-link' href='javascript:;'>"+i+"</a></li>");
                                if(i==1){
                                    $prebutton.after(lione);
                                }else{
                                    $afterbutton.before(lione);
                                }

                            }
                        }
                        $(".pagebar li a").off("click");
                        $(".pagebar").on("click","li>a",function(){

                            var pagenum=$(this).text();
                            if(pagenum!="»" && pagenum!="«"){
                                $.ajax({
                                    url:"/filesInfoType",
                                    method:"post",
                                    asyn:false,
                                    data:{page:pagenum,typename:typeName},
                                    dataType:"json",
                                    error:function(){
                                        alert("请检查网络情况,或者联系管理员");
                                    },
                                    success:function(respone){
                                        $("#showfiles>.file").remove();
                                        showPagesInfo(respone);
                                    }
                                });

                            }




                        });
                    }
                })
            });








            /*
            *
            * 对搜索框操作
            * */
    // 打开搜索框
    $(".input-group-text").click(function (e) {
        $("#search-win").toggle()
        $("#sortlist").hide();
        e.stopPropagation()
    });
    // 关闭搜索框
    $("#close").click(function () {
        $("#search-win").hide()
    });
    $("#clear").click(function (){
        var searchinput = document.getElementById("search-input");
        var frame = document.getElementById("tip-frame");
        var win = document.getElementById("search-win");
        frame.style.display = "block";
        win.style.height = "180px";
        searchinput.value="";
    })


    /*
    * 对全选按钮的事件
    *
    * */
    var flag =true;
    $(".input-group .btn-success").on("click",function(){
        if(flag){
            $("#showfiles").find("input:not(:checked)").trigger("click");
            $(".fadeIn").stop(false,true);
            $(".form-check-input").stop(false,true);
            $(".fadeIn").fadeIn("slow",function(){});
            $("#showfiles .form-check-input").fadeIn("slow",function(){});
            $(".img-responsive,.fa").css("opacity","0.4");
            $(this).html("<svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' " +
                "viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' " +
                "stroke-linejoin='round' class='feather feather-minus-circle text-primary' style='color:white !important;'>" +
                "<circle cx='12' cy='12' r='10'></circle>" +
                "<line x1='8' y1='12' x2='16' y2='12'></line>" +
                "</svg>");
            $(this).append("取消全选");
            flag=false;
        }else{
            $("#showfiles").find("input:checked").trigger("click");
            $(".fadeIn").fadeOut("slow",function(){});
            $("#showfiles .form-check-input").fadeOut("slow",function(){});
            $(".fadeIn").stop(false,true);
            $("#showfiles .form-check-input").stop(false,true);
            $(".img-responsive,.fa").css("opacity","1");
            $(this).html("<svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none'" +
                " stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'" +
                " class='feather feather-check-circle text-primary' style='color:white !important;'>" +
                "<path d='M22 11.08V12a10 10 0 1 1-5.93-9.14'></path>" +
                "<polyline points='22 4 12 14.01 9 11.01'></polyline>" +
                "</svg>")
            $(this).append("全部选择");
            flag=true;
        }

    });

    /*
    * 批量下载
    *
    * */
    $(".input-group .btn-info").on("click",function(){
        var flag=confirm("是否要下载选中文件");
        if(flag){
            var $checkNum =$("input:checked");
            if($checkNum.length>1){
                $checkNum.parents(".file").find(".download").trigger("click");
            } else alert("请选择下载文件");
         }
    });

    /*
    * 批量删除
    * */

    $(".input-group .btn-danger").on("click",function(){
        var flag=confirm("是否要删除选中文件")
        if(flag){
            var $checkNum =$("input:checked");
            if($checkNum.length>1){
                var $fileName  = $checkNum.parents(".file").find(".download");
                var infos = "";
                for(var i=0;i<$fileName.length;i++){
                    if(i!=$fileName.length-1)
                        infos+=$fileName[i].getAttribute("info")+"--->";
                    else
                        infos+=$fileName[i].getAttribute("info");
                }
                $.ajax({
                    url:"/batchdelete",
                    method:"post",
                    async:"false",
                    dataType:"json",
                    data:{path:infos},
                    error:function(){
                        alert("删除失败,请稍后在试");
                    },
                    beforeSend: function(e){
                        layui.layer.msg('删除中', {icon: 16, time: 0});
                    },
                    success:function(respones){
                        if(respones.states == 'invalid'){
                            layui.layer.msg("删除失败,请稍后在试:"+respones.msg);
                        }else if(respones.states == 'success') {
                            $.ajax({
                                url: "/filesInfo",
                                method: "post",
                                async: false,
                                data: {page: currentpage},
                                dataType: "json",
                                error: function () {
                                    alert("请检查网络情况,或者联系管理员");
                                },
                                success: function (respone) {
                                    $("#showfiles>.file").remove();
                                    showPagesInfo(respone);
                                    $("#showRecentlyfile>tr").remove();
                                    showCurrentInfo(respone);
                                    /*
                                    * 分页按钮
                                    * */
                                    $(".pagebar .pagination>li").not("#prebutton,#afterbutton").remove();
                                    if(pages<3){
                                        for(var i=1;i<=pages;i++){
                                            var lione=$("<li id='pagebutton"+i+"' class='page-item'><a class='page-link' href='javascript:;'>"+i+"</a></li>");
                                            if(i==1){
                                                $prebutton.after(lione);
                                            }else{
                                                $afterbutton.before(lione);
                                            }
                                        }
                                    }else{
                                        for(var i=1;i<=3;i++){
                                            var lione=$("<li id='pagebutton"+i+"' class='page-item'><a class='page-link' href='javascript:;'>"+i+"</a></li>");
                                            if(i==1){
                                                $prebutton.after(lione);
                                            }else{
                                                $afterbutton.before(lione);
                                            }
                                        }
                                    }
                                    layui.layer.msg('批量删除成功',{icon:1});
                                }
                            });

                        }
                    }
                })
            }else alert("请选择删除文件");
        }

    })

    /*
    * 搜索
    *
    * */
    $(".search-frame .btn-success").on("click",function(){
       var val = $("#search-input").val();
       var filename;
        if(val.indexOf("图片:")==0){
            typeName="image";
            filename=val.substring(val.indexOf(":")+1);
        }else if(val.indexOf("视频:")==0){
            typeName="video";
            filename=val.substring(val.indexOf(":")+1);
        }else if(val.indexOf("音频:")==0){
            typeName="audio";
            filename=val.substring(val.indexOf(":")+1);
        }else if(val.indexOf("压缩文件:")==0){
            typeName="package";
            filename=val.substring(val.indexOf(":")+1);
        }else if(val.indexOf("文档:")==0){
            typeName="doc";
            filename=val.substring(val.indexOf(":")+1);
        }else{
            typeName="all";
            filename=val;
        }
        $.ajax({
            url:'/searchFile',
            method:'post',
            async:'false',
            dataType:'json',
            data:{typename:typeName,filename:filename},
            error:function(){
                alert("加载失败,请检查网络");
                window.location.href='/fileManager'
            },
            success:function(respone){
                $("#showfiles>.file").remove();
                showPagesInfo(respone);
                for(var i=1;i<=3;i++){
                    $("#pagebutton"+i).remove();
                }
                if(pages<3){
                    for(var i=1;i<=pages;i++){
                        var lione=$("<li id='pagebutton"+i+"' class='page-item'><a class='page-link' href='javascript:;'>"+i+"</a></li>");
                        if(i==1){
                            $prebutton.after(lione);
                        }else{
                            $afterbutton.before(lione);
                        }
                    }
                }else{
                    for(var i=1;i<=3;i++){
                        var lione=$("<li id='pagebutton"+i+"' class='page-item'><a class='page-link' href='javascript:;'>"+i+"</a></li>");
                        if(i==1){
                            $prebutton.after(lione);
                        }else{
                            $afterbutton.before(lione);
                        }

                    }
                }
                $(".pagebar li a").off("click");
                $(".pagebar").on("click","li>a",function(){
                    var pagenum=$(this).text();
                    if(pagenum!="»" && pagenum!="«"){
                        $.ajax({
                            url:"/filesInfoType",
                            method:"post",
                            asyn:false,
                            data:{page:pagenum,typename:typeName},
                            dataType:"json",
                            error:function(){
                                alert("请检查网络情况,或者联系管理员");
                            },
                            success:function(respone){
                                $("#showfiles>.file").remove();
                                showPagesInfo(respone);
                            }
                        });

                    }
                });
            }
        })


    });






    /*
    *
    *
    *添加文件按键事件
    *
    *
    *
    * $("#addFile").on("change",function(){
            var formData = new FormData();
            var files=document.getElementById("addFile").files[0];
            formData.append("files",files);
            $.ajax({
               url:"/upload",
                async:true,
                method:"post",
                dataType:"json",
                data:formData,
                processData:false,
                contentType:false,
                error:function(){
                   alert("上传失败,请检查网络");
                },
                success:function(respone){
                   if(respone.status=='invalid'){
                       alert(respone.message);
                   }else if(respone.status == 'success'){
                       alert(respone.message);
                       window.location.href="/fileManager";
                   }
                }

            });
    });

    *
    *
    *
    * */



})(jQuery)