package com.dengdeng123.blowupplane.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.util.FileUtil;

public class MyGifView extends View {
	private long movieStart;
	private Movie movie;

	public MyGifView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		// getResources().openRawResource(R.drawable.explode);
		// getAssets().open("splash.gif");

		// try {
		// InputStream is = FileUtil.getInputStream(context, "img/splash.gif");
		// movie = Movie.decodeStream(is);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		movie = Movie.decodeStream(FileUtil.getInputStream(context, R.drawable.explode));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		long curTime = android.os.SystemClock.uptimeMillis();

		// 第一次播放
		if (movieStart == 0) {
			movieStart = curTime;
		}

		if (movie != null) {
			int duraction = movie.duration();
			int relTime = (int) ((curTime - movieStart) % duraction);
			movie.setTime(relTime);
			movie.draw(canvas, 0, 0);

			// 强制重绘
			invalidate();
		}

		super.onDraw(canvas);
	}
}