package sj.usual.lib.log;

import android.text.TextUtils;
import android.util.Log;

/**
 * 调试打印信息
 * @author WuShengjun
 * @date 2017年2月13日
 */
public class MyLg {
	private static boolean isDebug = true; // 是否调式

	public static boolean isDebug() {
		return isDebug;
	}

	public static void setDebug(boolean isDebug) {
		MyLg.isDebug = isDebug;
	}

	public static void v(String tag, String msg) {
		if(isDebug && !TextUtils.isEmpty(msg)) {
			Log.v(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if(isDebug && !TextUtils.isEmpty(msg)) {
			Log.e(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if(isDebug && !TextUtils.isEmpty(msg)) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if(isDebug && !TextUtils.isEmpty(msg)) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if(isDebug && !TextUtils.isEmpty(msg)) {
			Log.w(tag, msg);
		}
	}
}
