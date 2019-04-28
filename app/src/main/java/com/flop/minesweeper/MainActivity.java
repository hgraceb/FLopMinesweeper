package com.flop.minesweeper;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.flop.minesweeper.Util.NetWorkUtil;
import com.flop.minesweeper.Util.ToastUtil;
import com.flop.minesweeper.zhangye.bean.RawEventDetailBean;
import com.flop.minesweeper.zhangye.bean.VideoDisplayBean;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flop.minesweeper.Constant.VIDEO_INFO;
import static com.flop.minesweeper.Constant.bean;
import static com.flop.minesweeper.Constant.rawVideo;
import static com.flop.minesweeper.Util.MarginsUtil.setMargins;

/***
 * Created by Flop on 2018/10/01.
 *
 * 别的小朋友国庆都出去玩了
 *
 * 而我还在敲代码
 *
 * (╯'-')╯︵ ╧══╧（掀！桌！子！）
 *
 * ╤══╤ ノ('-'ノ) （摆好摆好）
 *
 * (╯°Д°)╯︵ ╧══╧(再他妈的掀一次）
 *
 **/

public class MainActivity extends AppCompatActivity {

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
    public boolean leftClick = false;//鼠标左键
    public boolean rightClick = false;//鼠标右键
    public boolean middleClick = false;//判断中键是否点击
    public boolean clickValid = true;//当前点击是否单击有效
    public int plan = 0;//录像播放进度
    public int size = 0;//plan最大值
    public double realTime = 0;//录像实际时间
    public double speed = 1.0;//录像播放速度
    public double millisecond = 0;//录像当前时间
    public ImageView front;
    public ImageView current = null;
    List<RawEventDetailBean> events;//鼠标事件
    private static Thread mThread;//录像读取线程

    public int openCount = 0;//已打开方块数目，判断游戏是否胜利
    public int flagAround;//方块周围旗子数目,判断点击当前格子是否打开双击开周围方块
    public int bombNumber;//总雷数
    public int mineNumber;//剩余雷数
    private double beginTime;//开始时间
    private double passTime;//退出页面前已经计时的时间
    private Timer timer;//定时器
    DecimalFormat df = new DecimalFormat("#0.00");//格式化两位小数

