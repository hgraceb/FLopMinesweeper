package com.flop.minesweeper.Adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.flop.minesweeper.R;
import com.flop.minesweeper.Util.ToastUtil;
import com.flop.minesweeper.Variable.OrderOption;
import com.flop.minesweeper.VideosFragment.RankingFragment;

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
    private boolean moveIndicate;
    private int itemCount;

    public OrderMenuRankingAdapter(Activity activity, RankingFragment rankingFragment, boolean moveIndicate) {
        this.mActivity = activity;
        this.rvOrderOption = activity.findViewById(R.id.rvOrderOption);
        this.rvOrderMenu = activity.findViewById(R.id.rvOrderMenu);
        this.mViewPager = activity.findViewById(R.id.container);
        this.rankingFragment = rankingFragment;
        this.tvOrderTop = mActivity.findViewById(R.id.tvOrder);
        this.ivOrderIndicate = mActivity.findViewById(R.id.ivOrderIndicate);
        this.moveIndicate = moveIndicate;
        this.mOrderOption = orderRanking;
        this.itemCount = orderMenuRanking.length;
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
                if (moveIndicate) moveIndicate(position * 2 + 1);// 初始化指示方块位置
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
                    moveIndicate(getLayoutPosition() * 2 + 1);
                    rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, orderRankingFirst, rankingFragment));
                    break;
                case 1:
                    // 如果是进步排行榜页面
                    if ("Grow".equals(orderRanking.getMenu())) {
                        ToastUtil.showShort(mActivity, "进步排行榜页面暂不支持条件排序");
                        return;
                    } else {
                        moveIndicate(getLayoutPosition() * 2 + 1);
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

    //移动排序菜单菜单底部指示图标
    private void moveIndicate(int parts) {
        moveIndicate = false;
        //取消上一个动画
        if (indicateAnimatorSet != null) indicateAnimatorSet.cancel();

        //位移宽度
        float width = rvOrderMenu.getWidth() / (itemCount * 2) * parts - dip2px(mActivity, 40) / 2;
        //设定时间
        long duration = 600;
        //水平动画
        ObjectAnimator animLyX;
        //动画集合
        indicateAnimatorSet = new AnimatorSet();

        animLyX = ObjectAnimator.ofFloat(ivOrderIndicate, "translationX", ivOrderIndicate.getTranslationX(), width);

        indicateAnimatorSet.play(animLyX);
        indicateAnimatorSet.setInterpolator(new OvershootInterpolator(1.2f));
        indicateAnimatorSet.setDuration(duration);
        indicateAnimatorSet.start();
    }
}
