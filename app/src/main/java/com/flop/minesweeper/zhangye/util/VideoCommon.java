package com.flop.minesweeper.zhangye.util;

import com.flop.minesweeper.zhangye.bean.BoardBean;
import com.flop.minesweeper.zhangye.bean.EventBean;
import com.flop.minesweeper.zhangye.bean.RawBaseBean;
import com.flop.minesweeper.zhangye.bean.RawBoardBean;
import com.flop.minesweeper.zhangye.bean.RawVideoBean;
import com.flop.minesweeper.zhangye.bean.VideoDisplayBean;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件解析接口
 *
 * @author zhangYe
 * @version 2013-11-3
 */
public class VideoCommon
{
    /**
     * 转换录像
     *
     * @param rawVideoBean
     *            传入录像bean
     * @param videoDisplayBean
     *            转换录像bean
     */
    public static void convertVideoDisplay(RawVideoBean rawVideoBean, VideoDisplayBean videoDisplayBean)
    {
        if (rawVideoBean.isCheckFlag())
        {
            // 设定基本信息
            setBaseInfo(rawVideoBean, videoDisplayBean);
            // 设定board信息
            setBoardInfo(rawVideoBean, videoDisplayBean);
            // 设定event信息
            setEventInfo(rawVideoBean, videoDisplayBean);
            // 设定计算信息
            setCalcInfo(rawVideoBean, videoDisplayBean);
        }
        else
        {
            fillBean(videoDisplayBean, "ZZV5");
        }
    }

