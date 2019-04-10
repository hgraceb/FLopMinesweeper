package com.flop.minesweeper.VideosFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.flop.minesweeper.Constant;
import com.flop.minesweeper.R;
import com.flop.minesweeper.Util.LogUtil;
import com.flop.minesweeper.Util.TimeUtil;
import com.flop.minesweeper.Util.ToastUtil;
import com.flop.minesweeper.VideosActivity;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

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

import static com.flop.minesweeper.Constant.ALL_PAGE;
import static com.flop.minesweeper.Constant.DOMAIN_PAGE;
import static com.flop.minesweeper.Constant.LATEST_PAGE;
import static com.flop.minesweeper.Constant.ORDER_MENU;
import static com.flop.minesweeper.Constant.SAOLEI_LATEST;
import static com.flop.minesweeper.Constant.TAG;
import static com.flop.minesweeper.Constant.orderOption;
import static com.flop.minesweeper.Constant.orderDomain;

/**
 * Created by Flop on 2018/10/14.
 */
public class Latest extends Fragment {

    private String mDate[];
    private String mName[];
    private String mSex[];
    private String mBv[];
    private String mBvs[];
    private String mLevel[];
    private String mTime[];
    private String mStyle[];
    private String mDown[];
    private String mPlayerId[];
    private String mVideoId[];
    private Thread mThread;
    private int playerId;
    //mData定义为static类型，保证用户按返回键之后onPause时数据不被销毁
    public List<Map<String, String>> mData;

    Context mContext;
    Activity mActivity;

    @BindView(R.id.rvVideos)
    RecyclerView mRecyclerView;
    @BindView(R.id.pbVideos)
    ProgressBar mProgressBar;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    private Handler handlerRefresh;//刷新管理
    private String mPage;//页面名称
    private int mItem;//Item数目

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    //构造函数传入参数
    public static Latest newInstance(String pageName, int itemCount) {
        Latest newFragment = new Latest();
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

        VideosActivity videosActivity = new VideosActivity();
        handlerRefresh = videosActivity.handlerVideos;

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                if (mThread != null && !mThread.isAlive()) {
                    refreshVideos();
                } else {
                    refreshlayout.finishRefresh(false);//传入false表示刷新失败
                }
            }
        });

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
            refreshVideos();
        }
    }

    //初始化录像数据
    public void initVideos() {
        LogUtil.i("初始化" + mPage + "页面", new Throwable());
        initFields();

        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        refreshLayout.setEnableRefresh(false);

        getVideoItem();
    }

    /*初始化变量*/
    public void initFields() {
        mDate = new String[mItem];
        mName = new String[mItem];
        mSex = new String[mItem];
        mBv = new String[mItem];
        mBvs = new String[mItem];
        mLevel = new String[mItem];
        mTime = new String[mItem];
        mStyle = new String[mItem];
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
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) setVideoItem();
            else if (msg.what == -1) {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                ToastUtil.showShort(mActivity, "网络错误");
                refreshLayout.finishRefresh(false);
                refreshLayout.setEnableRefresh(true);

            }
        }
    };

    //请求雷网录像数据
    public void getVideoItem() {
        mThread = new Thread() {
            @Override
            public void run() {
                super.run();

                //Message已经不是自己创建的了,而是从MessagePool拿的,省去了创建对象申请内存的开销
                Message messageRefresh = handlerRefresh.obtainMessage();
                messageRefresh.obj = "Refreshing" + mPage;
                handlerRefresh.sendMessage(messageRefresh);

                HttpURLConnection conn = null;
                BufferedReader in = null;
                try {
//                    long time = System.currentTimeMillis();
                    String path;
                    switch (mPage) {
                        case "Latest":
                            path = SAOLEI_LATEST + LATEST_PAGE;
                            break;
                        case "All":
                            path = "http://www.saolei.net/Video/Video_"
                                    + orderOption.getMenu()
                                    + ".asp?Page=" + ALL_PAGE
                                    + "&Order=" + orderOption.getSort()
                                    + "&Bv=" + orderOption.getBv();
                            break;
                        case "Domain":
                            if(orderDomain.getMenu().equals(ORDER_MENU[0])){
                                path = "http://www.saolei.net/Video/My.asp?Id=";
                            }else{
                                path = "http://www.saolei.net/Video/My_"+ orderDomain.getMenu()+".asp?Id=";
                            }
                            setPlayerId(Constant.playerId);
                            path += Constant.playerId
                                    + "&Page=" + DOMAIN_PAGE
                                    + "&Order=" + orderDomain.getSort()
                                    + "&Bv=" + orderDomain.getBv();
                            break;
                        default:
                            path = SAOLEI_LATEST + LATEST_PAGE;
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
                        response = response.substring(response.indexOf("4%"));
                        mDate[i] = response.substring(22, response.indexOf("</"))
                                .replace("年", "-")
                                .replace("月", "-")
                                .replace("日&nbsp;", " ");
                        mDate[i] = TimeUtil.dateFormat(mDate[i]);

                        response = response.substring(response.indexOf("/P"));
                        mPlayerId[i]=response.substring(20, response.indexOf("')"));

                        response = response.substring(response.indexOf("息\""));
                        mName[i] = response.substring(3, response.indexOf("<"));

                        response = response.substring(response.indexOf("r\""));
                        mSex[i] = response.substring(3, response.indexOf("<"));

                        response = response.substring(response.indexOf("V_"));
                        mBv[i] = "3BV=" + response.substring(response.indexOf(">") + 1, response.indexOf("<"));

                        response = response.substring(response.indexOf("S_"));
                        mBvs[i] = "3BV/s=" + response.substring(response.indexOf(">") + 1, response.indexOf("<"));

                        response = response.substring(response.indexOf("5%"));
                        mLevel[i] = response.substring(40, 42);
                        mDown[i] = Constant.SAOLEI_NET + response.substring(94, response.indexOf("&"));

                        mVideoId[i]=response.substring(113, response.indexOf("&t"));

                        response = response.substring(response.indexOf("像\""));
                        mTime[i] = response.substring(3, response.indexOf("<"));

                        response = response.substring(response.indexOf("/a"));
                        if (response.substring(4, 5).equals("s")) {
                            mStyle[i] = "NF";
                        } else {
                            mStyle[i] = "FL";
                        }
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

    //将获取的网页内容显示到当前Fragment中
    private void setVideoItem() {
        //定义泛型
        mData = new ArrayList<>();
        for (int i = 0; i < mItem; i++) {
            Map<String, String> keyValuePair = new HashMap<>();
            keyValuePair.put("Date", mDate[i]);
            keyValuePair.put("Name", mName[i]);
            keyValuePair.put("Sex", mSex[i]);
            keyValuePair.put("Bv", mBv[i]);
            keyValuePair.put("Bvs", mBvs[i]);
            keyValuePair.put("Level", mLevel[i]);
            keyValuePair.put("Time", mTime[i]);
            keyValuePair.put("Style", mStyle[i]);
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
        mRecyclerView.setAdapter(new AdapterLatest(mActivity, mData));

        //完成刷新
        refreshLayout.finishRefresh();

        //重新开放下拉刷新操作
        refreshLayout.setEnableRefresh(true);

        Message messageRefresh = handlerRefresh.obtainMessage();
        messageRefresh.obj = "Refreshed" + mPage;
        handlerRefresh.sendMessage(messageRefresh);
    }
}
