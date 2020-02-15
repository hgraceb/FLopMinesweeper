package com.flop.minesweeper.base;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

/**
 * 用户偏好设置Fragment基类
 * <p>
 * Created by Flop on 2020/2/15.
 */
public abstract class BasePrefFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // 设置页面布局
        setPreferencesFromResource(getLayoutId(), rootKey);
        // 初始化页面
        init();
    }

    /**
     * 获取布局ID
     *
     * @return 布局ID
     */
    public abstract int getLayoutId();

    /**
     * 初始化
     */
    public abstract void init();
}
