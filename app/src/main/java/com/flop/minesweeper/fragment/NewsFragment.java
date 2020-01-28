package com.flop.minesweeper.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flop.minesweeper.adapter.NewsAdapter;
import com.flop.minesweeper.variable.Constant;
import com.flop.minesweeper.R;
import com.flop.minesweeper.util.LogUtil;
import com.flop.minesweeper.util.TimeUtil;
import com.flop.minesweeper.util.ToastUtil;
import com.flop.minesweeper.activity.MainActivity;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.flop.minesweeper.variable.Constant.NEWS_ITEM;
import static com.flop.minesweeper.variable.Constant.NEWS_PAGE;
import static com.flop.minesweeper.variable.Constant.ORDER_MENU;
import static com.flop.minesweeper.variable.Constant.PROGRESS_PAGE;
import static com.flop.minesweeper.variable.Constant.SAOLEI_NEWS;
import static com.flop.minesweeper.variable.Constant.TAG;
import static com.flop.minesweeper.variable.Constant.orderProgress;

/**
 * Created by Flop on 2018/10/14.
 */
public class NewsFragment extends Fragment {
    private String[] mDate;//日期
    private String[] mName;//姓名
    private String[] mSex;//性别
    private String[] mLevel;//级别
    private String[] mRecord;//纪录
    private String[] mPromote;//提升
    private String[] mAchieve;//提升
    private String[] mDown;//录像地址
    private String[] mPlayerId;//用户ID
    private String[] mVideoId;//录像ID
    private int playerId;
    private static Thread mThread;
    //mData定义为static类型，保证用户按返回键之后onPause时数据不被销毁
    private List<Map<String, String>> mData;

//    private int mItem = NEWS_ITEM;//Item数目

    private Context mContext;
    private Activity mActivity;

    @BindView(R.id.rvVideos)
    RecyclerView mRecyclerView;
    @BindView(R.id.pbVideos)
    ProgressBar mProgressBar;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    private Handler handlerRefresh;//刷新管理

    /*
     * 因为默认先执行setUserVisibleHint再onCreate，因此默认当前页面为雷界快讯
     * 在setUserVisibleHint函数中判断是否为雷界快讯页面，是则等待onCreate函数执行完毕
     * 在onCreateView判断是否为雷界快讯页面，是则刷新一次页面
     */
    private String mPage = "NewsFragment";//页面名称
    private int mItem = NEWS_ITEM;//Item数目

    //构造函数传入参数
    public static NewsFragment newInstance(String pageName, int itemCount) {
        NewsFragment newFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("mPage", pageName);//页面的名称
        bundle.putInt("mItem", itemCount);//Item数目
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取构造函数中的变量
        Bundle args = getArguments();
        if (args != null) {
            this.mPage = args.getString("mPage");
            this.mItem = args.getInt("mItem");
        }
    }

    //获取当前Fragment的Context和Activity，避免有的生命周期内部getContext返回Null
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        ButterKnife.bind(this, view);

        MainActivity mainActivity = new MainActivity();
        handlerRefresh = mainActivity.handlerVideos;

        refreshLayout.setOnRefreshListener(refreshlayout -> {
            if (mThread != null && !mThread.isAlive()) {
                refreshVideos();
            } else {
                refreshlayout.finishRefresh(false);//传入false表示刷新失败
            }
        });

