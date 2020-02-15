package com.flop.minesweeper.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flop.minesweeper.R;
import com.flop.minesweeper.util.ToastUtil;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;

import static com.flop.minesweeper.variable.Constant.PLAY_REQUEST_CODE;
import static com.flop.minesweeper.variable.Constant.SETTINGS_RESULT_CODE;

/**
 * 新游戏页面
 * Created by Flop on 2018/10/01.
 */
public class NewGameActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private String TAG = "FLOP";//日志TAG

    //全局变量
    public int sideLength;//边长
    public int glRows;//行
    public int glColumns;//列
    public int gameLevel = 1;//游戏级别
    public ImageView[][] cellArray;//方块
    public int[][] boardArray;//方块数字
    public String[][] styleArray;//方块状态
    public boolean gameOver = false;//游戏结束标志
    public boolean longClickVibration = true;//长按时是否震动
    public boolean winVibration = false;//胜利时是否震动
    public boolean loseVibration = false;//失败时是否震动

    public boolean firstClick = true;//第一次点击
    public long millisecond = 0;//录像当前时间

    public int openCount = 0;//已打开方块数目，判断游戏是否胜利
    public int flagAround;//方块周围旗子数目,判断点击当前格子是否打开双击开周围方块
    public boolean openAround = false;//帮助判断点击当前格子是否打开双击开周围方块
    public boolean flStyle = false;//风格选择
    public int bombNumber;//总雷数
    public int mineNumber;//剩余雷数
    private long beginTime;//开始时间
    private long passTime;//退出页面前已经计时的时间
    private Timer timer;//定时器

    //定义Handler句柄
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                millisecond = System.currentTimeMillis() - beginTime + passTime;
                setTimeCount();
            }
            return false;
        }
    });

    @BindView(R.id.hsMinesweeper) HorizontalScrollView hsMinesweeper;//水平滑动框
    @BindView(R.id.glMinesweeper) GridLayout glMinesweeper;//方块总框架
    @BindView(R.id.lyMinesweeper) LinearLayout lyMinesweeper;//方块边框
    @BindView(R.id.rlTop) RelativeLayout rlTop;//顶部信息栏
    @BindView(R.id.rlBottom) RelativeLayout lyBottom;//底部信息栏
    @BindView(R.id.ivFlagBomb) ImageView ivFlagBomb;//风格
    @BindView(R.id.ivVideo) ImageView ivVideo;//设置
    @BindView(R.id.ivSettings) ImageView ivSettings;//设置
    @BindView(R.id.ivPlay) ImageView ivPlay;//新游戏
    @BindView(R.id.ivMouse) ImageView ivMouse;//录像鼠标指针
    @BindView(R.id.scMinesweeper) ScrollView scMinesweeper;//垂直滚动框
    @BindView(R.id.ivBombBit) ImageView ivBombBit;//剩余雷数个位
    @BindView(R.id.ivBombTen) ImageView ivBombTen;//剩余雷数十位
    @BindView(R.id.ivBombHun) ImageView ivBombHun;//剩余雷数百位
    @BindView(R.id.ivTimeBit) ImageView ivTimeBit;//时间个位
    @BindView(R.id.ivTimeTen) ImageView ivTimeTen;//时间十位
    @BindView(R.id.ivTimeHun) ImageView ivTimeHun;//时间百位

    //点击录像按钮退出当前界面，返回主页面
    public void finishVideos() {
        finish();
    }

    //点击设置按钮进入设置页面
    public void changeSettings() {
        Intent intent = new Intent(this, GameSettingsActivity.class);
        startActivityForResult(intent, PLAY_REQUEST_CODE);
    }

    //启动其他页面后返回其数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SETTINGS_RESULT_CODE) {
            int currentGameLevel = gameLevel;
            getUserData();
            if (currentGameLevel != gameLevel) {
                initGame(gameLevel);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        ButterKnife.bind(this);
        getUserData();
        rlTop.setVisibility(View.VISIBLE);
        //等待页面布局完成后才能获取坐标位置，并初始化游戏
        lyBottom.post(() -> initGame(gameLevel));
    }

    //回到界面继续上次时间计时
    @Override
    protected void onResume() {
        super.onResume();
        if (timer != null) {
            passTime = millisecond;
            resetTimer();
            Log.i(TAG, "onResume: resetTimer()");
        }
    }

    //退出界面暂停定时器
    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            Log.i(TAG, "onPause: timer.cancel");
        }
        saveUserData();
    }

    //保存用户配置数据
    private void saveUserData() {
        SharedPreferences sp = getSharedPreferences("Flop", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("gameLevel", gameLevel);
        editor.putBoolean("longClickVibration", longClickVibration);
        editor.putBoolean("winVibration", winVibration);
        editor.putBoolean("loseVibration", loseVibration);
        editor.putFloat("scale", (float) scale);
        editor.apply();
    }

    //读取用户配置数据
    private void getUserData() {
        SharedPreferences sp = getSharedPreferences("Flop", Context.MODE_PRIVATE);

        gameLevel = sp.getInt("gameLevel", 1);
        longClickVibration = sp.getBoolean("longClickVibration", true);
        winVibration = sp.getBoolean("winVibration", false);
        loseVibration = sp.getBoolean("loseVibration", false);
        scale = sp.getFloat("scale", 1.0f);
    }

    //改变扫雷风格
    public void changeStyle() {
        flStyle = !flStyle;
        if (flStyle) {
            setImageButton(ivFlagBomb, "flag");
        } else {
            setImageButton(ivFlagBomb, "mine");
        }
    }

    //切换下一个级别
    public void changeLevel() {
        if (gameLevel == 1) initGame(2);
        else if (gameLevel == 2) initGame(3);
        else if (gameLevel == 3) initGame(1);
    }

    //开始新游戏
    public void playGame() {
        initGame(0);
    }

    //绑定单击事件
    @OnClick({R.id.ivFlagBomb, R.id.ivPlay, R.id.ivVideo, R.id.ivSettings})
    public void bindViewOnClick(View v) {
        switch (v.getId()) {
            case R.id.ivFlagBomb:
                changeStyle();
                break;
            case R.id.ivPlay:
                playGame();
                break;
            case R.id.ivVideo:
                finishVideos();
                break;
            case R.id.ivSettings:
                changeSettings();
                break;
        }
    }

    //绑定长按时间
    @OnLongClick({R.id.ivPlay})
    public boolean bindViewOnLongClick(View v) {
        switch (v.getId()) {
            //长按笑脸切换下一个级别
            case R.id.ivPlay:
                changeLevel();
                break;
        }
        return true;
    }

    //绑定笑脸触摸事件
    @OnTouch(R.id.ivPlay)
    public boolean newGame(View iv, MotionEvent event) {
        ImageView v = (ImageView) iv;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指在当前方块按下时改变其图片为按下状态
                setImageButton(v, "face_press");
                break;
            case MotionEvent.ACTION_UP:
                //手指抬起时改变当前方块图片为正常状态
                setImageButton(v, "face_normal");
                break;
        }
        return false;
    }

    //绑定风格按钮触摸事件
    @OnTouch(R.id.ivFlagBomb)
    public boolean changeFlagBomb(View iv, MotionEvent event) {
        ImageView v = (ImageView) iv;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指在当前方块按下时改变其图片为按下状态
                if (flStyle) {
                    setImageButton(v, "flag_press");
                } else {
                    setImageButton(v, "mine_press");
                }
                break;
            case MotionEvent.ACTION_UP:
                //解决Cancel事件不响应，通过判断手指抬起位置判断是否改变风格
                if (isInnerUp(v, event)) {
                    flStyle = !flStyle;
                }
                //手指抬起时改变当前方块图片为正常状态
                if (flStyle) {
                    setImageButton(ivFlagBomb, "flag");
                } else {
                    setImageButton(ivFlagBomb, "mine");
                }
                break;
        }
        return true;//屏蔽当前方块默认OnClick事件
    }

    //判断手指触摸事件是否发生在有效范围内
    private boolean isInnerUp(View v, MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        float maxX = v.getWidth();
        float maxY = v.getHeight();

        return touchX >= 0 && touchX <= maxX && touchY >= 0 && touchY <= maxY;
    }

    //初始化游戏
    public void initGame(int level) {
        ivMouse.setVisibility(View.GONE);

        //如果级别不改变则保留原先表格布局，不进行表格组件的移除和重新添加，减轻手机卡顿
        if (level == 0) {
            initVideos(level);
            return;
        }

        initLevel(level);
        initVideos(level);
        initCell();
        initTimer();
        setMineCount();
        initScale();
    }

    //初始化定时器
    private void initTimer() {
        if (timer != null) timer.cancel();
        setImageCount(ivTimeHun, 0);
        setImageCount(ivTimeTen, 0);
        setImageCount(ivTimeBit, 0);
    }

    //重置定时器
    private void resetTimer() {
        //产生一个毫秒数，即当前时间距离1970-01-01 00:00:00的毫秒数
        beginTime = System.currentTimeMillis();
        //初始化定时器任务，这里是向Handler发送一个消息，由Handler类进行处理。
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, 1000);
    }

    //初始化方块
    @SuppressLint("ClickableViewAccessibility")
    private void initCell() {
        //设置为几行几列
        glMinesweeper.setRowCount(glRows);
        glMinesweeper.setColumnCount(glColumns);
        lyMinesweeper.setBackgroundResource(R.drawable.ic_ly_background);

        //为每个方块绑定事件
        for (int i = 0; i < glRows; i++) {
            for (int j = 0; j < glColumns; j++) {
                cellArray[i][j] = new ImageView(this);
                cellArray[i][j].setOnClickListener(this);
                cellArray[i][j].setOnTouchListener(this);
                cellArray[i][j].setOnLongClickListener(this);
                cellArray[i][j].setImageResource(R.mipmap.iv_normal);
                cellArray[i][j].setId(i * glColumns + j);
                glMinesweeper.addView(cellArray[i][j], sideLength, sideLength);
            }
        }
    }

    //初始化级别
    private void initLevel(int level) {
        glMinesweeper.removeAllViews();
        glWidth = getGlWidth() - 32;
        glHeight = getGLHeight() - 32;//32为背景图片边框宽度
        if (level > 0) gameLevel = level;

        //不同级别设置变量及边距
        if (level == 1) {
            glRows = 8;
            glColumns = 8;
            bombNumber = 10;
            sideLength = (glWidth < glHeight ? glWidth : glHeight) / 8 - 8;//双侧边缘稍微留空
            setMargins(scMinesweeper, (glWidth - glColumns * sideLength) / 2, (glHeight - glRows * sideLength) / 2, 0, 0);
        } else if (level == 2) {
            glRows = 16;
            glColumns = 16;
            bombNumber = 40;
            sideLength = glHeight / 16;
            if (glWidth < glHeight) {//横屏状态
                setMargins(scMinesweeper, 0, 0, 0, 0);
            } else {
                setMargins(scMinesweeper, (glWidth - glColumns * sideLength) / 2, (glHeight - glRows * sideLength) / 2, 0, 0);
            }
        } else if (level == 3) {
            glRows = 16;
            glColumns = 30;
            bombNumber = 99;
            sideLength = glHeight / 16;
            if (glWidth < glHeight) {//横屏状态
                setMargins(scMinesweeper, 0, 0, 0, 0);
            } else {
                setMargins(scMinesweeper, (glWidth - glColumns * sideLength) / 2, (glHeight - glRows * sideLength) / 2, 0, 0);
            }
        }
        mineNumber = bombNumber;
    }

    //顶部信息栏显示剩余雷数
    private void setMineCount() {
        if (mineNumber < -99) {
            setImageCount(ivBombHun, 11);
            setImageCount(ivBombTen, 12);
            setImageCount(ivBombBit, 13);
            if (mineNumber == -100) ToastUtil.showShort("干哈呢？好好扫雷！");
            else if (mineNumber == -381) ToastUtil.showShort("傻呢吗...");
            return;
        }

        int bit = Math.abs(mineNumber % 10);
        int ten = Math.abs(mineNumber / 10 % 10);
        int hun = Math.abs(mineNumber / 100 % 10);
        if (mineNumber < 0) {
            hun = 10;
        }
        setImageCount(ivBombHun, hun);
        setImageCount(ivBombTen, ten);
        setImageCount(ivBombBit, bit);
    }

    //顶部信息栏显示已用时间
    private void setTimeCount() {
        int second = (int) Math.ceil((double) (millisecond) / 1000);
        //时间超过999秒自动判负
        if (second > 999) {
            gameLose();
            ToastUtil.showShort("游戏超时");
            return;
        }
        int bit = Math.abs(second % 10);
        int ten = Math.abs(second / 10 % 10);
        int hun = Math.abs(second / 100 % 10);
        setImageCount(ivTimeHun, hun);
        setImageCount(ivTimeTen, ten);
        setImageCount(ivTimeBit, bit);
    }

    //初始化变量
    private void initVideos(int level) {
        if (level == 0) {
            for (int i = 0; i < glRows; i++) {
                for (int j = 0; j < glColumns; j++) {
                    setImageButton(cellArray[i][j], "normal");
                }
            }
            mineNumber = bombNumber;
            initTimer();
            setMineCount();
        } else {
            cellArray = new ImageView[glRows][glColumns];
        }
        firstClick = true;
        if (flStyle) changeStyle();
        boardArray = new int[glRows][glColumns];
        styleArray = new String[glRows][glColumns];
        for (int i = 0; i < glRows; i++) {
            for (int j = 0; j < glColumns; j++) {
                styleArray[i][j] = "normal";
            }
        }
        gameOver = false;
        openCount = 0;
    }

    //新游戏布雷
    private void resetBomb(ImageView v) {
        firstClick = false;
        int vRow = v.getId() / glColumns;
        int vColumn = v.getId() % glColumns;

        int bomb = 0;//当前雷数
        int row;//将雷放在第几行
        int column;//将雷放在第几列
        Random random = new Random();//生成随机数
        while (bomb < bombNumber) {
            row = random.nextInt(glRows);//生成[0,glRows)区间内的随机整数
            column = random.nextInt(glColumns);//生成[0,glColumns)区间内的随机整数

            //如果随机到的方块未重复则置雷
            if ((row != vRow || column != vColumn) && boardArray[row][column] != -1) {
                boardArray[row][column] = -1;
                bomb++;
                //雷周围数字自增
                for (int i = row - 1; i <= row + 1; i++) {
                    for (int j = column - 1; j <= column + 1; j++) {
                        if (i != row || j != column) increaseNumber(i, j);
                    }
                }
            }
        }
        openCell(v);
    }

    //设置数字图片,同时改变styleArray对应值
    private void setImageNumber(ImageView v) {
        int row = v.getId() / glColumns;
        int column = v.getId() % glColumns;
        openCount++;
        styleArray[row][column] = "number";
        switch (boardArray[row][column]) {
            case 0:
                v.setImageResource(R.mipmap.iv_n0);
                break;
            case 1:
                v.setImageResource(R.mipmap.iv_n1);
                break;
            case 2:
                v.setImageResource(R.mipmap.iv_n2);
                break;
            case 3:
                v.setImageResource(R.mipmap.iv_n3);
                break;
            case 4:
                v.setImageResource(R.mipmap.iv_n4);
                break;
            case 5:
                v.setImageResource(R.mipmap.iv_n5);
                break;
            case 6:
                v.setImageResource(R.mipmap.iv_n6);
                break;
            case 7:
                v.setImageResource(R.mipmap.iv_n7);
                break;
            case 8:
                v.setImageResource(R.mipmap.iv_n8);
                break;
        }
    }

    //设置一般图片
    private void setImageButton(ImageView v, String string) {
        switch (string) {
            case "mine":
                v.setImageResource(R.mipmap.iv_mine);
                break;
            case "flag":
                v.setImageResource(R.mipmap.iv_flag);
                break;
            case "mine_press":
                v.setImageResource(R.mipmap.iv_mine_press);
                break;
            case "flag_press":
                v.setImageResource(R.mipmap.iv_flag_press);
                break;
            case "first_bomb":
                v.setImageResource(R.mipmap.iv_first_bomb);
                break;
            case "flag_rough":
                v.setImageResource(R.mipmap.iv_flag_rough);
                break;
            case "blank":
                v.setImageResource(R.mipmap.iv_n0);
                break;
            case "normal":
                v.setImageResource(R.mipmap.iv_normal);
                break;
            case "face_normal":
                v.setImageResource(R.mipmap.iv_face_normal);
                break;
            case "face_press":
                v.setImageResource(R.mipmap.iv_face_press);
                break;
            case "face_click":
                v.setImageResource(R.mipmap.iv_face_click);
                break;
            case "face_jiujiu":
                v.setImageResource(R.mipmap.iv_face_jiujiu);
                break;
            case "face_luelue":
                v.setImageResource(R.mipmap.iv_face_luelue);
                break;
        }
    }

    //设置计数器图片
    private void setImageCount(ImageView v, int number) {
        switch (number) {
            case 0:
                v.setImageResource(R.mipmap.iv_c0);
                break;
            case 1:
                v.setImageResource(R.mipmap.iv_c1);
                break;
            case 2:
                v.setImageResource(R.mipmap.iv_c2);
                break;
            case 3:
                v.setImageResource(R.mipmap.iv_c3);
                break;
            case 4:
                v.setImageResource(R.mipmap.iv_c4);
                break;
            case 5:
                v.setImageResource(R.mipmap.iv_c5);
                break;
            case 6:
                v.setImageResource(R.mipmap.iv_c6);
                break;
            case 7:
                v.setImageResource(R.mipmap.iv_c7);
                break;
            case 8:
                v.setImageResource(R.mipmap.iv_c8);
                break;
            case 9:
                v.setImageResource(R.mipmap.iv_c9);
                break;
            case 10:
                v.setImageResource(R.mipmap.iv_c10);
                break;
            case 11:
                v.setImageResource(R.mipmap.iv_c11);
                break;
            case 12:
                v.setImageResource(R.mipmap.iv_c12);
                break;
            case 13:
                v.setImageResource(R.mipmap.iv_c13);
                break;
        }
    }

    //设置字符图片,同时改变styleArray对应值
    private void setImageString(ImageView v, String string) {
        int row = v.getId() / glColumns;
        int column = v.getId() % glColumns;
        styleArray[row][column] = string;
        switch (string) {
            case "flag":
                v.setImageResource(R.mipmap.iv_flag);
                break;
            case "normal":
                v.setImageResource(R.mipmap.iv_normal);
                break;
            case "wrong_flag":
                v.setImageResource(R.mipmap.iv_wrong_flag);
                break;
            case "bomb":
                v.setImageResource(R.mipmap.iv_bomb);
                break;
        }
    }

    //计算方块对应数字
    private void increaseNumber(int row, int column) {
        if (row > -1 && row < glRows && column > -1 && column < glColumns) {
            if (boardArray[row][column] != -1) {
                boardArray[row][column]++;
            }
        }
    }

    //设置边距
    public static void setMargins(View v, int left, int top, int right, int bottom) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            v.requestLayout();
        }
    }

    //获取GridLayout高度
    private int getGLHeight() {
        if (rlTop.getVisibility() == View.VISIBLE) {
            return (int) (lyBottom.getY() - rlTop.getY() - rlTop.getLayoutParams().height);
        } else {
            return (int) (lyBottom.getY() - rlTop.getY());
        }
    }

    //获取GridLayout宽度
    public int getGlWidth() {
        WindowManager wm = this.getWindowManager();
        return wm.getDefaultDisplay().getWidth();
    }

    //打开周围方块
    private void openAround(ImageView v) {
        int row = getRow(v);
        int column = getColumn(v);
        if (styleArray[row][column].equals("number") && boardArray[row][column] > 0) {
            flagAround = 0;
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = column - 1; j <= column + 1; j++) {
                    if (i != row || j != column) countFlag(i, j);
                }
            }

            //当前方块周围旗子数目与方块数字相同则双击开周围方块
            if (flagAround == boardArray[row][column]) {
                for (int i = row - 1; i <= row + 1; i++) {
                    for (int j = column - 1; j <= column + 1; j++) {
                        if (i != row || j != column) chordOpen(i, j);
                    }
                }
            }
        }
    }

    //打开双击事件传入的方块
    private void chordOpen(int row, int column) {
        if (row > -1 && row < glRows && column > -1 && column < glColumns) {
            openCell(cellArray[row][column]);
        }
    }

    //判断传入方块是否为雷
    private void countFlag(int row, int column) {
        if (row > -1 && row < glRows && column > -1 && column < glColumns
                && styleArray[row][column].equals("flag")) {
            flagAround++;
        }
    }

    //游戏胜利
    private void gameWin() {
        if (openCount != glRows * glColumns - bombNumber || timer == null) {
            return;
        }
        timer.cancel();
        timer = null;
        if (winVibration) {
            vibrationDevice();
        }
        gameOver = true;
        flagMineCell();
        setImageButton(ivPlay, "face_jiujiu");
        ToastUtil.showShort("游戏胜利");
    }

    //游戏胜利后标记所有雷
    private void flagMineCell() {
        String string;
        if (mineNumber == bombNumber) {//若为NF风格则将未标记雷所在格子的图片置为flag样式
            string = "flag";
        } else {//若为FL风格则将未标记雷所在格子的图片置为mine样式
            string = "flag_rough";
        }
        for (int i = 0; i < glRows; i++) {
            for (int j = 0; j < glColumns; j++) {
                if (styleArray[i][j].equals("normal")) {
                    setImageButton(cellArray[i][j], string);
                }
            }
        }
    }

    //游戏失败
    private void gameLose() {
        if (gameOver || timer == null) {
            return;
        }
        timer.cancel();
        timer = null;
        if (loseVibration) {
            vibrationDevice();
        }
        gameOver = true;
        openMineCell();
        setImageButton(ivPlay, "face_luelue");
        ToastUtil.showShort("游戏失败");
    }

    //游戏失败后翻开所有雷
    private void openMineCell() {
        for (int i = 0; i < glRows; i++) {
            for (int j = 0; j < glColumns; j++) {
                if (styleArray[i][j].equals("normal") && boardArray[i][j] == -1) {
                    setImageString(cellArray[i][j], "bomb");
                } else if (styleArray[i][j].equals("flag") && boardArray[i][j] != -1) {
                    setImageString(cellArray[i][j], "wrong_flag");
                }
            }
        }
    }

    //打开方块
    private void openCell(ImageView v) {
        int row = getRow(v);
        int column = getColumn(v);
        if (!styleArray[row][column].equals("normal")) {
            return;
        }
        if (boardArray[row][column] == -1) {
            gameLose();
            setImageButton(v, "first_bomb");
            return;
        }
        if (styleArray[row][column].equals("normal")) {
            setImageNumber(v);
            //如果当前方块是空则递归打开周围方块
            if (boardArray[row][column] == 0) {
                for (int i = row - 1; i <= row + 1; i++) {
                    for (int j = column - 1; j <= column + 1; j++) {
                        if (i != row || j != column) openOpening(i, j);
                    }
                }
            }
        }
    }

    //开空
    private void openOpening(int row, int column) {
        if (row > -1 && row < glRows && column > -1 && column < glColumns
                && styleArray[row][column].equals("normal")) {
            openCell(cellArray[row][column]);
        }
    }

    //获取当前方块所在行
    private int getRow(ImageView v) {
        return v.getId() / glColumns;
    }

    //获取当前方块所在列
    private int getColumn(ImageView v) {
        return v.getId() % glColumns;
    }

    //标记或取消标记当前方块
    private void flagCell(ImageView v) {
        int row = getRow(v);
        int column = getColumn(v);
        if (styleArray[row][column].equals("normal")) {
            mineNumber--;
            setImageString(v, "flag");
        } else if (styleArray[row][column].equals("flag")) {
            setImageString(v, "normal");
            mineNumber++;
        } else {
            return;
        }
        setMineCount();
    }

    //将周围方块显示为空白
    private void changeAroundBlank(ImageView v) {
        int row = getRow(v);
        int column = getColumn(v);
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = column - 1; j <= column + 1; j++) {
                if (i != row || j != column) changeCellBlank(i, j);
            }
        }
    }

    //将传入方块显示为空白
    private void changeCellBlank(int row, int column) {
        if (row > -1 && row < glRows && column > -1 && column < glColumns
                && styleArray[row][column].equals("normal")) {
            setImageButton(cellArray[row][column], "blank");
        }
    }

    //将周围方块显示为正常
    private void changeAroundNormal(ImageView v) {
        int row = getRow(v);
        int column = getColumn(v);
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = column - 1; j <= column + 1; j++) {
                if (i != row || j != column) changeCellNormal(i, j);
            }
        }
    }

    //将传入方块显示为正常
    private void changeCellNormal(int row, int column) {
        if (row > -1 && row < glRows && column > -1 && column < glColumns
                && styleArray[row][column].equals("normal")) {
            setImageButton(cellArray[row][column], "normal");
        }
    }

    //方块单击事件
    @Override
    public void onClick(View v) {
        if (gameOver) {
            return;
        }

        ImageView iv = (ImageView) v;

        int row = getRow(iv);
        int column = getColumn(iv);

        if (styleArray[row][column].equals("number")) {
            return;
        }

        if (flStyle) {
            flagCell(iv);
        } else {
            if (firstClick) {
                resetBomb(iv);
                resetTimer();
                passTime = 0;
                return;
            }
            openCell(iv);
        }
        gameWin();
    }

    //方块长按事件
    @Override
    public boolean onLongClick(View v) {
        if (gameOver) {
            return true;//返回true以屏蔽单击事件
        }
        ImageView iv = (ImageView) v;

        int row = getRow(iv);
        int column = getColumn(iv);

        if (!styleArray[row][column].equals("number")) {
            if (longClickVibration) {
                vibrationDevice();
            }
        } else {
            return true;
        }

        if (!flStyle) {
            flagCell(iv);
        } else {
            if (firstClick) {
                resetBomb(iv);
                resetTimer();
                passTime = 0;
                return true;
            }
            openCell(iv);
        }
        gameWin();

        return true;
    }

    //设备震动
    private void vibrationDevice() {
        Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(50);
    }

    private boolean scaleCanChange = false;//只有双指触控时才能进行缩放操作
    private double distanceFirst;//双指触控初始距离
    private double distanceSecond;//双指移动后的距离
    private double scale = 1.0;//当前方格比例
    private double scaleFirst;//双指触控初始比例
    int glWidth;//方块总宽度
    int glHeight;//方块总高度

    //获取双指间距离
    private double spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Math.sqrt(x * x + y * y);
    }

    //根据双之间距离改变缩放格子比例
    private void setCellScale(MotionEvent event) {
        distanceSecond = spacing(event);
        scale = distanceSecond / distanceFirst * scaleFirst;
        if (scale < 1) {
            scale = 1;
            scaleFirst = scale;
            distanceFirst = distanceSecond;
        } else if (scale > 1.4) {
            scale = 1.4;
            scaleFirst = scale;
            distanceFirst = distanceSecond;
        }
        if (scale != glMinesweeper.getScaleX()) {
            glMinesweeper.setScaleX((float) scale);
            glMinesweeper.setScaleY((float) scale);
            setCellPadding();
        }
    }

    //缩放后部分显示不全，设置Padding解决
    private void setCellPadding() {
        int paddingHorizontal = (int) ((scale - 1) * sideLength * glColumns / 2);
        int paddingVertical = (int) ((scale - 1) * sideLength * glRows / 2);
        glMinesweeper.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);

        int marginHorizontal = (int) ((glWidth - glColumns * sideLength * scale) / 2);
        int marginVertical = (int) ((glHeight - glRows * sideLength * scale) / 2);
        if (marginHorizontal < 0) marginHorizontal = 0;
        if (marginVertical < 0) marginVertical = 0;
        setMargins(scMinesweeper, marginHorizontal, marginVertical, marginHorizontal, marginVertical);
    }

    //初始化缩放比例
    private void initScale() {
        glMinesweeper.setScaleX((float) scale);
        glMinesweeper.setScaleY((float) scale);
        setCellPadding();
    }

    @BindView(R.id.cellScale) View cellScale;//双指缩放时遮罩拦截点击

    //分发onTouch事件，用于缩放界面
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                if (scaleCanChange && event.getPointerCount() == 2) {
                    setCellScale(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    //首次双指触控
                    if (!scaleCanChange) {
                        scaleCanChange = true;
                        cellScale.setClickable(true);
                    }
                    scaleFirst = glMinesweeper.getScaleX();
                    distanceFirst = spacing(event);
                } else if (event.getPointerCount() > 2) {
                    //多于两个手指触控时取消缩放操作，当所有手指放开并再次双指触控时进行缩放操作
                    scaleCanChange = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                scaleCanChange = false;
                cellScale.setClickable(false);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    //方块触摸事件
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (gameOver) {
            return true;//返回true以屏蔽单击事件
        }

        ImageView iv = (ImageView) v;
        int row = getRow(iv);
        int column = getColumn(iv);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setImageButton(ivPlay, "face_click");
                if (styleArray[row][column].equals("number")) {
                    openAround = true;
                    changeAroundBlank(iv);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                setImageButton(ivPlay, "face_normal");
                if (styleArray[row][column].equals("number")) {
                    openAround = false;
                    changeAroundNormal(iv);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (styleArray[row][column].equals("number")) {
                    if (openAround) {
                        openAround(iv);
                        gameWin();
                    }
                    openAround = false;
                    changeAroundNormal(iv);
                }
                if (!gameOver) setImageButton(ivPlay, "face_normal");
                break;
        }
        return false;
    }
}
