package com.flop.minesweeper.Variable;

/**
 * Created by Flop on 2019/03/17.
 */
public class OrderOption {
    private String menu="";
    private String sort ="";
    private String bv="";

    public OrderOption(String menu, String sort, String bv){
        this.menu = menu;
        this.sort = sort;
        this.bv=bv;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setBv(String bv) {
        this.bv = bv;
    }

    public String getMenu() {
        return menu;
    }

    public String getSort() {
        return sort;
    }

    public String getBv() {
        return bv;
    }
}