        if (mPage.equals("NewsFragment")) refreshVideos();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //当前Fragment可见而mData数据为空，判断为第一次进入此界面
        if (isVisibleToUser && mData == null) {
            if (!mPage.equals("NewsFragment")) refreshVideos();
        }
    }

    //初始化录像数据
    public void initVideos() {
        LogUtil.i("初始化" + mPage + "页面", new Throwable());
        initFields();

        //显示进度条
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

        //翻页时不可进行下拉刷新
        refreshLayout.setEnableRefresh(false);

        getVideoItem();
    }

    //初始化变量
    public void initFields() {
        mDate = new String[mItem];
        mName = new String[mItem];
        mSex = new String[mItem];
        mLevel = new String[mItem];
        mRecord = new String[mItem];
        mPromote = new String[mItem];
        mAchieve = new String[mItem];
        mDown = new String[mItem];
        mPlayerId = new String[mItem];
        mVideoId = new String[mItem];
    }

    //刷新当前界面
    public void refreshVideos() {
        LogUtil.i("刷新" + mPage + "页面", new Throwable());
        initFields();
        getVideoItem();
    }

    //定义Handler句柄
    Handler handler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 200) setVideoItem();
            else if (msg.what == -1) {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                ToastUtil.showShort(mActivity, "网络错误");
                refreshLayout.finishRefresh(false);
                refreshLayout.setEnableRefresh(true);
                // 设置Item为空，保证其他操作可以正常执行，如：翻页
                setVideoItem();
            }
            return false;
        }
    });

    public void getVideoItem() {
        mThread = new Thread() {
            @Override
            public void run() {
                super.run();
                HttpURLConnection conn = null;
                BufferedReader in = null;
                try {
//                    long time = System.currentTimeMillis();

                    //Message已经不是自己创建的了,而是从MessagePool拿的,省去了创建对象申请内存的开销
                    Message messageRefresh = handlerRefresh.obtainMessage();
                    messageRefresh.obj = "Refreshing" + mPage;
                    handlerRefresh.sendMessage(messageRefresh);

                    String path;
                    switch (mPage) {
                        case "NewsFragment":
                            path = SAOLEI_NEWS + NEWS_PAGE;
                            break;
                        case "ProgressFragment":
                            setPlayerId(Constant.playerId);
                            if (orderProgress.getMenu().equals(ORDER_MENU[0])) {
                                path = "http://www.saolei.wang/News/My.asp?Id=" + playerId + "&Page=" + PROGRESS_PAGE;
                            } else {
                                path = "http://www.saolei.wang/News/My_" + orderProgress.getMenu() + ".asp?Id=" + playerId + "&Page=" + PROGRESS_PAGE;
                            }
                            break;
                        default:
                            path = SAOLEI_NEWS + NEWS_PAGE;
                            break;
                    }

                    URL url = new URL(path);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("contentType", "GB2312");
                    conn.setConnectTimeout(5 * 1000);
                    conn.setRequestMethod("GET");
                    InputStream inStream = conn.getInputStream();
                    in = new BufferedReader(new InputStreamReader(inStream, "GB2312"));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        buffer.append(line);
                    }
                    String response = buffer.toString();

//                    Log.i(TAG, "connection请求耗时: " + (System.currentTimeMillis() - time));
//                    time = System.currentTimeMillis();

                    for (int i = 0; i < mItem; i++) {
                        if (!response.contains("息\"")) {
                            Log.i(TAG, "blankMessage: ");
                            break;
                        }

                        response = response.substring(response.indexOf("135"));
                        mDate[i] = response.substring(5, response.indexOf("</"))
                                .replace("年", "-")
                                .replace("月", "-")
                                .replace("日&nbsp;", " ");
                        mDate[i] = TimeUtil.dateFormat(mDate[i]);

                        response = response.substring(response.indexOf("/P"));
                        mPlayerId[i] = response.substring(20, response.indexOf("')"));

                        response = response.substring(response.indexOf("息\""));
                        mName[i] = response.substring(15, response.indexOf("<"));

                        response = response.substring(response.indexOf("r\""));
                        mSex[i] = response.substring(3, response.indexOf("<"));

                        response = response.substring(response.indexOf("将"));
                        mLevel[i] = response.substring(1, response.indexOf("记"));

                        response = response.substring(response.indexOf("/V"));
                        mDown[i] = Constant.SAOLEI_NET + response.substring(0, response.indexOf("'"));

                        mVideoId[i] = response.substring(19, response.indexOf("')"));

                        response = response.substring(response.indexOf(">"));
                        mRecord[i] = response.substring(1, response.indexOf("<"));

                        response = response.substring(response.indexOf("↑"));
                        mPromote[i] = response.substring(0, response.indexOf("<"));

                        response = response.substring(response.indexOf(">"));
                        if ("s".equals(mAchieve[i] = response.substring(3, 4))) {
                            response = response.substring(response.indexOf("Si"));
                            mAchieve[i] = response.substring(5, response.indexOf("!") + 1);
                        } else {
                            mAchieve[i] = "";
                        }
//
//                        Log.i(TAG, "mName: "+mName[i]+" mSex: "+mSex[i]+" mLevel: "+mLevel[i]
//                                +" mDown: "+mDown[i]);
//                        Log.i(TAG, "mRecord: "+mRecord[i]+" mPromote: "+mPromote[i]+" mAchieve: "+mAchieve[i]);
                    }

//                    Log.i(TAG, "connection计算耗时: " + (System.currentTimeMillis() - time));
//                    time = System.currentTimeMillis();

                    Message msg = new Message();
                    msg.what = 200;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = -1;
                    handler.sendMessage(msg);
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        };
        mThread.start();
    }

    public static boolean getNewsThreadAlive() {
        if (mThread != null) {
            return mThread.isAlive();
        }
        return true;
    }

    //将获取的网页内容显示到当前Fragment中
    private void setVideoItem() {
        //定义泛型
        mData = new ArrayList<>();
        for (int i = 0; i < mItem; i++) {
            Map<String, String> keyValuePair = new HashMap<>();
            keyValuePair.put("Date", mDate[i]);
            keyValuePair.put("Name", mName[i]);
            keyValuePair.put("Sex", mSex[i]);
            keyValuePair.put("Level", mLevel[i]);
            keyValuePair.put("Record", mRecord[i]);
            keyValuePair.put("Promote", mPromote[i]);
            keyValuePair.put("Achieve", mAchieve[i]);
            keyValuePair.put("Down", mDown[i]);
            keyValuePair.put("PlayerId", mPlayerId[i]);
            keyValuePair.put("VideoId", mVideoId[i]);
            mData.add(keyValuePair);
        }

        //隐藏进度条，显示主界面
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);

        //设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //设置适配器
        mRecyclerView.setAdapter(new NewsAdapter(mActivity, mData));

        //完成刷新
        refreshLayout.finishRefresh();

        //重新开放下拉刷新操作
        refreshLayout.setEnableRefresh(true);

        Message messageRefresh = handlerRefresh.obtainMessage();
        messageRefresh.obj = "Refreshed" + mPage;
        handlerRefresh.sendMessage(messageRefresh);
    }
}
