package com.flop.minesweeper.errorLogInfo;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.orhanobut.hawk.Hawk;

/**
 * 初始化应用
 * <p>
 * Created by Flop on 2019/02/25.
 */
public class FlopApplication extends Application {

    private static FlopApplication mInstance;
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // 初始化实例
        mInstance = this;
        // 初始化全局异常捕获
        CrashHandler handler = CrashHandler.getInstance(this);
        Thread.setDefaultUncaughtExceptionHandler(handler);
        // 初始化Volley请求队列
        mRequestQueue = Volley.newRequestQueue(this);
        // 初始化hawk进行key-value储存
        Hawk.init(this).build();
    }

    public static FlopApplication getInstance() {
        return mInstance;
    }

    public static RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