    /**
     * 设定问号模式
     *
     * @param markFlag
     *            问号标记值
     * @return string 问号标记对应值
     */
    public static String setMarkFlag(String markFlag)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("0", "UNMARK");
        map.put("1", "MARK");
        return map.get(markFlag);
    }
    public static void fillBean(VideoDisplayBean bean, String str)
    {
        Class<?> classMethod = null;
        try
        {
            String classNameStr = "com.zy.minesweeperStudio.bean.VideoDisplayBean";
            classMethod = Class.forName(classNameStr);
            Method[] methods = classMethod.getMethods();
            for (Method method : methods)
            {
                String name = method.getName();
                if (!"setName".equals(name) && name.startsWith("set"))
                {
                    Method setMethod = classMethod.getMethod(name, String.class);
                    setMethod.invoke(bean, str);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 设定模式
     *
     * @param mode
     *            模式值
     * @param type
     *            类型
     * @return string 模式对应值
     */
    public static String setMode(String mode, String type)
    {
        Map<String, String> map = new HashMap<String, String>();
        if (type.equals("clone"))
        {
            map.put("0", "null");
            map.put("1", "classic");
            map.put("2", "density");
            map.put("3", "UPK");
            map.put("4", "cheat");
        }
        else
        {
            map.put("0", "null");
            map.put("1", "classic");
            map.put("2", "classic");
            map.put("3", "classic");
            map.put("4", "density");
        }
        return map.get(mode);
    }

    /**
     * 设定级别
     *
     * @param level
     *            级别值
     * @return string 级别对应值
     */
    public static String setLevel(String level)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("0", "null");
        map.put("1", "Beg");
        map.put("2", "Int");
        map.put("3", "Exp");
        map.put("4", "custom");
        map.put("5", "custom");
        return map.get(level);
    }

    /**
     * 出现错误情况
     *
     * @param rawVideoBean
     *            传入录像bean
     * @param errMessage
     *            出错信息
     * @return rawVideoBean 传入录像bean
     */
    public static RawVideoBean errorVideo(RawVideoBean rawVideoBean, String errMessage)
    {
        rawVideoBean.setCheckFlag(false);
        rawVideoBean.setErrorMessage(errMessage);
        return rawVideoBean;
    }

    /**
     * 设置基本信息
     *
     * @param rawVideoBean
     *            传入录像bean
     * @param videoDisplayBean
     *            转换录像bean
     */
    private static void setBaseInfo(RawVideoBean rawVideoBean, VideoDisplayBean videoDisplayBean)
    {
        RawBaseBean rawBaseBean = rawVideoBean.getRawBaseBean();
        // 设定程序名称
        videoDisplayBean.setMvfType(rawBaseBean.getProgram());
        // 设定程序版本
        videoDisplayBean.setVersion(rawBaseBean.getVersion());
        // 设定用户标识
        videoDisplayBean.setUserID(rawBaseBean.getPlayer());
        // 设定时间
        videoDisplayBean.setDate(rawBaseBean.getTimeStamp());
        // 设定级别
        videoDisplayBean.setLevel(setLevel(rawBaseBean.getLevel()));
        // 设定模式
        videoDisplayBean.setMode(setMode(rawBaseBean.getMode(), rawBaseBean.getProgram()));
        // 设定问号模式
        videoDisplayBean.setMarkFlag(setMarkFlag(rawBaseBean.getQm()));
    }

    /**
     * 根据board读取board信息
     *
     * @param rawVideoBean
     *            传入录像bean
     * @param videoDisplayBean
     *            转换录像bean
     */
    private static void setBoardInfo(RawVideoBean rawVideoBean, VideoDisplayBean videoDisplayBean)
    {
        RawBoardBean rawBoardBean = rawVideoBean.getRawBoardBean();
        // 根据board 解析board内容
        BoardBean boardBean = BoardCommon.getBoardBean(rawBoardBean.getWidth(), rawBoardBean.getHeight(), rawBoardBean.getMines(), rawBoardBean.getCbBoard());
        videoDisplayBean.setBbbv(String.valueOf(boardBean.getBbbv()));
        videoDisplayBean.setZini(String.valueOf(boardBean.getZini()));
        videoDisplayBean.setHzini(String.valueOf(boardBean.getHzini()));
        videoDisplayBean.setNum0(String.valueOf(boardBean.getNum0()));
        videoDisplayBean.setNum1(String.valueOf(boardBean.getNum1()));
        videoDisplayBean.setNum2(String.valueOf(boardBean.getNum2()));
        videoDisplayBean.setNum3(String.valueOf(boardBean.getNum3()));
        videoDisplayBean.setNum4(String.valueOf(boardBean.getNum4()));
        videoDisplayBean.setNum5(String.valueOf(boardBean.getNum5()));
        videoDisplayBean.setNum6(String.valueOf(boardBean.getNum6()));
        videoDisplayBean.setNum7(String.valueOf(boardBean.getNum7()));
        videoDisplayBean.setNum8(String.valueOf(boardBean.getNum8()));
        int numall = boardBean.getNum1() * 1 + boardBean.getNum2() * 2 +
                boardBean.getNum3()  * 3 + boardBean.getNum4()  * 4
                + boardBean.getNum5()  * 5 + boardBean.getNum6() * 6
                + boardBean.getNum7() * 7 + boardBean.getNum8() * 8;
        videoDisplayBean.setNumAll(String.valueOf(numall));
        videoDisplayBean.setOpenings(String.valueOf(boardBean.openings));
        videoDisplayBean.setIslands(String.valueOf(boardBean.islands));
    }

    /**
     * 根据event读取event信息
     *
     * @param rawVideoBean
     *            传入录像bean
     * @param videoDisplayBean
     *            转换录像bean
     */
    private static void setEventInfo(RawVideoBean rawVideoBean, VideoDisplayBean videoDisplayBean)
    {
        EventBean eventBean = EventCommon.getEventBean(rawVideoBean,videoDisplayBean);
        videoDisplayBean.setBoom(eventBean.isBoom()?"boom":"");
        videoDisplayBean.setLclicks(String.valueOf(eventBean.getL()));
        videoDisplayBean.setDclicks(String.valueOf(eventBean.getD()));
        videoDisplayBean.setRclicks(String.valueOf(eventBean.getR()));
        videoDisplayBean.setCloneR(String.valueOf(eventBean.getR() + eventBean.getCloneR()));
        int allClicks = eventBean.getL() + eventBean.getD() + eventBean.getR();
        videoDisplayBean.setAllClicks(String.valueOf(allClicks));
        videoDisplayBean.setFlags(String.valueOf(eventBean.getFlags()));
        videoDisplayBean.setTime(String.format("%.3f", new Object[] { eventBean.getSaoleiTime() }));
        videoDisplayBean.setDistance(String.format("%.3f", new Object[] { eventBean.getDistance() }));
        videoDisplayBean.setHold(String.valueOf(eventBean.getHolds()));
        videoDisplayBean.setEventSize(String.valueOf(eventBean.getEventSize()));
        videoDisplayBean.setMvsize(String.valueOf(eventBean.getMvsize()));
        videoDisplayBean.setLcsize(String.valueOf(eventBean.getLcsize()));
        videoDisplayBean.setLrsize(String.valueOf(eventBean.getLrsize()));
        videoDisplayBean.setRcsize(String.valueOf(eventBean.getRcsize()));
        videoDisplayBean.setRrsize(String.valueOf(eventBean.getRrsize()));
        videoDisplayBean.setMcsize(String.valueOf(eventBean.getMcsize()));
        videoDisplayBean.setMrsize(String.valueOf(eventBean.getMrsize()));
        videoDisplayBean.setWasteflags(String.valueOf(eventBean.getWastedflags()));
        videoDisplayBean.setFirstLx(String.valueOf(eventBean.getFirstlx()));
        videoDisplayBean.setFirstLy(String.valueOf(eventBean.getFirstly()));
        videoDisplayBean.setMisscl(String.valueOf(eventBean.getMisscl()));
        videoDisplayBean.setOutcl(String.valueOf(eventBean.getOutcl()));

        videoDisplayBean.setMissclL0(String.valueOf(eventBean.getMissclL0()));

        videoDisplayBean.setMissclL1(String.valueOf(eventBean.getMissclL1()));
        videoDisplayBean.setMissclD0(String.valueOf(eventBean.getMissclD0()));
        videoDisplayBean.setMissclD1(String.valueOf(eventBean.getMissclD1()));
        videoDisplayBean.setMissclD2(String.valueOf(eventBean.getMissclD2()));
        videoDisplayBean.setMissclR(String.valueOf(eventBean.getMissclR()));
        videoDisplayBean.setOutclL(String.valueOf(eventBean.getOutclL()));
        videoDisplayBean.setOutclD(String.valueOf(eventBean.getOutclD()));
        videoDisplayBean.setOutclR(String.valueOf(eventBean.getOutclR()));
        videoDisplayBean.setClickE(String.valueOf(allClicks-eventBean.getMisscl()-eventBean.getOutcl()));
        videoDisplayBean.setMaxHit(String.valueOf(eventBean.getMaxHit()));
        videoDisplayBean.setClickEL(String.valueOf(eventBean.getL()-eventBean.getMissclL0()-eventBean.getMissclL1()-eventBean.getOutclL()));
        videoDisplayBean.setClickED(String.valueOf(eventBean.getD()-eventBean.getMissclD0()-eventBean.getMissclD1()-eventBean.getMissclD2()-eventBean.getOutclD()));
        videoDisplayBean.setClickER(String.valueOf(eventBean.getR()-eventBean.getMissclR()-eventBean.getOutclR()));
    }

    /**
     * 得出计算参数
     *
     * @param rawVideoBean
     *            传入录像bean
     * @param videoDisplayBean
     *            转换录像bean
     */
    private static void setCalcInfo(RawVideoBean rawVideoBean, VideoDisplayBean videoDisplayBean)
    {
        // 风格(非严格判定)
        if ("0".equals(videoDisplayBean.getRclicks()))
        {
            videoDisplayBean.setStyle("1");
        }
        else
        {
            videoDisplayBean.setStyle("");
        }
        // 3bvs =3bv/time
        videoDisplayBean.setBbbvs(String.format("%.3f", new Object[] { Double.valueOf(videoDisplayBean.getBbbv()) / (Double.valueOf(videoDisplayBean.getTime())) }));
        // clicks =allclick/time
        videoDisplayBean.setClicks(String.format("%.3f", new Object[] { Double.valueOf(videoDisplayBean.getAllClicks()) / (Double.valueOf(videoDisplayBean.getTime())) }));
        // rqp =(time*(time+1))/3bv
        videoDisplayBean.setRqp(String.format("%.3f",
                new Object[] { (Double.valueOf(videoDisplayBean.getTime()) + 1.0D) * (Double.valueOf(videoDisplayBean.getTime())) / Double.valueOf(videoDisplayBean.getBbbv()) }));
        // ioe =3bv/allclick
        videoDisplayBean.setIoe(String.format("%.3f", new Object[] { (Double.valueOf(videoDisplayBean.getBbbv()) / Double.valueOf(videoDisplayBean.getAllClicks())) }));
        // dispeed=distance/time
        videoDisplayBean.setDisSpeed(String.format("%.3f", new Object[] { (Double.valueOf(videoDisplayBean.getDistance())) / Double.valueOf(videoDisplayBean.getTime()) }));
        // disbv=distance/3bv
        videoDisplayBean.setDisBv(String.format("%.3f", new Object[] { (Double.valueOf(videoDisplayBean.getDistance())) / Double.valueOf(videoDisplayBean.getBbbv()) }));
        // disNum=distance/numAll
        videoDisplayBean.setDisNum(String.format("%.3f", new Object[] { (Double.valueOf(videoDisplayBean.getDistance())) / Double.valueOf(videoDisplayBean.getNumAll()) }));
        // hzoe=hzini/allclick
        videoDisplayBean.setHzoe(String.format("%.3f", new Object[] { (Double.valueOf(videoDisplayBean.getHzini())) / Double.valueOf(videoDisplayBean.getAllClicks()) }));
        // zoe=zini/allclick
        videoDisplayBean.setZoe(String.format("%.3f", new Object[] { (Double.valueOf(videoDisplayBean.getZini())) / Double.valueOf(videoDisplayBean.getAllClicks()) }));
        // numspeed=numall/time
        videoDisplayBean.setNumSpeed(String.format("%.3f", new Object[] { (Double.valueOf(videoDisplayBean.getNumAll())) / Double.valueOf(videoDisplayBean.getTime()) }));
        // hzinis=hzini/time
        videoDisplayBean.setHzinis(String.format("%.3f", new Object[] { (Double.valueOf(videoDisplayBean.getHzini())) / Double.valueOf(videoDisplayBean.getTime()) }));
        // zinis=zini/time
        videoDisplayBean.setZinis(String.format("%.3f", new Object[] { (Double.valueOf(videoDisplayBean.getZini())) / Double.valueOf(videoDisplayBean.getTime()) }));
        // clickes=clicke/time
        videoDisplayBean.setClickEs(String.format("%.3f", new Object[] { (Double.valueOf(videoDisplayBean.getClickE())) / Double.valueOf(videoDisplayBean.getTime()) }));
        // occam=3bvs*ioe
        videoDisplayBean.setOccam(String.format("%.3f", new Object[] { (Double.valueOf(videoDisplayBean.getBbbvs())) * Double.valueOf(videoDisplayBean.getIoe()) }));
        // qg=(time^1.7)/3bv
        videoDisplayBean.setQg(String.format("%.3f", new Object[] { Double.valueOf((Math.pow(Double.valueOf(videoDisplayBean.getTime()), 1.7D)) / Double.valueOf(videoDisplayBean.getBbbv())) }));
        // corr=clicke/click
        videoDisplayBean.setCorr(String.format("%.3f", new Object[] { (Double.valueOf(videoDisplayBean.getClickE())) / Double.valueOf(videoDisplayBean.getAllClicks()) }));
        // thrp=3bv/clicke
        videoDisplayBean.setThrp(String.format("%.3f", new Object[] { Double.valueOf(videoDisplayBean.getBbbv()) / Double.valueOf(videoDisplayBean.getClickE()) }));
        String[] namestr= videoDisplayBean.getName().split("\\_");
        String[] bhid=namestr[1].split("\\.");
        videoDisplayBean.setBh(namestr[0]);
        videoDisplayBean.setBhid(bhid[0]);
    }
}
