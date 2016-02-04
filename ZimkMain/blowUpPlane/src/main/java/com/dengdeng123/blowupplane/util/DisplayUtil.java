package com.dengdeng123.blowupplane.util;

import java.lang.reflect.Method;

import com.dengdeng123.blowupplane.widget.ProcessDashboardImageView;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;

/**
 * densityDpi 表示, 每英寸多少个像素点,<br/>
 * density = densityDpi / 160 (谷歌定义的标准为160),<br/>
 * density = resolution (分辨率) / Screen size (屏幕尺寸)
 * 
 * @author imknown
 */
public class DisplayUtil {
	/** 屏幕测量 */
	private DisplayMetrics dm;

	/** 屏幕分辨率的宽 */
	private int displayWidth;

	/** 获取屏幕分辨率的高 */
	private int displayHeight;

	private Context context;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 */
	public DisplayUtil(Context context) {
		this.context = context;
		this.dm = context.getResources().getDisplayMetrics();
		this.displayWidth = dm.widthPixels;// 获取屏幕宽
		this.displayHeight = dm.heightPixels;// 获取屏幕高
	}

	/** 获取屏幕分辨率的宽 */
	public int getDisplayWidth() {
		return displayWidth;
	}

	/** 获取屏幕分辨率的高 */
	public int getDisplayHeight() {
		return displayHeight;
	}

	public float dip2px_M1(float dipValue) {
		final float density = context.getResources().getDisplayMetrics().density;
		return (dipValue * density + 0.5f * (dipValue >= 0 ? 1 : -1));
	}

	public float px2dip_M1(float pxValue) {
		final float density = context.getResources().getDisplayMetrics().density;
		return (pxValue / density + 0.5f * (pxValue >= 0 ? 1 : -1));
	}

	public float dip2px_M2(float dipValue) {
		final float densityDpi = context.getResources().getDisplayMetrics().densityDpi;
		return (dipValue * (densityDpi / 160));
	}

	public float px2dip_M2(float pxValue) {
		final float densityDpi = context.getResources().getDisplayMetrics().densityDpi;
		return ((pxValue * 160) / densityDpi);
	}

	public float dip2px_M3(float dipValue) {
		final Resources resources = context.getResources();

		int unit = TypedValue.COMPLEX_UNIT_DIP;

		DisplayMetrics displayMetrics = resources.getDisplayMetrics();

		float fPx = TypedValue.applyDimension(unit, dipValue, displayMetrics);
		// int iPx = Math.round(fPx);

		return fPx;
	}

	public float px2dip_M3(float dipValue) {
		final Resources resources = context.getResources();

		int unit = TypedValue.COMPLEX_UNIT_PX;

		DisplayMetrics displayMetrics = resources.getDisplayMetrics();

		float fDip = TypedValue.applyDimension(unit, dipValue, displayMetrics);
		// int iDip = Math.round(fDip);

		return fDip;
	}

	public String getDpi() {
		String dpi = null;

		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();

		DisplayMetrics dm = new DisplayMetrics();

		try {
			Class<?> c = Class.forName("android.view.Display");

			Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);

			method.invoke(display, dm);

			dpi = dm.widthPixels + " x " + dm.heightPixels;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dpi;
	}

	// =====

	/**
	 * 获取 状态栏高度
	 * 
	 * @param a
	 *            当前Activity对象
	 * @return 状态栏高度(px)
	 */
	public static int getStatusBarHeight(Activity a) {
		Rect frame = new Rect();
		a.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;// 状态栏高度

		return statusBarHeight;
	}

	/**
	 * 获取 标题栏高度
	 * 
	 * @param a
	 *            当前Activity对象
	 * @return 标题栏高度(px)
	 */
	public static int getTitleBarHeight(Activity a) {
		View v = a.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
		int contentTop = v.getTop();
		int titleBarHeight = contentTop - getStatusBarHeight(a);// 标题栏高度

		if (titleBarHeight < 0) {
			titleBarHeight = 0;
		}

		return titleBarHeight;
	}

	/**
	 * 根据起始坐标 和 矢量, 计算出 目标坐标
	 * 
	 * @param start_location
	 *            起始坐标
	 * @param distance
	 *            向量的长度
	 * 
	 * @return 目标坐标
	 */
	public static int[] getDestinationLocation(double[] start_location, float degrees, double distance) {
		double x = start_location[0];
		double y = start_location[1];

		double cotFigure = MathUtil.cot(degrees);

		int flagX = 1;
		int flagY = 1;

		if (ProcessDashboardImageView.MIN_PROCESS <= degrees && degrees < -90) {
			flagX = 1;
			flagY = -1;
		} else if (-90 < degrees && degrees <= 0) {
			flagX = 1;// 等于 0 的情况 属于无穷大, 交给Java 自己就能处理
			flagY = 1;
		} else if (0 < degrees && degrees <= 90) {
			flagX = -1;
			flagY = 1;
		} else if (90 < degrees && degrees <= ProcessDashboardImageView.MAX_PROCESS) {
			flagX = -1;
			flagY = -1;
		} else {
			// 未知情况
		}

		// TODO 此处可以化简, 但是没有化简明白
		double a = x - flagX * distance / Math.sqrt(Math.pow(cotFigure, 2) + 1);
		double b = y - flagY * distance / Math.sqrt(Math.pow(cotFigure, -2) + 1);

		int[] end_location = new int[] { (int) a, (int) b };

		return end_location;
	}
}
