package sj.usual.lib.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Okhttp网络请求管理类
 * Created by WuShengjun on 2016/12/21.
 */

public abstract class OkHttpBaseManager {

    private Handler mHandler; // 主线程Handler
    private static OkHttpBaseManager mInstance;
    private OkHttpClient mOkHttpClient;
    private ArrayList<Call> mRequestCalls; // 所有的网络请求对象
    private static final int NORMAL_TIMEOUT = 10000; // 超时时间(ms)

    private OnReqCallBackOfString onReqCallBackOfString;
    private OnReqCallBackOfBytes onReqCallBackOfBytes;
    private OnReqCallBackOfInputStream onReqCallBackOfInputStream;

    private String mCookie, mUserAgent;

    public OkHttpBaseManager() {
        mHandler = new Handler(Looper.getMainLooper());
        mRequestCalls = new ArrayList<>();
        mOkHttpClient = new OkHttpClient.Builder().connectTimeout(NORMAL_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response response = chain.proceed(chain.request());
                        ProgressResponseBody progressResponseBody = new ProgressResponseBody(response.body()) {
                            @Override
                            public void updateProgress(long downloadedSize, long totalSize) {
                                if(onReqCallBackOfBytes != null) {
                                    onReqCallBackOfBytes.updateProgress(downloadedSize, totalSize);
                                }
                                if(onReqCallBackOfInputStream != null) {
                                    onReqCallBackOfInputStream.updateProgress(downloadedSize, totalSize);
                                }
                            }
                        };
                        return response.newBuilder().body(progressResponseBody).build();
                    }
                }).build();
    }

    // 数据类型
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * POST传递JSON
     * @param url 请求的url地址
     * @param json 上传的json字符串
     * @param onReqCallBackOfString 结果回调对象
     */
    public synchronized Call postJson(String url, String json, OnReqCallBackOfString onReqCallBackOfString) {
        // 创建一个RequestBody(参数1：数据类型，参数2：传递的JSON字符串)
        RequestBody requestBody = RequestBody.create(JSON, json);
        // 创建一个请求对象
        final Request request = getBuilder().url(url).post(requestBody).build();
        // 执行请求
        return request(request, onReqCallBackOfString);
    }

    /**
     * get请求
     * @param url 请求的url地址
     * @param onReqCallBackOfString 结果回调的对象
     */
    public synchronized Call getReq(String url, final OnReqCallBackOfString onReqCallBackOfString) {
        Builder reqBuilder = getBuilder();
        Request request = reqBuilder.url(url).build();
        return request(request, onReqCallBackOfString);
    }

    /**
     * 下载文件
     * @param url 请求的url地址
     * @param onReqCallBackOfInputStream 结果回调的对象
     */
    public synchronized Call downloadFile(String url, String filePath, String fileName, final OnReqCallBackOfInputStream onReqCallBackOfInputStream) {
        return downloadFile(url, new File(filePath), fileName, onReqCallBackOfInputStream);
    }

    /**
     * 下载文件
     * @param url 请求的url地址
     * @param onReqCallBackOfInputStream 结果回调的对象
     */
    public synchronized Call downloadFile(String url, File pathFile, String fileName, final OnReqCallBackOfInputStream onReqCallBackOfInputStream) {
        if(!pathFile.exists()) {
            pathFile.mkdirs();
        }
        File absolutPathFile = new File(pathFile, fileName);
        return downloadFile(url, absolutPathFile, onReqCallBackOfInputStream);
    }

    /**
     * 下载文件
     * @param url 请求的url地址
     * @param onReqCallBackOfInputStream 结果回调的对象
     */
    public synchronized Call downloadFile(String url, File absolutePathFile, final OnReqCallBackOfInputStream onReqCallBackOfInputStream) {
        Builder reqBuilder = getBuilder();
        Request request = reqBuilder.url(url).build();
        return request(request, absolutePathFile, onReqCallBackOfInputStream);
    }

    /**
     * get请求
     * @param url 请求的url地址
     * @param onReqCallBackOfBytes 结果回调的对象
     */
    public synchronized Call downloadFile(String url, String filePath, String fileName, final OnReqCallBackOfBytes onReqCallBackOfBytes) {
        return downloadFile(url, new File(filePath), fileName, onReqCallBackOfBytes);
    }

    /**
     * get请求
     * @param url 请求的url地址
     * @param onReqCallBackOfBytes 结果回调的对象
     */
    public synchronized Call downloadFile(String url, File pathFile, String fileName, final OnReqCallBackOfBytes onReqCallBackOfBytes) {
        if(!pathFile.exists()) {
            pathFile.mkdirs();
        }
        File absolutPathFile = new File(pathFile, fileName);
        return downloadFile(url, absolutPathFile, onReqCallBackOfBytes);
    }

    /**
     * get请求
     * @param url 请求的url地址
     * @param onReqCallBackOfBytes 结果回调的对象
     */
    public synchronized Call downloadFile(String url, File absolutePathFile, final OnReqCallBackOfBytes onReqCallBackOfBytes) {
        Builder reqBuilder = getBuilder();
        Request request = reqBuilder.url(url).build();
        return request(request, absolutePathFile, onReqCallBackOfBytes);
    }

    /**
     * post请求
     * @param url 请求的url地址
     * @param onReqCallBackOfString 结果回调的对象
     */
    public synchronized Call postReq(String url, OnReqCallBackOfString onReqCallBackOfString) {
        return postReq(url, new Param[0], onReqCallBackOfString);
    }

    /**
     * post请求
     * @param url 请求的url地址
     * @param params 附带的Map参数
     * @param onReqCallBackOfString 结果回调的对象
     */
    public synchronized Call postReq(String url, Map<String, String> params, OnReqCallBackOfString onReqCallBackOfString) {
        return postReq(url, map2Params(params), onReqCallBackOfString);
    }

    /**
     * post请求
     * @param url 请求的url地址
     * @param params 附带的List参数
     * @param onReqCallBackOfString 结果回调的参数
     */
    public synchronized Call postReq(String url, List<Param> params, OnReqCallBackOfString onReqCallBackOfString) {
        return postReq(url, list2Params(params), onReqCallBackOfString);
    }

    /**
     * post请求
     * @param url 请求的url地址
     * @param params 附带的数组参数
     * @param onReqCallBackOfString 结果回调的对象
     */
    public synchronized Call postReq(String url, Param[] params, final OnReqCallBackOfString onReqCallBackOfString) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                builder.add(ifNull(params[i].key), ifNull(params[i].value));
            }
        }

        Builder reqBuilder = getBuilder();
        Request request = reqBuilder.url(url).post(builder.build()).build();
        return request(request, onReqCallBackOfString);
    }

    private String ifNull(String str) {
        return str == null ? "" : str;
    }

    protected void setCookie(String cookie) {
        this.mCookie = cookie;
    }

    protected void setUserAgent(String userAgent) {
        this.mUserAgent = userAgent;
    }

    protected Builder getBuilder() {
        Builder reqBuilder = new Request.Builder();

        if(!TextUtils.isEmpty(mCookie)) {
            reqBuilder.addHeader("Cookie", mCookie);
        }
        if(!TextUtils.isEmpty(mUserAgent)) {
            reqBuilder.addHeader("User-Agent", mUserAgent);
        }

        reqBuilder.addHeader("Connection", "Keep-Alive");
        return reqBuilder;
    }

    private Call request(Request request, final OnReqCallBackOfString onReqCallBackOfString) {
        Call call = mOkHttpClient.newCall(request);
        mRequestCalls.add(call); // 把请求对象放进容器，以备管理
        this.onReqCallBackOfString = onReqCallBackOfString;

        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if(response.isSuccessful()) {
                    Headers headers = response.headers();
                    mCookieList = headers.values("Set-Cookie"); // 获取cookie信息
                    responseCookieList(mCookieList);
                    setCookie(mCookie);
                    setUserAgent(mUserAgent);

                    final String result = response.body().string(); //返回结果字符串
                    if(onReqCallBackOfString != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                onReqCallBackOfString.onResponse(result, call, response);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(final Call call, final IOException e) {
                if(onReqCallBackOfString != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onReqCallBackOfString.onFailure(call, e);
                        }
                    });
                }
            }
        });
        return call;
    }

    private Call request(Request request, final File absolutPathFile, final OnReqCallBackOfInputStream onReqCallBackOfInputStream) {
        Call call = mOkHttpClient.newCall(request);
        mRequestCalls.add(call); // 把请求对象放进容器，以备管理
        this.onReqCallBackOfInputStream = onReqCallBackOfInputStream;

        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    Headers headers = response.headers();
                    mCookieList = headers.values("Set-Cookie"); // 获取cookie信息
                    responseCookieList(mCookieList);
                    setCookie(mCookie);
                    setUserAgent(mUserAgent);

                    final InputStream inputStream = response.body().byteStream(); // 返回结果输入流
                    FileOutputStream os = new FileOutputStream(absolutPathFile);
                    try {
                        int bytesRead = 0;
                        byte[] buffer = new byte[1024 * 8];
                        while ((bytesRead = inputStream.read(buffer, 0, 1024 * 8)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        if (onReqCallBackOfInputStream != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onReqCallBackOfInputStream.onSucc(absolutPathFile, inputStream, call, response);
                                }
                            });
                        }
                    } finally {
                        os.close();
                        inputStream.close();
                    }
                }
            }

            @Override
            public void onFailure(final Call call, final IOException e) {
                if(onReqCallBackOfInputStream != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onReqCallBackOfInputStream.onFail(call, e);
                        }
                    });
                }
            }
        });
        return call;
    }

    private Call request(Request request, final File absolutFile, final OnReqCallBackOfBytes onReqCallBackOfBytes) {
        Call call = mOkHttpClient.newCall(request);
        mRequestCalls.add(call); // 把请求对象放进容器，以备管理
        this.onReqCallBackOfBytes = onReqCallBackOfBytes;

        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    Headers headers = response.headers();
                    mCookieList = headers.values("Set-Cookie"); // 获取cookie信息
                    responseCookieList(mCookieList);
                    setCookie(mCookie);
                    setUserAgent(mUserAgent);

                    final byte[] bytesData = response.body().bytes(); // 返回结果字节流
                    BufferedOutputStream bos = null;
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(absolutFile);
                        bos = new BufferedOutputStream(fos);
                        bos.write(bytesData);

                        if (onReqCallBackOfBytes != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onReqCallBackOfBytes.onSucc(absolutFile, bytesData, call, response);
                                }
                            });
                        }
                    } catch (IOException e) {
                        onFailure(null, e);
                    } finally {
                        try {
                            if (bos != null) bos.close();
                            if (fos != null) fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(final Call call, final IOException e) {
                if(onReqCallBackOfBytes != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onReqCallBackOfBytes.onFail(call, e);
                        }
                    });
                }
            }
        });
        return call;
    }

    private List<String> mCookieList;
    public List<String> getResponseCookieList() {
        return mCookieList;
    }

    public void responseCookieList(List<String> cookieList) {
        this.mCookieList = cookieList;
    }

    /**
     * 取消所有网络请求
     */
    public void cancelAllRequest() {
        for(Call call : mRequestCalls) {
            cancelCall(call);
        }
        mRequestCalls.clear();
    }

    /**
     * 取消网络请求
     * @param call
     */
    public void cancelCall(Call call) {
        if(call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    /**
     * Map对象转数组
     * @param params 目标对象
     * @return
     */
    private Param[] map2Params(Map<String, String> params) {
        if (params == null) {
            return new Param[0];
        }
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    /**
     * 列表对象转数组
     * @param params 目标对象
     * @return
     */
    private Param[] list2Params(List<Param> params) {
        if(params == null) {
            return new Param[0];
        }
        int size = params.size();
        Param[] res = new Param[size];
        int index = 0;
        for(Param param : params) {
            res[index++] = param;
        }
        return res;
    }

    /**
     * 回调结果接口
     */
    public interface OnReqCallBackOfString {
        void onResponse(String result, Call call, Response response);
        void onFailure(Call call, IOException e);
    }

    /**
     * 回调结果接口
     */
    public interface OnReqCallBackOfInputStream {
        void onSucc(File absolutFile, InputStream inputStream, Call call, Response response);
        void updateProgress(long downloadedSize, long totalSize);
        void onFail(Call call, IOException e);
    }

    /**
     * 回调结果接口
     */
    public interface OnReqCallBackOfBytes {
        void onSucc(File absolutFile, byte[] bytes, Call call, Response response);
        void updateProgress(long downloadedSize, long totalSize);
        void onFail(Call call, IOException e);
    }

    /**
     * post请求中的参数类
     */
    public static class Param {
        String key;
        String value;

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public Param(String key, int value) {
            this.key = key;
            this.value = String.valueOf(value);
        }

        public Param(String key, long value) {
            this.key = key;
            this.value = String.valueOf(value);
        }

        public Param(String key, double value) {
            this.key = key;
            this.value = String.valueOf(value);
        }

        public Param(String key, boolean value) {
            this.key = key;
            this.value = String.valueOf(value);
        }
    }
}