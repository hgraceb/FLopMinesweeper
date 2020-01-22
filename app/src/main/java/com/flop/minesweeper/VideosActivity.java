package com.flop.minesweeper;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.flop.minesweeper.Adapter.OrderMenuAdapter;
import com.flop.minesweeper.Adapter.OrderMenuRankingAdapter;
import com.flop.minesweeper.Adapter.OrderOptionAdapter;
import com.flop.minesweeper.Fragment.LatestFragment;
import com.flop.minesweeper.Fragment.NewsFragment;
import com.flop.minesweeper.Fragment.RankingFragment;
import com.flop.minesweeper.Util.KeyboardHeightObserver;
import com.flop.minesweeper.Util.KeyboardHeightProvider;
import com.flop.minesweeper.Util.SDCardUtil;
import com.flop.minesweeper.Util.ToastUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

import static com.flop.minesweeper.Constant.ALL_ITEM;
import static com.flop.minesweeper.Constant.ALL_PAGE;
import static com.flop.minesweeper.Constant.DOMAIN_ITEM;
import static com.flop.minesweeper.Constant.DOMAIN_PAGE;
import static com.flop.minesweeper.Constant.LATEST_ITEM;
import static com.flop.minesweeper.Constant.LATEST_PAGE;
import static com.flop.minesweeper.Constant.NEWS_ITEM;
import static com.flop.minesweeper.Constant.NEWS_PAGE;
import static com.flop.minesweeper.Constant.PAGE_MAX;
import static com.flop.minesweeper.Constant.PAGE_MIN;
import static com.flop.minesweeper.Constant.PERMISSIONS_STORAGE;
import static com.flop.minesweeper.Constant.PROGRESS_ITEM;
import static com.flop.minesweeper.Constant.PROGRESS_PAGE;
import static com.flop.minesweeper.Constant.RANKING_ITEM;
import static com.flop.minesweeper.Constant.RANKING_PAGE;
import static com.flop.minesweeper.Constant.REQUEST_EXTERNAL_STORAGE_CODE;
import static com.flop.minesweeper.Constant.STORAGE_PERMISSION_REQUEST_CODE;
import static com.flop.minesweeper.Constant.VIDEO_REQUEST_CODE_LOCAL;
import static com.flop.minesweeper.Constant.orderAnimatorSet;
import static com.flop.minesweeper.Constant.orderMenuLevel;
import static com.flop.minesweeper.Constant.orderMenuRanking;
import static com.flop.minesweeper.Constant.orderMenuWorld;
import static com.flop.minesweeper.Constant.orderOptionFirst;
import static com.flop.minesweeper.Constant.orderRankingFirst;
import static com.flop.minesweeper.Constant.playerId;
import static com.flop.minesweeper.Util.EdgeUtil.setPaddingBottom;
import static com.flop.minesweeper.Util.SDCardUtil.loadFileFromSDCard;

/**
 * 主界面
 */
