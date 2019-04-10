package com.flop.minesweeper.Util;

import android.content.Context;

/**
 * Created by Flop on 2019/03/19.
 */
public class PixelUtil {
    //dp转为px
    public static int dip2px(Context context, float dpValue){
        final float scale =context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

    //px转为dp
    public static int px2dip(Context context, float pxValue){
        final float scale =context.getResources().getDisplayMetrics().density;
        return (int)(pxValue/scale+0.5f);
    }
}
