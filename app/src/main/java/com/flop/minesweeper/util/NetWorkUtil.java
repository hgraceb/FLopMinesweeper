package com.flop.minesweeper.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网络工具类
 * Created by Flop on 2018/11/18.
 */
public class NetWorkUtil {

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 将参数转换为字符串
     */
    public static String convertMapToString(Map<String, String> params) {
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(params.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (value != null) {
                content.append(i == 0 ? "" : "&").append(key).append("=").append(value);
            } else {
                content.append(i == 0 ? "" : "&").append(key).append("=");
            }
        }
        return content.toString();
    }

    /**
     * 将参数进行URLEncode编码转换
     */
    public static Map<String, String> getURLEncodeParams(Map<String, String> params) {
        Map<String, String> map = new HashMap<String, String>();
        for (String key : params.keySet()) {
            String value = params.get(key);
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * 拼接url和参数，用于Get请求
     */
    public static String getUrlWithParams(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        params = getURLEncodeParams(params);
        String paramsStr = convertMapToString(params);
        if (!url.endsWith("?")) {
            url += "?";
        }
        url += paramsStr;
        return url;
    }
}
