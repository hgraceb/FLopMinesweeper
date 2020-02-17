package com.flop.minesweeper.bean;

/**
 * 用户实体类
 * <p>
 * Created by Flop on 2020/2/17.
 */
public class Player {
    /** 用户账号 */
    private String mAccount;
    /** 用户密码 */
    private String mPassword;
    /** 用户头像地址 */
    private String mAvatar;
    /** 用户ID */
    private String mId;
    /** 用户cookie */
    private String mCookie;

    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String mAccount) {
        this.mAccount = mAccount;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getCookie() {
        return mCookie;
    }

    public void setCookie(String mCookie) {
        this.mCookie = mCookie;
    }
}