    //定义Handler句柄
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 1) {
                playEvents();
            } else if (message.what == 200 && bean != null) {
                initVideos();
            } else if (message.what == -1) {
                ToastUtil.showShort(MainActivity.this, "网络错误");
                finish();
            } else if (message.what == -2) {
                ToastUtil.showShort(MainActivity.this, "录像不存在");
                finish();
            }
        }
    };

    @BindView(R.id.hsMinesweeper) HorizontalScrollView hsMinesweeper;//水平滑动框架
    @BindView(R.id.glMinesweeper) GridLayout glMinesweeper;//方块总框架
    @BindView(R.id.lyMinesweeper) LinearLayout lyMinesweeper;//方块边框
    @BindView(R.id.rlTop) RelativeLayout rlTop;//顶部信息栏
    @BindView(R.id.lyBottom) LinearLayout lyBottom;//底部信息栏
    @BindView(R.id.ivMouse) ImageView ivMouse;//录像鼠标指针
    @BindView(R.id.scMinesweeper) ScrollView scMinesweeper;//垂直滚动框
    @BindView(R.id.tvUserId) TextView tvUserId;//用户标识

    @BindView(R.id.ivRestart) ImageButton ivRestart;//重新播放
    @BindView(R.id.ivPause) ImageButton ivPause;//暂停播放
    @BindView(R.id.ivStop) ImageButton ivStop;//结束播放
    @BindView(R.id.ivInfo) ImageButton ivInfo;//录像信息

    @BindView(R.id.sbSpeed) SeekBar sbSpeed;//速度选择
    @BindView(R.id.tvSpeed) TextView tvSpeed;//速度显示

    @BindView(R.id.sbTime) SeekBar sbTime;//时间选择
    @BindView(R.id.tvTime) TextView tvTime;//时间显示

    //绑定单击事件
    @OnClick({R.id.ivRestart, R.id.ivPause, R.id.ivStop, R.id.ivInfo, R.id.tvSpeed})
    public void bindViewOnClick(View v) {
        String function = "bindViewOnClick";
        switch (v.getId()) {
            case R.id.ivRestart:
                restartPlayVideos();
                break;
            case R.id.ivPause:
                pausePlayVideos();
                break;
            case R.id.ivStop:
                finish();
                break;
            case R.id.ivInfo:
                dialogInfo();
                break;
            case R.id.tvSpeed:
                resetSpeed();
                break;
        }
    }

    //对话框显示录像信息
    private void dialogInfo() {
        String function = "dialogInfo";
        boolean pause = false;

        if (timer != null) {
            //暂停录像播放
            pause = true;
            pausePlayVideos();
        }

        //填充ListView布局
        View dialogView = View.inflate(this, R.layout.dialog_videos_info, null);
        //初始化ListView控件
        ListView lvVideosInfo = dialogView.findViewById(R.id.lvVideosInfo);

        //定义泛型
        List<Map<String, ?>> list = new ArrayList<>();
        for (int i = 0; i < VIDEO_INFO.length; i++) {
            Map<String, String> keyValuePair = new HashMap<>();
            keyValuePair.put("Key", VIDEO_INFO[i]);
            switch (VIDEO_INFO[i]) {
                case "ID":
                    keyValuePair.put("Value", bean.getUserID());
                    break;
                case "Time":
                    keyValuePair.put("Value", df.format(realTime));
                    break;
                case "Date":
                    keyValuePair.put("Value", bean.getDate().substring(0, bean.getDate().indexOf(".")));
                    break;
                case "3BV":
                    keyValuePair.put("Value", bean.getBbbv());
                    break;
                case "3BV/s":
                    keyValuePair.put("Value", bean.getBbbvs());
                    break;
                case "Openings":
                    keyValuePair.put("Value", bean.getOpenings());
                    break;
                case "Islands":
                    keyValuePair.put("Value", bean.getIslands());
                    break;
                case "Ces":
                    keyValuePair.put("Value", bean.getClickE() + "@" + df.format(Double.parseDouble(bean.getClickE()) / realTime));
                    break;
                case "Clicks":
                    keyValuePair.put("Value", bean.getAllClicks() + "@" + df.format(Double.parseDouble(bean.getAllClicks()) / realTime));
                    break;
                case "Left":
                    keyValuePair.put("Value", bean.getLclicks() + "@" + df.format(Double.parseDouble(bean.getLclicks()) / realTime));
                    break;
                case "Right":
                    keyValuePair.put("Value", bean.getRclicks() + "@" + df.format(Double.parseDouble(bean.getRclicks()) / realTime));
                    break;
                case "Double":
                    keyValuePair.put("Value", bean.getDclicks() + "@" + df.format(Double.parseDouble(bean.getDclicks()) / realTime));
                    break;
                case "Flags":
                    keyValuePair.put("Value", bean.getFlags());
                    break;
                case "IOE":
                    keyValuePair.put("Value", bean.getIoe());
                    break;
                case "Thrp":
                    keyValuePair.put("Value", bean.getThrp());
                    break;
                case "Corr":
                    keyValuePair.put("Value", bean.getCorr());
                    break;
                case "QG":
                    keyValuePair.put("Value", bean.getQg());
                    break;
                case "RQP":
                    keyValuePair.put("Value", bean.getRqp());
                    break;
                case "Path":
                    keyValuePair.put("Value", bean.getDistance());
                    break;
                case "STNB":
                    if (gameLevel == 1) {
                        keyValuePair.put("Value", df.format(47.299 / Double.parseDouble(bean.getQg())));
                        break;
                    } else if (gameLevel == 2) {
                        keyValuePair.put("Value", df.format(153.73 / Double.parseDouble(bean.getQg())));
                        break;
                    } else if (gameLevel == 3) {
                        keyValuePair.put("Value", df.format(435.001 / Double.parseDouble(bean.getQg())));
                        break;
                    }
                default:
                    keyValuePair.put("Value", "无键值" + i);
            }
            list.add(keyValuePair);
        }

        //ListView设置适配器
        lvVideosInfo.setAdapter(new SimpleAdapter(
                this,
                list,
                R.layout.dialog_videos_info_item,
                new String[]{"Key", "Value"},
                new int[]{R.id.tvInfoKey, R.id.tvInfoValue}));

        //将pause传入dialog内
        final boolean finalPause = pause;

        //初始化dialog
        AlertDialog infoDialog = new AlertDialog.Builder(this)
                .setTitle("录像信息")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //用户点击按钮主动触发dismiss无法再次触发setOnCancelListener
                        if (finalPause) {
                            //继续录像播放
                            pausePlayVideos();
                        }
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    //setOnCancelListener只能监听返回键和范围外点击
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (finalPause) {
                            //继续录像播放
                            pausePlayVideos();
                        }
                    }
                })
                .setView(dialogView)
                .create();
        infoDialog.show();

        //设置dialog宽度，必须在show()后面执行
        Window window = infoDialog.getWindow();
        //防止getAttributes空指针警告
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            Display display = getWindowManager().getDefaultDisplay();
            lp.width = (int) (display.getWidth() * 0.5); //设置宽度
            infoDialog.getWindow().setAttributes(lp);
        }
    }

    //重置录像播放速度为1.00
    private void resetSpeed() {
        sbSpeed.setProgress(198);
        if (timer == null) {
            //暂停录像播放时只改变speed值
            speed = 1.00;
        } else {
            pausePlayVideos();
            speed = 1.00;
            pausePlayVideos();
        }
    }

    //暂停录像
    private void pausePlayVideos() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        } else {
            passTime = (events.get(plan).getSec() * 1000 + events.get(plan).getHun() * 10);
            setVideoTimer();
        }
    }

    //重新开始播放录像
    private void restartPlayVideos() {
        initField(0);
        setVideoTimer();
    }

    //初始化界面数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //判断是否进行录像播放
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            initSeekBarListeners();
            rlTop.setVisibility(View.INVISIBLE);
            hsMinesweeper.setVisibility(View.INVISIBLE);
            lyBottom.setVisibility(View.INVISIBLE);
            if (bundle.getInt("request") == Constant.VIDEO_REQUEST_CODE_ONLINE) {
                analyzeOnlineVideo(bundle.getString("down"));
            } else if (bundle.getInt("request") == Constant.VIDEO_REQUEST_CODE_LOCAL) {
                analyzeLocalVideo(bundle.getByteArray("byteStream"), bundle.getString("videoType"));
            }
        }
    }

    //读取本地录像
    private void analyzeLocalVideo(byte[] byteStream, String videoType) {
        bean = new VideoDisplayBean();
        //反射调用类，通过获取类类型，进而获取Method对象，进而调用类的方法
        Class<?> classMethod;
        try {
            String classNameStr = "com.flop.minesweeper.zhangye.util." + videoType + "Util";
            classMethod = Class.forName(classNameStr);
            Method method = classMethod.getMethod("analyzeVideo", byte[].class, VideoDisplayBean.class);
            method.invoke(classMethod.newInstance(), byteStream, bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //在子线程中将Message对象发出去
        Message message = new Message();
        message.what = 200;
        handler.sendMessage(message);
    }

    //初始化SeekBar操作
    private void initSeekBarListeners() {
        //速度控制条
        sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            double value;
            String str;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //最小速度为0.01
                value = progress + 2;

                if (value <= 200) {
                    //1-200对应速度0.02-1.00
                    value = value / 200;
                } else if (value <= 300) {
                    //200-300对应速度1.00-5.00
                    value = (value - 200) * 0.04 + 1;
                } else {
                    //300-400对应速度5.00-10.00
                    value = (value - 300) * 0.05 + 5;
                }
                str = df.format(value) + "x";
                tvSpeed.setText(str);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (timer == null) {
                    //暂停录像播放时只改变speed值
                    speed = value;
                } else {
                    pausePlayVideos();
                    speed = value;
                    pausePlayVideos();
                }
            }
        });

        //时间进度条
        sbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //value要进行除法运算
            double value;
            String strTime;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;

                if (fromUser) {
                    strTime = df.format(value / sbTime.getMax() * realTime);
                    tvTime.setText(strTime);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }

                if (value / sbTime.getMax() * realTime >= events.get(plan).getEventTime()) {
                    beginTime = System.currentTimeMillis();
                    passTime = value / sbTime.getMax() * realTime * 1000;
                    setVideoTimer();
                } else {
                    initField(0);
                    passTime = value / sbTime.getMax() * realTime * 1000;
                    setVideoTimer();
                }
            }
        });
    }

    //获取录像信息
    private void analyzeOnlineVideo(final String videoPage) {
        mThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String videoPageContent = Jsoup.connect(videoPage).get().toString();

                    //录像被删除或被冻结都有可能造成录像获取错误
                    if (!videoPageContent.contains("/V")) {
                        //在子线程中将Message对象发出去
                        Message message = new Message();
                        message.what = -2;
                        handler.sendMessage(message);
                        return;
                    }
                    String videoUrl = Constant.SAOLEI_NET + videoPageContent.substring(videoPageContent.indexOf("/V"),
                            videoPageContent.indexOf("'", videoPageContent.indexOf("/V"))).replaceAll(" ", "%20");
                    String videoType = videoUrl.substring((videoUrl.lastIndexOf(".")));
                    switch (videoType) {
                        case ".avf":
                            videoType = "Avf";
                            break;
                        case ".mvf":
                            videoType = "Mvf";
                            break;
                        default:
                            return;
                    }

                    //用HttpClient发送请求，分为五步
                    //第一步：创建HttpClient对象
                    HttpClient httpClient = new DefaultHttpClient();
                    //第二步：创建代表请求的对象,参数是访问的服务器地址
                    HttpGet httpGet = new HttpGet(videoUrl);
                    //第三步：执行请求，获取服务器发还的相应对象
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //第五步：从相应对象当中取出数据，放到entity当中
                        HttpEntity entity = httpResponse.getEntity();
                        //将entity当中的数据以二进制流读取
                        byte[] byteStream = EntityUtils.toByteArray(entity);

                        bean = new VideoDisplayBean();
                        bean.setName(videoUrl.substring((videoUrl.lastIndexOf("/") + 1)));

                        //反射调用类，通过获取类类型，进而获取Method对象，进而调用类的方法
                        Class<?> classMethod;
                        try {
                            String classNameStr = "com.flop.minesweeper.zhangye.util." + videoType + "Util";
                            classMethod = Class.forName(classNameStr);
                            Method method = classMethod.getMethod("analyzeVideo", byte[].class, VideoDisplayBean.class);
                            method.invoke(classMethod.newInstance(), byteStream, bean);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //在子线程中将Message对象发出去
                        Message message = new Message();
                        message.what = 200;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (!NetWorkUtil.isNetworkConnected(MainActivity.this)) {
                        //在子线程中将Message对象发出去
                        Message message = new Message();
                        message.what = -1;
                        handler.sendMessage(message);
                    }
                }
            }
        };
        mThread.start();
    }

    //回到界面继续上次时间计时
    @Override
    protected void onResume() {
        super.onResume();
        if (timer != null) {
            timer.cancel();
            passTime = (events.get(plan).getSec() * 1000 + events.get(plan).getHun() * 10);
            setVideoTimer();
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

        //退出时结束线程，可能会有BUG
        if (mThread != null) {
            while (mThread.isAlive()) {
            }
            mThread.interrupt();
        }
    }

    //界面销毁时停止定时器。。并不知道有没有用的一段语句，可能可以减少闪退的情况发生？？
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
            Log.i(TAG, "onDestroy: timer=null");
        }
    }

    //初始化游戏
    public void initGame(int level) {
        ivMouse.setVisibility(View.GONE);
        initLevel(level);
        initField(level);
        initCell();
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
            sideLength = (glWidth < glHeight ? glWidth : glHeight) / 9;
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
    }

    //初始化方块
    @SuppressLint("ClickableViewAccessibility")
    private void initCell() {
        //设置为几行几列
        glMinesweeper.setRowCount(glRows);
        glMinesweeper.setColumnCount(glColumns);
        lyMinesweeper.setBackgroundResource(R.drawable.ic_ly_background);

        //为每个方块设置图片和ID
        for (int i = 0; i < glRows; i++) {
            for (int j = 0; j < glColumns; j++) {
                cellArray[i][j] = new ImageView(this);
                cellArray[i][j].setImageResource(R.mipmap.iv_normal);
                cellArray[i][j].setId(i * glColumns + j);
                glMinesweeper.addView(cellArray[i][j], sideLength, sideLength);
            }
        }

        //设置用户标识宽度
        tvUserId.setWidth(lyMinesweeper.getWidth() + glColumns * sideLength);
    }

    //初始化变量
    private void initField(int level) {
        gameOver = false;
        firstClick = false;//第一次点击
        leftClick = false;//鼠标左键
        rightClick = false;//鼠标右键
        middleClick = false;//判断中键是否点击
        clickValid = true;
        passTime = 0;
        plan = 0;
        current = null;
        openCount = 0;
        mineNumber = bombNumber;

        if (level == 0) {
            for (int i = 0; i < glRows; i++) {
                for (int j = 0; j < glColumns; j++) {
                    setImageString(cellArray[i][j], "normal");
                }
            }
        } else {
            boardArray = new int[glRows][glColumns];
            cellArray = new ImageView[glRows][glColumns];
            styleArray = new String[glRows][glColumns];
            for (int i = 0; i < glRows; i++) {
                for (int j = 0; j < glColumns; j++) {
                    styleArray[i][j] = "normal";
                }
            }
        }

    }

    //获取录像事件并初始化游戏
    private void initVideos() {
        String function = "initVideos";

        if (bean.getLevel() == null) {//bean内没有解析自定义录像的代码段，有需要时需自行添加
            ToastUtil.showShort(this, "录像读取出错");
            finish();
            return;
        }
        switch (bean.getLevel()) {
            case "Beg":
                initGame(1);
                break;
            case "Int":
                initGame(2);
                break;
            case "Exp":
                initGame(3);
                break;
            default:
                ToastUtil.showShort(this, "录像读取出错");
                finish();
                return;
        }

        List<Integer> board = rawVideo.getRawBoardBean().getBoard();

        //根据录像内board信息重新布雷
        for (int row = 0; row < glRows; row++) {
            for (int column = 0; column < glColumns; column++) {
                //置雷
                if (board.get(row * glColumns + column) == 1) {
                    boardArray[row][column] = -1;
                    //雷周围数字自增
                    for (int i = row - 1; i <= row + 1; i++) {
                        for (int j = column - 1; j <= column + 1; j++) {
                            if (i != row || j != column) increaseNumber(i, j);
                        }
                    }
                }
            }
        }

        //初始化鼠标指针图片大小
        ivMouse.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams para;
        para = ivMouse.getLayoutParams();
        para.height = sideLength * 19 / 16;
        para.width = sideLength * 19 / 16;
        ivMouse.setLayoutParams(para);

        //设置用户标识
        tvUserId.setText(bean.getUserID());

        //重新显示界面控件
        rlTop.setVisibility(View.GONE);
        hsMinesweeper.setVisibility(View.VISIBLE);
        lyBottom.setVisibility(View.VISIBLE);

        //设置鼠标事件
        events = rawVideo.getRawEventDetailBean();

        //设置事件进度条长度
        if (events.get(events.size() - 1).getEventTime() == -1) {
            size = events.size() - 2;
        } else {
            size = events.size() - 1;
        }
        realTime = events.get(size).getEventTime();

        //开始根据鼠标事件播放录像
        setVideoTimer();
    }

    //根据鼠标事件播放录像
    private void playEvents() {
        millisecond = (System.currentTimeMillis() - beginTime) * speed + passTime;

        //avf录像数据最后一个鼠标事件的时间为-1，mvf录像最后一个鼠标事件的时间为游戏结束时间
        while (plan <= size && millisecond > (events.get(plan).getEventTime() * 1000)) {

            //鼠标指针超出指定范围则只进行变量赋值，以及方块的changeAroundNormal方法
            if ((events.get(plan).getY() / 16 + 1) > glRows || (events.get(plan).getX() / 16 + 1) > glColumns) {
                Log.i(TAG, "overflow: " + (events.get(plan).getY() / 16) + " " + (events.get(plan).getX() / 16));

                //避免超出后继续进行操作导致的有部分方块没有恢复正常状态
                if(current!=null)changeAroundNormal(current);//判断current是否已经初始化

                if (events.get(plan).getMouseType() == 3) {//lc
                    leftClick = true;
                } else if (events.get(plan).getMouseType() == 9) {//rc
                    rightClick = true;
                } else if (events.get(plan).getMouseType() == 5 || events.get(plan).getMouseType() == 21) {//lr
                    leftClick = false;
                } else if (events.get(plan).getMouseType() == 17 || events.get(plan).getMouseType() == 145) {//rr
                    rightClick = false;
                } else if (events.get(plan).getMouseType() == 33) {//mc
                    middleClick = true;
                } else if (events.get(plan).getMouseType() == 65 || events.get(plan).getMouseType() == 193) {//mr
                    middleClick = false;
                }

                if (leftClick && rightClick) {
                    clickValid = false;
                } else if (!leftClick && !rightClick) {
                    clickValid = true;
                }

                plan++;
                continue;
            }

            //设置鼠标指针的位置
            if (plan < events.size() - 1) {
                //+16为背景边框宽度
                setMargins(ivMouse, (int) (events.get(plan).getX() * sideLength / 16 * scale + 16), (int) (events.get(plan).getY() * sideLength / 16 * scale + 16), 0, 0);
            }

            front = current;
            //横竖方向是个大坑......注意前面是getY(),后面才是getX()
            current = cellArray[(events.get(plan).getY() / 16)][(events.get(plan).getX() / 16)];

            if (events.get(plan).getMouseType() == 1) {//mv
                if (front != null) {
                    if (leftClick && !rightClick) {
                        //判断条件不需要添加getStyle(current).equals("blank")，因为并无"blank"的style
                        //可能会出现BUG
                        changeCellNormal(front);
                    } else if ((leftClick && rightClick) || middleClick) {
                        changeAroundNormal(front);
                    }
                }

                //缺少clickValid判断会导致多余的方块改变样式
                if (getStyle(current).equals("normal") && clickValid && leftClick && !rightClick) {
                    changeCellBlank(current);
                } else if ((leftClick && rightClick) || middleClick) {
                    changeAroundBlank(current);
                }
            } else if (events.get(plan).getMouseType() == 3) {//lc
                leftClick = true;

                if (rightClick) {
                    changeAroundBlank(current);
                }
            } else if (events.get(plan).getMouseType() == 9) {//rc
                rightClick = true;
                if (clickValid) {
                    //判断条件中的!leftClick不可省略,后面的条件需要用括号合并以改变判断条件的优先级
                    if (!leftClick && (getStyle(current).equals("normal") || getStyle(current).equals("flag"))) {
                        flagCell(current);
                    }
                }

                if (leftClick) {
                    changeAroundBlank(current);
                }
            } else if (events.get(plan).getMouseType() == 5 || events.get(plan).getMouseType() == 21) {//lr
                leftClick = false;
                if (clickValid) {
                    //判断条件中的!rightClick不可省略
                    if (!rightClick && getStyle(current).equals("normal")) {
                        openCell(current);
                    }
                }

                if (rightClick && getStyle(current).equals("number")) {
                    openAround(current);
                }

                if (rightClick) {
                    changeAroundNormal(current);
                }
            } else if (events.get(plan).getMouseType() == 17 || events.get(plan).getMouseType() == 145) {//rr
                rightClick = false;
                if (leftClick && getStyle(current).equals("number")) {
                    openAround(current);
                }

                if (leftClick) {
                    changeAroundNormal(current);
                }
            } else if (events.get(plan).getMouseType() == 33) {//mc
                middleClick = true;
                changeAroundBlank(current);
            } else if (events.get(plan).getMouseType() == 65 || events.get(plan).getMouseType() == 193) {//mr
                middleClick = false;
                changeAroundNormal(current);
                openAround(current);
            }

            if (leftClick && rightClick) {
                clickValid = false;
            } else if (!leftClick && !rightClick) {
                clickValid = true;
            }


//            if (events.get(plan).getEventTime() > 3.70 && events.get(plan).getEventTime() < 4) {
//                String temp = (plan + 23) + " " + events.get(plan).getSec() + "." + events.get(plan).getHun() + " ";
//                if (events.get(plan).getMouseType() == 1) {//mv
//                    temp += "mv ";
//                } else if (events.get(plan).getMouseType() == 3) {//lc
//                    temp += "lc ";
//                } else if (events.get(plan).getMouseType() == 9) {//rc
//                    temp += "rc ";
//                } else if (events.get(plan).getMouseType() == 5 || events.get(plan).getMouseType() == 21) {//lr
//                    temp += "lr ";
//                } else if (events.get(plan).getMouseType() == 17 || events.get(plan).getMouseType() == 145) {//rr
//                    temp += "rr ";
//                } else if (events.get(plan).getMouseType() == 33) {//mc
//                    temp += "mc ";
//                } else if (events.get(plan).getMouseType() == 65 || events.get(plan).getMouseType() == 193) {//mr
//                    temp += "mr ";
//                }
//                temp += (events.get(plan).getX() / 16 + 1) + " ";
//                temp += (events.get(plan).getY() / 16 + 1) + "(";
//                temp += +events.get(plan).getX() + " ";
//                temp += events.get(plan).getY() + ")";
//                Log.i(TAG, temp + " leftClick:" + leftClick + " rightClick:" + rightClick + " clickValid:" + clickValid);
//            }

            plan++;
        }

        //处理plan越界
        if (plan > size) {
            Log.i(TAG, plan + " > " + size);
            //重置plan，省略此句会造成无法回放及无法取消定时器
            plan = size;

            //未重复测试是否每次超时都会使plan越界
            if (events.get(size).getEventTime() >= 999) {
                //判断游戏是否超时
                ToastUtil.showShort(this, "游戏超时");
                gameLose();

            }
        }

        //非用户手动触发设置事件进度
        if (!sbTime.isPressed()) {
            tvTime.setText(df.format(events.get(plan).getEventTime()));
            sbTime.setProgress((int) ((events.get(plan).getEventTime()) / realTime * sbTime.getMax()));
        }

        //判断游戏是否胜利
        gameWin();
    }

    //设置录像播放定时器
    private void setVideoTimer() {
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
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(task, 0, 1);
    }

    //计算方块对应数字
    private void increaseNumber(int row, int column) {
        if (row > -1 && row < glRows && column > -1 && column < glColumns) {
            if (boardArray[row][column] != -1) {
                boardArray[row][column]++;
            }
        }
    }

    //获取GridLayout高度
    private int getGLHeight() {
        if (rlTop.getVisibility() == View.VISIBLE) {
            return (int) (lyBottom.getY() - rlTop.getY() - rlTop.getLayoutParams().height) - tvUserId.getHeight();
        } else {
            return (int) (lyBottom.getY() - rlTop.getY()) - tvUserId.getHeight();
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
        if (gameOver || openCount != glRows * glColumns - bombNumber || timer == null) {
            return;
        }
        timer.cancel();
        timer = null;
        if (winVibration) {
            vibrationDevice();
        }
        ToastUtil.showShort(this, "游戏胜利");
        gameOver = true;
        flagMineCell();
    }

    //游戏胜利后标记所有雷
    private void flagMineCell() {
        String string;
        if (mineNumber == bombNumber) {
            //若为NF风格则将未标记雷所在格子的图片置为flag样式
            string = "flag";
        } else {
            //若为FL风格则将未标记雷所在格子的图片置为mine样式
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
        //取消定时器
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (gameOver) {
            return;
        }
        if (loseVibration) {
            vibrationDevice();
        }
        ToastUtil.showShort(this, "游戏失败");
        gameOver = true;
        openMineCell();
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
            setImageString(v, "flag");
            mineNumber--;
        } else if (styleArray[row][column].equals("flag")) {
            setImageString(v, "normal");
            mineNumber++;
        }
    }

    //将周围方块显示为空白
    private void changeAroundBlank(ImageView v) {
        int row = getRow(v);
        int column = getColumn(v);
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = column - 1; j <= column + 1; j++) {
                //此处无需添加if (i != row || j != column)
                changeCellBlank(i, j);
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

    //将传入方块显示为空白
    private void changeCellBlank(ImageView v) {
        int row = getRow(v);
        int column = getColumn(v);
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
                //此处无需添加if (i != row || j != column)
                changeCellNormal(i, j);
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

    //将传入方块显示为正常
    private void changeCellNormal(ImageView v) {
        int row = getRow(v);
        int column = getColumn(v);
        if (row > -1 && row < glRows && column > -1 && column < glColumns
                && styleArray[row][column].equals("normal")) {
            setImageButton(cellArray[row][column], "normal");
        }
    }

    //获取传入方块的状态
    private String getStyle(ImageView v) {
        int row = getRow(v);
        int column = getColumn(v);
        return styleArray[row][column];
    }

    //设备震动
    private void vibrationDevice() {
        Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(50);
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
            case "blank":
                v.setImageResource(R.mipmap.iv_n0);
                break;
            case "wrong_flag":
                v.setImageResource(R.mipmap.iv_wrong_flag);
                break;
            case "bomb":
                v.setImageResource(R.mipmap.iv_bomb);
                break;
        }
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
        } else if (scale > 2.0) {
            scale = 2.0;
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
        tvUserId.setWidth((int) (glColumns * sideLength * scale) + 32);
        setMargins(ivMouse, (int) (events.get(plan).getX() * sideLength / 16 * scale + 16), (int) (events.get(plan).getY() * sideLength / 16 * scale + 16), 0, 0);
    }

    //根据级别初始化缩放比例
    private void initScale() {
        if (gameLevel == 1) {
            glMinesweeper.setScaleX(1.0f);
            glMinesweeper.setScaleY(1.0f);
            glMinesweeper.setPadding(0, 0, 0, 0);
        } else {
            glMinesweeper.setScaleX((float) scale);
            glMinesweeper.setScaleY((float) scale);
            setCellPadding();
        }
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
}
