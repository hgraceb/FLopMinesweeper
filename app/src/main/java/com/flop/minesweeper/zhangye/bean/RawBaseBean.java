package com.flop.minesweeper.zhangye.bean;

import java.io.Serializable;

/**
 * 文件检验BEAN
 *
 * @author zhangYe
 *
 */
public class RawBaseBean implements Serializable
{
    /**
     * UID
     */
    private static final long serialVersionUID = -2730987152467866530L;

    /** 录像解析工具版本 */
    private String rawVFVersion = "Rev5";
    /** 录像软件 */
    private String program;
    /** 软件版本 */
    private String version;
    /** id */
    private String player;
    /** 时间戳 */
    private String timeStamp;
    /** 等级 */
    private String level;
    /** 宽 */
    private String width;
    /** 高 */
    private String height;
    /** 雷数 */
    private String mines;
    /** 皮肤 */
    private String skin;
    /** 模式 */
    private String mode;
    /** 模式 */
    private String luckLibrary;
    /** 模式 */
    private String luckSolver;
    /** 是否使用问号 */
    private String qm;

    public String getRawVFVersion()
    {
        return rawVFVersion;
    }

    public void setRawVFVersion(String rawVFVersion)
    {
        this.rawVFVersion = rawVFVersion;
    }

    public String getProgram()
    {
        return program;
    }

    public void setProgram(String program)
    {
        this.program = program;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getPlayer()
    {
        return player;
    }

    public void setPlayer(String player)
    {
        this.player = player;
    }

    public String getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getWidth()
    {
        return width;
    }

    public void setWidth(String width)
    {
        this.width = width;
    }

    public String getHeight()
    {
        return height;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    public String getMines()
    {
        return mines;
    }

    public void setMines(String mines)
    {
        this.mines = mines;
    }

    public String getSkin()
    {
        return skin;
    }

    public void setSkin(String skin)
    {
        this.skin = skin;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getQm()
    {
        return qm;
    }

    public void setQm(String qm)
    {
        this.qm = qm;
    }

    public String getLuckLibrary()
    {
        return luckLibrary;
    }

    public void setLuckLibrary(String luckLibrary)
    {
        this.luckLibrary = luckLibrary;
    }

    public String getLuckSolver()
    {
        return luckSolver;
    }

    public void setLuckSolver(String luckSolver)
    {
        this.luckSolver = luckSolver;
    }

}