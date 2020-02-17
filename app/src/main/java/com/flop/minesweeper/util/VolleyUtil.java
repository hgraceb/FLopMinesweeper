package com.flop.minesweeper.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.flop.minesweeper.BuildConfig;
import com.flop.minesweeper.errorLogInfo.FlopApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Flop on 2020/2/17.
 */
public class VolleyUtil {
    public static final String TAG = "VolleyUtil";
    public static final int NET_ERROR_VOLLEY = -2;
    private static RequestQueue mRequestQueue = FlopApplication.getRequestQueue();

    public static class Builder {
        /** 请求地址 */
        private String mUrl;
        /** 用户定义的消息代码 */
        private int mWhat;
        /** bundle参数，通过handle回传数据 */
        private Bundle mBundle;
        /** 异步消息的处理 */
        private Handler mHandler;
        /** 请求参数 */
        private Map<String, String> mParams;
        /** 请求头 */
        private Map<String, String> mRequestHeaders;
        /** 响应头 */
        private Map<String, String> mResponseHeaders;

        public Builder setUrl(@NonNull String url) {
            this.mUrl = url;
            return this;
        }

        public Builder setWhat(int mWhat) {
            this.mWhat = mWhat;
            return this;
        }

        public Builder setBundle(Bundle mBundle) {
            this.mBundle = mBundle;
            return this;
        }

        public Builder setHandler(@NonNull Handler mHandler) {
            this.mHandler = mHandler;
            return this;
        }

        public Builder setParams(Map<String, String> mParams) {
            this.mParams = mParams;
            return this;
        }

        public Builder setRequestHeaders(Map<String, String> mRequestHeaders) {
            this.mRequestHeaders = mRequestHeaders;
            return this;
        }

        public Builder setResponseHeaders(Map<String, String> mResponseHeaders) {
            this.mResponseHeaders = mResponseHeaders;
            return this;
        }

        public void get() {
            request(Request.Method.GET);
        }

        public void post() {
            request(Request.Method.POST);
        }

        public void request(int method) {
            addRequest(method, mHandler, mWhat, mBundle, mUrl, mParams, mRequestHeaders, mResponseHeaders);
        }
    }

