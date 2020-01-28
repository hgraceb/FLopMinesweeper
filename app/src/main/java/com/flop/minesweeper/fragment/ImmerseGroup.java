package com.flop.minesweeper.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

/**
 * 沉浸式状态栏Toolbar辅助类
 * 避免Toolbar设置fitSystemWindows="true"，当软键盘弹出时Toolbar被拉伸
 */
public class ImmerseGroup extends FrameLayout {
    public ImmerseGroup(Context context) {
        super(context);
    }

    public ImmerseGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImmerseGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置底内边距为0
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), 0);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
