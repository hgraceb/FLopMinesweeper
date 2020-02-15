package com.flop.minesweeper.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.flop.minesweeper.R;
import com.flop.minesweeper.base.BasePrefsActivity;
import com.flop.minesweeper.fragment.AboutFragment;

/**
 * 关于页面
 * <p>
 * Created by Flop on 2020/2/15.
 */
public class AboutActivity extends BasePrefsActivity {

    /**
     * 设置布局layout
     *
     * @return 布局ID
     */
    @Override
    public int getLayoutId() {
        return R.layout.activity_prefs;
    }

    /**
     * 初始化 Toolbar
     */
    @Override
    public void initToolBar() {
        // 设置顶部导航栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置返回按钮
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 初始化fragment
     */
    @Override
    public void initFragment() {
        // 设置Fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.prefs, new AboutFragment())
                .commit();

        // 设置返回栈事件监听
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            // 如果是栈底，即设置页面的主页面
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                // 设置主页面标题
                setTitle(getString(R.string.title_activity_settings));
            }
        });
    }
}
