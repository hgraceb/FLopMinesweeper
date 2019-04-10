package com.flop.minesweeper.zhangye.util;

/**
 * Created by Flop on 2018/11/27.
 */
public class CountryUtil {
    //-1表示录像内不记录地区信息
    public static int index = -1;

    public static void setIndex(int index) {
        CountryUtil.index = index;
    }

    public static int getIndex() {
        return index;
    }

    public static String strCountryName[][] = {
            //默认采用中文字符编码
            {"未设定", "(N/A)", "0", "gb2312"},
            {"阿富汗", "Afghanistan", "2d", ""},
            {"阿尔巴尼亚", "Albania", "2e", ""},
            {"阿尔及利亚", "Algeria", "2f", ""},
            {"安道尔", "Andorra", "30", ""},
            {"安哥拉", "Angola", "31", ""},
            {"阿根廷", "Argentina", "32", ""},
            {"亚美尼亚", "Armenia", "33", ""},
            {"澳大利亚", "Australia", "1", ""},
            {"奥地利", "Austria", "2", ""},
            {"阿塞拜疆", "Azerbaijan", "34", ""},
            {"孟加拉国", "Bangladesh", "35", ""},
            {"白俄罗斯", "Belarus", "36", ""},
            {"比利时", "Belgium", "3", ""},
            {"伯利兹", "Belize", "37", ""},
            {"波斯尼亚", "Bosnia", "4", ""},
            {"巴西", "Brazil", "5", ""},
            {"保加利亚", "Bulgaria", "6", ""},
            {"柬埔寨", "Cambodia", "38", ""},
            {"加拿大", "Canada", "7", ""},
            {"乍得", "Chad", "39", ""},
            {"智利", "Chile", "3a", ""},
            {"中国", "China", "8", "gb2312"},
            {"哥伦比亚", "Colombia", "3b", ""},
            {"克罗地亚", "Croatia", "3c", ""},
            {"古巴", "Cuba", "3d", ""},
            {"塞浦路斯", "Cyprus", "3e", ""},
            {"捷克", "Czech Republic", "3f", ""},
            {"丹麦", "Denmark", "9", ""},
            {"埃及", "Egypt", "40", ""},
            {"英格兰", "England", "a", ""},
            {"爱沙尼亚", "Estonia", "41", ""},
            {"欧盟", "Euro Union", "42", ""},
            {"芬兰", "Finland", "b", ""},
            {"法国", "France", "c", ""},
            {"德国", "Germany", "d", "latin2"},
            {"希腊", "Greece", "e", ""},
            {"中国", "Hong Kong", "f", ""},
            {"匈牙利", "Hungary", "10", ""},
            {"冰岛", "Iceland", "11", ""},
            {"印度", "India", "12", ""},
            {"印度尼西亚", "Indonesia", "43", ""},
            {"伊朗", "Iran", "13", ""},
            {"伊拉克", "Iraq", "44", ""},
            {"爱尔兰", "Ireland", "14", ""},
            {"以色列", "Israel", "15", ""},
            {"意大利", "Italy", "16", ""},
            {"日本", "Japan", "45", ""},
            {"朝鲜", "Korea North", "46", ""},
            {"韩国", "Korea South", "17", ""},
            {"拉脱维亚", "Latvia", "47", ""},
            {"列支士登", "Liechtenstein", "48", ""},
            {"立陶宛", "Lithuania", "54", ""},
            {"卢森堡", "Luxembourg", "49", ""},
            {"马来西亚", "Malaysia", "18", ""},
            {"墨西哥", "Mexico", "19", ""},
            {"摩尔多瓦", "Moldova", "4a", ""},
            {"摩纳哥", "Monaco", "4b", ""},
            {"黑山", "Montenegro", "2c", ""},
            {"荷兰", "Netherlands", "1a", ""},
            {"新西兰", "New Zealand", "1b", ""},
            {"挪威", "Norway", "1c", ""},
            {"巴勒斯坦", "Palestine", "4c", ""},
            {"波兰", "Poland", "1d", "latin2"},
            {"葡萄牙", "Portugal", "4d", ""},
            {"卡塔尔", "Qatar", "4e", ""},
            {"罗马尼亚", "Romania", "1e", ""},
            {"俄罗斯", "Russia", "1f", ""},
            {"塞尔维亚", "Serbia", "20", ""},
            {"新加坡", "Singapore", "21", ""},
            {"斯洛伐克", "Slovakia", "4f", ""},
            {"斯洛文尼亚", "Slovenia", "50", ""},
            {"南非", "South Africa", "51", ""},
            {"西班牙", "Spain", "22", ""},
            {"瑞典", "Sweden", "23", ""},
            {"瑞士", "Switzerland", "24", ""},
            {"中国", "Taiwan", "25", ""},
            {"泰国", "Thailand", "26", ""},
            {"火鸡", "Turkey", "27", ""},
            {"阿联酋", "UAE", "52", ""},
            {"乌克兰", "Ukraine", "28", ""},
            {"美国", "USA", "29", ""},
            {"越南", "Vietnam", "2a", ""},
            {"威尔士", "Wales", "2b", ""},
            {"南斯拉夫", "Yugoslavia", "53", ""}
    };
}
