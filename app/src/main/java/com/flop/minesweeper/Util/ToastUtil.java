package com.flop.minesweeper.Util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 * Created by Flop on 2018/10/23.
 */
public class ToastUtil {
    private static Toast toast;

    /**
     * 显示Toast
     *
     * @param context 上下文
     * @param content 要显示的内容
     */
    public static void showShort(Context context, String content) {
        toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);//null用于取消应用名提示
        toast.setText(content);
        toast.show();
    }

    /**
     * 显示Toast
     *
     * @param context 上下文
     * @param content 要显示的资源id
     */
    public static void showLong(Context context, String content) {
        toast = Toast.makeText(context, null, Toast.LENGTH_LONG);//null用于取消应用名提示
        toast.setText(content);
        toast.show();
    }
}
