package com.flop.minesweeper.errorLogInfo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * 异常捕获
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler myCrashHandler;

    private String TAG = "FLOP";

    private Context mContext;

    private CrashHandler(Context context) {
        mContext = context;
    }

    public static synchronized CrashHandler getInstance(Context context) {
        if (null == myCrashHandler) {
            myCrashHandler = new CrashHandler(context);
        }
        return myCrashHandler;
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        long threadId = thread.getId();
        String message = throwable.getMessage();
        String localizedMessage = throwable.getLocalizedMessage();

        ByteArrayOutputStream exception = new ByteArrayOutputStream();
        try {
            throwable.printStackTrace(new PrintStream(exception));
        } finally {
            try {
                exception.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String info = "FlopMine抛出错误：" + "\n\n" +
                "堆栈信息：" + "\n" +
                exception.toString();

        Log.i(TAG, info);
        LogErrorInfo.append(info, throwable);

//        // TODO 下面捕获到异常以后要做的事情，可以重启应用，获取手机信息上传到服务器等
//        Log.i(TAG, "------------------应用被重启----------------");
//        // 重启应用
//        Toast.makeText(mContext, "程序出错退出", Toast.LENGTH_SHORT).show();
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName()));
        //干掉当前的程序
        android.os.Process.killProcess(android.os.Process.myPid());
//        Log.i(TAG, "------------------应用被退出----------------");
        Toast.makeText(mContext, "程序出错退出", Toast.LENGTH_SHORT).show();
    }
}