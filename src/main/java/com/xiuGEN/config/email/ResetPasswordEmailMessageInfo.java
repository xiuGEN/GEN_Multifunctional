package com.xiuGEN.config.email;

public class ResetPasswordEmailMessageInfo {
    private  String title="多功能用户管理网站";
    private String href;
    private String id;
    public ResetPasswordEmailMessageInfo(String href, String id){
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
        return "<p>您好 O(∩_∩)O~~<br><br>XIUGEN<br><br>帐户是否需要重置密码，"+
                "如需要重置,请确保本人点击下方链接进行重置密码)" +
                "<br><br>请在30分钟内点击下面的链接立即重置帐户密码： "+
                "<br><a href='"+getHref()+"?id="+getId()+"'>"+getHref()+"?id="+getId()+"</a></p>";
    }

}
