package com.xiuGEN.config.email;

public class MergeEmailMessageInfo {
    private  String title="多功能用户管理网站";
    private String time;
    private String code;
    public MergeEmailMessageInfo(String time, String code){
        this.time=time;
        this.code = code;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return "<p>您好 O(∩_∩)O~~<br><br>欢迎加入XIUGEN<br><br>邮箱验证码："+this.getCode()
                +"你正在进行账号合并，切勿将验证码告知他人,验证码有效时间:"+this.getTime()+"分钟)";
    }



}
