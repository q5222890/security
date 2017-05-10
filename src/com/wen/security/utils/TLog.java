package com.wen.security.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class TLog {
	private final static String TAG = "Security";
	private static boolean logEnable = MainConfig.logEnable;
	private static boolean isDebug = MainConfig.isDebug;
	private static Toast toast =null;
	/**
	 * 显示土司
	 * @param context
	 * @param text     正式版的提示文字
	 * @param debug    开发版的提示文字
	 */
	public static void showToast(Context context, String text,String debug){
		if(toast ==null){
			if (isDebug) {
				toast =Toast.makeText(context,debug,Toast.LENGTH_SHORT);
			}else {
				toast =Toast.makeText(context,text,Toast.LENGTH_SHORT);
			}
		}else{
			if (isDebug) {
				toast.setText(debug);
			}else{
				toast.setText(text);
			}
		}
		toast.show();
	}

	public static void i(String subTag, String content){
		if (logEnable)
			Log.i(TAG, subTag + "  ->  " + content);
	}

	public static void i(String content){
		if (logEnable)
			Log.i(TAG, content);
	}

	public static void d(String subTag, String content){
		if (logEnable)
			Log.d(TAG, subTag + "  ->  " + content);
	}

	public static void d(String content){
		if (logEnable)
			Log.d(TAG, content);
	}

	public static void w(String subTag, String content){
		if (logEnable)
			Log.w(TAG, subTag + "  ->  " + content);
	}

	public static void w(String subTag, String content, Throwable tr){
		if (logEnable)
			Log.w(TAG, subTag + "  ->  " + content, tr);
	}

	public static void w(String content){
		if (logEnable)
			Log.w(TAG, content);
	}

	public static void w(String content, Throwable tr){
		if (logEnable)
			Log.w(TAG, content, tr);
	}

	public static void e(String subTag, String content){
		if (logEnable)
			Log.e(TAG, subTag + "  ->  " + content);
	}

	public static void e(String subTag, String content, Throwable tr){
		if (logEnable)
			Log.e(TAG, subTag + "  ->  " + content, tr);
	}

	public static void e(String content){
		if (logEnable)
			Log.e(TAG, content);
	}

	public static void e(String content, Throwable tr){
		if (logEnable)
			Log.e(TAG, content, tr);
	}

	public static void v(String subTag, String content){
		if (logEnable)
			Log.v(TAG, subTag + "  ->  " + content);
	}
	public static void loadLog(String subTag, String content){
		if (logEnable)
			Log.v("loadLog", subTag + "  ->  " + content);
	}
	public static void v(String content){
		if (logEnable)
			Log.v(TAG, content);
	}
}