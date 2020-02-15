package com.flop.minesweeper.errorLogInfo;

import android.app.Application;

/**
 * 初始化应用
 * <p>
 * Created by Flop on 2019/02/25.
 */
public class FlopApplication extends Application {

    private static FlopApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        // 初始化
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // 初始化全局异常捕获
        CrashHandler handler = CrashHandler.getInstance(this);
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    public static FlopApplication getInstance() {
        return mInstance;
    }
}
