package com.flop.minesweeper.util;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.flop.minesweeper.bean.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * 扫雷网工具类
 * <p>
 * Created by Flop on 2020/2/17.
 */
public class SaoleiUtil {

    /** 扫雷网网址 */
    private static final String BASE_URL = "http://www.saolei.wang/";
    /** 登录链接 */
    private static final String LOGIN_URL = BASE_URL + "Player/Action/Login_Action.asp";
    /** 登录用户名 */
    private static final String LOGIN_PLAYER_NAME = "Player_Name";
    /** 登录密码 */
    private static final String LOGIN_PLAYER_PASSWORD = "Player_Password";
    /** 登出链接 */
    public static final String LOGOUT_URL = BASE_URL + "Player/Action/Quit_Action.asp";

    /** 默认返回数据的编码格式 */
    private static final String RESPONSE_CONTENT_TYPE = "text/html; charset=gb2312";

    /** 登录对应的消息代码 */
    public static final int LOGIN_WHAT = 2333;
    /** 登出对应的消息代码 */
    public static final int LOGOUT_WHAT = 2334;

    /**
     * 登录接口
     *
     * @param username 用户名
     * @param password 密码
     * @param what     用户定义的消息代码
     * @param handler  处理异步消息
     */
    public static void login(@NonNull String username, @NonNull String password, int what, @NonNull Handler handler) {
        Map<String, String> params = new HashMap<>();
        params.put(LOGIN_PLAYER_NAME, username);
        params.put(LOGIN_PLAYER_PASSWORD, password);
        // 如果有对应的登录请求则先取消
        VolleyUtil.cancelRequest(what);
        new VolleyUtil.Builder().setUrl(LOGIN_URL).setHandler(handler)
                .setBundle(new Bundle()).setWhat(what).setParams(params)
                .setRequestHeaders(getRequestHeaders()).setResponseHeaders(getResponseHeaders()).post();
    }

    /**
     * 获取请求头
     */
    private static Map<String, String> getRequestHeaders() {
        Map<String, String> requestHeaders = new HashMap<>();
        // 设置cookie
        requestHeaders.put("Cookie", "ASPSESSIONIDQAQDTBTS=HMFFCEKALMLBCCLAFLJGNNCF");
        return requestHeaders;
    }

    /**
     * 获取响应头
     */
    private static Map<String, String> getResponseHeaders() {
        Map<String, String> responseHeaders = new HashMap<>();
        // 设置请求数据的解析编码格式
        responseHeaders.put("Content-Type", RESPONSE_CONTENT_TYPE);
        return responseHeaders;
    }

    /**
     * 保存用户
     */
    public static void saveUser(Player player) {

    }
}