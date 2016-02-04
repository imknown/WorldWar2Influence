package com.dengdeng123.blowupplane.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class MyFrameAnimation {
	private ImageView mImageView;

	/** 所有帧的图片 */
	private int[] mFrameRess;

	/** 两帧之间时间, 单位 毫秒 */
	private int[] mDurations;

	/** 两帧之间时间(相同的), 单位 毫秒 */
	private int mDuration;

	/** 最后一张 */
	private int mLastFrameNo;

	/** 最后一张的延迟时间, 单位 毫秒 */
	private long mBreakDelay;

	/** 是否循环播放 */
	private boolean loopPlay = true;

	/** 是否可以播放 */
	private boolean canPlay = true;

	/** 是否正在播放 */
	private boolean isPlaying = false;

	private Context mContext;

	/** 播放次数 */
	private int playTimes = 0;

	private PlayObserver iPlay;

	public void setIPlay(PlayObserver iPlay) {
		this.iPlay = iPlay;
	}

	public void setContext(Context context) {
		this.mContext = context;
	}

	public void setLoopPlay(boolean loopPlay) {
		this.loopPlay = loopPlay;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public int getPlayTimes() {
		return playTimes;
	}

	/**
	 * 
	 * @param pImageView
	 *            图片控件
	 * @param pFrameRess
	 *            所有帧的图片
	 * @param pDurations
	 *            两帧之间时间, 单位 毫秒
	 * @param playAtOnce
	 *            是否立即执行动画
	 */
	public MyFrameAnimation(ImageView pImageView, int[] pFrameRess, int[] pDurations, boolean playAtOnce) {
		mImageView = pImageView;
		mFrameRess = pFrameRess;
		mDurations = pDurations;
		mLastFrameNo = pFrameRess.length - 1;

		mImageView.setBackgroundResource(mFrameRess[0]);

		if (playAtOnce) {
			play(1);
		}
	}

	/**
	 * 
	 * @param pImageView
	 *            图片控件
	 * @param pFrameRess
	 *            所有帧的图片
	 * @param pDuration
	 *            两帧之间时间(相同的), 单位 毫秒
	 * @param playAtOnce
	 *            是否立即执行动画
	 */
	public MyFrameAnimation(ImageView pImageView, int[] pFrameRess, int pDuration, boolean playAtOnce) {
		mImageView = pImageView;
		mFrameRess = pFrameRess;
		mDuration = pDuration;
		mLastFrameNo = pFrameRess.length - 1;

		mImageView.setBackgroundResource(mFrameRess[0]);

		if (playAtOnce) {
			playConstant(1);
		}
	}

	/**
	 * 
	 * @param pImageView
	 *            图片控件
	 * @param pFrameRess
	 *            所有帧的图片
	 * @param pDuration
	 *            两帧之间时间(相同的), 单位 毫秒
	 * @param pBreakDelay
	 *            最后一张的延迟时间, 单位 毫秒
	 * @param playAtOnce
	 *            是否立即执行动画
	 */
	public MyFrameAnimation(ImageView pImageView, int[] pFrameRess, int pDuration, long pBreakDelay, boolean playAtOnce) {
		mImageView = pImageView;
		mFrameRess = pFrameRess;
		mDuration = pDuration;
		mLastFrameNo = pFrameRess.length - 1;
		mBreakDelay = pBreakDelay;

		mImageView.setBackgroundResource(mFrameRess[0]);

		if (playAtOnce) {
			playConstant(1);
		}
	}

	private Bitmap bCache;

	private BitmapFactory.Options options;

	@SuppressWarnings("deprecation")
	private void initOptions() {
		if (options == null) {
			options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
		}
	}

	/**
	 * 均匀播放
	 * 
	 * @param pFrameNo
	 *            从第几帧开始
	 */
	public void playConstant(final int pFrameNo) {
		if (!checkStatus()) {
			return;
		}

		++playTimes;
		if (playTimes == 1) {
			iPlay.onPlayStart();
		}

		mImageView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!checkStatus()) {
					return;
				}

				initOptions();

				bCache = BitmapFactory.decodeStream(mContext.getResources().openRawResource(mFrameRess[pFrameNo]), null, options);
				// bCache = BitmapFactory.decodeResource(mContext.getResources(), mFrameRess[pFrameNo], options);
				mImageView.setImageBitmap(bCache);
				// mImageView.setBackgroundResource(mFrameRess[pFrameNo]);

				if (bCache != null && !bCache.isRecycled()) {
					// bCache.recycle();
					bCache = null;
				}

				System.gc();

				if (pFrameNo == mLastFrameNo) {
					if (loopPlay) {
						isPlaying = true;
						playConstant(0);
					} else {
						isPlaying = false;

						if (bCache != null && !bCache.isRecycled()) {
							// bCache.recycle();
							bCache = null;
						}
						System.gc();

						iPlay.onPlayEnd();
					}
				} else {
					isPlaying = true;
					playConstant(pFrameNo + 1);
				}
			}
		}, (pFrameNo == mLastFrameNo && mBreakDelay > 0) ? mBreakDelay : mDuration);
	}

	/**
	 * 自定义速度播放
	 * 
	 * @param pFrameNo
	 *            从第几帧开始
	 */
	public void play(final int pFrameNo) {
		if (!checkStatus()) {
			return;
		}

		++playTimes;
		if (playTimes == 1) {
			iPlay.onPlayStart();
		}

		mImageView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!checkStatus()) {
					return;
				}

				initOptions();

				bCache = BitmapFactory.decodeStream(mContext.getResources().openRawResource(mFrameRess[pFrameNo]), null, options);
				// bCache = BitmapFactory.decodeResource(mContext.getResources(), mFrameRess[pFrameNo], options);
				mImageView.setImageBitmap(bCache);
				// mImageView.setBackgroundResource(mFrameRess[pFrameNo]);

				if (pFrameNo == mLastFrameNo) {
					if (loopPlay) {
						isPlaying = true;
						play(0);
					} else {
						isPlaying = false;

						if (bCache != null && !bCache.isRecycled()) {
							// bCache.recycle();
							bCache = null;
						}
						System.gc();

						iPlay.onPlayEnd();
					}
				} else {
					isPlaying = true;
					play(pFrameNo + 1);
				}
			}
		}, mDurations[pFrameNo]);
	}

	private boolean checkStatus() {
		if (!canPlay) {
			iPlay.onPlayInterrupt();
			return false;
		}

		return true;
	}

	/** 停止播放 */
	public void stop() {
		canPlay = false;
	}

	public abstract class PlayObserver {
		public void onPlayStart() {
		}

		public void onPlayInterrupt() {
		}

		public void onPlayEnd() {
		}
	}
}
