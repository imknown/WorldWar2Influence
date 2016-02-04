package com.dengdeng123.blowupplane.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.dengdeng123.blowupplane.R;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;

/**
 * Author: wyouflf Date: 13-11-12 Time: 上午10:24
 */
public class BitmapHelp
{
	private BitmapHelp()
	{
	}

	private static BitmapUtils bitmapUtils;

	/**
	 * BitmapUtils不是单例的 根据需要重载多个获取实例的方法
	 * 
	 * @param appContext
	 *            application context
	 * @return
	 */
	public static BitmapUtils getBitmapUtils(Context appContext)
	{
		if (bitmapUtils == null)
		{
			bitmapUtils = new BitmapUtils(appContext);

			bitmapUtils.configDefaultLoadingImage(R.drawable.nothing);
			bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_launcher);
			bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);

			// 设置最大宽高, 不设置时更具控件属性自适应.
			bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(appContext).scaleDown(3));
		}

		return bitmapUtils;
	}
}
