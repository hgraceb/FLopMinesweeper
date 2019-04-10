package com.flop.minesweeper.zhangye.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件检验BEAN
 *
 * @author zhangYe
 *
 */
public class RawProbBoardBean implements Serializable
{
    /**
     * UID
     */
    private static final long serialVersionUID = -2730987152467866530L;
    /** 软件版本 */
    private int width;
    /** 软件版本 */
    private int height;
    /** 软件版本 */
    private int mines;
    /** 软件版本 */
    private int knowMines=0;
    /** 软件版本 */
    private List<Integer> board;
    /** 软件版本 */
    private CellBean[] cbBoard;
    /** 软件版本 */
    private CellsBean[] cells;
    /** 软件版本 */
    private List<BrdBean> brdList=new ArrayList<BrdBean>();
    public List<Integer> getBoard()
    {
        return board;
    }

    public void setBoard(List<Integer> board)
    {
        this.board = board;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getMines()
    {
        return mines;
    }

    public void setMines(int mines)
    {
        this.mines = mines;
    }

    public CellBean[] getCbBoard()
    {
        return cbBoard;
    }

    public void setCbBoard(CellBean[] cbBoard)
    {
        this.cbBoard = cbBoard;
    }

    public CellsBean[] getCells()
    {
        return cells;
    }

    public void setCells(CellsBean[] cells)
    {
        this.cells = cells;
    }

    public int getKnowMines() {
        return knowMines;
    }

    public void setKnowMines(int knowMines) {
        this.knowMines = knowMines;
    }

    public List<BrdBean> getBrdList() {
        return brdList;
    }

    public void setBrdList(List<BrdBean> brdList) {
        this.brdList = brdList;
    }

}