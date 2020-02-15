package com.flop.minesweeper.fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.flop.minesweeper.R;

/**
 * Created by Flop on 2020/2/15.
 */
public class AboutFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_about, rootKey);

        // 初始化设置页面
        // init();
    }
}
