package com.dengdeng123.blowupplane.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DatabaseUtil {

	/** 数据库在手机里的路径 */
	public static String DATABASE_PATH;

	/** 数据库的名字 */
	public final static String DB_NAME = "Fly.sqlite";

	/**
	 * 判断数据库是否存在
	 * 
	 * @return 存在=true, 不存在=false
	 */
	public static boolean checkDatabase() {
		SQLiteDatabase db = null;

		try {
			String databaseFilename = DATABASE_PATH + DB_NAME;
			db = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READWRITE);
			db.rawQuery("select count(1) from Flyfight", null);
		} catch (SQLiteException e) {
			db = null;
			Log.e("Jinhe", "### 没有数据库吧? ###");
		}

		if (db != null) {
			db.close();
		}

		boolean exists = ((db != null) ? true : false);

		return exists;
	}

	/**
	 * 复制数据库到手机指定文件夹下
	 * 
	 * @throws IOException
	 */
	public static void copyDatabase(Context context) throws IOException {
		if (checkDatabase()) {
			return;
		}

		File dir = new File(DATABASE_PATH);

		if (!dir.exists()) { // 判断文件夹是否存在，不存在就新建一个
			dir.mkdirs();
		}

		String databaseFilenames = DATABASE_PATH + DB_NAME;

		FileOutputStream os = new FileOutputStream(databaseFilenames);// 得到数据库文件的写入流
		InputStream is = FileUtil.getInputStream(context, "db" + File.separator + DB_NAME);// 得到数据库文件的数据流

		byte[] buffer = new byte[8 * 1024];

		int count = 0;

		while ((count = is.read(buffer)) > 0) {
			os.write(buffer, 0, count);
			os.flush();
		}

		is.close();
		os.close();
	}

}
