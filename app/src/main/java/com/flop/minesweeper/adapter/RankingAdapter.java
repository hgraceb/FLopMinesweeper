package com.flop.minesweeper.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.flop.minesweeper.variable.Constant;
import com.flop.minesweeper.activity.VideoPlayActivity;
import com.flop.minesweeper.R;
import com.flop.minesweeper.util.ToastUtil;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flop.minesweeper.variable.Constant.orderRanking;

/**
 * 排行榜页面Adapter
 * Created by Flop on 2018/11/16.
 */
public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.MyViewHolder> {

    private String TAG = "FLOP";

    private Activity mActivity;
    private List<Map<String, String>> mData;
    private ViewPager mViewPager;
    //默认显示顺序为初级、中级、高级、总计，第一行为当前排序依据，剩下的三项按默认排序显示，并依据相应键值获取
    private String[] optionSum = {
            "总计：", "初级：", "中级：", "高级：",
            "Sum_Time", "Beg_Time", "Int_Time", "Exp_Time",
            " | ", " | ", " | ", " | ",
            "Sum_3BVS", "Beg_3BVS", "Int_3BVS", "Exp_3BVS"};
    private String[] optionBeg = {
            "初级：", "中级：", "高级：", "总计：",
            "Beg_Time", "Int_Time", "Exp_Time", "Sum_Time",
            " | ", " | ", " | ", " | ",
            "Beg_3BVS", "Int_3BVS", "Exp_3BVS", "Sum_3BVS"};
    private String[] optionInt = {
            "中级：", "初级：", "高级：", "总计：",
            "Int_Time", "Beg_Time", "Exp_Time", "Sum_Time",
            " | ", " | ", " | ", " | ",
            "Int_3BVS", "Beg_3BVS", "Exp_3BVS", "Sum_3BVS"};
    private String[] optionExp = {
            "高级：", "初级：", "中级：", "总计：",
            "Exp_Time", "Beg_Time", "Int_Time", "Sum_Time",
            " | ", " | ", " | ", " | ",
            "Exp_3BVS", "Beg_3BVS", "Int_3BVS", "Sum_3BVS"};
    private String[] optionNull = {//无可用信息
            "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", "", "", ""};
    private String[] optionCurrent = {};//当前item内信息排版

    public RankingAdapter(Activity activity, List<Map<String, String>> data) {
        this.mActivity = activity;
        this.mData = data;

        this.mViewPager = activity.findViewById(R.id.frame_layout);

        if (orderRanking.getSort().contains("Sum")) {
            optionCurrent = optionSum;
        } else if (orderRanking.getSort().contains("Beg")) {
            optionCurrent = optionBeg;
        } else if (orderRanking.getSort().contains("Int")) {
            optionCurrent = optionInt;
        } else if (orderRanking.getSort().contains("Exp")) {
            optionCurrent = optionExp;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(
                mActivity).inflate(R.layout.item_ranking, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Map<String, String> keyValuePair = mData.get(position);

        String[] current = this.optionCurrent;

        if (keyValuePair.get("mName") != null) {//判断当前item是否有信息
            //排名和姓名重新显示
            holder.tvName.setClickable(true);
            holder.tvVideoRanking.setClickable(true);

            holder.tvName.setText(keyValuePair.get("mName"));
            holder.tvName.setTextColor(mActivity.getResources().getColorStateList(R.color.text_color_player_man));
            holder.tvVideoRanking.setText(keyValuePair.get("mRanking"));
            holder.tvRankingChange.setText(keyValuePair.get("mRankingChange"));
        } else {
            //防止排名和姓名消耗点击事件影响水波纹效果
            holder.tvName.setClickable(false);
            holder.tvVideoRanking.setClickable(false);

            holder.tvName.setText(null);
            holder.tvVideoRanking.setText(null);
            holder.tvRankingChange.setText(null);

            //清除当前item所有内容
            current = optionNull;
        }

        //显示级别信息
        holder.tvLevelFirst.setText(current[0]);
        holder.tvLevelSecond.setText(current[1]);
        holder.tvLevelThird.setText(current[2]);
        holder.tvLevelFourth.setText(current[3]);

        //显示Time信息
        holder.tvTimeFirst.setText(keyValuePair.get(current[4]));
        holder.tvTimeSecond.setText(keyValuePair.get(current[5]));
        holder.tvTimeThird.setText(keyValuePair.get(current[6]));
        holder.tvTimeFourth.setText(keyValuePair.get(current[7]));

        //显示分隔符
        holder.tvCutFirst.setText(current[8]);
        holder.tvCutSecond.setText(current[9]);
        holder.tvCutThird.setText(current[10]);
        holder.tvCutFourth.setText(current[11]);

        //显示BVS信息
        holder.tvBvsFirst.setText(keyValuePair.get(current[12]));
        holder.tvBvsSecond.setText(keyValuePair.get(current[13]));
        holder.tvBvsThird.setText(keyValuePair.get(current[14]));
        holder.tvBvsFourth.setText(keyValuePair.get(current[15]));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvVideoRanking)//排名
                TextView tvVideoRanking;
        @BindView(R.id.tvVideoName)//姓名
                TextView tvName;
        @BindView(R.id.tvRankingChange)//排名变化
                TextView tvRankingChange;

        @BindView(R.id.tvLevelFirst)//级别1
                TextView tvLevelFirst;
        @BindView(R.id.tvLevelSecond)//级别2
                TextView tvLevelSecond;
        @BindView(R.id.tvLevelThird)//级别3
                TextView tvLevelThird;
        @BindView(R.id.tvLevelFourth)//级别4
                TextView tvLevelFourth;

        @BindView(R.id.tvTimeFirst)//级别1Time
                TextView tvTimeFirst;
        @BindView(R.id.tvTimeSecond)//级别2Time
                TextView tvTimeSecond;
        @BindView(R.id.tvTimeThird)//级别3Time
                TextView tvTimeThird;
        @BindView(R.id.tvTimeFourth)//级别4Time
                TextView tvTimeFourth;

        @BindView(R.id.tvCutFirst)//级别1分隔符
                TextView tvCutFirst;
        @BindView(R.id.tvCutSecond)//级别2分隔符
                TextView tvCutSecond;
        @BindView(R.id.tvCutThird)//级别3分隔符
                TextView tvCutThird;
        @BindView(R.id.tvCutFourth)//级别4分隔符
                TextView tvCutFourth;

        @BindView(R.id.tvBvsFirst)//级别1Bvs
                TextView tvBvsFirst;
        @BindView(R.id.tvBvsSecond)//级别2Bvs
                TextView tvBvsSecond;
        @BindView(R.id.tvBvsThird)//级别3Bvs
                TextView tvBvsThird;
        @BindView(R.id.tvBvsFourth)//级别4Bvs
                TextView tvBvsFourth;

        @OnClick({R.id.lyVideo, R.id.tvVideoName, R.id.tvVideoRanking,
                R.id.tvLevelFirst, R.id.tvLevelSecond, R.id.tvLevelThird, R.id.tvLevelFourth,
                R.id.tvTimeFirst, R.id.tvTimeSecond, R.id.tvTimeThird, R.id.tvTimeFourth,
                R.id.tvBvsFirst, R.id.tvBvsSecond, R.id.tvBvsThird, R.id.tvBvsFourth})
        void bindViewOnClick(View v) {
            switch (v.getId()) {
                case R.id.lyVideo:
                    playVideo(0);//传入0获取空数据
                    break;
                case R.id.tvVideoName:
                    resetPlayer();
                    break;
                case R.id.tvVideoRanking:
                    resetPlayer();
                    break;

                case R.id.tvLevelFirst:
                    break;
                case R.id.tvLevelSecond:
                    break;
                case R.id.tvLevelThird:
                    break;
                case R.id.tvLevelFourth:
                    break;

                case R.id.tvTimeFirst:
                    playVideo(4);
                    break;
                case R.id.tvTimeSecond:
                    playVideo(5);
                    break;
                case R.id.tvTimeThird:
                    playVideo(6);
                    break;
                case R.id.tvTimeFourth:
                    playVideo(7);
                    break;

                case R.id.tvBvsFirst:
                    playVideo(12);
                    break;
                case R.id.tvBvsSecond:
                    playVideo(13);
                    break;
                case R.id.tvBvsThird:
                    playVideo(14);
                    break;
                case R.id.tvBvsFourth:
                    playVideo(15);
                    break;
            }
        }

        void playVideo(int index) {
            Map<String, String> keyValuePair = mData.get(getAdapterPosition());
            Log.i(TAG, "playVideo: " + keyValuePair.get("mName"));
            String url = keyValuePair.get(optionCurrent[index] + "_Down");
            if (url != null) {
                Intent intent = new Intent(mActivity, VideoPlayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("request", Constant.VIDEO_REQUEST_CODE_ONLINE);
                bundle.putString("down", url);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            } else if (keyValuePair.get("mName") == null) {
                //当录像地址为空并且姓名为空时判断当前item为空，否则为总计Time或总计Bvs
                ToastUtil.showShort("此时无声胜有声");
            }
        }

        void resetPlayer() {
            String playerId = mData.get(getAdapterPosition()).get("mPlayerId");

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
