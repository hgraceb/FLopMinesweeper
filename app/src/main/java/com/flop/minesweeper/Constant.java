package com.flop.minesweeper;

import android.animation.AnimatorSet;

import com.flop.minesweeper.Variable.OrderOption;
import com.flop.minesweeper.zhangye.bean.RawVideoBean;
import com.flop.minesweeper.zhangye.bean.VideoDisplayBean;

/**
 * Created by Flop on 2018/10/13.
 */
public class Constant {
    public static final String TAG = "FLOP";//日志TAG

    public static RawVideoBean rawVideo;//录像事件
    public static VideoDisplayBean bean;//录像信息

    public static String SAOLEI_NET = "http://www.saolei.wang";

    //雷界快讯页面数据请求目标网址
    public static String SAOLEI_NEWS = "http://www.saolei.wang/News/Index.asp?Page=";
    public static String[] SAOLEI_NEWS_ORDER = {
            "http://www.saolei.wang/News/Index.asp?Page=",
            "http://www.saolei.wang/News/Hero.asp?Page=",
            "http://www.saolei.wang/News/Man.asp?Page="};

    //最新录像页面数据请求目标网址
    public static String SAOLEI_LATEST = "http://www.saolei.wang/Video/New_Index.asp?Page=";
    public static String[] SAOLEI_LATEST_ORDER = {
            "http://www.saolei.wang/Video/New_Index.asp?Page=",
            "http://www.saolei.wang/Video/New_Hero.asp?Page=",
            "http://www.saolei.wang/Video/New_Man.asp?Page="};

    //全部录像页面数据请求目标网址
    public static String SAOLEI_ALL = "http://www.saolei.wang/Video/Video_All.asp?Page=";

    //全部录像页面数据请求目标网址
    public static String SAOLEI_DOMAIN = "http://www.saolei.wang/Video/My.asp?Id=";

    //各个页面Item数目
    public static int NEWS_ITEM = 12;
    public static int LATEST_ITEM = 22;
    public static int RANKING_ITEM = 20;
    public static int ALL_ITEM = 21;
    public static int DOMAIN_ITEM = 22;
    public static int PROGRESS_ITEM = 12;

    //各个页面当前页码
    public static int NEWS_PAGE = 1;
    public static int LATEST_PAGE = 1;
    public static int RANKING_PAGE = 1;
    public static int ALL_PAGE = 1;
    public static int DOMAIN_PAGE = 1;
    public static int PROGRESS_PAGE = 1;

    // 最小页码
    public static int PAGE_MIN = 1;
    // 最大页码
    public static int PAGE_MAX = 99999;

    //排行榜排序依据
    public static OrderOption orderRanking = new OrderOption("All", "Sum_Time", "");
    //全部录像排序依据
    public static OrderOption orderOption = new OrderOption("All", "Time", "");
    //地盘录像排序依据
    public static OrderOption orderDomain = new OrderOption("All", "Time", "");
    //进步历程录像排序依据
    public static OrderOption orderProgress = new OrderOption("All", "", "");

    //用户ID,用于地盘与进步历程
    public static int playerId =14512;


    //排序菜对应关键字
    public static String[] ORDER_MENU = {"All", "Beg", "Int", "Exp"};
    public static String[] ORDER_SORT = {"Time", "Score", "3BV", "3BVS","Comment"};
    //排序选项(子菜单)
    public static final String orderOptionFirst[] = new String[]{"全部", "初级", "中级", "高级"};
    public static final String orderOptionSecond[] = new String[]{"上传时间", "成绩", "3BV", "3BV/s", "评论", "", "", ""};
    //排序选项(主菜单)
    public static final String orderMenuLevel[] = new String[]{"全部", "上传时间", "BV"};


    //排行榜排序关键字(网址)
    public static String[] ORDER_RANKING_MENU = {"All", "Man", "Hero", "NF","Grow","Area","Click",""};
    public static String[] ORDER_RANKING_SORT = {"Beg_Time", "Beg_3BVS", "Int_Time", "Int_3BVS","Exp_Time","Exp_3BVS","Sum_Time","Sum_3BVS"};
    //排行榜排序选项(子菜单)
    public static final String orderRankingFirst[] = new String[]{"雷界", "人界", "神界", "NF", "进步", "地区", "人气",""};
    public static final String orderRankingSecond[] = new String[]{"初级", "3BV/s", "中级", "3BV/s", "高级", "3BV/s", "总计","3BV/s"};
    //排行榜排序选项(主菜单)
    public static final String orderMenuRanking[] = new String[]{"雷界", "总计"};


    //雷界快讯、最新录像的排序菜单(主菜单)
    public static final String orderMenuWorld[] = new String[]{"全部", "神界", "人界"};


    public static String[] VIDEO_INFO = {"ID", "Time", "3BV", "3BV/s", "Ces", "Clicks", "Corr", "STNB", "Flags",
            "Left", "Right", "Double", "Openings", "Islands", "IOE", "Thrp", "QG", "RQP", "Path", "Date"};

    //弹出对话框请求储存空间权限
    public static final int REQUEST_EXTERNAL_STORAGE_CODE = 10512;

    //用户拒绝请求并选择不再询问，引导用户手动授权储存空间权限
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 10513;

    //应用所需的全部权限请求
    public static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    //RequestCode不能为负值,也不能大于16位bit值65536
    //请求播放在线录像
    public static final int VIDEO_REQUEST_CODE_ONLINE = 15819;
    //请求播放本地录像
    public static final int VIDEO_REQUEST_CODE_LOCAL = 15820;

    public static final int PLAY_RESULT_CODE = 14512;
    public static final int PLAY_REQUEST_CODE = 14512;

    public static final int SETTINGS_RESULT_CODE = 13512;
    public static final int SETTINGS_REQUEST_CODE = 13512;

    //排序菜单下拉和关闭动画，static修饰防止动画之间发生冲突
    public static AnimatorSet orderAnimatorSet;
    public static AnimatorSet indicateAnimatorSet;
}