public class VideosActivity extends AppCompatActivity implements KeyboardHeightObserver, NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "FLOP";

    private NewsFragment newsFragment;//雷界快讯
    private NewsFragment progressFragment;//进步历程
    private LatestFragment latestFragment;//最新录像
    private RankingFragment rankingFragment;//排行榜
    private LatestFragment allFragment;//全部录像
    private LatestFragment domainFragment;//我的地盘

    private Activity mActivity = this;

    private Context mContext = this;

    //静态变量，保证handler内有效修改变量
    public static boolean refreshingNews = true;//雷界快讯页面正在刷新
    public static boolean refreshingProgress = true;//进步历程页面正在刷新
    public static boolean refreshingLatest = true;//最新录像页面正在刷新
    public static boolean refreshingRanking = true;//排行榜页面正在刷新
    public static boolean refreshingAll = true;//全部录像页面正在刷新
    public static boolean refreshingDomain = true;//我的地盘录像页面正在刷新

    //用户按返回键退出程序再重新进入时会触发软键盘监听事件，初始值0判断此种情况下不进行页面跳转
    private String videoPage = "0";

    // 软键盘监听
    private KeyboardHeightProvider keyboardHeightProvider;

    @BindView(R.id.drawer_layout) DrawerLayout drawer;//抽屉根布局
    @BindView(R.id.toolbar) Toolbar toolbar;//顶部导航栏

    @BindView(R.id.rlVideosBottom) RelativeLayout rlVideosBottom;//底部导航栏根布局
    @BindView(R.id.lvVideosBottom) LinearLayout lvVideosBottom;//底部导航容器
    @BindView(R.id.btnLastPage) Button btnLastPage;//上一页
    @BindView(R.id.btnNextPage) Button btnNextPage;//下一页
    @BindView(R.id.etPage) EditText etPage;//页码输入框
    @BindView(R.id.btnFrontCursor) Button btnFrontCursor;//点击时光标移动到最前面
    @BindView(R.id.btnBehindCursor) Button btnBehindCursor;//点击时光标移动到最后面

    @BindView(R.id.rlOrder) RelativeLayout rlOrder;//筛选和排序根布局
    @BindView(R.id.flOrder) FrameLayout flOrder;//筛选和排序菜单项根布局
    @BindView(R.id.lyOrder) LinearLayout lyOrder;//排序菜单
    @BindView(R.id.maskOrder) View maskOrder;//半透明遮罩，点击收回菜单
    @BindView(R.id.ivOrderIndicate) ImageView ivOrderIndicate;

    @BindView(R.id.rvOrderOption) RecyclerView rvOrderOption;//排序选项，点击进行排序
    @BindView(R.id.rvOrderMenu) RecyclerView rvOrderMenu;//排序菜单，点击进行选择
    @BindView(R.id.tvOrder) TextView tvOrder;//顶部信息栏显示当前排序依据

    //定义Handler句柄,接收页面刷新信息
    public Handler handlerVideos = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String action = (String) msg.obj;//当前进行的操作
            switch (action) {
                case "RefreshedNewsFragment":
                    refreshingNews = false;
                    break;
                case "RefreshedProgressFragment":
                    refreshingProgress = false;
                    break;
                case "RefreshedLatestFragment":
                    refreshingLatest = false;
                    break;
                case "RefreshedRankingFragment":
                    refreshingRanking = false;
                    break;
                case "RefreshedAllFragment":
                    refreshingAll = false;
                    break;
                case "RefreshedDomainFragment":
                    refreshingDomain = false;
                    break;

                case "RefreshingNewsFragment":
                    refreshingNews = true;
                    break;
                case "RefreshingProgressFragment":
                    refreshingProgress = true;
                    break;
                case "RefreshingLatestFragment":
                    refreshingLatest = true;
                    break;
                case "RefreshingRankingFragment":
                    refreshingRanking = true;
                    break;
                case "RefreshingAllFragment":
                    refreshingAll = true;
                    break;
                case "RefreshingDomainFragment":
                    refreshingDomain = true;
                    break;
            }
            return false;
        }
    });

    //排序菜单是否显示
    public static boolean menuDrop = false;

    //获取用户用软键盘键入的页面数
    @OnTextChanged(value = R.id.etPage, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable s) {
        //将输入框中的内容转换为字符串并去掉字符串前面多余的0
        videoPage = s.toString().replaceAll("^(0+)", "");
    }

    /**
     * 判断软键盘弹出与隐藏，进而判断是否已经完成页面的输入
     */
    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        String or = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";
        // 键盘高度大于0则认为软键盘弹出
        if (height > 0) {
            // 如果是点击底部页面输入框弹出的软键盘
            if (etPage.isFocused()) {
                // 设置光标选择按钮可点击
                btnFrontCursor.setClickable(true);
                btnBehindCursor.setClickable(true);

                // 设置页码输入框样式
                etPage.setCursorVisible(true);
                etPage.setBackgroundResource(R.color.editView);

                // 设置和键盘高度相同的底边距
                setPaddingBottom(height, rlVideosBottom, mViewPager);
            }
        } else {
            // 设置光标选择按钮不可点击
            btnFrontCursor.setClickable(false);
            btnBehindCursor.setClickable(false);

            // 还原页码输入框
            etPage.setCursorVisible(false);
            etPage.setBackgroundResource(android.R.color.transparent);

            // 还原视图的底边距
            setPaddingBottom(0, rlVideosBottom, mViewPager);

            // 判断是否是合法的页码
            if (!("").equals(videoPage) && !("0").equals(videoPage)) {
                skipEditPage();
            } else {
                resetPage();
            }

            // 如果rlOrder可见，则说明已经打开过排序筛选菜单，需要重新设置各元素位置
            if (rlOrder.getVisibility() == View.VISIBLE) {
                // 重置指示方块的位置，防止屏幕旋转导致指示方块位置错误
                // onConfigurationChanged方法内获取的宽度是旋转之前的，无法进行计算
                // 排行榜页面
                if (mViewPager.getCurrentItem() == 2) {
                    OrderMenuRankingAdapter orderMenuRankingAdapter = (OrderMenuRankingAdapter) rvOrderMenu.getAdapter();
                    if (orderMenuRankingAdapter != null) {
                        orderMenuRankingAdapter.moveIndicate(-1);
                    }
                    // 全部录像页面和我的地盘页面
                } else if (mViewPager.getCurrentItem() == 3 || mViewPager.getCurrentItem() == 4) {
                    final OrderMenuAdapter orderMenuAdapter = (OrderMenuAdapter) rvOrderMenu.getAdapter();
                    if (orderMenuAdapter != null) {
                        orderMenuAdapter.moveIndicate(-1);
                    }
                }
                // 如果没有显示排序筛选菜单，横屏时收回菜单后切换为竖屏可能导致部分重新显示，如全部录像页面的BV筛选
                // 如果有显示排序筛选菜单则不用进行任何处理，界面会自动适应
                if (!menuDrop) {
                    // 取消收回菜单的动画
                    if (orderAnimatorSet != null) orderAnimatorSet.cancel();
                    lyOrder.setTranslationY(-lyOrder.getHeight());
                }
            }
        }
    }

    //重置当前页面数,需要在AdapterOrderOption中二次声明
    private void resetPage() {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                videoPage = NEWS_PAGE + "";
                break;
            case 1:
                videoPage = LATEST_PAGE + "";
                break;
            case 2:
                videoPage = RANKING_PAGE + "";
                break;
            case 3:
                videoPage = ALL_PAGE + "";
                break;
            case 4:
                videoPage = DOMAIN_PAGE + "";
                break;
            case 5:
                videoPage = PROGRESS_PAGE + "";
                break;
            default:
                videoPage = "1";
                break;
        }
        etPage.setText(videoPage);
    }

    //绑定按钮单击事件
    @OnClick({R.id.btnLastPage, R.id.btnNextPage, R.id.btnFrontCursor, R.id.btnBehindCursor, R.id.ivOrder})
    public void bindViewOnClick(View v) {
        switch (v.getId()) {
            case R.id.btnLastPage:
                skipLastPage();
                break;
            case R.id.btnNextPage:
                skipNextPage();
                break;
            case R.id.btnFrontCursor:
                etPage.requestFocus();
                etPage.setSelection(0);
                break;
            case R.id.btnBehindCursor:
                etPage.requestFocus();
                etPage.setSelection(etPage.length());
                break;
            case R.id.ivOrder:
                // 收起软键盘
                closeKeyBoard();
                toggleOrderVideosMenu();
                break;
        }
    }

    //点击遮罩收回菜单
    @OnTouch(R.id.maskOrder)
    public boolean maskOrderTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            closeOrderMenu();
        }
        return false;
    }

    //关闭排序菜单,需要在OrderOptionAdapter中二次声明
    public void closeOrderMenu() {
        // 如果排序菜单已经关闭
        if (!menuDrop) return;
        //取消上一个动画
        if (orderAnimatorSet != null) orderAnimatorSet.cancel();

        menuDrop = false;
        //取消遮罩点击事件监听
        maskOrder.setClickable(false);

        //位移高度
        float height = lyOrder.getHeight();
        //设定时间
        int duration = 200;
        //下拉动画
        ObjectAnimator animLyY;
        //透明度动画
        ObjectAnimator animMaskAlpha;
        //动画集合
        orderAnimatorSet = new AnimatorSet();

        animLyY = ObjectAnimator.ofFloat(lyOrder, "translationY", lyOrder.getTranslationY(), -height);
        animMaskAlpha = ObjectAnimator.ofFloat(maskOrder, "Alpha", maskOrder.getAlpha(), 0f);

        orderAnimatorSet.playTogether(animLyY, animMaskAlpha);
        orderAnimatorSet.setDuration(duration);
        orderAnimatorSet.start();
    }

    //打开排序菜单
    private void dropOrderMenu() {
        // 如果排序菜单已经打开
        if (menuDrop) return;
        //取消上一个动画，避免设定时间duration差异较大时发生错误
        if (orderAnimatorSet != null) orderAnimatorSet.cancel();

        menuDrop = true;
        //遮罩拦截点击事件
        maskOrder.setClickable(true);

        //位移高度
        float height = lyOrder.getHeight();
        //设定时间
        int duration = 250;
        //下拉动画
        ObjectAnimator animLyY;
        //透明度动画
        ObjectAnimator animMaskAlpha;
        //动画集合
        orderAnimatorSet = new AnimatorSet();

        if (rlOrder.getVisibility() == View.VISIBLE) {
            if (lyOrder.getVisibility() == View.INVISIBLE) {
                //重新显示排序菜单
                lyOrder.setVisibility(View.VISIBLE);
                animLyY = ObjectAnimator.ofFloat(lyOrder, "translationY", -height, 0);
                animMaskAlpha = ObjectAnimator.ofFloat(maskOrder, "Alpha", maskOrder.getAlpha(), 1f);
            } else {
                animLyY = ObjectAnimator.ofFloat(lyOrder, "translationY", lyOrder.getTranslationY(), 0);
                animMaskAlpha = ObjectAnimator.ofFloat(maskOrder, "Alpha", maskOrder.getAlpha(), 1f);
            }
        } else {
            //首次进入程序时不显示排序菜单,首次打开排序菜单时才显示
            rlOrder.setVisibility(View.VISIBLE);
            lyOrder.setVisibility(View.VISIBLE);
            animLyY = ObjectAnimator.ofFloat(lyOrder, "translationY", -height, 0);
            animMaskAlpha = ObjectAnimator.ofFloat(maskOrder, "Alpha", 0f, 1f);
        }

        orderAnimatorSet.playTogether(animLyY, animMaskAlpha);
        orderAnimatorSet.setDuration(duration);
        orderAnimatorSet.start();
    }

    // 切换排序信息框的显示和隐藏
    private void toggleOrderVideosMenu() {
        if (menuDrop) {
            closeOrderMenu();
        } else {
            dropOrderMenu();
        }
    }

    //输入完成后跳转到指定的页面
    private void skipEditPage() {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                if (refreshingNews) {
                    //当用户输入指定页面，而当前页面还未加载完毕时不重新跳转页面
                    resetPage();
                    return;
                } else if (!(NEWS_PAGE == Integer.parseInt(videoPage))) {
                    NEWS_PAGE = Integer.parseInt(videoPage);
                    newsFragment.initVideos();
                }
                break;
            case 1:
                if (refreshingLatest) {
                    //当用户输入指定页面，而当前页面还未加载完毕时不重新跳转页面
                    resetPage();
                    return;
                } else if (!(LATEST_PAGE == Integer.parseInt(videoPage))) {
                    LATEST_PAGE = Integer.parseInt(videoPage);
                    latestFragment.initVideos();
                }
                break;
            case 2:
                if (refreshingRanking) {
                    //当用户输入指定页面，而当前页面还未加载完毕时不重新跳转页面
                    resetPage();
                    return;
                } else if (!(RANKING_PAGE == Integer.parseInt(videoPage))) {
                    RANKING_PAGE = Integer.parseInt(videoPage);
                    rankingFragment.initVideos();
                }
                break;
            case 3:
                if (refreshingAll) {
                    //当用户输入指定页面，而当前页面还未加载完毕时不重新跳转页面
                    resetPage();
                    return;
                } else if (!(ALL_PAGE == Integer.parseInt(videoPage))) {
                    ALL_PAGE = Integer.parseInt(videoPage);
                    allFragment.initVideos();
                }
                break;
            case 4:
                if (refreshingDomain) {
                    //当用户输入指定页面，而当前页面还未加载完毕时不重新跳转页面
                    resetPage();
                    return;
                } else if (!(DOMAIN_PAGE == Integer.parseInt(videoPage))) {
                    DOMAIN_PAGE = Integer.parseInt(videoPage);
                    domainFragment.initVideos();
                }
                break;
            case 5:
                if (refreshingProgress) {
                    //当用户输入指定页面，而当前页面还未加载完毕时不重新跳转页面
                    resetPage();
                    return;
                } else if (!(PROGRESS_PAGE == Integer.parseInt(videoPage))) {
                    PROGRESS_PAGE = Integer.parseInt(videoPage);
                    progressFragment.initVideos();
                }
                break;
        }
        etPage.setText(videoPage);
    }

    //下一页面
    private void skipNextPage() {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                if (NEWS_PAGE < PAGE_MAX && !refreshingNews) {
                    NEWS_PAGE++;
                    newsFragment.initVideos();
                }
                break;
            case 1:
                if (LATEST_PAGE < PAGE_MAX && !refreshingLatest) {
                    LATEST_PAGE++;
                    latestFragment.initVideos();
                }
                break;
            case 2:
                if (RANKING_PAGE < PAGE_MAX && !refreshingRanking) {
                    RANKING_PAGE++;
                    rankingFragment.initVideos();
                }
                break;
            case 3:
                if (ALL_PAGE < PAGE_MAX && !refreshingAll) {
                    ALL_PAGE++;
                    allFragment.initVideos();
                }
                break;
            case 4:
                if (DOMAIN_PAGE < PAGE_MAX && !refreshingDomain) {
                    DOMAIN_PAGE++;
                    domainFragment.initVideos();
                }
                break;
            case 5:
                if (PROGRESS_PAGE < PAGE_MAX && !refreshingProgress) {
                    PROGRESS_PAGE++;
                    progressFragment.initVideos();
                }
                break;
        }
        resetPage();
    }

    //上一页面
    private void skipLastPage() {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                if (NEWS_PAGE > PAGE_MIN && !refreshingNews) {
                    NEWS_PAGE--;
                    newsFragment.initVideos();
                }
                break;
            case 1:
                if (LATEST_PAGE > PAGE_MIN && !refreshingLatest) {
                    LATEST_PAGE--;
                    latestFragment.initVideos();
                }
                break;
            case 2:
                if (RANKING_PAGE > PAGE_MIN && !refreshingRanking) {
                    RANKING_PAGE--;
                    rankingFragment.initVideos();
                }
                break;
            case 3:
                if (ALL_PAGE > PAGE_MIN && !refreshingAll) {
                    ALL_PAGE--;
                    allFragment.initVideos();
                }
                break;
            case 4:
                if (DOMAIN_PAGE > PAGE_MIN && !refreshingDomain) {
                    DOMAIN_PAGE--;
                    domainFragment.initVideos();
                }
                break;
            case 5:
                if (PROGRESS_PAGE > PAGE_MIN && !refreshingProgress) {
                    PROGRESS_PAGE--;
                    progressFragment.initVideos();
                }
                break;
        }
        resetPage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 取消软键盘监听
        keyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 设置软键盘监听
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    //退出程序
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭软键盘监听
        keyboardHeightProvider.close();
    }

    //声明适配器
    private SectionsPagerAdapter mSectionsPagerAdapter;

    //声明ViewPager
    private ViewPager mViewPager;

    //初始化界面数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        //每次加载主界面时检测应用是否有相应的权限
        int permissionWrite = ActivityCompat.checkSelfPermission(this,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        int permissionRead = ActivityCompat.checkSelfPermission(this,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permissionWrite != PackageManager.PERMISSION_GRANTED
                || permissionRead != PackageManager.PERMISSION_GRANTED) {
            // 没有读写的权限，去申请读写的权限，会弹出对话框
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE_CODE);
        }

        //检测日志文件是否超过10M
        String logPath = "FlopMine/ErrorLog.txt";
        if (SDCardUtil.getFileSize(logPath) > 10 * 1024 * 1024) {
            SDCardUtil.removeFileFromSDCard(logPath);
            ToastUtil.showShort(this, "日志文件超过10M已删除");
        }

        ButterKnife.bind(this);

        // 设置顶部导航栏
        setSupportActionBar(toolbar);

        //侧边栏
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                // 打开侧边栏时自动关闭排序菜单
                closeOrderMenu();
                // 打开侧边栏时收起软键盘
                closeKeyBoard();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // 设置键盘监听器
        keyboardHeightProvider = new KeyboardHeightProvider(this);
        drawer.post(new Runnable() {
            @Override
            public void run() {
                // 在onResume方法之后开始监听软键盘
                keyboardHeightProvider.start();
            }
        });

        //侧边栏点击事件监听
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //最多同时显示相邻的其他10(左右各5个)个Fragment界面,防止Fragment被销毁
        //默认只保留相邻的Fragment,销毁其它Fragment
        mViewPager.setOffscreenPageLimit(5);

        final TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageSelected(int position) {
                //切换到不同页面时重置部分显示信息
                super.onPageSelected(position);
                resetPage();
                resetOrderMenu();
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    /**
     * 收起软键盘
     */
    private void closeKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    //重置排序菜单
    private void resetOrderMenu() {
        //隐藏排序菜单，防止重置adapter时高度变化更新不及时导致的排序菜单位置越界，dropOrderMenu()时重新显示
        lyOrder.setVisibility(View.INVISIBLE);
        switch (mViewPager.getCurrentItem()) {
            case 0:
                //隐藏菜单选项
                flOrder.setVisibility(View.GONE);
                //设置排序选项布局管理器
                rvOrderOption.setLayoutManager(new GridLayoutManager(mActivity, 1, RecyclerView.VERTICAL, false));
                //设置排序选项适配器
                rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, orderMenuWorld, newsFragment));
                break;
            case 1:
                //隐藏菜单选项
                flOrder.setVisibility(View.GONE);
                //设置排序选项布局管理器
                rvOrderOption.setLayoutManager(new GridLayoutManager(mActivity, 1, RecyclerView.VERTICAL, false));
                //设置排序选项适配器
                rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, orderMenuWorld, latestFragment));
                break;
            case 2:
                flOrder.setVisibility(View.VISIBLE);
                //设置排序菜单布局管理器
                rvOrderMenu.setLayoutManager(new GridLayoutManager(mActivity, orderMenuRanking.length, RecyclerView.VERTICAL, false));
                //设置排序菜单适配器
                rvOrderMenu.setAdapter(new OrderMenuRankingAdapter(mActivity, rankingFragment));

                //设置排序选项布局管理器
                rvOrderOption.setLayoutManager(new GridLayoutManager(mActivity, 4, RecyclerView.VERTICAL, false));
                //设置排序选项适配器
                rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, orderRankingFirst, rankingFragment));
                break;
            case 3:
                flOrder.setVisibility(View.VISIBLE);
                //设置排序菜单布局管理器
                rvOrderMenu.setLayoutManager(new GridLayoutManager(mActivity, orderMenuLevel.length, RecyclerView.VERTICAL, false));
                //设置排序菜单适配器
                rvOrderMenu.setAdapter(new OrderMenuAdapter(mActivity, allFragment));

                //设置排序选项布局管理器
                rvOrderOption.setLayoutManager(new GridLayoutManager(mActivity, 4, RecyclerView.VERTICAL, false));
                //设置排序选项适配器
                rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, orderOptionFirst, allFragment));
                break;
            case 4:
                if (domainFragment.getPlayerId() != playerId) {
                    domainFragment.setPlayerId(playerId);
                    DOMAIN_PAGE = 1;
                    domainFragment.initVideos();
                    etPage.setText("1");
                }

                flOrder.setVisibility(View.VISIBLE);
                //设置排序菜单布局管理器，我的地盘页面暂不支持指定BV
                rvOrderMenu.setLayoutManager(new GridLayoutManager(mActivity, orderMenuLevel.length - 1, RecyclerView.VERTICAL, false));
                //设置排序菜单适配器
                rvOrderMenu.setAdapter(new OrderMenuAdapter(mActivity, domainFragment));

                //设置排序选项布局管理器
                rvOrderOption.setLayoutManager(new GridLayoutManager(mActivity, 4, RecyclerView.VERTICAL, false));
                //设置排序选项适配器
                rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, orderOptionFirst, domainFragment));
                break;
            case 5:
                if (progressFragment.getPlayerId() != playerId) {
                    progressFragment.setPlayerId(playerId);
                    PROGRESS_PAGE = 1;
                    progressFragment.initVideos();
                    etPage.setText("1");
                }

                //隐藏菜单选项
                flOrder.setVisibility(View.GONE);
                //设置排序选项布局管理器
                rvOrderOption.setLayoutManager(new GridLayoutManager(mActivity, 1, RecyclerView.VERTICAL, false));
                //设置排序选项适配器
                rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, orderOptionFirst, progressFragment));
                break;
            default:
                //重置顶部排序信息显示
                tvOrder.setText("默认排序");
        }
    }

    //用户点击返回键关闭侧边栏
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (menuDrop) {
            closeOrderMenu();
        } else {
            //模拟Home键，避免数据销毁而重新加载
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            //调用父类返回键默认方法会销毁当前页面数据
            //super.onBackPressed();
        }
    }

