package com.dengdeng123.blowupplane.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 设备信息
 * 
 * @author imknown
 */
public class KeyboardUtil
{
	/** 隐藏输入软键盘 */
	public static void hideSystemKeyBoard(Activity a, View v)
	{
		InputMethodManager imm = (InputMethodManager) (a.getSystemService(Context.INPUT_METHOD_SERVICE));
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
}
