package com.flop.minesweeper.util;

import android.content.Context;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import com.flop.minesweeper.R;

/**
 * Created by Flop on 2020/2/2.
 */
public class PreferencesHelper {
    /**
     * 获取“我的地盘/进步历程”页面的默认ID
     *
     * @param context 上下文
     * @return “我的地盘/进步历程”页面的默认ID
     */
    public static int getDomainProgressId(Context context) {
        return getIntFromResources(context, R.string.prefs_domain_progress_id_key, R.string.prefs_domain_progress_id_default);
    }

    public static int getIntFromResources(Context context, int resKeyId, int resDefValueId) {
        return getInt(context, context.getString(resKeyId), context.getString(resDefValueId));
    }

    public static int getInt(Context context, String key, String defValue) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
        return Integer.parseInt(TextUtils.isEmpty(value) ? defValue : value);
    }
}
