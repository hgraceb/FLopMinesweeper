package com.flop.minesweeper.Util;

import android.util.Log;

import java.util.List;
import java.util.Map;

import static com.flop.minesweeper.Constant.TAG;

/**
 * 日志工具类
 * Created by Flop on 2019/03/05.
 */
public class LogUtil {
    private static String log = "";
    private static String mInfo = "";
    private static Throwable mThrowable;

//    LogUtil.i("信息", new Throwable());

    public static void i(List<Map<String, String>> info, Throwable throwable) {
        mInfo = "";
        if (info != null) {
            for (int i = 0; i < info.size(); i++) {
                if (i == 0) {
                    mInfo += "\n";
                }
                mInfo += "第" + i + "条数据： " + info.get(i) + "\n";
            }
        } else {
            mInfo = null;
        }
        mThrowable = throwable;
        initLog();
    }

    public static void i(String info, Throwable throwable) {
        mInfo = info;
        mThrowable = throwable;
        initLog();
    }

    public static void i(int i, Throwable throwable) {
        mInfo = i + "";
        mThrowable = throwable;
        initLog();
    }

    //打印日志信息
    private static void initLog() {
        Log.i(TAG, ((mThrowable.getStackTrace()[0])).getFileName() + " "
                + "第" + ((mThrowable.getStackTrace()[0])).getLineNumber() + "行: "
                + mInfo);
    }
}
