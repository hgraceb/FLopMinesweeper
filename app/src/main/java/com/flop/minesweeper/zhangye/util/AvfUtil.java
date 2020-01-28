package com.flop.minesweeper.zhangye.util;

import com.flop.minesweeper.zhangye.bean.CellBean;
import com.flop.minesweeper.zhangye.bean.CellsBean;
import com.flop.minesweeper.zhangye.bean.RawBaseBean;
import com.flop.minesweeper.zhangye.bean.RawBoardBean;
import com.flop.minesweeper.zhangye.bean.RawEventDetailBean;
import com.flop.minesweeper.zhangye.bean.RawVideoBean;
import com.flop.minesweeper.zhangye.bean.VideoDisplayBean;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.flop.minesweeper.variable.Constant.rawVideo;

/**
 * 拆分方法该方法主要内容为对avf文件解析 关于avf 介绍
 *
 * @author zhangye
 * @version 2013-11-20
 */
public class AvfUtil implements VideoUtil {

    /**
     * 解析avf录像
     *
     * @param byteStream 字节流
     * @param bean       要转换的bean
     */
    public void analyzeVideo(byte[] byteStream, VideoDisplayBean bean) {
        RawVideoBean rawVideoBean = convertRawVideo(byteStream);

        VideoCommon.convertVideoDisplay(rawVideoBean, bean);
    }

