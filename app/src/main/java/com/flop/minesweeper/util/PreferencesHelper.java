package com.flop.minesweeper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import com.flop.minesweeper.R;
import com.flop.minesweeper.errorLogInfo.FlopApplication;

/**
 * Created by Flop on 2020/2/2.
 */
public class PreferencesHelper {
    /**
     * 获取“我的地盘/进步历程”页面的默认ID
     *
     * @return “我的地盘/进步历程”页面的默认ID
     */
    public static int getDomainProgressId() {
        return getIntFromResources(FlopApplication.getInstance(), R.string.prefs_domain_progress_id_key, R.string.prefs_domain_progress_id_default);
    }

    /**
     * 获取选择本地录像时的默认打开路径
     *
     * @return 选择本地录像时的默认打开路径
     */
    public static String getDefaultPath() {
        return getStringFromResources(FlopApplication.getInstance(), R.string.prefs_default_path_key, SDCardUtil.getSDCardBaseDir());
    }

    /**
     * 设置选择本地录像时的默认打开路径
     */
    public static void setDefaultPath(String value) {
        setString(FlopApplication.getInstance(), R.string.prefs_default_path_key, value);
    }

    /**
     * 获取是否记住本地录像路径
     */
    public static boolean isRememberPath() {
        return getBooleanFromResources(FlopApplication.getInstance(), R.string.prefs_remember_path_key, R.bool.prefs_remember_path_default);
    }

    public static int getIntFromResources(Context context, int resKeyId, int resDefValueId) {
        return getInt(context, context.getString(resKeyId), context.getString(resDefValueId));
    }

    public static int getInt(Context context, String key, String defValue) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
        return Integer.parseInt(TextUtils.isEmpty(value) ? defValue : value);
    }

    /**
     * 获取字符串类型的偏好设置内容
     */
    public static String getStringFromResources(Context context, int resKeyId, String defValue) {
        return getString(context, context.getString(resKeyId), defValue);
    }

    public static String getString(Context context, String key, String defValue) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
        return TextUtils.isEmpty(value) ? defValue : value;
    }

    public static void setString(Context context, int key, String value) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString(context.getString(key), value);
        edit.apply();
    }

    public static boolean getBooleanFromResources(Context context, int resKeyId, int resDefValueId) {
        return getBoolean(context, context.getString(resKeyId), context.getResources().getBoolean(resDefValueId));
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defValue);
    }
}
