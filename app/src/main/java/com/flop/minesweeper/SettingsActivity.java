package com.flop.minesweeper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

/**
 * 新游戏设置页面
 * Created by Flop on 2018/10/09.
 */
public class SettingsActivity extends Activity {
    private String TAG = "FLOP";//日志TAG

    @BindView(R.id.tvBeg) TextView tvBeg;
    @BindView(R.id.tvInt) TextView tvInt;
    @BindView(R.id.tvExp) TextView tvExp;

    @BindView(R.id.scBeg) Switch scBeg;
    @BindView(R.id.scInt) Switch scInt;
    @BindView(R.id.scExp) Switch scExp;

    @BindView(R.id.tvLongClickVibration) TextView tvLongClickVibration;
    @BindView(R.id.tvWinVibration) TextView tvWinVibration;
    @BindView(R.id.tvLoseVibration) TextView tvLoseVibration;

    @BindView(R.id.scLongClickVibration) Switch scLongClickVibration;
    @BindView(R.id.scWinVibration) Switch scWinVibration;
    @BindView(R.id.scLoseVibration) Switch scLoseVibration;

    @OnTouch({R.id.tvBeg, R.id.tvInt, R.id.tvExp,
            R.id.tvLongClickVibration, R.id.tvWinVibration, R.id.tvLoseVibration})
    public boolean bindViewOnTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //手指移出当前控件范围又重新移入时设置按压样式
                if (isInnerUp(v, event)) {
                    v.setPressed(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                //手指在当前控件范围内方开始执行相应操作
                if (isInnerUp(v, event)) {
                    switch (v.getId()) {
                        case R.id.tvBeg:
                            changeLevel(1);
                            break;
                        case R.id.tvInt:
                            changeLevel(2);
                            break;
                        case R.id.tvExp:
                            changeLevel(3);
                            break;
                        case R.id.tvLongClickVibration:
                            changeLongClickVibration();
                            break;
                        case R.id.tvWinVibration:
                            changeWinVibration();
                            break;
                        case R.id.tvLoseVibration:
                            changeLoseVibration();
                            break;
                    }
                    setResult(Constant.SETTINGS_RESULT_CODE);
                }
                break;
        }
        return false;
    }

    //判断手指触摸事件是否发生在有效范围内
    private boolean isInnerUp(View v, MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        float maxX = v.getWidth();
        float maxY = v.getHeight();

        return touchX >= 0 && touchX <= maxX && touchY >= 0 && touchY <= maxY;
    }

    private void changeLevel(int gameLevel) {
        SharedPreferences sp = getSharedPreferences("Flop", Context.MODE_PRIVATE);
        setLevelSwitch(gameLevel);
        sp.edit().putInt("gameLevel", gameLevel).apply();
    }

    private void setLevelSwitch(int gameLevel) {
        if (gameLevel == 1) {
            scBeg.setChecked(true);
            scInt.setChecked(false);
            scExp.setChecked(false);
        } else if (gameLevel == 2) {
            scBeg.setChecked(false);
            scInt.setChecked(true);
            scExp.setChecked(false);
        } else if (gameLevel == 3) {
            scBeg.setChecked(false);
            scInt.setChecked(false);
            scExp.setChecked(true);
        }
    }


    private void changeLoseVibration() {
        scLoseVibration.setChecked(!scLoseVibration.isChecked());
        SharedPreferences sp = getSharedPreferences("Flop", Context.MODE_PRIVATE);
        if (scLoseVibration.isChecked()) {
            sp.edit().putBoolean("loseVibration", true).apply();
        } else {
            sp.edit().putBoolean("loseVibration", false).apply();
        }
    }

    private void changeWinVibration() {
        scWinVibration.setChecked(!scWinVibration.isChecked());
        SharedPreferences sp = getSharedPreferences("Flop", Context.MODE_PRIVATE);
        if (scWinVibration.isChecked()) {
            sp.edit().putBoolean("winVibration", true).apply();
        } else {
            sp.edit().putBoolean("winVibration", false).apply();
        }
    }

    private void changeLongClickVibration() {
        scLongClickVibration.setChecked(!scLongClickVibration.isChecked());
        SharedPreferences sp = getSharedPreferences("Flop", Context.MODE_PRIVATE);
        if (scLongClickVibration.isChecked()) {
            sp.edit().putBoolean("longClickVibration", true).apply();
        } else {
            sp.edit().putBoolean("longClickVibration", false).apply();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        initSettings();

    }

    private void initSettings() {
        SharedPreferences sp = getSharedPreferences("Flop", Context.MODE_PRIVATE);
        int gameLevel = sp.getInt("gameLevel", 1);
        boolean longClickVibration = sp.getBoolean("longClickVibration", true);
        boolean winVibration = sp.getBoolean("winVibration", false);
        boolean loseVibration = sp.getBoolean("loseVibration", false);

        setLevelSwitch(gameLevel);
        scLongClickVibration.setChecked(longClickVibration);
        scWinVibration.setChecked(winVibration);
        scLoseVibration.setChecked(loseVibration);
    }
}