    /**
     * 这段代码来自C语言的改写<svg xmlns="http://www.w3.org/2000/svg" version="1.1" height="190">
     * <svg xmlns="http://www.w3.org/2000/svg" version="1.1" height="190">
     * <polygon points="100,10 40,180 190,60 10,60 160,180"
     * style="fill:lime;stroke:purple;stroke-width:5;fill-rule:evenodd;" />
     * </svg>
     *
     * @param byteStream 字节流
     * @return RawVideoBean 转换的bean
     */
    private RawVideoBean convertRawVideo(byte[] byteStream) {
        RawVideoBean rawVideoBean = new RawVideoBean();
        RawBaseBean rawBaseBean = new RawBaseBean();
        rawBaseBean.setProgram("arbiter");
        int offset = 0x00;
        int version = byteStream[0x00] & 0xFF;
        int l = 0;
        int ll = 0;
        int ls = 0;
        int c = 0;
        int mode = 0;
        int w = 0;
        int h = 0;
        int m = 0;
        int[] cr = new int[8];
        int[] timestamp = new int[100];
        String ts = "";
        int qm = 0;
        int fs = 0;
        if (version != 0) {
            fs = 1;
            // meaningless bytes 原解释
        } else {
            version = byteStream[0x01] & 0xFF;
            c = byteStream[0x02] & 0xff;
            l = c & 0x1;
            ll = c & 0x8;
            ls = c & 0x10;
            // int
        }
        offset += 5;
        c = byteStream[offset] & 0xff;
        mode = c - 2;
        switch (mode) {
            case 1:
                w = h = 8;
                m = 10;
                break;
            case 2:
                w = h = 16;
                m = 40;
                break;
            case 3:
                w = 30;
                h = 16;
                m = 99;
                break;
            case 4:
                w = (byteStream[offset] & 0xff) + 1;
                h = (byteStream[offset + 1] & 0xff) + 1;
                m = (byteStream[offset + 2] & 0xff);
                m = m * 256 + (byteStream[offset + 3] & 0xff);
                offset += 4;
                break;
            default:
                return VideoCommon.errorVideo(rawVideoBean, "mode不正确");
        }
        List<Integer> board = new ArrayList<Integer>();
        CellBean[] cbBoard = new CellBean[w * h];
        CellsBean[] cells = new CellsBean[(h + 2) * (w + 2)];
        for (int i = 0; i < (h + 2) * (w + 2); i++) {
            cells[i] = new CellsBean(0);
        }
        for (int i = 0; i < w * h; ++i) {
            int temp = 0;
            board.add(temp);
            cbBoard[i] = new CellBean();
            cbBoard[i].mine = (cbBoard[i].opened = cbBoard[i].flagged = cbBoard[i].opening = cbBoard[i].opening2 = 0);
        }
        for (int i = 0; i < m; ++i) {
            offset++;
            // 分别为x坐标 和y坐标
            c = (byteStream[offset] & 0xff) - 1;
            offset++;
            int d = (byteStream[offset] & 0xff) - 1;
            board.set(c * w + d, 1);
            int pos = (d) * h + c;
            cbBoard[pos].mine = 1;
            int posbean = (c + 1) * (w + 2) + d + 1;
            cells[posbean].what = 9;
        }

        // question marks | length of timestamp | [timestamp]
        for (int i = 0; i < 7; ++i) {
            cr[i] = 0;
        }
        while (true) {
            while (cr[3] != 0x5b) // search for timestamp [
            {
                cr[0] = cr[1];
                cr[1] = cr[2];
                cr[2] = cr[3];
                offset++;
                cr[3] = byteStream[offset] & 0xff;

                //FLOP:获取AVF录像内的国家信息
                if ("d3".equals(Integer.toHexString(byteStream[offset] & 0xff))
                        && "d3".equals(Integer.toHexString(byteStream[offset + 1] & 0xff))
                        && !"d3".equals(Integer.toHexString(byteStream[offset - 1] & 0xff))
                        ) {
                    for (int i = 0; i < CountryUtil.strCountryName.length; i++) {
                        if (CountryUtil.strCountryName[i][2].equals(Integer.toHexString(byteStream[offset + 4] & 0xff)))
                        {
                            CountryUtil.setIndex(i);
                            i=CountryUtil.strCountryName.length;//退出当前内循环
                        }
                    }
                }

            }
            cr[0] = cr[1];
            cr[1] = cr[2];
            cr[2] = cr[3];
            offset++;
            cr[3] = byteStream[offset] & 0xff;
            if (cr[3] - 47 == mode) {
                break;
            }
        }
        if (cr[0] != 17 && cr[0] != 127) {
            return VideoCommon.errorVideo(rawVideoBean, "qm不正确");
        }
        qm = (cr[0] == 17 ? 1 : 0);
        offset++;
        int i = 0;
        int tsS = offset;
        while (i < 100) {
            offset++;
            // 0x7c |
            if ((timestamp[i++] = byteStream[offset] & 0xff) == 0x7c) {
                timestamp[--i] = 0;
                break;
            }
        }
        int tsE = offset;
        ts = new String(byteStream, tsS + 1, tsE - tsS - 1);
        while ((int) (byteStream[offset] & 0xff) != 0x5d) {
            offset++;
        }
        for (i = 0; i < 7; ++i) {
            cr[i] = 0;
        }
        while (cr[2] != 1 || cr[1] > 1) // cr[2]=time=1; cr[1]=position_x div
        // 256<=30*16 div 256 = 1
        {
            cr[0] = cr[1];
            cr[1] = cr[2];
            offset++;
            cr[2] = byteStream[offset] & 0xff;
        }
        for (i = 3; i < 8; ++i) {
            offset++;
            cr[i] = byteStream[offset] & 0xff;
        }
        // events
        List<RawEventDetailBean> lst = new ArrayList<RawEventDetailBean>();
        int cur = 0;
        while (true) {
            RawEventDetailBean bean = new RawEventDetailBean();
            bean.setMouseType(cr[0] & 0xff);
            bean.setCur(cur);
            bean.setX((int) (cr[1] & 0xff) * 256 + (cr[3] & 0xff));
            bean.setY((int) (cr[5] & 0xff) * 256 + (cr[7] & 0xff));
            bean.setSec((int) (cr[6] & 0xff) * 256 + (cr[2] & 0xff) - 1);
            bean.setHun((int) cr[4] & 0xff);
            bean.setEventTime((double) (bean.getSec()) + (double) (bean.getHun()) / 100.0d);
            lst.add(bean);
            if (bean.getSec() < 0) {
                break;
            }
            for (i = 0; i < 8; ++i) {
                offset++;
                cr[i] = byteStream[offset] & 0xff;
            }
            ++cur;
        }
        // let's find player's name
        for (i = 0; i < 3; ++i) {
            cr[i] = 0;
        }
        // cs=
        while (cr[0] != 'c' || cr[1] != 's' || cr[2] != '=') {
            cr[0] = cr[1];
            cr[1] = cr[2];
            offset++;
            cr[2] = byteStream[offset] & 0xff;
        }
        if (fs == 0) {
            for (i = 0; i < cur; ++i) {
                offset++;
                lst.get(i).setThs(byteStream[offset] & 0xF);
            }
            for (i = 0; i < 17; ++i) {
                offset++;
            }
            while ((int) (byteStream[offset] & 0xff) != 13) {
                offset++;
            }
        } else {
            for (i = 0; i < 17; ++i) {
                offset++;
            }
        }
        offset++;
        // 下面是skin 签名 和 版本
        int next = 0;
        // real time
        while (byteStream[offset] != 13) {
            offset++;
        }
        // skin
        while (byteStream[offset] != 0x3a) {
            offset++;
        }
        offset++;
        int nt = offset;
        // skin
        while (byteStream[nt + next] != 13) {
            offset++;
            next++;
        }
        String skin = new String(byteStream, nt, next);
        offset++;
        nt = offset;
        next = 0;
        // skin
        while (byteStream[nt + next] != 13) {
            offset++;
            next++;
        }

        //FLOP:根据不同国家字符编码设置用户标识
        String userID = null;
        try {
            //默认采用中文字符编码
            if(CountryUtil.strCountryName[CountryUtil.getIndex()][3]!=null){
                userID = new String(byteStream, nt, next, CountryUtil.strCountryName[CountryUtil.getIndex()][3]);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (userID == null) {
            userID = new String(byteStream, nt, next);
        }

        String ver = new String(byteStream, offset + 21, 6);
        // 设定版本
        rawBaseBean.setVersion(ver);
        // 设定用户id
        rawBaseBean.setPlayer(userID);
        // 设定时间戳
        rawBaseBean.setTimeStamp(convertTimeStamp(ts));
        // 设定级别
        rawBaseBean.setLevel(String.valueOf(mode));
        // 设定宽
        rawBaseBean.setWidth(String.valueOf(w));
        // 设定高
        rawBaseBean.setHeight(String.valueOf(h));
        // 设定雷数
        rawBaseBean.setMines(String.valueOf(m));
        // 设定皮肤
        rawBaseBean.setSkin(skin);
        if (l == 0) {
            // 设定模式
            rawBaseBean.setMode(String.valueOf(mode));
        } else {
            rawBaseBean.setMode("Lucky");
            rawBaseBean.setLuckLibrary(String.valueOf(ll));
            rawBaseBean.setLuckSolver(String.valueOf(ls));
        }
        rawBaseBean.setQm(String.valueOf(qm));
        rawVideoBean.setRawBaseBean(rawBaseBean);
        RawBoardBean rawBoardBean = new RawBoardBean();
        rawBoardBean.setHeight(h);
        rawBoardBean.setWidth(w);
        rawBoardBean.setBoard(board);
        rawBoardBean.setCbBoard(cbBoard);
        rawBoardBean.setCells(cells);
        rawBoardBean.setMines(m);
        rawVideoBean.setRawBoardBean(rawBoardBean);
        rawVideoBean.setRawEventDetailBean(lst);

        rawVideo = rawVideoBean;

        return rawVideoBean;
    }

    /**
     * 转换arbiter时间戳
     *
     * @param temp temp
     * @return temp
     */
    private String convertTimeStamp(String temp) {
        String ts = "";
        String[] tsTemp = temp.split("\\.");
        // 正常情况下分成4块，但科长录像特殊 分成3块
        int tsTempLength = tsTemp.length;
        if (tsTempLength == 4 || tsTempLength == 3) {
            String day = "";
            String month = "";
            String year = "";
            String time = "";
            // 4块场合 日月年 时分秒毫秒
            if (tsTempLength == 4) {
                day = tsTemp[0];
                month = tsTemp[1];
                year = tsTemp[2];
                time = tsTemp[3];

            } else if (tsTempLength == 3) {
                String daymonth = tsTemp[0];
                day = daymonth.substring(0);
                month = daymonth.substring(1, daymonth.length() - 1);
                year = tsTemp[1];
                time = tsTemp[2];
            }
            String[] timeTemp = time.split("\\:");
            String hour = timeTemp[0];
            String minute = timeTemp[1];
            String second = timeTemp[2];
            String misecond = timeTemp[3];
            int misSecSize = misecond.length();
            // misecond可能3位或4位
            int misec = Integer.valueOf(misecond) * (misSecSize == 3 ? 10 : 1);
            // int month,year,year1,year2,day,hour,minute,second;
            ts = (String.format("%02d/%02d/%02d %02d:%02d:%02d.%04d",
                    new Object[]{Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day), Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second), misec}));

        } else {
            ts = "error";
        }
        return ts;
    }
}

