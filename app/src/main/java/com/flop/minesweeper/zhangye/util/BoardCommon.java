package com.flop.minesweeper.zhangye.util;


import com.flop.minesweeper.zhangye.bean.BoardBean;
import com.flop.minesweeper.zhangye.bean.CellBean;

/**
 * board解析
 *
 * @author zhangYe
 * @version 2013-11-3
 */
public class BoardCommon
{
    /**关闭的格子数 初始为0 */
    private static int CLOSED_CELLS = 0;
    /**ZINI值 初始为0 */
    private static int ZINI = 0;
    /**HZINI值 初始为0 */
    private static int HZINI = 0;

    /**
     * 转换录像 这段代码采取的方式是先竖再横？
     *
     * @param width 宽
     * @param height 高
     * @param mines 雷数
     * @param board 格子信息
     * @return BoardBean 录像格子信息
     */
    public static BoardBean getBoardBean(int width, int height, int mines, CellBean[] board)
    {
        int size = width * height;
        CLOSED_CELLS = width * height;
        // 列 比如1 6
        int openings = 0;
        int bbbv = 0;
        int num0 = 0;
        int num1 = 0;
        int num2 = 0;
        int num3 = 0;
        int num4 = 0;
        int num5 = 0;
        int num6 = 0;
        int num7 = 0;
        int num8 = 0;
        int islands = 0;
        /** initialize cell positions */
        for (int r = 0; r < height; r++)
        {
            for (int c = 0; c < width; c++)
            {
                int index = c * height + r;
                board[index].rb = (r != 0 ? r - 1 : r);
                board[index].re = (r == height - 1 ? r : r + 1);
                board[index].cb = (c != 0 ? c - 1 : c);
                board[index].ce = (c == width - 1 ? c : c + 1);
                board[index].islands = 0;
                board[index].openingAr = 0;
            }

        }
        /** initialize numbers */
        for (int i = 0; i < size; i++)
        {
            board[i].premium = (-(board[i].number = getnumber(board, height, i)) - 2);
        }
        for (int i = 0; i < size; i++)
        {
            if (board[i].mine == 0)
            {
                switch (board[i].number)
                {
                    case 0:
                        num0++;
                        break;
                    case 1:
                        num1++;
                        break;
                    case 2:
                        num2++;
                        break;
                    case 3:
                        num3++;
                        break;
                    case 4:
                        num4++;
                        break;
                    case 5:
                        num5++;
                        break;
                    case 6:
                        num6++;
                        break;
                    case 7:
                        num7++;
                        break;
                    case 8:
                        num8++;
                        break;
                    default:
                        break;
                }
            }
        }
        // =0*16+5
        // 现在= 5 *width
        for (int i = 0; i < size; i++)
        {
            if ((board[i].number == 0) && (board[i].opening == 0))
            {
                processopening(board, height, ++openings, i);
            }
        }

        for (int i = 0; i < size; i++)
        {
            if (board[i].mine == 1)
            {
                board[i].openingAr = 2;
            }
            if ((board[i].opening > 0) && (board[i].mine == 0))
            {
                board[i].openingAr = 1;
               /* for (int rr = board[i].rb; rr <= board[i].re; rr++)
                {
                    for (int cc = board[i].cb; cc <= board[i].ce; cc++)
                    {
                        if (board[(cc * height + rr)].openingAr != 2)
                        {
                            board[(cc * height + rr)].openingAr = 1;
                        }
                    }
                }*/
            }
        }

        for (int i = 0; i < size; i++)
        {
            if (board[i].openingAr == 0)
            {
                islands++;
                board[i].islands = islands;
                board[i].openingAr = 3;
                islands(board, height, size, i,islands);

            }
        }

        bbbv = openings;
        for (int i = 0; i < size; i++)
        {
            if ((board[i].opening == 0) && (board[i].mine == 0))
            {
                bbbv++;
            }
            board[i].premium += getadj3bv(board, height, i);
        }
        /** initialize cell positions */
        // 计算zini
        zinialg(false, mines, board, height, size);
        for (int i = 0; i < size; i++)
        {
            board[i].opened = 0;
            board[i].flagged = 0;
            board[i].premium = (-(board[i].number = getnumber(board, height, i)) - 2);
        }
        for (int i = 0; i < size; i++)
        {
            board[i].premium += getadj3bv(board, height, i);
        }
        /** initialize cell positions */
        // 计算hzini
        zinialg(true, mines, board, height, size);
        BoardBean boardBean = new BoardBean();
        boardBean.setBbbv(bbbv);
        boardBean.setIslands(islands);
        boardBean.setNum0(num0);
        boardBean.setNum1(num1);
        boardBean.setNum2(num2);
        boardBean.setNum3(num3);
        boardBean.setNum4(num4);
        boardBean.setNum5(num5);
        boardBean.setNum6(num6);
        boardBean.setNum7(num7);
        boardBean.setNum8(num8);
        boardBean.setOpenings(openings);
        boardBean.setZini(ZINI);
        boardBean.setHzini(HZINI);

        return boardBean;
    }

