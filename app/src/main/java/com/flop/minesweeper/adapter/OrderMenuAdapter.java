package com.flop.minesweeper.adapter;

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

import com.flop.minesweeper.fragment.LatestFragment;
import com.flop.minesweeper.R;
import com.flop.minesweeper.util.ToastUtil;
import com.flop.minesweeper.variable.OrderOption;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flop.minesweeper.variable.Constant.ORDER_MENU;
import static com.flop.minesweeper.variable.Constant.ORDER_SORT;
import static com.flop.minesweeper.variable.Constant.indicateAnimatorSet;
import static com.flop.minesweeper.variable.Constant.orderDomain;
import static com.flop.minesweeper.variable.Constant.orderMenuLevel;
import static com.flop.minesweeper.variable.Constant.orderOption;
import static com.flop.minesweeper.variable.Constant.orderOptionFirst;
import static com.flop.minesweeper.variable.Constant.orderOptionSecond;
import static com.flop.minesweeper.util.PixelUtil.dip2px;

/**
 * 排序主菜单Adapter
 * Created by Flop on 2018/11/25.
 */
public class OrderMenuAdapter extends RecyclerView.Adapter<OrderMenuAdapter.MyViewHolder> {
    private Activity mActivity;
    private OrderOption mOrderOption;
    private RecyclerView rvOrderOption;
    private RecyclerView rvOrderMenu;
    private ViewPager mViewPager;
    private LatestFragment latestFragment;
    private TextView tvOrderTop;
    private ImageView ivOrderIndicate;
    private String TAG = "FLOP";
    private int itemCount;
    private int parts;

    public OrderMenuAdapter(Activity activity, LatestFragment latestFragment) {
        this.mActivity = activity;
        this.rvOrderOption = activity.findViewById(R.id.rvOrderOption);
        this.rvOrderMenu = activity.findViewById(R.id.rvOrderMenu);
        this.mViewPager = activity.findViewById(R.id.container);
        this.latestFragment = latestFragment;
        this.tvOrderTop = mActivity.findViewById(R.id.tvOrder);
        this.ivOrderIndicate = mActivity.findViewById(R.id.ivOrderIndicate);
        // 初始化指示方块位置
        this.parts = 0;
        rvOrderMenu.post(() -> moveIndicate(parts));

        if (mViewPager.getCurrentItem() == 3) {
            mOrderOption = orderOption;
            itemCount = orderMenuLevel.length;
        } else if (mViewPager.getCurrentItem() == 4) {
            itemCount = orderMenuLevel.length - 1;
            mOrderOption = orderDomain;
        }
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
                for (int i = 0; i < ORDER_MENU.length; i++) {
                    if (mOrderOption.getMenu().equals(ORDER_MENU[i])) {
                        holder.tvOrder.setText(orderOptionFirst[i]);
                        tvOrderTop.setText(orderOptionFirst[i]);
                        break;
                    }
                }
                break;
            case 1:
                for (int i = 0; i < ORDER_SORT.length; i++) {
                    if (mOrderOption.getSort().equals(ORDER_SORT[i])) {
                        holder.tvOrder.setText(orderOptionSecond[i]);
                        break;
                    }
                }
                break;
            case 2:
                String bv = "BV";
                if ("".equals(mOrderOption.getBv())) {
                    holder.tvOrder.setText(bv);
                } else {
                    bv += "=" + mOrderOption.getBv();
                    holder.tvOrder.setText(bv);
                    tvOrderTop.setText(tvOrderTop.getText() + "  " + bv);
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
                    rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, orderOptionFirst, latestFragment));
                    break;
                case 1:
                    if (mOrderOption.getMenu().equals(ORDER_MENU[0])) {
                        ToastUtil.showShort(mActivity, "请先选择游戏级别");
                    } else {
                        moveIndicate(getLayoutPosition());
                        rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, orderOptionSecond, latestFragment));
                    }
                    break;
                case 2:
                    if (mOrderOption.getMenu().equals(ORDER_MENU[0])) {
                        ToastUtil.showShort(mActivity, "请先选择游戏级别");
                    } else if (mOrderOption.getMenu().equals(ORDER_MENU[1])) {
                        //初级BV
                        String[] data = new String[53];
                        for (int j = 0; j < data.length; j++) {
                            data[j] = j + 2 + "";
                        }
                        moveIndicate(getLayoutPosition());
                        rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, data, latestFragment));
                    } else if (mOrderOption.getMenu().equals(ORDER_MENU[2])) {
                        //中级BV
                        String[] data = new String[192];
                        for (int j = 0; j < data.length; j++) {
                            data[j] = j + 25 + "";
                        }
                        moveIndicate(getLayoutPosition());
                        rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, data, latestFragment));
                    } else if (mOrderOption.getMenu().equals(ORDER_MENU[3])) {
                        //高级BV
                        String[] data = new String[286];
                        for (int j = 0; j < data.length; j++) {
                            data[j] = j + 96 + "";
                        }
                        moveIndicate(getLayoutPosition());
                        rvOrderOption.setAdapter(new OrderOptionAdapter(mActivity, data, latestFragment));
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
