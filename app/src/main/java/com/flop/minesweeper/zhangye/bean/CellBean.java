package com.flop.minesweeper.zhangye.bean;

public class CellBean
{
    public int mine;
    public int opening;
    public int opening2;
    public int number;
    public int rb;
    public int re;
    public int cb;
    public int ce;
    public int opened;
    public int flagged;
    public int premium;
    // openingAr 一圈 0 表示在岛上 1 表示 海洋 2 表示 雷
    public int openingAr;
    /** 岛 */
    public int islands;
}