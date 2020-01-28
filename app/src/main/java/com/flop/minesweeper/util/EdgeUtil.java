package com.flop.minesweeper.util;

import android.view.View;
import android.view.ViewGroup;

/**
 * 边距工具类
 * Created by Flop on 2018/10/24.
 */
public class EdgeUtil {
    /**
     * 设置边距
     *
     * @param left   左边距
     * @param top    上边距
     * @param right  右边距
     * @param bottom 底边距
     * @param views  视图
     */
    public static void setMargins(int left, int top, int right, int bottom, View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                marginLayoutParams.setMargins(left, top, right, bottom);
                view.requestLayout();
            }
        }

    }

    /**
     * 设置左边距
     *
     * @param left  左边距
     * @param views 视图
     */
    public static void setMarginsLeft(int left, View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                marginLayoutParams.setMargins(left, marginLayoutParams.topMargin, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
                view.requestLayout();
            }
        }

    }

    /**
     * 设置上边距
     *
     * @param top   上边距
     * @param views 视图
     */
    public static void setMarginsTop(int top, View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                marginLayoutParams.setMargins(marginLayoutParams.leftMargin, top, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
                view.requestLayout();
            }
        }

    }

    /**
     * 设置右边距
     *
     * @param right 右边距
     * @param views 视图
     */
    public static void setMarginsRight(int right, View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                marginLayoutParams.setMargins(marginLayoutParams.leftMargin, marginLayoutParams.topMargin, right, marginLayoutParams.bottomMargin);
                view.requestLayout();
            }
        }

    }

    /**
     * 设置底边距
     *
     * @param bottom 底边距
     * @param views  视图
     */
    public static void setMarginsBottom(int bottom, View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                marginLayoutParams.setMargins(marginLayoutParams.leftMargin, marginLayoutParams.topMargin, marginLayoutParams.rightMargin, bottom);
                view.requestLayout();
            }
        }

    }

    /**
     * 设置内边距
     *
     * @param left   左内边距
     * @param top    上内边距
     * @param right  右内边距
     * @param bottom 底内边距
     * @param views  视图
     */
    public static void setPadding(int left, int top, int right, int bottom, View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            view.setPadding(left, top, right, bottom);
        }

    }

    /**
     * 设置左内边距
     *
     * @param left  左内边距
     * @param views 视图
     */
    public static void setPaddingLeft(int left, View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            view.setPadding(left, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /**
     * 设置上内边距
     *
     * @param top   上内边距
     * @param views 视图
     */
    public static void setPaddingTop(int top, View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            view.setPadding(view.getPaddingLeft(), top, view.getPaddingRight(), view.getPaddingBottom());
        }

    }

    /**
     * 设置右内边距
     *
     * @param right 右内边距
     * @param views 视图
     */
    public static void setPaddingRight(int right, View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), right, view.getPaddingBottom());
        }

    }

    /**
     * 设置下内边距
     *
     * @param bottom 下内边距
     * @param views  视图
     */
    public static void setPaddingBottom(int bottom, View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), bottom);
        }
    }
}
