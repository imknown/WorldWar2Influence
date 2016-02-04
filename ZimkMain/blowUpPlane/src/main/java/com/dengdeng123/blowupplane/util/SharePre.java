package com.dengdeng123.blowupplane.util;

import android.content.Context;
import android.preference.PreferenceManager;

public class SharePre {
	/** 存储强制升级报文 */
	public static String getUpdateInfo(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString("update_obj", "");
	}

	/** 获取强制升级报文 */
	public static void setUpdateInfo(Context context, String update_obj) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString("update_obj", update_obj).commit();
	}

	/** 获取升级下载好 apk URL */
	public static String getApkUrl(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString("apk_url", "");
	}

	/** 存储升级下载好 apk URL */
	public static void setApkUrl(Context context, String apk_url) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString("apk_url", apk_url).commit();
	}

	/** 获取 account */
	public static String getUsername(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString("username", "");
	}

	/** 存储 account */
	public static void setUsername(Context context, String username) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString("username", username).commit();
	}

	/** 获取 pwd */
	public static String getPwd(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString("pwd", "");
	}

	/** 存储 pwd */
	public static void setPwd(Context context, String pwd) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString("pwd", pwd).commit();
	}

	public static boolean getLoginState(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("loginState", false);
	}

	public static void setLoginState(Context context, boolean loginState) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("loginState", loginState).commit();
	}
}
