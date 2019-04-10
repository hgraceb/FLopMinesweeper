package com.flop.minesweeper.ErrorLogInfo;

import android.app.Application;

/**
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
