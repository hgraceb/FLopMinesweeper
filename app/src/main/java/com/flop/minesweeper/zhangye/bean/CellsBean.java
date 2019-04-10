package com.flop.minesweeper.zhangye.bean;

/**
 * CellsBean
 *
 * @author zhangye
 *
 */
public class CellsBean
{
    /** -1 Wall, 0 closed, 1 flagged, 2 marked, 3 opened */
    public int status = 0;
    /** 0~8 the numbers of mines around, 9 mine */
    public int what = 0;
    public int opened = 0;
    public String sta = "_";
    /** _ 已开 不用计算 A 需要计算*/
    public double prop = 0.0d;
    /** _ 已开 不用计算 A 需要计算*/
    public int unKnown = 0;
    /** 1 表示一定是雷 -1 表示一定不是雷*/
    public int pmine = 0;
    /** _ 已开 不用计算 A 需要计算*/
    public int unsolved = 0;
    public boolean psolved = false;
    public int border = 0;
    public CellsBean(int what)
    {
        status = 0;
        sta = "_";
        this.what = what;
    }
}