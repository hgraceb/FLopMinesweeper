package com.zy.minesweeper.base.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 文件检验BEAN
 *
 * @author zhangYe
 */
public class RawEventDetailBean implements Serializable {
    /**
     * UID
     */
    private static final long serialVersionUID = -2730987152467866530L;
    /**
     * 事件事件
     */
    private BigDecimal eventTime;
    private long rmvTime;
    /**
     * 事件类型
     */
    private String rmvMouseType;
    /**
    /**
     * 事件类型
     */
    private int mouseType;
    /**
     * 鼠标cur
     */
    private int cur;
    /**
     * 鼠标cur
     */
    private String mouse;
    /**
     * sec
     */
    private int sec;
    /**
     * hun
     */
    private int hun;
    /**
     * ths
     */
    private int ths;
    /**
     * x
     */
    private int x;
    /**
     * y
     */
    private int y;
    /**
     * qx
     */
    private int qx;
    /**
     * qy
     */
    private int qy;
    /**
     * info
     */
    private String info;
    /**
     * rb clone使用
     */
    private int rb;
    /**
     * mb clone使用
     */
    private int mb;
    /**
     * lb clone使用
     */
    private int lb;

    public BigDecimal getEventTime() {
        return eventTime;
    }

    public void setEventTime(BigDecimal eventTime) {
        this.eventTime = eventTime;
    }

    public String getMouse() {
        return mouse;
    }

    public void setMouse(String mouse) {
        this.mouse = mouse;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getQx() {
        return qx;
    }

    public void setQx(int qx) {
        this.qx = qx;
    }

    public int getQy() {
        return qy;
    }

    public void setQy(int qy) {
        this.qy = qy;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getMouseType() {
        return mouseType;
    }

    public void setMouseType(int mouseType) {
        this.mouseType = mouseType;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public int getHun() {
        return hun;
    }

    public void setHun(int hun) {
        this.hun = hun;
    }

    public int getCur() {
        return cur;
    }

    public void setCur(int cur) {
        this.cur = cur;
    }

    public int getThs() {
        return ths;
    }

    public void setThs(int ths) {
        this.ths = ths;
    }

    public int getRb() {
        return rb;
    }

    public void setRb(int rb) {
        this.rb = rb;
    }

    public int getMb() {
        return mb;
    }

    public void setMb(int mb) {
        this.mb = mb;
    }

    public int getLb() {
        return lb;
    }

    public void setLb(int lb) {
        this.lb = lb;
    }

    public String getRmvMouseType()
    {
        return rmvMouseType;
    }

    public void setRmvMouseType(String rmvMouseType)
    {
        this.rmvMouseType = rmvMouseType;
    }

    public long getRmvTime()
    {
        return rmvTime;
    }

    public void setRmvTime(long rmvTime)
    {
        this.rmvTime = rmvTime;
    }

}