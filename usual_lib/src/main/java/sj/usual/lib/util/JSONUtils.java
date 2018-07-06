package sj.usual.lib.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import sj.usual.lib.dialog.OnLoadingDialog;

/**
 * Created by WuShengjun on 2017/11/3.
 */

public abstract class JSONUtils {

    public JSONUtils() {
        this(GSON_PARSE); // 默认构造也是Gson解析
    }

    public JSONUtils(int parseTool) {
        this.parseTool = parseTool;
    }

    public static String createJson(Object obj) {
        return JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue);
    }

    public final static int GSON_PARSE = 0; // Gson解析
    public final static int FASTJSON_PARSE = 1; // FastJson解析
    public final static int PARSE_SUCC = 100;
    public final static int PARSE_ERROR = -101;
    private int parseTool = GSON_PARSE; // 默认Gson解析
    private int moreLengthUseFastjson = 1000000; // 设置json字符串长度大于多少时用FastJson解析

    private Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            OnLoadingDialog.dismissDialog();
            if(msg.what == PARSE_SUCC) {
                parseFinish(msg.obj, msg.arg1);
            } else {
                parseError((Exception) msg.obj, msg.arg1);
            }
        }
    };

    /**
     * 解析成Javabean对象
     * @param json
     * @param clz
     */
    public synchronized <T> void parseObj(final String json, final Class<T> clz) {
        parseObj(json, clz, -1);
    }

    /**
     * 解析成Javabean对象
     * @param json Json字符串
     * @param clz 目的Javabean类class
     * @param flag 可用来区分多个解析过程，在完成解析时返回
     * @param <T>
     */
    public synchronized <T>  void parseObj(final String json, final Class<T> clz, final int flag) {
        new Thread() {
            @Override
            public void run() {
                if(json == null) {
                    sendMsg(new NullPointerException("Result of json is null"), flag);
                    return;
                }
                try {
                    T t = null;
                    if (parseTool == GSON_PARSE && json.length() < moreLengthUseFastjson) {
                        t = new Gson().fromJson(json, clz);
                    } else {
                        parseTool = FASTJSON_PARSE;
                        t = JSON.parseObject(json, clz);
                    }
                    sendMsg(PARSE_SUCC, t, flag);
                } catch (Exception e) {
                    sendMsg(PARSE_ERROR, e, flag);
                }
            }
        }.start();
    }

    /**
     * 解析成指定对象List
     * @param json Json字符串
     * @param clz 目的Javabean类class
     */
    public synchronized <T> void parseArr(final String json, final Class<T> clz) {
        parseArr(json, clz, -1);
    }

    /**
     * 解析成指定对象List
     * @param json Json字符串
     * @param clz 目的Javabean类class
     * @param flag 可用来区分多个解析过程，在完成解析时返回
     * @param <T>
     */
    public synchronized <T> void parseArr(final String json, final Class<T> clz, final int flag) {
        new Thread() {
            @Override
            public void run() {
                if(json == null) {
                    sendMsg(new NullPointerException("Result of json is null"), flag);
                    return;
                }
                try {
                    List<T> tList = new ArrayList<>();
                    if(parseTool == GSON_PARSE && json.length() < moreLengthUseFastjson) {
//                        tList = new Gson().fromJson(json, new TypeToken<List<T>>(){}.getType());

                        JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
                        for (JsonElement element : jsonArray) {
                            tList.add(new Gson().fromJson(element, clz));
                        }
                    } else {
                        parseTool = FASTJSON_PARSE;
                        tList = JSON.parseArray(json, clz);
                    }
                    sendMsg(PARSE_SUCC, tList, flag);
                } catch (Exception e) {
                    sendMsg(PARSE_ERROR, e, flag);
                }
            }
        }.start();
    }

    private void sendMsg(Object obj) {
        sendMsg(obj, -1);
    }

    private void sendMsg(Object obj, int arg1) {
        sendMsg(PARSE_ERROR, obj, arg1);
    }

    private void sendMsg(int what, Object obj, int arg1) {
        Message msg = mainHandler.obtainMessage();
        msg.what = what;
        msg.obj = obj;
        msg.arg1 = arg1;
        mainHandler.sendMessage(msg);
    }

    public int getParseTool() {
        return parseTool;
    }

    public void setParseTool(int parseTool) {
        this.parseTool = parseTool;
    }

    public int getMoreLengthUseFastjson() {
        return moreLengthUseFastjson;
    }

    public void setMoreLengthUseFastjson(int moreLengthUseFastjson) {
        this.moreLengthUseFastjson = moreLengthUseFastjson;
    }

    /**
     * 解析完成重写回调
     * @param obj
     */
    public abstract void parseFinish(Object obj, int flag);

    /**
     * 解析出错回调
     * @param e
     * @param flag
     */
    public abstract void parseError(Exception e, int flag);
}
