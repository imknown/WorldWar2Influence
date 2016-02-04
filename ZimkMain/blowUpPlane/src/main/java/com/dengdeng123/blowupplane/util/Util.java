package com.dengdeng123.blowupplane.util;

import android.content.Context;
import android.os.Environment;

public class Util
{
	public static String getUpdate_app_path(Context context)
	{
		return getSavePath(context) + "/dengdeng/app/";
	}

	/***
	 * 获取保存的路径
	 * 
	 * @return
	 */
	public static String getSavePath(Context context)
	{
		String save_path;

		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		{
			save_path = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		else
		{
			save_path = context.getFilesDir().getAbsolutePath();
		}

		return save_path;
	}
}
