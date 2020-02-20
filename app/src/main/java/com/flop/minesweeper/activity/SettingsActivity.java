package com.flop.minesweeper.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.flop.minesweeper.R;
import com.flop.minesweeper.base.BasePrefsActivity;
import com.flop.minesweeper.ui.TextPreference;
import com.flop.minesweeper.util.EdgeUtil;
import com.flop.minesweeper.util.PreferencesHelper;
import com.flop.minesweeper.util.SDCardUtil;

public class SettingsActivity extends BasePrefsActivity {

    /**
     * 设置布局layout
     *
     * @return 布局ID
     */
    @Override
    public int getLayoutId() {
        return R.layout.activity_frame_layout;
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
                .replace(R.id.frame_layout, new SettingsFragment())
                .commit();

        // 设置返回栈事件监听
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            // 如果是栈底，即设置页面的主页面
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                // 设置主页面标题
                setTitle(getString(R.string.activity_settings_title));
            }
        });
    }

    /**
     * 设置主页面
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_root, rootKey);

            // 初始化设置页面
            init();
        }

        /**
         * 初始化设置页面
         */
        private void init() {
            // 初始化“我的地盘/进步历程”的默认ID设置
            TextPreference prefDomainProgressId = findPreference(getString(R.string.prefs_domain_progress_id_key));
            if (prefDomainProgressId != null) {
                prefDomainProgressId.setOnPreferenceClickListener(preference -> {
                    // 创建并显示设置“我的地盘/进步历程”默认ID的对话框
                    showDomainProgressIdDialog();

                    // 返回 true 屏蔽原生事件
                    return true;
                });
            }

            // 初始化“本地录像”的默认路径设置项
            Preference prefDefaultPath = findPreference(getString(R.string.prefs_default_path_key));
            if (prefDefaultPath != null) {
                // 获取本地录像默认路径
                String defaultPath = PreferencesHelper.getDefaultPath();
                // 显示本地录像默认路径
                prefDefaultPath.setSummary(defaultPath == null ? SDCardUtil.getSDCardBaseDir() : defaultPath);
            }

        }

        /**
         * 创建并显示设置“我的地盘/进步历程”默认ID的对话框
         */
        private void showDomainProgressIdDialog() {
            // 创建输入框布局
            LinearLayout linearLayout = buildTextLinearLayout(mActivity, R.string.prefs_domain_progress_id_key);

            // 创建对话框
            AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                    .setTitle(getString(R.string.prefs_domain_progress_id_title))
                    .setPositiveButton("确定", (dialog, which) -> {
                        EditTextPreference editTextPreference = findPreference(getString(R.string.prefs_domain_progress_id_key));
                        EditText editText = linearLayout.findViewById(R.id.settings_dialog_tv);
                        // 更新设置
                        if (editTextPreference != null && editText != null) {
                            editTextPreference.setText(editText.getText().toString());
                        }
                    })
                    .setNegativeButton("取消", null)
                    .setView(linearLayout).create();

            // 设置软键盘自动弹出
            Window window = alertDialog.getWindow();
            if (window != null) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            // 显示对话框
            alertDialog.show();
        }

        /**
         * 创建对话框的输入框布局
         *
         * @param context 上下文
         * @param resId   preference对应的的key
         * @return 只包含一个EditText子控件的LinearLayout布局
         */
        private LinearLayout buildTextLinearLayout(Context context, int resId) {
            // 定义LinearLayout布局作为editText的容器
            LinearLayout linearLayout = new LinearLayout(context);
            // linearLayout的父容器是context中的 FrameLayout
            linearLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            // 定义editText
            EditText editText = new EditText(context);
            // 设置editText的ID
            editText.setId(R.id.settings_dialog_tv);
            // editText的父容器是LinearLayout
            editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            // 设置editText的左右边距
            int verticalMargin = getResources().getDimensionPixelSize(R.dimen.dialog_vertical_margin);
            EdgeUtil.setMarginsLeft(verticalMargin, editText);
            EdgeUtil.setMarginsRight(verticalMargin, editText);

            // 如果是设置“我的地盘/进步历程”页面的默认ID
            if (resId == R.string.prefs_domain_progress_id_key) {
                // 获取偏好设置内容
                editText.setText(PreferenceManager.getDefaultSharedPreferences(context)
                        .getString(context.getString(R.string.prefs_domain_progress_id_key), getString(R.string.prefs_domain_progress_id_default)));
                // 设置输入类型为数字
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                // 设置过滤器，限制输入长度最长为6位
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            }

            // 在内容设置完成后获取焦点，保证指针在最后面
            editText.requestFocus();
            // 添加 editText 布局
            linearLayout.addView(editText);
            return linearLayout;
        }
    }

    /**
     * 高级设置页面
     */
    public static class AdvancedFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_advanced, rootKey);
        }
    }

}