    /**
     * @param method          Request.Method.GET 或 Request.Method.POST
     * @param handler         请求结束后将结果作为Message.obj发送到该Handler
     * @param what            请求结束后发送的Message.what
     * @param bundle          不参与网络请求，仅携带参数（请求结束后，通过Message.setData设置到Message对象，数据原样返回）
     * @param url             请求地址
     * @param params          请求参数
     * @param requestHeaders  请求头
     * @param responseHeaders 响应头
     * @param listener        监听器
     */
    private static void addRequest(
            int method,
            final Handler handler, final int what,
            final Bundle bundle, String url, final Map<String, String> params,
            final Map<String, String> requestHeaders, final Map<String, String> responseHeaders,
            final NetWorkRequestListener listener) {
        if (method == Request.Method.GET) {
            url = NetWorkUtil.getUrlWithParams(url, params);
        }
        listener.onPreRequest();
        StringRequest request = new StringRequest(method, url, response -> {
            // 请求成功
            onVolleyResponse(response, handler, what, bundle);
            listener.onResponse();
        }, volleyError -> {
            // 请求失败，重试
            onVolleyErrorResponse(volleyError, listener, handler, bundle);
        }) {
            /**
             * 获取请求头
             */
            @Override
            public Map<String, String> getHeaders() {
                // 在此统一添加header
                Map<String, String> map = requestHeaders != null ? requestHeaders : new HashMap<>();
                map.put("versionName", BuildConfig.APPLICATION_ID);
                return map;
            }

            /**
             * 获取表单参数，Volley仅在post的情况下会回调该方法
             */
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            /**
             * 解析返回数据
             */
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Map<String, String> headers = response.headers;
                for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
                    headers.put(entry.getKey(), entry.getValue());
                }
                bundle.putString("Set-Cookie", headers.get("Set-Cookie"));
                return super.parseNetworkResponse(response);
            }
        };
        // 把请求添加到队列里面
        addToRequestQueue(request, what);
    }

    /**
     * 使用默认监听器添加请求
     *
     * @param method          Request.Method.GET 或 Request.Method.POST
     * @param handler         请求结束后将结果作为Message.obj发送到该Handler
     * @param what            请求结束后发送的Message.what
     * @param bundle          不参与网络请求，仅携带参数（请求结束后，通过Message.setData设置到Message对象，数据原样返回）
     * @param url             请求地址
     * @param params          请求参数
     * @param requestHeaders  请求头
     * @param responseHeaders 响应头
     */
    public static void addRequest(
            int method,
            final Handler handler, final int what,
            @NonNull final Bundle bundle, String url, final Map<String, String> params,
            final Map<String, String> requestHeaders, final Map<String, String> responseHeaders) {
        addRequest(method, handler, what, bundle, url, params, requestHeaders, responseHeaders, new DefaultRequestListener() {
            @Override
            public boolean retry() {
                addRequest(method, handler, what, bundle, url, params, requestHeaders, responseHeaders,
                        ++retryTimer >= MAX_RETRY_TIME ? new DefaultRequestListener() : this);
                return true;
            }
        });
    }

    /**
     * 使用带有对话框的监听器添加请求
     *
     * @param method          Request.Method.GET 或 Request.Method.POST
     * @param context         上下文
     * @param handler         请求结束后将结果作为Message.obj发送到该Handler
     * @param what            请求结束后发送的Message.what
     * @param bundle          不参与网络请求，仅携带参数（请求结束后，通过Message.setData设置到Message对象，数据原样返回）
     * @param url             请求地址
     * @param params          请求参数
     * @param requestHeaders  请求头
     * @param responseHeaders 响应头
     */
    public static void addRequestWithDialog(
            int method, final Context context,
            final Handler handler, final int what,
            final Bundle bundle, String url, final Map<String, String> params,
            final Map<String, String> requestHeaders, final Map<String, String> responseHeaders) {
        addRequest(method, handler, what, bundle, url, params, requestHeaders, responseHeaders, new DefaultDialogRequestListener(context) {
            @Override
            public boolean retry() {
                addRequest(method, handler, what, bundle, url, params, requestHeaders, responseHeaders,
                        ++retryTimer >= MAX_RETRY_TIME ? new DefaultDialogRequestListener(context) : this);
                return true;
            }
        });
    }

    /**
     * 请求失败
     */
    private static void onVolleyErrorResponse(VolleyError volleyError, NetWorkRequestListener listener, Handler handler, Bundle bundle) {
        if (listener.retry()) {
            listener.onFailed();
            return;
        }
        Message msg = handler.obtainMessage(NET_ERROR_VOLLEY);
        msg.setData(bundle);
        handler.sendMessage(msg);
        listener.onFailed();
    }

    /**
     * 请求成功
     */
    private static void onVolleyResponse(String response, Handler handler, int what, Bundle bundle) {
        Message msg = handler.obtainMessage(what, response);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    /**
     * 把请求添加到队列里面
     */
    private static <T> void addToRequestQueue(Request<T> req, int tag) {
        // 设置一个标记，便于取消队列里的请求
        req.setTag(tag);
        mRequestQueue.add(req);
    }

    /**
     * 取消指定请求
     */
    public static void cancelRequest(int tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * 请求过程中显示加载对话框，且自动处理其生命周期
     */
    private static class DefaultDialogRequestListener extends DefaultRequestListener {

        Context context;
        ProgressDialog dialog;

        private DefaultDialogRequestListener(Context context) {
            this.context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        public void onPreRequest() {
            dialog.show();
        }

        @Override
        public void onResponse() {
            dialog.dismiss();
        }

        @Override
        public void onFailed() {
            dialog.dismiss();
        }
    }

    /**
     * 默认监听器，实现网络请求在不同时机回调的接口
     */
    private static class DefaultRequestListener implements NetWorkRequestListener {

        int retryTimer;// 当前重试次数

        static final int MAX_RETRY_TIME = 3;// 最大重试次数

        @Override
        public void onPreRequest() {

        }

        @Override
        public void onResponse() {

        }

        @Override
        public void onFailed() {

        }

        @Override
        public boolean retry() {
            return false;
        }
    }

    /**
     * 用于所有网络请求，在不同时机回调的接口
     */
    private static interface NetWorkRequestListener {
        /**
         * 预请求
         */
        void onPreRequest();

        /**
         * 请求成功
         */
        void onResponse();

        /**
         * 请求失败
         */
        void onFailed();

        /**
         * @return 是否重试
         */
        boolean retry();
    }
}
