package com.flop.minesweeper.zhangye.core;

import com.flop.minesweeper.zhangye.bean.BrdBean;
import com.flop.minesweeper.zhangye.bean.CellsBean;
import com.flop.minesweeper.zhangye.bean.RawProbBoardBean;
import com.flop.minesweeper.zhangye.bean.SquareBean;

import java.util.ArrayList;
import java.util.List;


/**
 * 计算扫雷概率 这是一个2年前就打算做 但一直没有找到合适的做法的 内容</p> 首先扫雷很多人说存在各种猜 但有的并不是真正的猜
 * 从第一下点击开始<p> 剩下每个点是雷还是非雷的概率都是真实存在的 如果是100%非雷那么就是安全点<p> 100%雷就是 确认雷点
 * 剩下的就属于非安全点</br> 对这些点的点击都有可能导致遇雷结束。 那么如何计算这个点是雷的概率呢 这个问题比较复杂 让先把模型建立好。</br>
 * 数据源 长 宽 和雷数 这是必须的</br> 还需要一个 当前状态的记录 追加字段 记录当前的 prob 首先找到确定雷和非确认雷</br>
 *
 * @author Administrator
 *
 */
public class ShowProb {
    private static int probabilities_iter = 0;

    public static CellsBean[] showProb(RawProbBoardBean bean) {
        CellsBean[] tempCells = bean.getCells();
        int width = bean.getWidth();
        int height = bean.getHeight();
        int knowMines = 0;
        System.out.println(bean.getKnowMines());
        // // find isolated mines
        for (int x = 1; x <= width; x++) {
            for (int y = 1; y <= height; y++) {
                if (bean.getCells()[(x - 1) * width + y - 1].opened == 1
                        && !bean.getCells()[(x - 1) * width + y - 1].psolved) {
                    if (bean.getCells()[(x - 1) * width + y - 1].what == 0) {
                        bean.getCells()[(x - 1) * width + y - 1].psolved = true;
                    } else if (bean.getCells()[(x - 1) * width + y - 1].what >= unknownSqAround(
                            bean.getCells(), x, y, width, height)
                            + knownMinesAround(bean.getCells(), x, y, width,
                            height)) {
                        bean = pSetMines(bean, x, y, width, height);
                    }
                }

            }
        }
        // find isolated safe squares
        for (int x = 1; x <= width; x++) {
            for (int y = 1; y <= height; y++) {
                if (bean.getCells()[(x - 1) * width + y - 1].opened == 1
                        && !bean.getCells()[(x - 1) * width + y - 1].psolved) {
                    if (bean.getCells()[(x - 1) * width + y - 1].what == 0) {
                        bean.getCells()[(x - 1) * width + y - 1].psolved = true;
                    } else if (bean.getCells()[(x - 1) * width + y - 1].what <= knownMinesAround(
                            bean.getCells(), x, y, width, height)) {
                        bean = pSetSafe(bean, x, y, width, height);
                    }
                }

            }
        }
        int boarder = 0;
        int X = 0;
        // init boarder
        for (int x = 1; x <= width; x++) {
            for (int y = 1; y <= height; y++) {
                if (bean.getCells()[(x - 1) * width + y - 1].opened == 1
                        && bean.getCells()[(x - 1) * width + y - 1].border == 0
                        && unknownSqAround(bean.getCells(), x, y, width, height) > 0) {
                    processBorderO(++boarder, bean, x, y, width, height);
                } else if (bean.getCells()[(x - 1) * width + y - 1].opened == 0
                        && bean.getCells()[(x - 1) * width + y - 1].border == 0
                        && openedSqAroundWithoutBorder(bean.getCells(), x, y,
                        width, height) == 0) {

                    ++X; // it's closed and not adjacent to border
                    bean.getCells()[(x - 1) * width + y - 1].prop = -1;
                }
            }
        }
        if (boarder == 0) {
            // todo
            double common_prob = (double) (bean.getMines() - bean
                    .getKnowMines()) / X;
            for (int i = 1; i <= bean.getWidth(); ++i)
                for (int j = 1; j <= bean.getHeight(); ++j)
                    if (bean.getCells()[(i - 1) * width + j - 1].opened == 0
                            && bean.getCells()[(i - 1) * width + j - 1].prop < 0)
                        bean.getCells()[(i - 1) * width + j - 1].prop = common_prob;
            return bean.getCells();
        }
        // find all possible solutions on border
        probabilities_iter = 0;
        int pmines = 0;
        System.out.println(bean.getKnowMines());
        for (int cur_brd = 1; cur_brd <= boarder; ++cur_brd) {
            bean.getBrdList().get(cur_brd).setMaxPmines(0);
            bean.getBrdList().get(cur_brd).setMinPmines(bean.getMines());
            p_solve_border(0, bean, cur_brd, pmines);

            System.out.println("probabilities_iter" + probabilities_iter);
        }
        // calculate external info for each border
        int N = bean.getMines() - bean.getKnowMines(); // unknown mines
        double[] coeff = new double[bean.getMines() + 1];
        double numer, denom;
        double common_prob = 0.0;
        for (int i = 1; i < bean.getBrdList().size(); ++i) {
            double j_coeff = 1.0; // binom{X}{N-j} / binom{X}{N-j_min}

            BrdBean brdBean = bean.getBrdList().get(i);
            p_get_ext_info(1, i, 0, 1, bean.getBrdList().size(), bean);
            denom = 0;
            // calculate external multiplier for each pmines
            for (int j = bean.getBrdList().get(i).getMinPmines(); j <= bean
                    .getBrdList().get(i).getMaxPmines(); ++j) {
                double k_coeff;

                k_coeff = j_coeff; // binom{X}{N-j} / binom
                coeff[j] = 0;

                // calc N_j = \sum_k moeb[k]*\binom{X}{N-j-k} /
                // binom{X}{N-j_min}
                for (int k = 0; k <= N - j; ++k) {
                    coeff[j] += (bean.getBrdList().get(i).getMob().get(k) == null ? 0
                            : bean.getBrdList().get(i).getMob().get(k))
                            * k_coeff;
                    k_coeff = k_coeff * (N - j - k) / (X - N + j + k + 1);
                }

                denom += coeff[j]
                        * (bean.getBrdList().get(i).getMob().get(j) == null ? 0
                        : bean.getBrdList().get(i).getMob().get(j));
                j_coeff = j_coeff * (N - j) / (X - N + j + 1);
            }
            for (int k = 0; k < bean.getBrdList().get(i).getC().size(); ++k) {
                numer = 0;
                for (int j = bean.getBrdList().get(i).getMinPmines(); j <= bean
                        .getBrdList().get(i).getMaxPmines(); ++j)
                    numer += coeff[j]
                            * (bean.getBrdList().get(i).getC().get(k).getPr()
                            .get(j) == null ? 0 : bean.getBrdList()
                            .get(i).getC().get(k).getPr().get(j));
                common_prob += (bean.getBrdList().get(i).getC().get(k).prob = numer
                        / denom);
                if (numer == denom) // mine for sure
                {
                    bean.getBrdList().get(i).getC().get(k).pmine = 1;
                    bean.getBrdList().get(i).getC().get(k).setPsolved(true);
                    bean.setKnowMines(bean.getKnowMines() + 1);
                    common_prob -= 1;
                } else if (numer == 0) // safe for sure
                {
                    bean.getBrdList().get(i).getC().get(k).pmine = -1;
                    bean.getBrdList().get(i).getC().get(k).setPsolved(true);
                }

            }
        }
        coeff = new double[bean.getMines() + 1];

        common_prob = (bean.getMines() - bean.getKnowMines() - common_prob) / X;
        for (int i = 1; i <= bean.getWidth(); ++i)
            for (int j = 1; j <= bean.getHeight(); ++j)
                if (bean.getCells()[(i - 1) * width + j - 1].opened == 0
                        && bean.getCells()[(i - 1) * width + j - 1].prop < 0)
                    bean.getCells()[(i - 1) * width + j - 1].prop = common_prob;
        // System.out.println("X" + X);
        // printCells(tempCells, width, height);
        // printCellsBorder(tempCells, width, height);
        printCellsProb(tempCells, width, height);
        // System.out.println(bean.getKnowMines());
        return tempCells;
    }

