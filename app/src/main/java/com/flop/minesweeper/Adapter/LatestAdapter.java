package com.flop.minesweeper.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flop.minesweeper.Constant;
import com.flop.minesweeper.MainActivity;
import com.flop.minesweeper.R;
import com.flop.minesweeper.Util.ToastUtil;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 最新录像页面Adapter
 * Created by Flop on 2018/11/16.
 */
public class LatestAdapter extends RecyclerView.Adapter<LatestAdapter.MyViewHolder> {

    private Activity mActivity;
    private List<Map<String, String>> mData;
    private ViewPager mViewPager;

    public LatestAdapter(Activity activity, List<Map<String, String>> data) {
        this.mActivity = activity;
        this.mData = data;

        this.mViewPager = activity.findViewById(R.id.container);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(
                mActivity).inflate(R.layout.item_videos, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Map<String, String> keyValuePair = mData.get(position);
        holder.tvDate.setText(keyValuePair.get("Date"));
        holder.tvName.setText(keyValuePair.get("Name"));
        if ("GG".equals(keyValuePair.get("Sex"))) {
            holder.tvName.setTextColor(mActivity.getResources().getColorStateList(R.color.text_color_player_man));
        } else {
            holder.tvName.setTextColor(mActivity.getResources().getColorStateList(R.color.text_color_player_woman));
        }
        holder.tvBv.setText(keyValuePair.get("Bv"));
        holder.tvBvs.setText(keyValuePair.get("Bvs"));
        holder.tvLevel.setText(keyValuePair.get("Level"));
        holder.tvTime.setText(keyValuePair.get("Time"));
        holder.tvStyle.setText(keyValuePair.get("Style"));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvVideoDate)//日期
                TextView tvDate;
        @BindView(R.id.tvVideoName)//姓名
                TextView tvName;
        @BindView(R.id.tvVideoBv)//3BV
                TextView tvBv;
        @BindView(R.id.tvVideoBvs)//3BV/s
                TextView tvBvs;
        @BindView(R.id.tvVideoLevel)//级别
                TextView tvLevel;
        @BindView(R.id.tvVideoTime)//用时
                TextView tvTime;
        @BindView(R.id.tvVideoStyle)//风格
                TextView tvStyle;

        @OnClick(R.id.lyVideo)
        void playVideo() {
            String url = mData.get(getAdapterPosition()).get("Down");
            if (url != null) {
                Intent intent = new Intent(mActivity, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("request", Constant.VIDEO_REQUEST_CODE_ONLINE);
                bundle.putString("down", url);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            } else {
                ToastUtil.showShort(mActivity, "此时无声胜有声");
            }
        }

        @OnClick(R.id.tvVideoName)
        void resetPlayer() {
            String playerId = mData.get(getAdapterPosition()).get("PlayerId");
            String videoId = mData.get(getAdapterPosition()).get("VideoId");

            if (playerId != null) {
                Constant.playerId = Integer.parseInt(playerId);
                mViewPager.setCurrentItem(4);
            }
        }

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
