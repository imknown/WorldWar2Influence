package com.dengdeng123.blowupplane.update;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.util.SharePre;
import com.dengdeng123.blowupplane.util.Util;

public class DownloadService extends Service {
	private String url;
	Handler handler = new Handler();

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		url = intent.getStringExtra("url");
		initNotify();
		downFile();
	}

	public void downFile() {
		SharePre.setApkUrl(this, "");
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long len = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {

						File cache = new File(Util.getUpdate_app_path(DownloadService.this));
						if (!cache.exists()) {
							cache.mkdirs();
						}

						File file = new File(Util.getUpdate_app_path(DownloadService.this) + url.substring(url.lastIndexOf("/") + 1));
						if (file.exists()) {
							file.delete();
						}
						file.createNewFile();
						fileOutputStream = new FileOutputStream(file);

						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						int percent = 1;
						int cur = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							cur += ch;
							if (count > (len / 100)) {
								count = 0;
								sendUpdateBroadcast(percent);
								percent++;
							} else if (cur == len) {
								sendUpdateBroadcast(100);
							}
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					// 下载成功 记录APK url
					SharePre.setApkUrl(DownloadService.this, Util.getUpdate_app_path(DownloadService.this) + url.substring(url.lastIndexOf("/") + 1));
					update();
					sendUpdateBroadcast(-2);
				} catch (final Throwable t) {
					t.printStackTrace();
					sendUpdateBroadcast(-1);
					handler.post(new Runnable() {
						@Override
						public void run() {
							// Toast.makeText(DownloadService.this, t.toString(), Toast.LENGTH_LONG).show();
							// Toast.makeText(DownloadService.this, getString(R.string.tips_network_error_2), Toast.LENGTH_LONG).show();
							BeginApplication.showMessageToast(R.string.tips_network_error_2);
						}
					});
				} finally {
					stopSelf();
				}
			}
		}.start();
	}

	private NotificationManager mNotificationManager;
	private RemoteViews mRemoteViews;
	private Notification notification;

	private PendingIntent mPendingIntent;
	private Intent mIntent;

	@SuppressWarnings("deprecation")
	private void initNotify() {
		notification = new Notification(R.drawable.ic_launcher, this.getResources().getString(R.string.str_more_update_isupdating), System.currentTimeMillis());
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_tips);

		mIntent = new Intent(this, UpdateActivity.class);
		mIntent.putExtra("from_notification", true);

		mPendingIntent = PendingIntent.getActivity(this, 0, mIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(this, "", "", mPendingIntent);

		notification.icon = R.drawable.ic_launcher;
		mRemoteViews.setImageViewResource(R.id.image, R.drawable.ic_launcher);

		mRemoteViews.setTextViewText(R.id.tip, getResources().getString(R.string.str_more_update_notification_tips));
		notification.contentView = mRemoteViews;
		mNotificationManager.notify(0, notification);
	}

	private void sendUpdateBroadcast(long percent) {
		// Intent intent = new Intent("O2OCOMM_UPDATE_APP");
		// intent.putExtra("percent", percent);
		if (percent % 5 == 0) {
			if ((int) percent / 5 == 20) {
				mRemoteViews.setTextViewText(R.id.tip, getResources().getString(R.string.str_more_update_notification_download_sucess));
			}
			mRemoteViews.setProgressBar(R.id.progressBar, 20, (int) percent / 5, false);
			mNotificationManager.notify(0, notification);
		}
		// sendBroadcast(intent);
	}

	private void update() {
		if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			// String[] args1 = { "777", Util.getShare_app_path() };
			// exec(args1);
			// String[] args2 = { "777", Util.getShare_app_path() + url.substring(url.lastIndexOf("/")+1) };
			// exec(args2);

			chmod("777", Util.getUpdate_app_path(this));
			chmod("777", Util.getUpdate_app_path(this) + url.substring(url.lastIndexOf("/") + 1));
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(Util.getUpdate_app_path(this) + url.substring(url.lastIndexOf("/") + 1))), "application/vnd.android.package-archive");
		startActivity(intent);
		stopSelf();
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

	@SuppressWarnings("unused")
	private String exec(String[] args) {
		String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			baos.write('\n');
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}
		return result;
	}

	public class LocalBinder extends Binder {
		DownloadService getService() {
			return DownloadService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();
}