    private static void p_get_ext_info(int cur, int proc, int sum, int cur_num,
                                       int brdSize, RawProbBoardBean bean) {
        // current border, excluded border, current sum, current number of
        // solutions
        if (cur == proc) {
            p_get_ext_info(cur + 1, proc, sum, cur_num, brdSize, bean);
        } else if (cur == brdSize)
            bean.getBrdList()
                    .get(proc)
                    .getMob()
                    .put(sum,
                            bean.getBrdList().get(proc).getMob().get(sum) == null ? 0
                                    : bean.getBrdList().get(proc).getMob()
                                    .get(sum)
                                    + cur_num);

        else
            for (int i = bean.getBrdList().get(cur).getMinPmines(); i <= bean
                    .getBrdList().get(cur).getMaxPmines(); ++i) {
                if (bean.getBrdList().get(cur).getMob().get(i) != null
                        && bean.getBrdList().get(cur).getMob().get(i) > 0)
                    p_get_ext_info(cur + 1, proc, sum + i, bean.getBrdList()
                            .get(cur).getMob().get(i), brdSize, bean);
            }
    }

    private static void p_solve_border(int sq, RawProbBoardBean bean,
                                       int cur_brd, int pmines) {
        ++probabilities_iter;
        System.out.println("probabilities_iter" + probabilities_iter);
        if (sq == bean.getBrdList().get(cur_brd).ox.size()) {
            // add a solution
            for (int i = 0; i < bean.getBrdList().get(cur_brd).c.size(); ++i) {
                if (bean.getBrdList().get(cur_brd).c.get(i).pmine == 1) {
                    bean.getBrdList().get(cur_brd).c.get(i).pr.put(
                            pmines,
                            bean.getBrdList().get(cur_brd).c.get(i).pr
                                    .get(pmines) == null ? 1 : bean
                                    .getBrdList().get(cur_brd).c.get(i).pr
                                    .get(pmines) + 1);

                }
            }
            if (pmines > bean.getBrdList().get(cur_brd).getMaxPmines()) {
                bean.getBrdList().get(cur_brd).setMaxPmines(pmines);
            }
            if (pmines < bean.getBrdList().get(cur_brd).getMinPmines()) {
                bean.getBrdList().get(cur_brd).setMinPmines(pmines);
            }
            bean.getBrdList()
                    .get(cur_brd)
                    .getMob()
                    .put(pmines,
                            bean.getBrdList().get(cur_brd).getMob().get(pmines) == null ? 1
                                    : bean.getBrdList().get(cur_brd).getMob()
                                    .get(pmines) + 1);
            return;
        }
        // determine unknown squares adjacent to c_brd->o[sq]
        // square* adj[8];
        List<SquareBean> adj = new ArrayList<SquareBean>();
        int n_adj = 0;
        int x = bean.getBrdList().get(cur_brd).ox.get(sq);
        int y = bean.getBrdList().get(cur_brd).oy.get(sq);
        // unknown mines around square
        int a_mines = bean.getCells()[(x - 1) * bean.getWidth() + y - 1].what
                - knownMinesAround(bean.getCells(), x, y, bean.getWidth(),
                bean.getHeight());
        for (int i = (x - 1) <= 0 ? 1 : (x - 1); i <= ((x + 1) >= bean
                .getWidth() ? bean.getWidth() : (x + 1)); i++) {
            for (int j = (y - 1) <= 1 ? 1 : (y - 1); j <= ((y + 1) >= bean
                    .getHeight() ? bean.getHeight() : (y + 1)); j++) {
                if (isUnknown(bean.getCells()[(i - 1) * bean.getWidth() + j - 1])) {
                    SquareBean squareBean = new SquareBean();
                    squareBean.ox = i;
                    squareBean.oy = j;
                    squareBean.pmine = bean.getCells()[(i - 1)
                            * bean.getWidth() + j - 1].pmine;
                    adj.add(squareBean);
                    n_adj++;
                }
            }
        }
        // not enough places or too many mines around
        if (n_adj < a_mines || a_mines < 0)
            return;
        ++sq;
        if (a_mines == 0) { // enough mines!
            for (int i = 0; i < n_adj; ++i) {
                adj.get(i).pmine = -1;
                bean.getCells()[(adj.get( i).getOx() - 1) * bean.getWidth()
                        + adj.get( i).getOy() - 1].pmine = -1;
            }

            p_solve_border(sq, bean, cur_brd, pmines);
            for (int i = 0; i < n_adj; ++i) {
                adj.get(i).pmine = 0;
                bean.getCells()[(adj.get( i).getOx() - 1) * bean.getWidth()
                        + adj.get( i).getOy() - 1].pmine = 0;
            }
            return;
        }
        pmines += a_mines;
        if (pmines > bean.getMines()) {
            pmines -= a_mines;
            return;
        } // too many mines
        // first combination
        for (int i = 1; i <= a_mines; ++i) {
            adj.get(n_adj - i).pmine = 1;
            bean.getCells()[(adj.get(n_adj - i).getOx() - 1) * bean.getWidth()
                    + adj.get(n_adj - i).getOy() - 1].pmine = 1;
        }

        for (int i = 0; i < n_adj - a_mines; ++i) {
            adj.get(i).pmine = -1;
            bean.getCells()[(adj.get(i).getOx() - 1) * bean.getWidth()
                    + adj.get(i).getOy() - 1].pmine = -1;
        }
        while (true) {
            int cur, o;
            p_solve_border(sq, bean, cur_brd, pmines);
            for (cur = n_adj - 1; cur >= 0 && adj.get(cur).pmine == -1; --cur){

            }
            ; // safes
            for (o = 0; cur >= 0 && adj.get(cur).pmine == 1; --cur, ++o){

            }
            ; // mines
            if (cur < 0)
                break;
            adj.get(cur).pmine = 1;
            bean.getCells()[(adj.get(cur).getOx() - 1) * bean.getWidth()
                    + adj.get(cur).getOy() - 1].pmine = 1;
            adj.get(++cur).pmine = -1;
            bean.getCells()[(adj.get(cur).getOx() - 1) * bean.getWidth()
                    + adj.get(cur).getOy() - 1].pmine = -1;
            for (++cur; cur < n_adj - o + 1; ++cur) {
                adj.get(cur).pmine = -1;
                bean.getCells()[(adj.get(cur).getOx() - 1) * bean.getWidth()
                        + adj.get(cur).getOy() - 1].pmine = -1;
            }
            for (; cur < n_adj; ++cur) {
                adj.get(cur).pmine = 1;
                bean.getCells()[(adj.get(cur).getOx() - 1) * bean.getWidth()
                        + adj.get(cur).getOy() - 1].pmine = 1;
            }
        }
        // clear
        pmines -= a_mines;
        for (int i = 0; i < n_adj; ++i) {
            adj.get(i).pmine = 0;
            bean.getCells()[(adj.get(i).getOx() - 1) * bean.getWidth()
                    + adj.get(i).getOy() - 1].pmine = 0;
        }
    }

