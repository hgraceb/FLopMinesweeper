package com.flop.minesweeper.zhangye.util;

import static com.flop.minesweeper.variable.Constant.rawVideo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.flop.minesweeper.zhangye.bean.CellBean;
import com.flop.minesweeper.zhangye.bean.CellsBean;
import com.flop.minesweeper.zhangye.bean.RawBaseBean;
import com.flop.minesweeper.zhangye.bean.RawBoardBean;
import com.flop.minesweeper.zhangye.bean.RawEventDetailBean;
import com.flop.minesweeper.zhangye.bean.RawVideoBean;
import com.flop.minesweeper.zhangye.bean.VideoCheckBean;
import com.flop.minesweeper.zhangye.bean.VideoDisplayBean;

/**
 * 拆分方法该方法主要内容为对rmv文件解析 关于rmv 介绍 rmv是用VSweeper 扫的，一般用在线上比赛上 早期版本后缀名为umv
 *
 * @author zhangye
 * @version 2013-1120
 */
public class RmvUtil implements VideoUtil
{

    /**
     * 类型rmv
     */
    private String TYPE_RMV = "rmv";
    /**
     * 版本2.0
     */
    private String VERSION2 = "2.0";

    /**
     * 检查录像版本 <br/>
     *
     * @param byteStream
     *            文件流
     * @return VideoCheckBean
     * @see <a href="http://www.minesweeper.info/forum/viewtopic.php?f=26&t=86"> http://www.minesweeper.info/forum/viewtopic.php?f=26&t=86</a>
     */

    public VideoCheckBean checkVersion(byte[] byteStream)
    {
        VideoCheckBean bean = new VideoCheckBean();
        // String videoType = new String(byteStream, 1, 4);
        bean.videoType = TYPE_RMV;
        bean.videoVersion = VERSION2;
        return bean;
    }

    /**
     * 解析录像版本 扫雷网录像以97为主 偶见97之前版本
     *
     * @param byteStream
     *            字节流
     * @param bean
     *            bean
     */
    public void analyzeVideo(byte[] byteStream, VideoDisplayBean bean) {
        RawVideoBean rawVideoBean = convertRawVideo(byteStream);
        VideoCommon.convertVideoDisplay(rawVideoBean, bean);
        rawVideo = rawVideoBean;
    }

    /**
     * 解析录像版本 头4位为版本 正常的显示为 *rmv（0x2A 0x72 0x6D 0x76） 之后2为是录像type 正常的为 1(0x00 0x01)
     */

