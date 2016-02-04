package com.dengdeng123.blowupplane.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.dengdeng123.blowupplane.util.DisplayUtil;

/** 所有操作的单位都是 度(°) */
public class ProcessDashboardImageView extends ImageView {

	private Paint paintTransparentBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintWhite = new Paint(Paint.ANTI_ALIAS_FLAG);

	private RectF arcRectF = new RectF();

	private boolean hasInitArcRectF = false;

	/** 左侧 最小进度, 闭区间 */
	public static final int MIN_PROCESS = -135;

	/** 右侧 最大进度, 闭区间 */
	public static final int MAX_PROCESS = 135;

	public ProcessDashboardImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paintTransparentBlack.setColor(Color.parseColor("#33000000"));
		paintTransparentBlack.setStyle(Paint.Style.FILL_AND_STROKE);
		// paintTransparentBlack.setStrokeWidth(2);

		paintWhite.setColor(Color.WHITE);
		paintWhite.setStyle(Paint.Style.FILL_AND_STROKE);
		paintWhite.setStrokeWidth(2);
	}

	public void setProcessDegrees(float processDegrees) {
		this.processDegrees = processDegrees;
	}

	/** 当前的进度 */
	private float processDegrees = MIN_PROCESS;

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (!hasInitArcRectF) {
			arcRectF = new RectF(getLeft(), getTop(), getRight(), getBottom());
		}

		float startAngle = getDegreesForCanvas(MIN_PROCESS);// 起始角度
		float sweepAngle = processDegrees - MIN_PROCESS;// 扫过的角度
		canvas.drawArc(arcRectF, startAngle, sweepAngle, true, paintTransparentBlack);

		double[] start_location = new double[] { getWidth() / 2, getHeight() / 2 };
		float degrees = processDegrees;
		double distance = getWidth() / 2 + 10;
		int[] end_location = DisplayUtil.getDestinationLocation(start_location, degrees, distance);
		canvas.drawLine(getWidth() / 2, getHeight() / 2, end_location[0], end_location[1], paintWhite);

		// invalidate(); // 必须在UI线程刷新
		// postInvalidate();// 可以在子线程进行刷新
	}

	/**
	 * 获取 画布使用的角度
	 * 
	 * @param 当前的进度
	 *            控件的角度
	 * @return 画布使用的角度
	 */
	private float getDegreesForCanvas(float degrees) {
		float degreesForCanvas = degrees - 90;

		return degreesForCanvas;
	}
}