    private static void processBorderO(int boarder, RawProbBoardBean bean,
                                       int x, int y, int width, int height) {

        bean.getCells()[(x - 1) * width + y - 1].border = boarder;
        while (bean.getBrdList().size() < boarder + 1) {
            bean.getBrdList().add(new BrdBean());
        }
        bean.getBrdList().get(boarder).ox.add(x);
        bean.getBrdList().get(boarder).oy.add(y);
        for (int i = (x - 1) <= 0 ? 1 : (x - 1); i <= ((x + 1) >= width ? width
                : (x + 1)); i++)
            for (int j = (y - 1) <= 1 ? 1 : (y - 1); j <= ((y + 1) >= height ? height
                    : (y + 1)); j++) {
                if (ifBorder(bean.getCells()[(i - 1) * width + j - 1], i, j,
                        width, height)) {
                    processBorderC(boarder, bean, i, j, width, height);
                }
            }
    }

    private static void processBorderC(int boarder, RawProbBoardBean bean,
                                       int x, int y, int width, int height) {

        bean.getCells()[(x - 1) * width + y - 1].border = boarder;
        bean.getCells()[(x - 1) * width + y - 1].prop = -1;
        SquareBean squareBean = new SquareBean();
        squareBean.setOx(x);
        squareBean.setOy(y);
        squareBean.setPmine(bean.getCells()[(x - 1) * width + y - 1].pmine);
        bean.getBrdList().get(boarder).c.add(squareBean);
        for (int i = (x - 1) <= 0 ? 1 : (x - 1); i <= ((x + 1) >= width ? width
                : (x + 1)); i++)
            for (int j = (y - 1) <= 1 ? 1 : (y - 1); j <= ((y + 1) >= height ? height
                    : (y + 1)); j++) {
                if (ifBorderC(bean.getCells()[(i - 1) * width + j - 1], i, j,
                        width, height)) {
                    bean.getBrdList().add(new BrdBean());
                    processBorderO(boarder, bean, i, j, width, height);
                }
            }
    }

