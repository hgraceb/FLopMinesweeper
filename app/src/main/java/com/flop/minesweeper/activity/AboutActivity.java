package com.flop.minesweeper.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;

import com.flop.minesweeper.BuildConfig;
import com.flop.minesweeper.R;
import com.flop.minesweeper.base.BasePrefFragment;
import com.flop.minesweeper.base.BasePrefsActivity;
import com.flop.minesweeper.update.UpdateManager;
import com.flop.minesweeper.util.QQGroupUtil;
import com.flop.minesweeper.util.ToastUtil;

import static com.flop.minesweeper.variable.Constant.QQ_GROUP_KEY;
import static com.flop.minesweeper.variable.Constant.UPDATE_URL;

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
                .replace(R.id.prefs, new AboutPrefFragment())
                .commit();

        // 设置返回栈事件监听
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            // 如果是栈底，即设置页面的主页面
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                // 设置主页面标题
                setTitle(getString(R.string.activity_about_title));
            }
        });
    }

    /**
     * 关于页面
     */
    public static class AboutPrefFragment extends BasePrefFragment {

        /**
         * 获取布局ID
         *
         * @return 布局ID
         */
        @Override
        public int getLayoutId() {
            return R.xml.preferences_about;
        }

        /**
         * 初始化
         */
        @Override
        public void init() {
            // 初始化“检查更新”设置
            Preference prefUpdate = findPreference(getString(R.string.about_update_check_key));
            if (prefUpdate != null) {
                // 设置版本信息
                prefUpdate.setSummary(String.format(mActivity.getResources().getString(R.string.about_update_check_summary),
                        BuildConfig.VERSION_NAME, BuildConfig.BUILD_TYPE));
                // 设置监听器
                prefUpdate.setOnPreferenceClickListener(preference -> {
                    // 检查应用更新
                    UpdateManager.create(mActivity).setManual(true).setUrl(UPDATE_URL).check();
                    return false;
                });
            }

            // 初始化“QQ交流群”设置项
            Preference prefDomainProgressId = findPreference(getString(R.string.about_qq_group_key));
            if (prefDomainProgressId != null) {
                prefDomainProgressId.setOnPreferenceClickListener(preference -> {
                    // 如果打开QQ群页面失败
                    if (!QQGroupUtil.joinQQGroup(mActivity, QQ_GROUP_KEY)) {
                        // 提示失败信息
                        ToastUtil.showShort(getString(R.string.about_qq_group_wrong_toast));
                    }

                    // 返回 true 屏蔽原生事件
                    return true;
                });
            }
        }

    }

    /**
     * 开放源代码页面
     */
    public static class OpenSourcePrefFragment extends BasePrefFragment {

        /**
         * 获取布局ID
         *
         * @return 布局ID
         */
        @Override
        public int getLayoutId() {
            return R.xml.preferences_open_source;
        }

        /**
         * 初始化
         */
        @Override
        public void init() {

        }
    }
}
