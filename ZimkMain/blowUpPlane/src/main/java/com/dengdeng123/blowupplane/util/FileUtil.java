package com.dengdeng123.blowupplane.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class FileUtil {
	/**
	 * 获取 raw, drawable 等文件夹中的文件的输入流
	 * 
	 * @param context
	 *            上下文
	 * @param rawResId
	 *            R.raw.certain_db, R.drawable.ic_launcher
	 * @return 文件的输入流
	 */
	public static InputStream getInputStream(Context context, int rawResId) {
		InputStream is = context.getResources().openRawResource(rawResId);
		return is;
	}

	/**
	 * 获取 assets 文件夹中的文件的输入流
	 * 
	 * @param context
	 *            上下文
	 * @param assetsPath
	 *            "db/certain_db.sqlite"
	 * @return 文件的输入流
	 * @throws IOException
	 */
	public static InputStream getInputStream(Context context, String assetsPath) throws IOException {
		InputStream is = context.getResources().getAssets().open(assetsPath);
		return is;
	}
}
