package com.flop.minesweeper.errorLogInfo;

import android.app.Application;

/**
 * 注册
 * Created by Flop on 2019/02/25.
 */
public class InitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler handler = CrashHandler.getInstance(this);
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }
}
