package com.xiuGEN.config.email;

public class RegisterEmailMessageInfo {
    private  String title="多功能用户管理网站";
    private String href;
    private String id;
    public RegisterEmailMessageInfo(String href, String id){
        this.href=href;
        this.id=id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return "<p>您好 O(∩_∩)O~~<br><br>欢迎加入XIUGEN<br><br>帐户需要激活才能使用，"+
                "赶紧激活成为XIUGEN正式的一员吧:)" +
                "<br><br>请在30分钟内点击下面的链接立即激活帐户： "+
                "<br><a href='"+getHref()+"?id="+getId()+"'>"+getHref()+"?id="+getId()+"</a></p>";
    }



}
