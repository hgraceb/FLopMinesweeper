package com.flop.minesweeper.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.flop.minesweeper.fragment.LatestFragment;
import com.flop.minesweeper.fragment.NewsFragment;
import com.flop.minesweeper.fragment.RankingFragment;
import com.flop.minesweeper.R;
import com.flop.minesweeper.activity.MainActivity;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flop.minesweeper.variable.Constant.ALL_PAGE;
import static com.flop.minesweeper.variable.Constant.DOMAIN_PAGE;
import static com.flop.minesweeper.variable.Constant.LATEST_PAGE;
import static com.flop.minesweeper.variable.Constant.NEWS_PAGE;
import static com.flop.minesweeper.variable.Constant.ORDER_MENU;
import static com.flop.minesweeper.variable.Constant.ORDER_RANKING_MENU;
import static com.flop.minesweeper.variable.Constant.ORDER_RANKING_SORT;
import static com.flop.minesweeper.variable.Constant.ORDER_SORT;
import static com.flop.minesweeper.variable.Constant.PROGRESS_PAGE;
import static com.flop.minesweeper.variable.Constant.RANKING_PAGE;
import static com.flop.minesweeper.variable.Constant.SAOLEI_LATEST;
import static com.flop.minesweeper.variable.Constant.SAOLEI_LATEST_ORDER;
import static com.flop.minesweeper.variable.Constant.SAOLEI_NEWS;
import static com.flop.minesweeper.variable.Constant.SAOLEI_NEWS_ORDER;
import static com.flop.minesweeper.variable.Constant.orderAnimatorSet;
import static com.flop.minesweeper.variable.Constant.orderDomain;
import static com.flop.minesweeper.variable.Constant.orderMenuWorld;
import static com.flop.minesweeper.variable.Constant.orderOption;
import static com.flop.minesweeper.variable.Constant.orderOptionFirst;
import static com.flop.minesweeper.variable.Constant.orderOptionSecond;
import static com.flop.minesweeper.variable.Constant.orderProgress;
import static com.flop.minesweeper.variable.Constant.orderRanking;
import static com.flop.minesweeper.variable.Constant.orderRankingFirst;
import static com.flop.minesweeper.variable.Constant.orderRankingSecond;

/**
 * 排序选项Adapter
 * Created by Flop on 2018/11/25.
 */
public class OrderOptionAdapter extends RecyclerView.Adapter<OrderOptionAdapter.MyViewHolder> {
    private Activity mActivity;
    private String[] mData;
    private View maskOrder;
    private LinearLayout lyOrder;
    private ViewPager mViewPager;
    private NewsFragment newsFragment;
    private LatestFragment latestFragment;
    private RankingFragment rankingFragment;
    private TextView tvOrderTop;
    private EditText etPage;
    private RecyclerView rvOrderOption;
    private RecyclerView rvOrderMenu;
    private FrameLayout flOrder;

    public OrderOptionAdapter(Activity activity, String[] data, NewsFragment newsFragment) {
        this.mActivity = activity;
        this.mData = data;
        this.newsFragment = newsFragment;
        initLayout();
    }

    public OrderOptionAdapter(Activity activity, String[] data, LatestFragment latestFragment) {
        this.mActivity = activity;
        this.mData = data;
        this.latestFragment = latestFragment;
        initLayout();
    }

    public OrderOptionAdapter(Activity activity, String[] data, RankingFragment rankingFragment) {
        this.mActivity = activity;
        this.mData = data;
        this.rankingFragment = rankingFragment;
        initLayout();
    }

