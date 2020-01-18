package com.flop.minesweeper.Util;

import android.view.View;
import android.view.ViewGroup;

/**
 * 边距工具类
 * Created by Flop on 2018/10/24.
 */
public class MarginsUtil {
    public static void setMargins(View v, int left, int top, int right, int bottom) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            v.requestLayout();
        }
    }

    public static void setMarginsBottom(View v, int bottom) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(0, 0, 0, bottom);
            v.requestLayout();
        }
    }
}
