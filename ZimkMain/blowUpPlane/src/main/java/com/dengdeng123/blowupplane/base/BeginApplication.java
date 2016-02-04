package com.dengdeng123.blowupplane.base;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.uncaught.CrashHandler;
import com.dengdeng123.blowupplane.entity.UserEntity;
import com.dengdeng123.blowupplane.util.DatabaseUtil;
import com.dengdeng123.blowupplane.util.DisplayUtil;
import com.dengdeng123.blowupplane.util.SharePre;
import com.lidroid.xutils.DbUtils;

public class BeginApplication extends Application {

	public static DbUtils dbUtils;
	public static UserEntity ue = new UserEntity();

	// =================

	@Override
	public void onCreate() {
		super.onCreate();

		mApplicationContext = this;

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);

		initDbUtils();
	}

	// =================

	private void initDbUtils() {
		String temp_db_name = "temp_db_name";
		DatabaseUtil.DATABASE_PATH = mApplicationContext.getDatabasePath(temp_db_name).getAbsolutePath().replace(temp_db_name, "");

		dbUtils = DbUtils.create(this, DatabaseUtil.DATABASE_PATH, DatabaseUtil.DB_NAME);// DbUtils db = DbUtils.create(this);
		dbUtils.configAllowTransaction(true);
		dbUtils.configDebug(true);
	}

	// =================

	// private static GifMovieView loading_gif;

	private static Context mApplicationContext;
	private static PopupWindow loadingPw;

	// =================

	private static PopupWindow getLoadingPopupWindow() {
		if (loadingPw == null) {
			loadingPw = initLoadingPopupWindow();
		}

		return loadingPw;
	}

	public static void showLoadingPopupWindow(View view) {
		getLoadingPopupWindow();
		// loading_gif.setPaused(false);
		loadingPw.showAtLocation(view, Gravity.CENTER, 0, 0);
	}

	public static void closeLoadingPopupWindow() {
		getLoadingPopupWindow();
		loadingPw.dismiss();
	}

	private static PopupWindow initLoadingPopupWindow() {
		View filterView = LayoutInflater.from(mApplicationContext).inflate(R.layout.popup_window_loading, (ViewGroup) null, false);

		DisplayUtil d = new DisplayUtil(mApplicationContext);

		int width = (int) (d.getDisplayWidth() / 2F);
		int height = width;// android.view.WindowManager.LayoutParams.WRAP_CONTENT;

		// loading_gif = (GifMovieView) filterView.findViewById(R.id.loading_gif);

		loadingPw = new PopupWindow(filterView, width, height, true);
		loadingPw.setAnimationStyle(android.R.style.Animation_Dialog);
		// pw.setTouchable(false);
		// pw.setOutsideTouchable(false);
		// pw.setFocusable(true);
		// pw.setBackgroundDrawable(new BitmapDrawable(context.getResources(), ((Bitmap) null)));

		// // 点击其他地方消失
		// filterView.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// v.performClick();
		// pw.dismiss();
		// return true;
		// }
		// });

		return loadingPw;
	}

	// =================

	private static Toast toast;
	private static Toast longToast;

	private static TextView message_txtv;

	private static void initMessageToast() {
		if (toast == null) {
			toast = new Toast(mApplicationContext);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setDuration(Toast.LENGTH_SHORT);
		}

		if (longToast == null) {
			longToast = new Toast(mApplicationContext);
			longToast.setGravity(Gravity.CENTER, 0, 0);
			longToast.setDuration(Toast.LENGTH_LONG);
		}

		if (message_txtv == null) {
			LayoutInflater inflater = LayoutInflater.from(mApplicationContext);
			View viewLayout = inflater.inflate(R.layout.toast_show_msg, (ViewGroup) null, false);

			message_txtv = (TextView) viewLayout.findViewById(R.id.message_txtv);

			toast.setView(viewLayout);
			longToast.setView(viewLayout);
		}
	}

	public static void showMessageToast(int messageResId) {
		initMessageToast();

		message_txtv.setText(messageResId);

		toast.show();
	}

	public static void showMessageToast(String message) {
		initMessageToast();

		message_txtv.setText(message);

		toast.show();
	}

	public static void showLongTimeMessageToast(int messageResId) {
		initMessageToast();

		message_txtv.setText(messageResId);

		longToast.show();
	}

	public static void showLongTimeMessageToast(String message) {
		initMessageToast();

		message_txtv.setText(message);

		longToast.show();
	}

	// =================

	// private static ProgressDialog progressDialog;
	//
	// public static void showWait(Context context) {
	// showWait(context.getResources().getString(R.string.tips_waiting), context);
	// }
	//
	// public static void showWait(String waitMsg, Context context) {
	// // dismissDialog();
	//
	// if (progressDialog == null) {
	// progressDialog = new ProgressDialog(context);
	// progressDialog.setCancelable(false);
	// progressDialog.setCanceledOnTouchOutside(false);
	// }
	//
	// progressDialog.setMessage(waitMsg);
	// progressDialog.show();
	// }
	//
	// public static void dismissDialog() {
	// if (progressDialog != null && progressDialog.isShowing()) {
	// progressDialog.dismiss();
	// progressDialog = null;
	// }
	// }

	// =================

	private static List<Activity> activityList = new LinkedList<Activity>();

	/**
	 * 添加Activity
	 * 
	 * @param activity
	 */
	public static void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/** 是否可以退出 */
	public static boolean canExit;

	private static final int howManyMillisecondBetweenDoubleClickForExit = 2000;

	/** 双击退出程序之前的准备 */
	public static void readyToExit() {
		if (!canExit) {
			canExit = true;

			showMessageToast(R.string.press_twice_to_exit);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					canExit = false;
				}
			}, howManyMillisecondBetweenDoubleClickForExit);

		} else {
			exitClient();
		}
	}

	/** 退出应用，关闭所有Activity */
	public static void exitClient() {
		finishAllActivity();

		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
		// Runtime.getRuntime().exit(0);
	}

	public static void finishAllActivity() {
		int size = activityList.size();

		for (int i = size - 1; i >= 0; i--) {
			Activity a = activityList.get(i);

			activityList.remove(a);

			if (null != a) {
				a.finish();
				a = null;
			}
		}

		SharePre.setLoginState(mApplicationContext, false);
	}

	public static void finishOtherActivity(Activity activity) {

		int size = activityList.size();

		for (int i = size - 1; i >= 0; i--) {
			Activity a = activityList.get(i);

			if (null == a) {
				activityList.remove(a);
			}

			if (null != a && !activity.getClass().toString().equals(a.getClass().toString())) {
				activityList.remove(a);

				a.finish();
				a = null;
			}
		}
	}

	public static void finishThisActivity(Activity activity) {

		int size = activityList.size();

		for (int i = size - 1; i >= 0; i--) {
			Activity a = activityList.get(i);

			if (null == a) {
				activityList.remove(a);
			}

			if (null != a && activity.getClass().toString().equals(a.getClass().toString())) {
				activityList.remove(a);

				a.finish();
				a = null;
			}
		}
	}

	public static Activity getCurrentActivity() {

		int size = activityList.size();

		return activityList.get(size - 1);
	}

	public static Activity getLastActivity() {

		int size = activityList.size();

		Activity last = null;

		if (size > 1) {
			last = activityList.get(size - 2);
		}

		return last;
	}

	// =================
}
