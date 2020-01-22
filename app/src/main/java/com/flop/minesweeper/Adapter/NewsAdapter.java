package com.flop.minesweeper.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
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
 * 雷界快讯页面Adapter
 * Created by Flop on 2018/11/16.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private Activity mActivity;
    private List<Map<String, String>> mData;
    private ViewPager mViewPager;

    public NewsAdapter(Activity activity, List<Map<String, String>> data) {
        this.mActivity = activity;
        this.mData = data;

        this.mViewPager = activity.findViewById(R.id.container);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(
                mActivity).inflate(R.layout.item_news, parent, false));
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
        holder.tvLevel.setText(keyValuePair.get("Level"));
        holder.tvRecord.setText(keyValuePair.get("Record"));
        holder.tvPromote.setText(keyValuePair.get("Promote"));
        holder.tvAchieve.setText(keyValuePair.get("Achieve"));
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
        @BindView(R.id.tvVideoLevel)//级别
                TextView tvLevel;
        @BindView(R.id.tvVideoRecord)//纪录
                TextView tvRecord;
        @BindView(R.id.tvVideoPromote)//提升
                TextView tvPromote;
        @BindView(R.id.tvVideoAchieve)//成就
                TextView tvAchieve;

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
