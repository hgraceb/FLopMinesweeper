package com.flop.minesweeper.base;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

/**
 * 用好偏好设置Activity基类
 * <p>
 * Created by Flop on 2020/2/15.
 */
public abstract class BasePrefsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    protected static AppCompatActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置布局内容
        setContentView(getLayoutId());
        // 初始化ToolBar
        initToolBar();
        // 初始化页面
        initFragment();

        mActivity = this;
    }

    /**
     * 监听设置页面的Fragment跳转
     */
    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Fragment跳转后重新设置标题
        setTitle(pref.getTitle());
        return false;
    }

    /**
     * 点击Toolbar返回按钮
     */
    @Override
    public boolean onSupportNavigateUp() {
        // 模拟返回事件
        this.onBackPressed();

        return super.onSupportNavigateUp();
    }

    /**
     * 点击返回按钮
     */
    @Override
    public void onBackPressed() {
        // 返回结果码，需要在执行onBackPressed父函数之前设置
        setResult(Activity.RESULT_OK);

        super.onBackPressed();
    }

    /**
     * 清理数据
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 回收内存，防止内存泄漏
        mActivity = null;
    }

    /**
     * 获取布局ID
     *
     * @return 布局ID
     */
    public abstract int getLayoutId();

    /**
     * 初始化toolbar
     */
    public abstract void initToolBar();

    /**
     * 初始化fragment
     */
    public abstract void initFragment();

}
