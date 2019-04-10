package com.flop.minesweeper.zhangye.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * board相关BEAN
 *
 * @author zhangYe
 *
 */
public class BrdBean implements Serializable
{
    /**
     * UID
     */
    private static final long serialVersionUID = -2730987152467866530L;
    public List<Integer> ox=new ArrayList<Integer>();
    public List<Integer> oy=new ArrayList<Integer>();
    public List<SquareBean> c=new ArrayList<SquareBean>();
    public int minPmines;
    public int maxPmines;
    public Map<Integer,Integer> mob=new HashMap<Integer,Integer>();
    public List<Integer> getOx() {
        return ox;
    }
    public void setOx(List<Integer> ox) {
        this.ox = ox;
    }
    public List<Integer> getOy() {
        return oy;
    }
    public void setOy(List<Integer> oy) {
        this.oy = oy;
    }
    public List<SquareBean> getC() {
        return c;
    }
    public void setC(List<SquareBean> c) {
        this.c = c;
    }
    public int getMinPmines() {
        return minPmines;
    }
    public void setMinPmines(int minPmines) {
        this.minPmines = minPmines;
    }
    public int getMaxPmines() {
        return maxPmines;
    }
    public void setMaxPmines(int maxPmines) {
        this.maxPmines = maxPmines;
    }
    public Map<Integer, Integer> getMob() {
        return mob;
    }
    public void setMob(Map<Integer, Integer> mob) {
        this.mob = mob;
    }

}