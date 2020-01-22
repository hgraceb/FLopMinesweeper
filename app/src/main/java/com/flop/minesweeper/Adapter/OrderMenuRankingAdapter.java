package com.flop.minesweeper.Adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.flop.minesweeper.Fragment.RankingFragment;
import com.flop.minesweeper.R;
import com.flop.minesweeper.Util.ToastUtil;
import com.flop.minesweeper.Variable.OrderOption;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flop.minesweeper.Constant.ORDER_RANKING_MENU;
import static com.flop.minesweeper.Constant.ORDER_RANKING_SORT;
import static com.flop.minesweeper.Constant.indicateAnimatorSet;
import static com.flop.minesweeper.Constant.orderMenuRanking;
import static com.flop.minesweeper.Constant.orderRanking;
import static com.flop.minesweeper.Constant.orderRankingFirst;
import static com.flop.minesweeper.Constant.orderRankingSecond;
import static com.flop.minesweeper.Util.PixelUtil.dip2px;

/**
 * 排行榜页面排序主菜单Adapter
 * Created by Flop on 2020/1/18.
 */
public class OrderMenuRankingAdapter extends RecyclerView.Adapter<OrderMenuRankingAdapter.MyViewHolder> {
    private Activity mActivity;
    private OrderOption mOrderOption;
    private RecyclerView rvOrderOption;
    private RecyclerView rvOrderMenu;
    private ViewPager mViewPager;
    private RankingFragment rankingFragment;
    private TextView tvOrderTop;
    private ImageView ivOrderIndicate;
    private String TAG = "FLOP";
    private int itemCount;
    private int parts;

    public OrderMenuRankingAdapter(Activity activity, RankingFragment rankingFragment) {
        this.mActivity = activity;
        this.rvOrderOption = activity.findViewById(R.id.rvOrderOption);
        this.rvOrderMenu = activity.findViewById(R.id.rvOrderMenu);
        this.mViewPager = activity.findViewById(R.id.container);
        this.rankingFragment = rankingFragment;
        this.tvOrderTop = mActivity.findViewById(R.id.tvOrder);
        this.ivOrderIndicate = mActivity.findViewById(R.id.ivOrderIndicate);
        this.mOrderOption = orderRanking;
        this.itemCount = orderMenuRanking.length;
        // 初始化指示方块位置
        this.parts = 0;
        rvOrderMenu.post(() -> moveIndicate(parts));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(
                mActivity).inflate(R.layout.item_order_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        switch (position) {
            case 0:
                for (int i = 0; i < ORDER_RANKING_MENU.length; i++) {
                    if (mOrderOption.getMenu().equals(ORDER_RANKING_MENU[i])) {
                        holder.tvOrder.setText(orderRankingFirst[i]);
                        tvOrderTop.setText(orderRankingFirst[i]);
                        break;
                    }
                }
                break;
            case 1:
                for (int i = 0; i < ORDER_RANKING_SORT.length; i++) {
                    if (mOrderOption.getSort().equals(ORDER_RANKING_SORT[i])) {
                        holder.tvOrder.setText(orderRankingSecond[i]);
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvOrder)
        TextView tvOrder;

        @OnClick(R.id.tvOrder)
        void chooseMenu() {
            switch (getLayoutPosition()) {
                case 0:
                    moveIndicate(getLayoutPosition());
                    rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, orderRankingFirst, rankingFragment));
                    break;
                case 1:
                    // 如果是进步排行榜页面
                    if ("Grow".equals(orderRanking.getMenu())) {
                        ToastUtil.showShort(mActivity, "进步排行榜页面暂不支持条件排序");
                        return;
                    } else {
                        moveIndicate(getLayoutPosition());
                        rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, orderRankingSecond, rankingFragment));
                    }
                    break;
            }
        }

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 移动排序菜单菜单底部指示图标
     *
     * @param parts 目标菜单位置，-1则为重置当前位置，如旋转屏幕时需要重置位置
     */
    public void moveIndicate(int parts) {
        // 判断目标位置
        this.parts = parts == -1 ? this.parts : parts * 2 + 1;
        // 取消上一个动画
        if (indicateAnimatorSet != null) indicateAnimatorSet.cancel();
        // 位移宽度
        float width = rvOrderMenu.getWidth() / (itemCount * 2) * this.parts - dip2px(mActivity, 40) / 2;
        // 水平动画
        ObjectAnimator animLyX = ObjectAnimator.ofFloat(ivOrderIndicate, "translationX", ivOrderIndicate.getTranslationX(), width);
        // 动画集合
        indicateAnimatorSet = new AnimatorSet();
        // 添加水平动画到集合中
        indicateAnimatorSet.play(animLyX);
        // 设置插值器动画
        indicateAnimatorSet.setInterpolator(new OvershootInterpolator(1.2f));
        // 设定动画时长
        indicateAnimatorSet.setDuration(parts == -1 ? 0 : 600);
        // 开始动画
        indicateAnimatorSet.start();
    }
}