    /**
     * 计算ZINI
     *
     * @param isHuman 是否计算HZINI true 时计算HZINI false 计算ZINI
     * @param mines 雷数
     * @param board 格子信息
     * @param height 高度
     * @param size 格子数
     */
    private static void zinialg(boolean isHuman, int mines, CellBean[] board, int height, int size)
    {
        if (isHuman)
        {
            HZINI = 0;
            CLOSED_CELLS = size;
            hitopenings(board, height, size);
        }
        else
        {
            ZINI = 0;
        }

        while (CLOSED_CELLS > mines)
        {
            applyzini(isHuman, board, height, size);

        }
    }
    /**
     * 点击opening
     * @param board 格子信息
     * @param height 高度
     * @param size 格子数
     */
    private static void hitopenings(CellBean[] board, int height, int size)
    {
        int j;
        for (j = 0; j < size; ++j)
        {
            if (board[j].number == 0 && board[j].opened == 0)
            {
                click(board, height, size, j, true);
            }
        }

    }
    /**
     * 判定是不是岛
     * @param board 格子信息
     * @param height 高度
     * @param size 格子数
     * @param index 编号
     * @param islands 编号
     */
    private static void islands(CellBean[] board, int height, int size, int index,int islands)
    {
        for (int rr = board[index].rb; rr <= board[index].re; rr++)
        {
            for (int cc = board[index].cb; cc <= board[index].ce; cc++)
            {
                if (board[(cc * height + rr)].openingAr == 0)
                {
                    board[(cc * height + rr)].openingAr = 3;
                    board[(cc * height + rr)].islands = islands;
                    islands(board, height, size, (cc * height + rr),islands);

                }
            }
        }
    }
    /**
     * 点击事件
     * @param board 格子信息
     * @param height 高度
     * @param size  格子数
     * @param index 编号
     * @param ishuman 是否计算HZINI
     */
    private static void click(CellBean[] board, int height, int size, int index, boolean ishuman)
    {
        reveal(board, height, size, index);
        if (ishuman)
        {
            HZINI += 1;
        }
        else
        {
            ZINI += 1;
        }
    }
    /**
     * 标旗
     * @param board 格子信息
     * @param height 高度
     * @param index 编号
     * @param isHuman 是否计算HZINI
     */
    private static void flag(CellBean[] board, int height, int index, boolean isHuman)
    {
        if (board[index].flagged != 0)
        {
            return;
        }
        if (isHuman)
        {
            HZINI += 1;
        }
        else
        {
            ZINI += 1;
        }
        board[index].flagged = 1;
        for (int rr = board[index].rb; rr <= board[index].re; rr++)
        {
            for (int cc = board[index].cb; cc <= board[index].ce; cc++)
            {
                board[(cc * height + rr)].premium += 1;
            }
        }
    }
    /**
     * 标旗周围
     * @param board 格子信息
     * @param height 高度
     * @param index 编号
     * @param isHuman 是否计算HZINI
     */
    private static void flagaround(CellBean[] board, int height, int index, boolean isHuman)
    {
        for (int rr = board[index].rb; rr <= board[index].re; rr++)
        {
            for (int cc = board[index].cb; cc <= board[index].ce; cc++)
            {
                int i = cc * height + rr;
                if (board[i].mine != 0)
                {
                    flag(board, height, i, isHuman);
                }
            }
        }
    }
    /**
     * 打开邻近的格子open all neighbors
     * @param board 格子信息
     * @param height 高度
     * @param size 格子数
     * @param index 编号
     * @param isHuman 是否计算HZINI
     */
    private static void chord(CellBean[] board, int height, int size, int index, boolean isHuman)
    {
        if (isHuman)
        {
            HZINI += 1;
        }
        else
        {
            ZINI += 1;
        }
        for (int rr = board[index].rb; rr <= board[index].re; rr++)
        {
            for (int cc = board[index].cb; cc <= board[index].ce; cc++)
            {
                reveal(board, height, size, cc * height + rr);
            }
        }
    }
    /**
     * 计算ZINI
     * @param human  是否计算HZINI
     * @param board 格子信息
     * @param height 高度
     * @param size 格子数
     */
    private static void applyzini(boolean human, CellBean[] board, int height, int size)
    {
        int maxp = -1;
        int curi = -1;
        for (int i = 0; i < size; ++i)
        {
            if (board[i].premium > maxp && (board[i].mine == 0) && ((board[i].opened > 0) || !human))
            {
                maxp = board[i].premium;
                curi = i;
            }
        }
        if (curi != -1)
        {
            if (board[curi].opened == 0)
            {
                click(board, height, size, curi, human);
            }
            flagaround(board, height, curi, human);
            chord(board, height, size, curi, human);
        }
        else
        {
            /** left-click */
            for (int j = 0; j < size; j++)
            {
                if ((board[j].opened == 0) && (board[j].mine == 0) && ((board[j].number == 0) || (board[j].opening == 0)))
                {
                    curi = j;
                    break;
                }
            }
            click(board, height, size, curi, human);
        }
    }
    /**
     * 点击
     * @param board 格子信息
     * @param height 高度
     * @param size 格子数
     * @param index 编号
     */
    private static void reveal(CellBean[] board, int height, int size, int index)
    {
        if (board[index].opened != 0)
        {
            return;
        }
        if (board[index].flagged != 0)
        {
            return;
        }
        if (board[index].number != 0)
        {
            open(board, height, index);
        }
        else
        {
            int op = board[index].opening;
            for (int i = 0; i < size; i++)
            {
                if ((board[i].opening2 == op) || (board[i].opening == op))
                {
                    if (board[i].opened == 0)
                    {
                        open(board, height, i);
                    }
                    board[i].premium -= 1;
                }
            }
        }
    }
    /**
     * 触发open事件
     * @param board 格子信息
     * @param height 高度
     * @param index 编号
     */
    private static void open(CellBean[] board, int height, int index)
    {
        board[index].opened = 1;
        board[index].premium += 1;
        if (board[index].opening == 0)
        {
            for (int rr = board[index].rb; rr <= board[index].re; rr++)
            {
                for (int cc = board[index].cb; cc <= board[index].ce; cc++)
                {
                    board[(cc * height + rr)].premium -= 1;
                }
            }
        }
        CLOSED_CELLS -= 1;
    }
    /**
     * 计算周围雷数
     * @param board 格子信息
     * @param height 高度
     * @param index 编号
     * @return mine 周围雷数
     */
    private static int getnumber(CellBean[] board, int height, int index)
    {
        int res = 0;
        for (int rr = board[index].rb; rr <= board[index].re; rr++)
        {
            for (int cc = board[index].cb; cc <= board[index].ce; cc++)
            {
                res += board[(cc * height + rr)].mine;
            }
        }
        return res;
    }
    /**
     * 设定opening边界
     * @param board 格子信息
     * @param op_id opening编号
     * @param index 编号
     */
    private static void setopeningborder(CellBean[] board, int op_id, int index)
    {
        if (board[index].opening == 0)
        {
            board[index].opening = op_id;
        }
        else if (board[index].opening != op_id)
        {
            board[index].opening2 = op_id;
        }
    }
    /**
     * 执行opening计算
     * @param board  格子信息
     * @param height 高度
     * @param op_id opening编号
     * @param index 编号
     */
    private static void processopening(CellBean[] board, int height, int op_id, int index)
    {
        board[index].opening = op_id;

        for (int rr = board[index].rb; rr <= board[index].re; rr++)
        {
            for (int cc = board[index].cb; cc <= board[index].ce; cc++)
            {
                int i = cc * height + rr;
                if (board[i].number != 0)
                {
                    setopeningborder(board, op_id, i);
                }
                else if (board[i].opening == 0)
                {
                    processopening(board, height, op_id, i);
                }
            }
        }
    }
    /**
     * 计算调整3bv数
     * @param board 格子信息
     * @param height 高度
     * @param index 编号
     * @return res 调整3bv数
     */
    private static int getadj3bv(CellBean[] board, int height, int index)
    {
        int res = 0;
        if (board[index].number == 0)
        {
            return 1;
        }
        for (int rr = board[index].rb; rr <= board[index].re; rr++)
        {
            for (int cc = board[index].cb; cc <= board[index].ce; cc++)
            {
                int i = cc * height + rr;
                res += ((board[i].mine == 0) && (board[i].opening == 0) ? 1 : 0);
            }
        }
        if (board[index].opening != 0)
        {
            res++;
        }
        if (board[index].opening2 != 0)
        {
            res++;
        }
        return res;
    }
}
