package com.flop.minesweeper.zhangye.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * board相关BEAN
 *
 * @author zhangYe
 *
 */
public class SquareBean implements Serializable
{
    /**
     * UID
     */
    private static final long serialVersionUID = -2730987152467866530L;
    public int ox;
    public int oy;
    public int pmine;
    public double prob;
    public boolean psolved = false;
    public Map<Integer,Integer> pr=new HashMap<Integer,Integer>();
    public int getOx() {
        return ox;
    }
    public void setOx(int ox) {
        this.ox = ox;
    }
    public int getOy() {
        return oy;
    }
    public void setOy(int oy) {
        this.oy = oy;
    }
    public int getPmine() {
        return pmine;
    }
    public void setPmine(int pmine) {
        this.pmine = pmine;
    }
    public Map<Integer, Integer> getPr() {
        return pr;
    }
    public void setPr(Map<Integer, Integer> pr) {
        this.pr = pr;
    }
    public double getProb() {
        return prob;
    }
    public void setProb(double prob) {
        this.prob = prob;
    }
    public boolean isPsolved() {
        return psolved;
    }
    public void setPsolved(boolean psolved) {
        this.psolved = psolved;
    }


}