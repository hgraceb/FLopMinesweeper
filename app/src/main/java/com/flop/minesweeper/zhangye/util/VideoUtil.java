package com.flop.minesweeper.zhangye.util;

import com.flop.minesweeper.zhangye.bean.VideoDisplayBean;

/**
 * 录像解析接口
 *
 * @author zhangYe
 * @version 2013-11-3
 */
public interface VideoUtil
{

    /**
     * 分析录像
     *
     * @param byteStream
     *            字节流
     * @param bean
     *            记录bean
     */
    void analyzeVideo(byte[] byteStream, VideoDisplayBean bean);

}