    private static boolean ifBorder(CellsBean bean, int x, int y, int width,
                                    int height) {
        return bean.border == 0 && bean.opened == 0 && bean.pmine == 0
                && (x >= -1 && x != width + 1) && (y >= -1 && y != height + 1);
    }

    private static boolean ifBorderC(CellsBean bean, int x, int y, int width,
                                     int height) {
        return bean.border == 0 && bean.opened > 0
                && (x > -1 && x != width + 1) && (y >= -1 && y != height + 1);
    }

    public static int unknownSqAround(CellsBean[] tempCells, int x, int y,
                                      int width, int height) {
        int unknown = 0;
        for (int i = (x - 1) <= 0 ? 1 : (x - 1); i <= ((x + 1) >= width ? width
                : (x + 1)); i++)
            for (int j = (y - 1) <= 1 ? 1 : (y - 1); j <= ((y + 1) >= height ? height
                    : (y + 1)); j++) {
                if (isUnknown(tempCells[(i - 1) * width + j - 1])) {
                    unknown++;
                }
            }
        return unknown;
    }

    public static int openedSqAroundWithoutBorder(CellsBean[] tempCells, int x,
                                                  int y, int width, int height) {
        int opened = 0;
        for (int i = (x - 1) <= 0 ? 1 : (x - 1); i <= ((x + 1) >= width ? width
                : (x + 1)); i++)
            for (int j = (y - 1) <= 1 ? 1 : (y - 1); j <= ((y + 1) >= height ? height
                    : (y + 1)); j++) {
                if ((tempCells[(i - 1) * width + j - 1].opened > 0)) {
                    opened++;
                }
            }
        return opened;
    }