//    //创建菜单栏
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    //主界面菜单项点击事件
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    //侧边栏点击事件
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_new) {
            startActivity(new Intent(this, NewGameActivity.class));
        } else if (id == R.id.nav_local) {
            chooseLocalVideo();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        //关闭侧边栏
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //打开本地文件管理器并选择相应文件
    private void chooseLocalVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");//类型限制，MimeType不提供.avf或.mvf格式，使用application/*限制简单筛选
//        intent.setType("*/*");//类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, VIDEO_REQUEST_CODE_LOCAL);
    }

    //其他页面返回数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Log.i(TAG, "onActivityResult: data.toString(): " + data.toString());
            Log.i(TAG, "onActivityResult: data.getData()(): " + data.getData());
        }
        if (requestCode == VIDEO_REQUEST_CODE_LOCAL && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String filepath = "";
            String videoType = "";
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                Log.i(TAG, "其他应用的路径:--" + uri.getPath() + "--");
                filepath = uri.getPath();
            } else {
                filepath = getPathByUri4kitkat(this, uri);
                Log.i(TAG, "4.4以后的路径:--" + filepath + "--");
            }

            Log.i(TAG, "文件路径: " + filepath);

            if (filepath == null) {
                ToastUtil.showShort(this, "请选择avf或mvf格式文件!");
                return;
            } else if (filepath.endsWith(".avf")) {
                videoType = "Avf";
            } else if (filepath.endsWith(".mvf")) {
                videoType = "Mvf";
            } else {
                ToastUtil.showShort(this, "请选择avf或mvf格式文件");
                return;
            }

            byte[] byteStream = loadFileFromSDCard(filepath);
            if (byteStream != null) {
                Intent intent = new Intent(this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("request", VIDEO_REQUEST_CODE_LOCAL);
                bundle.putByteArray("byteStream", byteStream);
                bundle.putString("videoType", videoType);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                ToastUtil.showShort(this, "录像信息提取出错");
            }
        } else if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            int permissionWrite = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            int permissionRead = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionWrite != PackageManager.PERMISSION_GRANTED
                    || permissionRead != PackageManager.PERMISSION_GRANTED) {
                // 没有读写的权限，去申请读写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE_CODE);
            }
        }
    }

    // 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
    @SuppressLint("NewApi")
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = uri;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    // 在高于安卓O（8.0）版本时将URI设为ContentUris.withAppendedId
                    // 会导致 Unknown URI 的问题，所以只需要判断一下当前的安卓版本，如果大于 O 则直接使用文件选择器返回的URI即可
                    contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                }
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            // (and
            // general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    //从uri中获取文件的绝对路径
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //检测权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE_CODE) {
            boolean hasAllGranted = true;
            for (int i = 0; i < grantResults.length; ++i) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    hasAllGranted = false;
                    //在用户已经拒绝授权的情况下，如果shouldShowRequestPermissionRationale返回false
                    //则可以推断出用户选择了“不在提示”选项，在这种情况下需要引导用户至设置页手动授权
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        //解释原因，并且引导用户至设置页手动授权
                        new AlertDialog.Builder(this)
                                .setMessage("获取相关权限失败：【储存空间】\n\n" +
                                        "您选择了不再提示授权信息，或者系统默认不再提示。" +
                                        "这将导致部分功能无法正常使用，请到设置页面手动授权。")
                                .setPositiveButton("授权", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //引导用户至设置页手动授权
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, STORAGE_PERMISSION_REQUEST_CODE);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //引导用户手动授权，权限请求失败
                                        Log.i(TAG, "用户点击取消按钮，权限请求失败");
                                        finish();
                                    }
                                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                //引导用户手动授权，权限请求失败
                                Log.i(TAG, "用户关闭对话框，权限请求失败");
                                finish();
                            }
                        }).show();
                    } else {
                        //权限请求失败，但未选中“不再提示”选项
                        Log.i(TAG, "权限请求失败，但未选中“不再提示”选项");
                        finish();
                    }
                    break;
                }
            }
            if (hasAllGranted) {
                //权限请求成功
                Log.i(TAG, "权限请求成功");
//                chooseLocalVideo();
            }
        }
    }

    //默认返回的Fragment类
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_default, container, false);
            TextView textView = rootView.findViewById(R.id.section_label);
            textView.setText(R.string.section_format);
            return rootView;
        }
    }

    //定义适配器类
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //不同Fragment设定不同内容
            switch (position) {
                case 0:
                    newsFragment = NewsFragment.newInstance("NewsFragment", NEWS_ITEM);
                    resetOrderMenu();
                    return newsFragment;
                case 1:
                    latestFragment = LatestFragment.newInstance("LatestFragment", LATEST_ITEM);
                    return latestFragment;
                case 2:
                    rankingFragment = RankingFragment.newInstance("RankingFragment", RANKING_ITEM);
                    return rankingFragment;
                case 3:
                    allFragment = LatestFragment.newInstance("AllFragment", ALL_ITEM);
                    return allFragment;
                case 4:
                    domainFragment = LatestFragment.newInstance("DomainFragment", DOMAIN_ITEM);
                    return domainFragment;
                case 5:
                    progressFragment = NewsFragment.newInstance("ProgressFragment", PROGRESS_ITEM);
                    return progressFragment;
                default:
                    return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            //显示的Fragment总数
            return 6;
        }
    }
}