    //获取所需控件
    private void initLayout() {
        this.maskOrder = mActivity.findViewById(R.id.maskOrder);
        this.lyOrder = mActivity.findViewById(R.id.lyOrder);
        this.mViewPager = mActivity.findViewById(R.id.container);
        this.tvOrderTop = mActivity.findViewById(R.id.tvOrder);
        this.etPage = mActivity.findViewById(R.id.etPage);
        this.rvOrderOption = mActivity.findViewById(R.id.rvOrderOption);
        this.rvOrderMenu = mActivity.findViewById(R.id.rvOrderMenu);
        this.flOrder = mActivity.findViewById(R.id.flOrder);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(
                mActivity).inflate(R.layout.item_order_option, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvOrder.setText(mData[position]);

        //最后一行选项不足时，其余控件用空字符串填充并置为不可点击
        if (mData[position].equals("")) {
            holder.tvOrder.setClickable(false);
        } else if (mViewPager.getCurrentItem() == 0) {//雷界快讯
            holder.tvOrder.setBackgroundResource(R.drawable.ripple_divider_bottom);
            if (SAOLEI_NEWS.equals(SAOLEI_NEWS_ORDER[position])) {
                //将雷界快讯当选排序依据文字置为粉色
                holder.tvOrder.setTextColor(mActivity.getResources().getColor(R.color.colorAccent));
                //刷新顶部排序信息显示
                tvOrderTop.setText(orderMenuWorld[position]);
            } else {
                //将其他排序选项置为默认灰色，按压时粉色
                //直接设置getColor只会显示selector中最底层的默认颜色
                holder.tvOrder.setTextColor(mActivity.getResources().getColorStateList(R.color.text_color_pink));
            }
        } else if (mViewPager.getCurrentItem() == 1) {//最新录像
            holder.tvOrder.setBackgroundResource(R.drawable.ripple_divider_bottom);
            if (SAOLEI_LATEST.equals(SAOLEI_LATEST_ORDER[position])) {
                holder.tvOrder.setTextColor(mActivity.getResources().getColor(R.color.colorAccent));
                tvOrderTop.setText(orderMenuWorld[position]);
            } else {
                holder.tvOrder.setTextColor(mActivity.getResources().getColorStateList(R.color.text_color_pink));
            }
        } else if (mViewPager.getCurrentItem() == 2) {//排行榜录像
            //圆角边框着重显示当前排序依据
            if (Arrays.equals(mData, orderRankingFirst)) {
                if (orderRanking.getMenu().equals(ORDER_RANKING_MENU[position])) {
                    holder.tvOrder.setBackgroundResource(R.drawable.corner_pink);
                    holder.tvOrder.setTextColor(Color.WHITE);
                }
            } else if (Arrays.equals(mData, orderRankingSecond)) {
                if (orderRanking.getSort().equals(ORDER_RANKING_SORT[position])) {
                    holder.tvOrder.setBackgroundResource(R.drawable.corner_pink);
                    holder.tvOrder.setTextColor(Color.WHITE);
                }
            }
        } else if (mViewPager.getCurrentItem() == 3) {//全部录像
            //圆角边框着重显示当前排序依据
            if (Arrays.equals(mData, orderOptionFirst)) {
                if (orderOption.getMenu().equals(ORDER_MENU[position])) {
                    holder.tvOrder.setBackgroundResource(R.drawable.corner_pink);
                    holder.tvOrder.setTextColor(Color.WHITE);
                }
            } else if (Arrays.equals(mData, orderOptionSecond)) {
                if (orderOption.getSort().equals(ORDER_SORT[position])) {
                    holder.tvOrder.setBackgroundResource(R.drawable.corner_pink);
                    holder.tvOrder.setTextColor(Color.WHITE);
                }
            } else {
                if (orderOption.getBv().equals(mData[position])) {
                    Log.i("FLOP", "getBv: " + orderOption.getBv() + "  mData: " + mData[position] + "  position: " + position);
                    holder.tvOrder.setBackgroundResource(R.drawable.corner_pink);
                    holder.tvOrder.setTextColor(Color.WHITE);
                } else {
                    //recyclerView中的元素是重复使用的，前面设置过圆角边框和字体颜色需要重新设置
                    holder.tvOrder.setBackgroundResource(R.drawable.order_option_background);
                    holder.tvOrder.setTextColor(mActivity.getResources().getColorStateList(R.color.text_color_white));
                }
            }
        } else if (mViewPager.getCurrentItem() == 4) {//我的地盘录像
            //圆角边框着重显示当前排序依据
            if (Arrays.equals(mData, orderOptionFirst)) {
                if (orderDomain.getMenu().equals(ORDER_MENU[position])) {
                    holder.tvOrder.setBackgroundResource(R.drawable.corner_pink);
                    holder.tvOrder.setTextColor(Color.WHITE);
                }
            } else if (Arrays.equals(mData, orderOptionSecond)) {
                if (orderDomain.getSort().equals(ORDER_SORT[position])) {
                    holder.tvOrder.setBackgroundResource(R.drawable.corner_pink);
                    holder.tvOrder.setTextColor(Color.WHITE);
                }
            } else {
                if (orderDomain.getBv().equals(mData[position])) {
                    Log.i("FLOP", "getBv: " + orderDomain.getBv() + "  mData: " + mData[position] + "  position: " + position);
                    holder.tvOrder.setBackgroundResource(R.drawable.corner_pink);
                    holder.tvOrder.setTextColor(Color.WHITE);
                } else {
                    //recyclerView中的元素是重复使用的，前面设置过圆角边框和字体颜色需要重新设置
                    holder.tvOrder.setBackgroundResource(R.drawable.order_option_background);
                    holder.tvOrder.setTextColor(mActivity.getResources().getColorStateList(R.color.text_color_white));
                }
            }
        } else if (mViewPager.getCurrentItem() == 5) {//进步历程
            holder.tvOrder.setBackgroundResource(R.drawable.ripple_divider_bottom);
            if (orderProgress.getMenu().equals(ORDER_MENU[position])) {
                //将雷界快讯当选排序依据文字置为粉色
                holder.tvOrder.setTextColor(mActivity.getResources().getColor(R.color.colorAccent));
                //刷新顶部排序信息显示
                tvOrderTop.setText(orderOptionFirst[position]);
            } else {
                //将其他排序选项置为默认灰色，按压时粉色
                //直接设置getColor只会显示selector中最底层的默认颜色
                holder.tvOrder.setTextColor(mActivity.getResources().getColorStateList(R.color.text_color_pink));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvOrder)
        TextView tvOrder;

        @BindView(R.id.lyOrderBackground)
        LinearLayout lyOrderBackground;

        @OnClick(R.id.tvOrder)
        void chooseMenu() {

            int position = getAdapterPosition();
            if (newsFragment != null) {
                if (mViewPager.getCurrentItem() == 0) {
                    //当前页面为雷界快讯页面
                    tvOrderTop.setText(orderMenuWorld[position]);
                    SAOLEI_NEWS = SAOLEI_NEWS_ORDER[position];
                    // 重置页码
                    NEWS_PAGE = 1;
                } else if (mViewPager.getCurrentItem() == 5) {
                    //当前页面为进步历程页面
                    tvOrderTop.setText(orderOptionFirst[position]);
                    orderProgress.setMenu(ORDER_MENU[position]);
                    // 重置页码
                    PROGRESS_PAGE = 1;
                }
                newsFragment.initVideos();
            } else if (latestFragment != null) {
                if (mViewPager.getCurrentItem() == 1) {
                    //当前页面为最新录像页面
                    tvOrderTop.setText(orderMenuWorld[position]);
                    SAOLEI_LATEST = SAOLEI_LATEST_ORDER[position];
                    // 重置页码
                    LATEST_PAGE = 1;
                } else if (mViewPager.getCurrentItem() == 3) {
                    //当前页面为全部录像页面
                    if (Arrays.equals(mData, orderOptionFirst)) {
                        orderOption.setMenu(ORDER_MENU[position]);

                        //不同级别BV范围不同，重新选择级别时清除BV选定信息
                        orderOption.setBv("");
                    } else if (Arrays.equals(mData, orderOptionSecond)) {
                        orderOption.setSort(ORDER_SORT[position]);
                    } else {
                        String bv = "";
                        if (orderOption.getMenu().equals(ORDER_MENU[1])) {
                            bv = (position + 2) + "";
                        } else if (orderOption.getMenu().equals(ORDER_MENU[2])) {
                            bv = (position + 25) + "";
                        }
                        if (orderOption.getMenu().equals(ORDER_MENU[3])) {
                            bv = (position + 96) + "";
                        }
                        orderOption.setBv(bv);
                    }
                    // 重置页码
                    ALL_PAGE = 1;
                } else if (mViewPager.getCurrentItem() == 4) {
                    //当前页面为全部录像页面
                    if (Arrays.equals(mData, orderOptionFirst)) {
                        orderDomain.setMenu(ORDER_MENU[position]);

                        //不同级别BV范围不同，重新选择级别时清除BV选定信息
                        orderDomain.setBv("");
                    } else if (Arrays.equals(mData, orderOptionSecond)) {
                        orderDomain.setSort(ORDER_SORT[position]);
                    }
                    // 重置页码
                    DOMAIN_PAGE = 1;
                }
                latestFragment.initVideos();
            } else if (rankingFragment != null) {
                if (mViewPager.getCurrentItem() == 2) {
                    //当前页面为排行榜录像页面
                    if (Arrays.equals(mData, orderRankingFirst)) {
                        orderRanking.setMenu(ORDER_RANKING_MENU[position]);
                    } else if (Arrays.equals(mData, orderRankingSecond)) {
                        orderRanking.setSort(ORDER_RANKING_SORT[position]);
                    }
                    // 重置页码
                    RANKING_PAGE = 1;
                }
                rankingFragment.initVideos();
            }
            //重置rvOrderMenu，根据已选项显示标题，此处不能重新实例化Adapter，避免parts变量被重置
            rvOrderMenu.setAdapter(rvOrderMenu.getAdapter());
            //重置当前适配器内容，着重显示已选项
            rvOrderOption.setAdapter(rvOrderOption.getAdapter());
            //重置当前页面数
            resetPage();
            //关闭排序菜单
            closeOrderMenu();
        }

        MyViewHolder(View view) {
            super(view);
            //控件注入
            ButterKnife.bind(this, view);
        }
    }

    //重置当前页面数
    private void resetPage() {
        String videoPage = "1";
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
        }
        etPage.setText(videoPage);
    }

    //关闭排序菜单
    private void closeOrderMenu() {
        //取消上一个动画
        if (orderAnimatorSet != null) orderAnimatorSet.cancel();

        MainActivity.menuDrop = false;
        //取消遮罩点击事件监听
        maskOrder.setClickable(false);

        //位移高度
        float height = lyOrder.getHeight();
        //设定时间，设定较短时间以避免卡顿感，只要我跑的够快，眼睛就追不上我
        int duration = 120;
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
}
