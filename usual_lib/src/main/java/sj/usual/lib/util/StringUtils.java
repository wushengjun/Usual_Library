package sj.usual.lib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * Created by WuShengjun on 2017/6/12.
 */

public class StringUtils {
    public static String ifNull(String val) {
        return ifNull(val, "");
    }

    public static String ifNull(String val, String defVal) {
        if(TextUtils.isEmpty(val)) {
            return defVal;
        }
        return val;
    }

    public static String hmsToChinese(String date) {
        if (TextUtils.isEmpty(date)) return "";
        String[] nums = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
                "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
                "二十一", "二十二", "二十三", "二十四", "二十五", "二十六", "二十七", "二十八", "二十九", "三十",
                "三十一", "三十二", "三十三", "三十四", "三十五", "三十六", "三十七", "三十八", "三十九", "四十",
                "四十一", "四十二", "四十三", "四十四", "四十五", "四十六", "四十七", "四十八", "四十九", "五十",
                "五十一", "五十二", "五十三", "五十四", "五十五", "五十六", "五十七", "五十八", "五十九"};

        String[] dateArr = date.split(":");
        String hms = "";
        if (dateArr.length == 2) { // 时分
            int d = Integer.valueOf(dateArr[0]); // 时
            if(d >= 0 && d < nums.length) {
                hms += nums[d] + "时";
            }
            int m = Integer.valueOf(dateArr[1]); // 分
            if(m >= 0 && m < nums.length) {
                hms += nums[m] + "分";
            }
            return hms;
        } else if (dateArr.length == 3) { // 时分秒
            int d = Integer.valueOf(dateArr[0]); // 时
            if(d >= 0 && d < nums.length) {
                hms += nums[d] + "时";
            }
            int m = Integer.valueOf(dateArr[1]); // 分
            if(m >= 0 && m < nums.length) {
                hms += nums[m] + "分";
            }
            int s = Integer.valueOf(dateArr[2]); // 分
            if(s >= 0 && s < nums.length) {
                hms += nums[s] + "秒";
            }
            return hms;
        }
        return hms;
    }

    public static String num2Chinese(String src) {
        if (TextUtils.isEmpty(src)) {
            return "";
        }
        String[] s1 = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] s2 = {"十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};
        String result = "";
        int n = src.length();
        for (int i = 0; i < n; i++) {
            int num = src.charAt(i) - '0';
            if (i != n - 1 && num != 0) {
                result += s1[num] + s2[n - 2 - i];
            } else {
                result += s1[num];
            }
        }
        return result;
    }

    /**
     * IP地址是否合法
     * @param IP
     * @return
     */
    public static boolean isCorrectIP(String IP) {
        // 判断IP格式和范围
        String regexIp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])"
                + "(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        return !TextUtils.isEmpty(IP) && IP.matches(regexIp);
    }

    /**
     * 端口号是否合法
     * @param portStr
     * @return
     */
    public static boolean isCorrectPort(String portStr) {
        try {
            int port = Integer.valueOf(portStr);
            return isCorrectPort(port);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 端口号是否合法
     * @param port
     * @return
     */
    public static boolean isCorrectPort(int port) {
        // 判断端口号范围
        return port > 0 && port <= 65535;
    }
}
