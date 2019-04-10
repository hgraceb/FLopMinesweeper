package com.flop.minesweeper.VideosFragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flop.minesweeper.R;
import com.flop.minesweeper.Util.ToastUtil;
import com.flop.minesweeper.Variable.OrderOption;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flop.minesweeper.Constant.ORDER_MENU;
import static com.flop.minesweeper.Constant.ORDER_SORT;
import static com.flop.minesweeper.Constant.indicateAnimatorSet;
import static com.flop.minesweeper.Constant.orderOption;
import static com.flop.minesweeper.Constant.orderDomain;
import static com.flop.minesweeper.Constant.orderOptionFirst;
import static com.flop.minesweeper.Constant.orderOptionSecond;
import static com.flop.minesweeper.Util.PixelUtil.dip2px;

/**
 * Created by Flop on 2018/11/25.
 */
public class AdapterOrderMenu extends RecyclerView.Adapter<AdapterOrderMenu.MyViewHolder> {
    private Activity mActivity;
    private OrderOption mOrderOption;
    private RecyclerView rvOrderOption;
    private RecyclerView rvOrderMenu;
    private ViewPager mViewPager;
    private Latest latest;
    private TextView tvOrderTop;
    private ImageView ivOrderIndicate;
    private String TAG="FLOP";
    private boolean moveIndicate;

    public AdapterOrderMenu(Activity activity, Latest latest,boolean moveIndicate) {
        this.mActivity = activity;
        this.rvOrderOption = activity.findViewById(R.id.rvOrderOption);
        this.rvOrderMenu = activity.findViewById(R.id.rvOrderMenu);
        this.mViewPager = activity.findViewById(R.id.container);
        this.latest = latest;
        this.tvOrderTop = mActivity.findViewById(R.id.tvOrder);
        this.ivOrderIndicate=mActivity.findViewById(R.id.ivOrderIndicate);
        this.moveIndicate=moveIndicate;

        if (mViewPager.getCurrentItem() == 3) {
            mOrderOption = orderOption;
        }else if (mViewPager.getCurrentItem() == 4) {
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
//                        tvOrderTop.setText(tvOrderTop.getText()+"  " +orderOptionSecond[i]);
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
                if(moveIndicate)moveIndicate(1);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvOrder)
        TextView tvOrder;

        @OnClick(R.id.tvOrder)
        void chooseMenu() {
            switch (getLayoutPosition()) {
                case 0:
                    moveIndicate(1);
                    rvOrderOption.setAdapter(new AdapterOrderOption(mActivity, orderOptionFirst, latest));
                    break;
                case 1:
                    if (mOrderOption.getMenu().equals(ORDER_MENU[0])) {
                        ToastUtil.showShort(mActivity, "请先选择游戏级别");
                    } else {
                        moveIndicate(3);
                        rvOrderOption.setAdapter(new AdapterOrderOption(mActivity, orderOptionSecond, latest));
                    }
                    break;
                case 2:
                    if (mOrderOption.getMenu().equals(ORDER_MENU[0])) {
                        ToastUtil.showShort(mActivity, "请先选择游戏级别");
                    } else if (mOrderOption.getMenu().equals(ORDER_MENU[1])) {
                        //初级BV
                        String data[] = new String[53];
                        for (int j = 0; j < data.length; j++) {
                            data[j] = j + 2 + "";
                        }
                        moveIndicate(5);
                        rvOrderOption.setAdapter(new AdapterOrderOption(mActivity, data, latest));
                    }else if (mOrderOption.getMenu().equals(ORDER_MENU[2])) {
                        //中级BV
                        String data[] = new String[192];
                        for (int j = 0; j < data.length; j++) {
                            data[j] = j + 25 + "";
                        }
                        moveIndicate(5);
                        rvOrderOption.setAdapter(new AdapterOrderOption(mActivity, data, latest));
                    }else if (mOrderOption.getMenu().equals(ORDER_MENU[3])) {
                        //高级BV
                        String data[] = new String[286];
                        for (int j = 0; j < data.length; j++) {
                            data[j] = j + 96 + "";
                        }
                        moveIndicate(5);
                        rvOrderOption.setAdapter(new AdapterOrderOption(mActivity, data, latest));
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
        moveIndicate=false;
        //取消上一个动画
        if (indicateAnimatorSet != null) indicateAnimatorSet.cancel();

        //位移宽度
        float width = rvOrderMenu.getWidth()/6*parts-dip2px(mActivity,40)/2;
        //设定时间
        int duration = 250;
        //水平动画
        ObjectAnimator animLyX;
        //动画集合
        indicateAnimatorSet = new AnimatorSet();

        animLyX = ObjectAnimator.ofFloat(ivOrderIndicate, "translationX", ivOrderIndicate.getTranslationX(), width);

        indicateAnimatorSet.play(animLyX);
        indicateAnimatorSet.setDuration(duration);
        indicateAnimatorSet.start();
    }
}