    public static int knownMinesAround(CellsBean[] tempCells, int x, int y,
                                       int width, int height) {
        int knowMines = 0;
        for (int i = (x - 1) <= 0 ? 1 : (x - 1); i <= ((x + 1) >= width ? width
                : (x + 1)); i++)
            for (int j = (y - 1) <= 1 ? 1 : (y - 1); j <= ((y + 1) >= height ? height
                    : (y + 1)); j++) {
                if (isKnownMines(tempCells[(i - 1) * width + j - 1])) {
                    knowMines++;
                }
            }
        return knowMines;
    }

    public static RawProbBoardBean pSetSafe(RawProbBoardBean bean, int x,
                                            int y, int width, int height) {
        for (int i = (x - 1) <= 0 ? 1 : (x - 1); i <= ((x + 1) >= width ? width
                : (x + 1)); i++) {
            for (int j = (y - 1) <= 1 ? 1 : (y - 1); j <= ((y + 1) >= height ? height
                    : (y + 1)); j++) {
                if (isUnknown(bean.getCells()[(i - 1) * width + j - 1])) {
                    bean.getCells()[(i - 1) * width + j - 1].prop = 0;
                    bean.getCells()[(i - 1) * width + j - 1].pmine = -1;
                    bean.getCells()[(i - 1) * width + j - 1].psolved = true;
                }
            }
        }
        bean.getCells()[(x - 1) * width + y - 1].psolved = true;
        return bean;
    }

    public static RawProbBoardBean pSetMines(RawProbBoardBean bean, int x,
                                             int y, int width, int height) {
        for (int i = (x - 1) <= 0 ? 1 : (x - 1); i <= ((x + 1) >= width ? width
                : (x + 1)); i++) {
            for (int j = (y - 1) <= 1 ? 1 : (y - 1); j <= ((y + 1) >= height ? height
                    : (y + 1)); j++) {
                if (isUnknown(bean.getCells()[(i - 1) * width + j - 1])) {
                    bean.getCells()[(i - 1) * width + j - 1].prop = 1;
                    bean.getCells()[(i - 1) * width + j - 1].pmine = 1;
                    bean.getCells()[(i - 1) * width + j - 1].psolved = true;
                    System.out.println("getKnowMinesi" + i);
                    System.out.println("getKnowMinesj" + j);
                    System.out.println("getKnowMines" + bean.getKnowMines());
                    bean.setKnowMines(bean.getKnowMines() + 1);
                }
            }
        }
        bean.getCells()[(x - 1) * width + y - 1].psolved = true;
        // printCellsProb(bean.getCells(), bean.getWidth(), bean.getHeight());
        return bean;
    }

    public static boolean isUnknown(CellsBean tempCell) {
        return tempCell.opened == 0 && tempCell.pmine == 0;
    }

    public static boolean isKnownMines(CellsBean tempCell) {
        return tempCell.pmine == 1;
    }

    public static void printCells(CellsBean[] tempCells, int width, int height) {
        for (int x = 1; x <= width; x++) {
            for (int y = 1; y <= height; y++) {
                System.out.print(tempCells[(x - 1) * width + y - 1].what);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printCellsBorder(CellsBean[] tempCells, int width,
                                        int height) {
        for (int x = 1; x <= width; x++) {
            for (int y = 1; y <= height; y++) {
                System.out.print(tempCells[(x - 1) * width + y - 1].border);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printCellsProb(CellsBean[] tempCells, int width,
                                      int height) {
        for (int x = 1; x <= width; x++) {
            for (int y = 1; y <= height; y++) {
                System.out.print(tempCells[(x - 1) * width + y - 1].prop);
            }
            System.out.println();
        }
        System.out.println();
    }
}