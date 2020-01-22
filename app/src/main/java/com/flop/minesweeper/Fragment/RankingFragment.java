package com.flop.minesweeper.Fragment;

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

import com.flop.minesweeper.Adapter.RankingAdapter;
import com.flop.minesweeper.Constant;
import com.flop.minesweeper.R;
import com.flop.minesweeper.Util.LogUtil;
import com.flop.minesweeper.Util.ToastUtil;
import com.flop.minesweeper.VideosActivity;
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

import static com.flop.minesweeper.Constant.RANKING_PAGE;
import static com.flop.minesweeper.Constant.orderRanking;

/**
 * Created by Flop on 2018/10/14.
 */
public class RankingFragment extends Fragment {
    private String TAG = Constant.TAG;

    private String[] mRanking;//排名
    private String[] mName;//姓名
    private String[] mPlayerId;//用户ID
    private String[] mTitle;//称号

    private String[] mBegTimeDown;//初级录像地址
    private String[] mBegTimeId;//初级录像ID
    private String[] mBegBvsDown;//初级录像地址
    private String[] mBegBvsId;//初级录像ID
    private String[] mBegTime;//初级时间
    private String[] mBegBvs;//初级Bvs

    private String[] mIntTimeDown;//中级录像地址
    private String[] mIntTimeId;//中级录像ID
    private String[] mIntBvsDown;//中级录像地址
    private String[] mIntBvsId;//中级录像ID
    private String[] mIntTime;//中级时间
    private String[] mIntBvs;//中级Bvs

    private String[] mExpTimeDown;//高级录像地址
    private String[] mExpTimeId;//高级录像ID
    private String[] mExpBvsDown;//高级录像地址
    private String[] mExpBvsId;//高级录像ID
    private String[] mExpTime;//高级时间
    private String[] mExpBvs;//高级Bvs

    private String[] mSumTime;//总计时间
    private String[] mSumBvs;//总计Bvs

    private String[] mRankingChange;//一个月内的排名变化

    private Thread mThread;

    private int playerId;
    //mData定义为static类型，保证用户按返回键之后onPause时数据不被销毁
    private List<Map<String, String>> mData;

