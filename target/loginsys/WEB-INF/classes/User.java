package com.user;

public class User {
    private String username;
    private String[] userDetail;

    private String loginTime;

    public User(String username, String[] userDetail, String loginTime) {
        this.username = username;
        this.userDetail = userDetail;
        this.loginTime = loginTime;
    }

    public String[] getUserDetail() {
        return userDetail;
    }

    public String getUsername() {
        return username;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public void setUserDetail(String[] userDetail) {
        this.userDetail = userDetail;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
