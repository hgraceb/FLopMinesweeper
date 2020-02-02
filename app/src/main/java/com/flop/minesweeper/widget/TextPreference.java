package com.flop.minesweeper.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.EditTextPreference;

/**
 * 自定义EditTextPreference，重写performClick()函数，判断是否屏蔽对话框操作
 * <p>
 * Created by Flop on 2020/2/1.
 */
public class TextPreference extends EditTextPreference {
    private static final String TAG = "FLOP";

    public TextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextPreference(Context context) {
        super(context);
    }

    @Override
    public void performClick() {
        // 获取mOnClickListener，当mOnClickListener返回值为 true 时不进行其他操作
        OnPreferenceClickListener mOnClickListener = getOnPreferenceClickListener();
        if (mOnClickListener != null && mOnClickListener.onPreferenceClick(this)) {
            return;
        }

        // 执行父函数
        super.performClick();
    }
}