    public RawVideoBean convertRawVideo(byte[] byteStream)
    {
        RawVideoBean rawVideoBean = new RawVideoBean();
        RawBaseBean rawBaseBean = new RawBaseBean();
        rawBaseBean.setProgram("rmv");
        rawVideoBean.setRawBaseBean(rawBaseBean);
        String checkHead = new String(byteStream, 0, 4);
        // Check first 4 bytes of header is *rmv
        if (!"*rmv".equals(checkHead))
        {
            return VideoCommon.errorVideo(rawVideoBean, "head不正确");
        }
        // The getint2 function reads 2 bytes at a time
        // In legitimate videos byte 4=0 and byte 5=1, getint2 sum is thus 1
        int type1 = byteStream[0x04] & 0xFF;
        int type2 = byteStream[0x05] & 0xFF;
        int type = type1 * 256 + type2;
        if (type != 1)
        {
            return VideoCommon.errorVideo(rawVideoBean, "type不正确");
        }
        int fs1 = byteStream[0x06] & 0xFF;
        int fs2 = byteStream[0x07] & 0xFF;
        int fs3 = byteStream[0x08] & 0xFF;
        int fs4 = byteStream[0x09] & 0xFF;
        long fs = fs1 * 65536 + fs2 * 16777216 + fs3 + fs4 * 256;
        // result_string_size
        int rs1 = byteStream[0x0A] & 0xFF;
        int rs2 = byteStream[0x0B] & 0xFF;
        int result_string_size = rs1 * 256 + rs2;
        // version_info_size
        int vi1 = byteStream[0x0C] & 0xFF;
        int vi2 = byteStream[0x0D] & 0xFF;
        int version_info_size = vi1 * 256 + vi2;
        // // play_info_size
        int pi1 = byteStream[0x0E] & 0xFF;
        int pi2 = byteStream[0x0F] & 0xFF;
        int pi = pi1 * 256 + pi2;
        // board_size
        int bo1 = byteStream[0x10] & 0xFF;
        int bo2 = byteStream[0x11] & 0xFF;
        int bo = bo1 * 256 + bo2;
        // preflags_size
        int pf1 = byteStream[0x12] & 0xFF;
        int pf2 = byteStream[0x13] & 0xFF;
        int pf = pf1 * 256 + pf2;
        // properties_size
        int pp1 = byteStream[0x14] & 0xFF;
        int pp2 = byteStream[0x15] & 0xFF;
        int pp = pp1 * 256 + pp2;
        // vid_size
        int vid1 = byteStream[0x16] & 0xFF;
        int vid2 = byteStream[0x17] & 0xFF;
        int vid3 = byteStream[0x18] & 0xFF;
        int vid4 = byteStream[0x19] & 0xFF;
        long vid = vid1 * 65536 + vid2 * 16777216 + vid3 + vid4 * 256;
        // cs_size
        int cs1 = byteStream[0x1A] & 0xFF;
        int cs2 = byteStream[0x1B] & 0xFF;
        int cs = cs1 * 256 + cs2;
        // 0x1C // newline
        int offSet = 0x1C;
        String bbbvString;
        String timeStamp;
        // Length of result_string_size starts 3 bytes before 'LEVEL' and ends on the '#' before Version
        // Version 2.2 was first to have a full length header
        // Earlier versions could have maximum header length of 35 bytes if Intermediate and 9999.99
        // This means it is Version 2.2 or later so we want to parse more of the header
        if (result_string_size > 35)
        {
            // Reads last part of string after '3BV'
            for (int i = 0; i < result_string_size - 32; ++i)
            {
                offSet++;
            }
            // char[] bbbv= new char[](byteStream, offSet+1, 3);
            char[] bbbv = new char[3];
            for (int i = 0; i < 3; ++i)
            {
                bbbv[i] = (char) (byteStream[++offSet] & 0xFF);
            }
            bbbvString = String.valueOf(bbbv).replaceAll("[^0-9]", "");
            // Throw away some bytes to get to Timestamp
            for (int i = 0; i < 16; ++i)
            {
                offSet++;
            }
            // Fetch Timestamp
            timeStamp = new String(byteStream, ++offSet, 10);
            offSet += 9;
        }
        // Release 2 beta and earlier versions do not have 3bv or Timestamp
        else
        {
            bbbvString = "";
            timeStamp = "";
            for (int i = 0; i < result_string_size - 3; ++i)
            {
                offSet++;
            }
        }

        // Throw away the 2 bytes '# ' before 'Vienna...'
        offSet += 2;
        String program = new String(byteStream, ++offSet, 18);
        offSet += (17);
        // Throw away the ' - '
        offSet += 3;
        // Put remainder of version string into a new string
        String version_info = new String(byteStream, ++offSet, version_info_size - 22);
        offSet += (version_info_size - 23);
        ++offSet;
        int num_player_info1 = byteStream[++offSet] & 0xFF;
        int num_player_info2 = byteStream[++offSet] & 0xFF;
        int num_player_info = num_player_info1 * 256 + num_player_info2;
        String name = "";
        // Fetch Player fields (name, nick, country, token) if they exist
        // These last 3 fields were defined in Viennasweeper 3.1 RC1
        if (num_player_info > 0)
        {
            int name_length2 = byteStream[++offSet] & 0xFF;
            name = new String(byteStream, ++offSet, name_length2);
            offSet += (name_length2 - 1);
        }
        String nick = "";
        if (num_player_info > 1)
        {
            int nick_length = byteStream[++offSet] & 0xFF;
            nick = new String(byteStream, ++offSet, nick_length);
            offSet += (nick_length - 1);
        }
        String country = "";
        if (num_player_info > 2)
        {
            int country_length = byteStream[++offSet] & 0xFF;
            country = new String(byteStream, ++offSet, country_length);
            offSet += (country_length - 1);
        }
        String token = "";
        if (num_player_info > 2)
        {
            int token_length = byteStream[++offSet] & 0xFF;
            token = new String(byteStream, ++offSet, token_length);
            offSet += (token_length - 1);
        }

        // Throw away next 4 bytes
        offSet += 4;
        // Get board size and Mine details
        // Next byte is w so 8, 9 or 1E
        int w = byteStream[++offSet] & 0xFF;
        // Next byte is h so 8, 9 or 10
        int h = byteStream[++offSet] & 0xFF;
        // Next two bytes are number of mines
        int m1 = byteStream[++offSet] & 0xFF;
        int m2 = byteStream[++offSet] & 0xFF;
        int m = m1 * 256 + m2;
        int[] board = new int[w * h];
        List<Integer> sboard = new ArrayList<>();
        CellBean[] cbBoard = new CellBean[w * h];
        CellsBean[] cells = new CellsBean[(h + 2) * (w + 2)];
        for (int i = 0; i < (h + 2) * (w + 2); i++)
        {
            cells[i] = new CellsBean(0);
        }
        for (int i = 0; i < w * h; ++i)
        {
            int temp = 0;
            sboard.add(temp);
            cbBoard[i] = new CellBean();
            cbBoard[i].mine = (cbBoard[i].opened = cbBoard[i].flagged = cbBoard[i].opening = cbBoard[i].opening2 = 0);
        }
        for (int i = 0; i < m; ++i)
        {
            //x y
            int col = byteStream[++offSet] & 0xFF;
            int row = byteStream[++offSet] & 0xFF;
            //System.out.println("x"+col);
            //System.out.println("y"+row);
            board[row * w + col] = 1;
            // 分别为x坐标 和y坐标

            sboard.set(row * w + col, 1);
            int pos = (col) * h + row;
            cbBoard[pos].mine = 1;
            int posbean = (row + 1) * (w + 2) + col + 1;
            cells[posbean].what = 9;
        }
        List<RawEventDetailBean> lst = new ArrayList<>();
        int cur = 0;
        String[] eventTypeList = { "", "mv", "lc", "lr", "rc", "rr", "mc", "mr", "", "pressed", "pressedqm", "closed", "questionmark", "flag", "blast", "lost", "won", "nonstandard", "number0",
                "number1", "number2", "number3", "number4", "number5", "number6", "number7", "number8", "blast" };
        // Check number of flags placed before game started
        if (pf > 0)
        {
            int pfN1 = byteStream[++offSet] & 0xFF;
            int pfN2 = byteStream[++offSet] & 0xFF;
            int pfN = pfN1 * 256 + pfN2;
            for (int i = 0; i < pfN; ++i)
            {
                int col = byteStream[++offSet] & 0xFF;
                int row = byteStream[++offSet] & 0xFF;
                RawEventDetailBean bean = new RawEventDetailBean();
                bean.setRmvMouseType(eventTypeList[4]);
                bean.setCur(cur);
                bean.setX(8 + 16 * col);
                bean.setY(8 + 16 * row);
                bean.setSec(0);
                bean.setHun(0);
                bean.setEventTime(new BigDecimal((bean.getSec()) + "." + (String.format("%02d", bean.getHun()))).doubleValue());
                lst.add(bean);
                cur++;
                RawEventDetailBean bean2 = new RawEventDetailBean();
                bean2.setRmvMouseType(eventTypeList[5]);
                bean2.setCur(cur);
                bean2.setX(8 + 16 * col);
                bean2.setY(8 + 16 * row);
                bean2.setSec(0);
                bean2.setHun(0);
                bean2.setEventTime(new BigDecimal((bean.getSec()) + "." + (String.format("%02d", bean.getHun()))).doubleValue());
                lst.add(bean2);
                cur++;
            }
        }
        // Value 1 if Questionmarks used, otherwise 0
        int qm = byteStream[++offSet] & 0xFF;
        // Value 1 if no Flags were used, otherwise 0
        int nf = byteStream[++offSet] & 0xFF;
        // Value 0 for Classic, 1 UPK, 2 Cheat, 3 Density
        int mode = byteStream[++offSet] & 0xFF;
        // Value 0 for Beg, 1 Int, 2 Exp, 3 Custom
        int level = byteStream[++offSet] & 0xFF;
        for (int i = 4; i < pp; ++i)
        {
            ++offSet;
        }
        int ic = pp;
        int k = 3;
        boolean firstflag = true;
        while (true)
        {
            int event = byteStream[++offSet] & 0xFF;
            RawEventDetailBean bean2 = new RawEventDetailBean();
            // Get next 4 bytes containing time of event
            if (event == 0)
            {
                int end1 = byteStream[++offSet] & 0xFF;
                int end2 = byteStream[++offSet] & 0xFF;
                int end3 = byteStream[++offSet] & 0xFF;
                int end4 = byteStream[++offSet] & 0xFF;
                long end = end1 * 65536 + end2 * 16777216 + end3 + end4 * 256;
                ic += 4;
            }

            // Get mouse event (3 bytes time, 1 wasted, 2 width, 2 height)
            else if (event <= 7)
            {
                ic += 8;
                int time1 = byteStream[++offSet] & 0xFF;
                int time2 = byteStream[++offSet] & 0xFF;
                int time3 = byteStream[++offSet] & 0xFF;
                long time = time3 + time2 * 256 + time1 * 65536;
                ++offSet;
                int x1 = byteStream[++offSet] & 0xFF;
                int x2 = byteStream[++offSet] & 0xFF;
                int x = x1 * 256 + x2 - 12;
                int y1 = byteStream[++offSet] & 0xFF;
                int y2 = byteStream[++offSet] & 0xFF;
                int y = y1 * 256 + y2 - 56;
                // Viennasweeper does not record clicks before timer starts
                // LR starts timer so the first LC is missed in the video file
                // This code generates the missing LC in that case
                // In other cases it generates a ghost event thus event[0] is empty
                if (firstflag)
                {
                    firstflag = false;
                    RawEventDetailBean beanfirst = new RawEventDetailBean();
                    beanfirst.setRmvMouseType(eventTypeList[2]);
                    beanfirst.setCur(cur);
                    beanfirst.setX(x);
                    beanfirst.setY(y);
                    beanfirst.setSec(0);
                    beanfirst.setHun(0);
                    beanfirst.setRmvTime(time);
                    lst.add(beanfirst);
                    cur++;
                }
                RawEventDetailBean beanfirst = new RawEventDetailBean();
                beanfirst.setRmvMouseType(eventTypeList[event]);
                beanfirst.setCur(cur);
                beanfirst.setX(x);
                beanfirst.setY(y);
                beanfirst.setSec(0);
                beanfirst.setHun(0);
                beanfirst.setRmvTime(time);
                lst.add(beanfirst);
                cur++;
            }
            else if (event == 8)
            {
                System.out.println("Invalid event");
            }
            else if (event <= 14 || (event >= 18 && event <= 27))
            {
                ic += 2;
                RawEventDetailBean beans = new RawEventDetailBean();
                beans.setRmvMouseType(eventTypeList[event]);
                beans.setCur(cur);
                int x = byteStream[++offSet] & 0xFF+1;
                int y = byteStream[++offSet] & 0xFF+1;
                beans.setX(x);
                beans.setY(y);
                cur++;
            }
            // Get game status (ie, 'won')
            else if (event <= 17)
            {
                RawEventDetailBean beans = new RawEventDetailBean();
                beans.setRmvMouseType(eventTypeList[event]);
                beans.setCur(cur);
                break;
            }
            else
            {
                System.out.println("Invalid event");
            }
        }
        if(version_info.contains("(")) {
            version_info=version_info.split("\\(")[0].trim();
        }
        if(version_info.endsWith("Copyright")) {
            version_info=version_info.replaceAll(" Copyright", "");
        }
        version_info=version_info.replaceAll("Vienna Minesweeper - ", "");
        RawBoardBean rawBoardBean = new RawBoardBean();
        rawBoardBean.setHeight(h);
        rawBoardBean.setWidth(w);
        // 设定版本
        rawBaseBean.setVersion(version_info);
        rawBaseBean.setPlayer(name);
        Timestamp timestamps=new Timestamp(Long.valueOf(timeStamp)*1000);
        String currentTimestampToString = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(timestamps);
        System.out.println(currentTimestampToString);
        rawBaseBean.setTimeStamp(currentTimestampToString);
        // 设定级别
        rawBaseBean.setLevel(String.valueOf(level+1));
        rawBaseBean.setMode(String.valueOf(mode+1));
        rawBaseBean.setQm(String.valueOf(qm));
        rawBoardBean.setBoard(sboard);
        rawBoardBean.setCbBoard(cbBoard);
        rawBoardBean.setCells(cells);
        rawBoardBean.setMines(m);
        rawVideoBean.setRawBoardBean(rawBoardBean);
        rawVideoBean.setRawEventDetailBean(lst);
       for(RawEventDetailBean bean:lst) {

            bean.setEventTime(BigDecimal.valueOf(bean.getRmvTime()).divide(new BigDecimal("1000")).doubleValue());
            //System.out.println(bean.getEventTime() +" "+bean.getRmvMouseType() +" "+  bean.getX() +" "+  bean.getY());
        }
        return rawVideoBean;
    }


    private String getEventName(int event)
    {
        String[] eventLst = new String[] { "", "mv", "lc", "lr", "rc", "rr", "mc", "mr", "", "pressed", "pressedqm", "closed", "questionmark", "flag", "blast", "boom", "won", "nonstandard", "number0",
                "number1", "number2", "number3", "number4", "number5", "number6", "number7", "number8", "blast" };
        return eventLst[event];
    }
}
