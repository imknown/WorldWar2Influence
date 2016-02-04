package com.dengdeng123.blowupplane.update;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Window;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.util.SharePre;
import com.dengdeng123.blowupplane.util.Util;

public class UpdateActivity extends Activity {
	private String resCode = "";
	private boolean isStart;
	private String resDesc = "";
	private String update_url;
	private String update_content = "";
	// private String from_where = "";

	// private NotificationManager mNotificationManager;
	// private RemoteViews mRemoteViews;
	// private PendingIntent mPendingIntent;
	// private Intent mIntent;
	// private Notification notification;
	// public DownLoadFile downLoadFile ;

	public static boolean canNotRefresh = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_update);

		// dismissDialog();

		resCode = getIntent().getStringExtra("resCode");
		update_url = getIntent().getStringExtra("update_url");
		isStart = getIntent().getBooleanExtra("from_start", false);
		update_content = getIntent().getStringExtra("update_content");
		resDesc = getIntent().getStringExtra("resDesc") + update_content;

		if (getIntent().getBooleanExtra("from_notification", false)) {
			if (SharePre.getApkUrl(this).equals("")) {
				finish();
				return;
			}
			update();
		} else {
			fromUpdate();
		}
	}

	@Override
	protected void onDestroy() {
		// stopUpload();
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	private void canRefresh() {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		ComponentName cnTop = am.getRunningTasks(1).get(0).topActivity;
		ComponentName cnBottom = am.getRunningTasks(1).get(0).baseActivity;
		if (cnTop.getClassName().contains("UpdateActivity") && cnBottom.getClassName().contains("MainProductActivity")) {
			canNotRefresh = true;
		}
	}

	public static final String NO_NEED_TO_UPDATE = "1390";
	public static final String RECOMMEND_UPDATING = "1391";
	public static final String ENFORCE_UPDATING = "1392";

	private void fromUpdate() {
		// 从升级进入
		if (resCode != null && !resCode.equals("")) {
			// 不需要升级
			if (resCode.equals(NO_NEED_TO_UPDATE) || resCode.equals("500")) {
				if (!isStart) {
					// 从更多 进入升级页面
					if (resCode.equals("500")) {
						resDesc = getResources().getString(R.string.str_more_update_no_need_update);
					}

					updateDialog(resDesc, R.string.confirm, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SharePre.setUpdateInfo(UpdateActivity.this, "");
							finish();
						}
					}, -1, null);

					// showConfirm1("不需要升级！", null);
				}
			} else if (resCode.equals(RECOMMEND_UPDATING)) {
				// 建议升级
				updateDialog(resDesc, R.string.str_update, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharePre.setUpdateInfo(UpdateActivity.this, "");
						// mNotificationManager.notify(0, notification);
						startUpload();
						finish();
					}
				}, R.string.str_notupdate, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharePre.setUpdateInfo(UpdateActivity.this, "");
						finish();
					}
				});
			} else if (resCode.equals(ENFORCE_UPDATING)) {
				// 强制升级
				updateDialog(resDesc, R.string.str_update, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharePre.setUpdateInfo(UpdateActivity.this, "");
						canRefresh();
						// mNotificationManager.notify(0, notification);
						// downLoadFile.downFile();
						startUpload();
						SharePre.setUpdateInfo(UpdateActivity.this, "");
						finish();
						BeginApplication.exitClient();
					}
				}, R.string.str_exitapp, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharePre.setUpdateInfo(UpdateActivity.this, "");
						finish();
						BeginApplication.exitClient();
					}
				});
			} else {
				if (!isStart) {
					updateDialog(resDesc, R.string.confirm, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SharePre.setUpdateInfo(UpdateActivity.this, "");
							finish();
						}

					}, -1, null);
				}
			}
		}
	}

	private void updateDialog(String msg, int btn1, DialogInterface.OnClickListener ocl1, int btn2, DialogInterface.OnClickListener ocl2) {
		Builder builder = new AlertDialog.Builder(this).setCancelable(false).setTitle(R.string.str_prompt).setMessage(msg).setPositiveButton(getResources().getString(btn1), ocl1).setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return true;
			}
		});

		if (btn2 != -1 && ocl2 != null) {
			builder.setNegativeButton(getResources().getString(btn2), ocl2);
		}
		// builder.g.setCanceledOnTouchOutside(false);
		builder.show();
	}

	private void startUpload() {
		// bindService(new Intent(UpdateActivity.this,
		// DownloadService.class), mConnection, Context.BIND_AUTO_CREATE);
		// mIsBound = true;

		Intent intent = new Intent();
		intent.setClass(this, DownloadService.class);
		intent.putExtra("url", update_url);
		this.startService(intent);
	}

	@SuppressWarnings("unused")
	private void stopUpload() {
		unbindService(mConnection);
	}

	// private boolean mIsBound;
	@SuppressWarnings("unused")
	private DownloadService downloadService;
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			downloadService = ((DownloadService.LocalBinder) service).getService();
			// downloadService.downFile(update_url);
		}

		public void onServiceDisconnected(ComponentName className) {
			downloadService = null;
		}
	};

	public void update() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();

		if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			chmod("777", Util.getUpdate_app_path(this));
			chmod("777", SharePre.getApkUrl(this));
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(SharePre.getApkUrl(this))), "application/vnd.android.package-archive");
		startActivity(intent);
		finish();
		SharePre.setApkUrl(this, "");
	}

	private void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
