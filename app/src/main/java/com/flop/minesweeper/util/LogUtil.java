package com.flop.minesweeper.util;

import android.util.Log;

import com.flop.minesweeper.BuildConfig;

import java.util.List;
import java.util.Map;

/**
 * 日志工具类
 * <p>
 * Created by Flop on 2019/03/05.
 */
public class LogUtil {

    private static final String TAG = "LogUtil";
    private static StringBuilder mInfo = new StringBuilder();
    private static Throwable mThrowable;

    /**
     * 打印列表日志信息
     */
    public static void i(List<Map<String, String>> info, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            mInfo = new StringBuilder();
            if (info != null) {
                for (int i = 0; i < info.size(); i++) {
                    if (i == 0) {
                        mInfo.append("\n");
                    }
                    mInfo.append("第").append(i).append("条数据： ").append(info.get(i)).append("\n");
                }
            }
            LogUtil.i(mInfo, throwable);
        }

    }

    /**
     * 打印其他日志信息
     */
    public static void i(Object info, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            LogUtil.i(info == null ? "" : info.toString(), throwable);
        }
    }

    /**
     * 打印字符串日志信息
     */
    public static void i(String info, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            mInfo = new StringBuilder(info);
            mThrowable = throwable;
            printLog();
        }
    }

    /**
     * 打印日志信息
     */
    private static void printLog() {
        if (BuildConfig.DEBUG) {
            // 堆栈节点
            StackTraceElement stackTraceElement = mThrowable.getStackTrace()[0];
            // 文件名
            String fileName = stackTraceElement.getFileName();
            // 行号
            int lineNumber = stackTraceElement.getLineNumber();
            // 打印日志信息
            Log.i(TAG, fileName + " " + "第" + lineNumber + "行: " + mInfo);
        }
    }
}