    private Context mContext;
    private Activity mActivity;

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
    public static RankingFragment newInstance(String pageName, int itemCount) {
        RankingFragment newFragment = new RankingFragment();
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

        refreshLayout.setOnRefreshListener(refreshlayout -> {
            if (mThread != null && !mThread.isAlive()) {
                refreshVideos();
            } else {
                refreshlayout.finishRefresh(false);//传入false表示刷新失败
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
        mRanking = new String[mItem];
        mName = new String[mItem];
        mPlayerId = new String[mItem];
        mTitle = new String[mItem];

        mBegTimeDown = new String[mItem];
        mBegTimeId = new String[mItem];
        mBegBvsDown = new String[mItem];
        mBegBvsId = new String[mItem];
        mBegTime = new String[mItem];
        mBegBvs = new String[mItem];

        mIntTimeDown = new String[mItem];
        mIntTimeId = new String[mItem];
        mIntBvsDown = new String[mItem];
        mIntBvsId = new String[mItem];
        mIntTime = new String[mItem];
        mIntBvs = new String[mItem];

        mExpTimeDown = new String[mItem];
        mExpTimeId = new String[mItem];
        mExpBvsDown = new String[mItem];
        mExpBvsId = new String[mItem];
        mExpTime = new String[mItem];
        mExpBvs = new String[mItem];

        mSumTime = new String[mItem];
        mSumBvs = new String[mItem];

        mRankingChange = new String[mItem];
    }

    //刷新当前界面
    public void refreshVideos() {
        LogUtil.i("刷新" + mPage + "页面", new Throwable());
        initFields();
        getVideoItem();
    }

    //定义Handler句柄
    Handler handler = new Handler(new Handler.Callback() {
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
                        case "RankingFragment":
                            path = "http://www.saolei.wang/Ranking/Ranking_"
                                    + orderRanking.getMenu() + ".asp?Page="
                                    + RANKING_PAGE
                                    + "&By=Player_"
                                    + orderRanking.getSort()
                                    + "_Score";
                            // NF排行榜页面还需要添加“_NF”的后缀
                            if ("NF".equals(orderRanking.getMenu())) {
                                path += "_NF";
                            }
                            break;
                        default:
                            //默认请求网址
                            path = "http://www.saolei.wang/Ranking/Ranking_All.asp?Page=1&By=Player_Sum_Time_Score";
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
                        if (!response.contains(";位")) {
                            Log.i(TAG, "blankMessage: ");
                            break;
                        }

                        // 如果是进步页面，则排名升降的数据在最前面
                        if (orderRanking.getMenu().equals("Grow")) {
                            // 如果有排名升降的数据
                            int index = response.indexOf("\"cursor");
                            if (index != -1) {
                                response = response.substring(index);
                                String temp = response.substring(0, response.indexOf("</"));
                                mRankingChange[i] = temp.substring(temp.lastIndexOf(">") + 1);
                            }
                        }

                        response = response.substring(response.indexOf("第&"));
                        mRanking[i] = response.substring(29, response.indexOf("</"));

                        response = response.substring(response.indexOf("/P"));
                        mPlayerId[i] = response.substring(20, response.indexOf("')"));

                        response = response.substring(response.indexOf("\">"));
                        mName[i] = response.substring(2, response.indexOf("</"));

                        response = response.substring(response.indexOf("/Help"));
                        mTitle[i] = response.substring(27, response.indexOf("\" t"));

                        response = response.substring(response.indexOf("/V"));
                        mBegTimeDown[i] = Constant.SAOLEI_NET + response.substring(0, response.indexOf("'"));

                        mBegTimeId[i] = mBegTimeDown[i].substring(40);

                        response = response.substring(response.indexOf("\">"));
                        mBegTime[i] = response.substring(2, response.indexOf("</"));

                        response = response.substring(response.indexOf("/V"));
                        mBegBvsDown[i] = Constant.SAOLEI_NET + response.substring(0, response.indexOf("'"));

                        mBegBvsId[i] = mBegBvsDown[i].substring(40);

                        response = response.substring(response.indexOf("\">"));
                        mBegBvs[i] = response.substring(2, response.indexOf("</"));

                        response = response.substring(response.indexOf("/V"));
                        mIntTimeDown[i] = Constant.SAOLEI_NET + response.substring(0, response.indexOf("'"));

                        mIntTimeId[i] = mIntTimeDown[i].substring(40);

                        response = response.substring(response.indexOf("\">"));
                        mIntTime[i] = response.substring(2, response.indexOf("</"));

                        response = response.substring(response.indexOf("/V"));
                        mIntBvsDown[i] = Constant.SAOLEI_NET + response.substring(0, response.indexOf("'"));

                        mIntBvsId[i] = mIntBvsDown[i].substring(40);

                        response = response.substring(response.indexOf("\">"));
                        mIntBvs[i] = response.substring(2, response.indexOf("</"));

                        response = response.substring(response.indexOf("/V"));
                        mExpTimeDown[i] = Constant.SAOLEI_NET + response.substring(0, response.indexOf("'"));

                        mExpTimeId[i] = mExpTimeDown[i].substring(40);

                        response = response.substring(response.indexOf("\">"));
                        mExpTime[i] = response.substring(2, response.indexOf("</"));

                        response = response.substring(response.indexOf("/V"));
                        mExpBvsDown[i] = Constant.SAOLEI_NET + response.substring(0, response.indexOf("'"));

                        mExpBvsId[i] = mExpBvsDown[i].substring(40);

                        response = response.substring(response.indexOf("\">"));
                        mExpBvs[i] = response.substring(2, response.indexOf("</"));

                        // 连续根据同一字符串进行定位查找时从第二个字符开始查找，避免数据重复
                        response = response.substring(response.indexOf("\">", 1));
                        mSumTime[i] = response.substring(2, response.indexOf("</"));

                        response = response.substring(response.indexOf("\">", 1));
                        mSumBvs[i] = response.substring(2, response.indexOf("</"));

                        // 如果不是进步页面，则排名升降的数据在最后面
                        if (!orderRanking.getMenu().equals("Grow")) {
                            // 如果有排名升降的数据
                            int index = response.indexOf("\"cursor");
                            if (index != -1) {
                                response = response.substring(index);
                                String temp = response.substring(0, response.indexOf("</"));
                                mRankingChange[i] = temp.substring(temp.lastIndexOf(">") + 1);
                            }
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
            keyValuePair.put("mRanking", mRanking[i]);
            keyValuePair.put("mName", mName[i]);
            keyValuePair.put("mPlayerId", mPlayerId[i]);
            keyValuePair.put("mTitle", mTitle[i]);

            keyValuePair.put("Beg_Time_Down", mBegTimeDown[i]);
            keyValuePair.put("Beg_Time_ID", mBegTimeId[i]);
            keyValuePair.put("Beg_3BVS_Down", mBegBvsDown[i]);
            keyValuePair.put("Beg_3BVS_ID", mBegBvsId[i]);
            keyValuePair.put("Beg_Time", mBegTime[i]);
            keyValuePair.put("Beg_3BVS", mBegBvs[i]);

            keyValuePair.put("Int_Time_Down", mIntTimeDown[i]);
            keyValuePair.put("Int_Time_ID", mIntTimeId[i]);
            keyValuePair.put("Int_3BVS_Down", mIntBvsDown[i]);
            keyValuePair.put("Int_3BVS_ID", mIntBvsId[i]);
            keyValuePair.put("Int_Time", mIntTime[i]);
            keyValuePair.put("Int_3BVS", mIntBvs[i]);

            keyValuePair.put("Exp_Time_Down", mExpTimeDown[i]);
            keyValuePair.put("Exp_Time_ID", mExpTimeId[i]);
            keyValuePair.put("Exp_3BVS_Down", mExpBvsDown[i]);
            keyValuePair.put("Exp_3BVS_ID", mExpBvsId[i]);
            keyValuePair.put("Exp_Time", mExpTime[i]);
            keyValuePair.put("Exp_3BVS", mExpBvs[i]);

            keyValuePair.put("Sum_Time", mSumTime[i]);
            keyValuePair.put("Sum_3BVS", mSumBvs[i]);

            keyValuePair.put("mRankingChange", mRankingChange[i]);
            mData.add(keyValuePair);
        }

        //隐藏进度条，显示主界面
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);

        //设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //设置适配器
        mRecyclerView.setAdapter(new RankingAdapter(mActivity, mData));

        //完成刷新
        refreshLayout.finishRefresh();

        //重新开放下拉刷新操作
        refreshLayout.setEnableRefresh(true);

        Message messageRefresh = handlerRefresh.obtainMessage();
        messageRefresh.obj = "Refreshed" + mPage;
        handlerRefresh.sendMessage(messageRefresh);
    }
}
