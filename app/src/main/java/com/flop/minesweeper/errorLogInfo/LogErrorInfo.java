package com.flop.minesweeper.errorLogInfo;

import com.flop.minesweeper.util.SDCardUtil;
import com.flop.minesweeper.util.TimeUtil;

/**
 * 日志输出
 * Created by Flop on 2019/02/26.
 */
public class LogErrorInfo {
    //输出日志到本地文件，方便其他手机无法查看Logcat时在代码行中直接调用调试
    private static String info = "";

    public static void append(String error, Throwable throwable) {
        info = "时间: " + TimeUtil.getCurrentTime() + "\n";
        info += "位置: " + ((throwable.getStackTrace()[0])).getFileName() + "  ";
        info += "第" + ((throwable.getStackTrace()[0])).getLineNumber() + "行\n";
        info += "原因: " + throwable.getLocalizedMessage() + "\n";
        info += "信息: " + error + "\n";
        info += "------------------------------------------------------------------------\n";

        SDCardUtil.saveFileToSDCardCustomDir(info.getBytes(), "FlopMine", "ErrorLog.txt", true);
    }
}