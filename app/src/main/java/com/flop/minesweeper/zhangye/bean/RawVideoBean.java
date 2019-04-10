package com.flop.minesweeper.zhangye.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 统一录像BEAN
 *
 * @author zhangYe
 *
 */
public class RawVideoBean implements Serializable
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7358112206234839795L;
    /** 检验标志默认为true */
    private boolean checkFlag = true;
    /** 检验标志默认为true */
    private String errorMessage;
    /** 录像基本信息 */
    private RawBaseBean rawBaseBean;
    /** 录像board信息 */
    private RawBoardBean rawBoardBean;
    /** 录像详细信息 */
    private List<RawEventDetailBean> rawEventDetailBean;

    public RawBaseBean getRawBaseBean()
    {
        return rawBaseBean;
    }

    public void setRawBaseBean(RawBaseBean rawBaseBean)
    {
        this.rawBaseBean = rawBaseBean;
    }

    public RawBoardBean getRawBoardBean()
    {
        return rawBoardBean;
    }

    public void setRawBoardBean(RawBoardBean rawBoardBean)
    {
        this.rawBoardBean = rawBoardBean;
    }

    public List<RawEventDetailBean> getRawEventDetailBean()
    {
        return rawEventDetailBean;
    }

    public void setRawEventDetailBean(List<RawEventDetailBean> rawEventDetailBean)
    {
        this.rawEventDetailBean = rawEventDetailBean;
    }

    public boolean isCheckFlag()
    {
        return checkFlag;
    }

    public void setCheckFlag(boolean checkFlag)
    {
        this.checkFlag = checkFlag;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

}