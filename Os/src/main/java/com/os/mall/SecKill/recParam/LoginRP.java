package com.os.mall.SecKill.recParam;

public class LoginRP {
    private String mobile;

    private String password;

    @Override
    public String toString() {
        return "LoginRecParam{" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
