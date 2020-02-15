package com.flop.minesweeper.util;

import android.widget.Toast;

import com.flop.minesweeper.errorLogInfo.FlopApplication;

/**
 * Toast工具类
 * <p>
 * Created by Flop on 2018/10/23.
 */
public class ToastUtil {
    private static Toast toast;

    /**
     * 显示Toast
     *
     * @param content 要显示的内容
     */
    public static void showShort(String content) {
        toast = Toast.makeText(FlopApplication.getInstance(), null, Toast.LENGTH_SHORT);//null用于取消应用名提示
        toast.setText(content);
        toast.show();
    }

    /**
     * 显示Toast
     *
     * @param content 要显示的资源id
     */
    public static void showLong(String content) {
        toast = Toast.makeText(FlopApplication.getInstance(), null, Toast.LENGTH_LONG);//null用于取消应用名提示
        toast.setText(content);
        toast.show();
    }
}
