package com.flop.minesweeper.zhangye.bean;

import java.io.Serializable;

/**
 * 文件检验BEAN
 *
 * @author zhangYe
 *
 */
public class VideoCheckBean implements Serializable
{
    /**
     * UID
     */
    private static final long serialVersionUID = -2730987152467866530L;
    /** 检验标志默认为true */
    public boolean checkFlag = true;
    /** 录像类型 初期期望支持mvf avf */
    public String videoType;
    /** 录像版本 为各版本子版本号 */
    public String videoVersion;
    /** 偏移值 */
    public int offset;

    public boolean getCheckFlag()
    {
        return checkFlag;
    }

    public void setCheckFlag(boolean checkFlag)
    {
        this.checkFlag = checkFlag;
    }

    public String getVideoType()
    {
        return videoType;
    }

    public void setVideoType(String videoType)
    {
        this.videoType = videoType;
    }

    public String getVideoVersion()
    {
        return videoVersion;
    }

    public void setVideoVersion(String videoVersion)
    {
        this.videoVersion = videoVersion;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

}