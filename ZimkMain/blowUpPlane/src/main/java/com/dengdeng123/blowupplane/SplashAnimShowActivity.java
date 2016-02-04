package com.dengdeng123.blowupplane;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.module.CoverActivity;
import com.dengdeng123.blowupplane.widget.MyFrameAnimation;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_anim_show)
public class SplashAnimShowActivity extends BaseActivity {

	@ViewInject(R.id.show_imgv)
	private ImageView show_imgv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// AnimationDrawable animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.splash_anim_show);

		// ============================

		showSplashAnim();
	}

	private void showSplashAnim() {
		int[] resIds = new int[] { R.drawable.splash_01, R.drawable.splash_02, R.drawable.splash_03, R.drawable.splash_04, R.drawable.splash_05, R.drawable.splash_06, R.drawable.splash_07, R.drawable.splash_08, R.drawable.splash_09, R.drawable.splash_10, R.drawable.splash_11, R.drawable.splash_12, R.drawable.splash_13, R.drawable.splash_14, R.drawable.splash_15, R.drawable.splash_16, R.drawable.splash_17, R.drawable.splash_18, R.drawable.splash_19 };

		final MyFrameAnimation sa = new MyFrameAnimation(show_imgv, resIds, 50, false);
		sa.setContext(this);
		sa.setIPlay(sa.new PlayObserver() {
			@Override
			public void onPlayEnd() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						endSplashAnimation();

						// Intent intent = new Intent(SplashAnimShowActivity.this, CoverActivity.class);
						// startActivity(intent);
						//
						// finish();
					}
				}, 1000);
			}
		});
		sa.setLoopPlay(false);
		sa.playConstant(1);

		// show_imgv.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// sa.stop();
		// }
		// });
	}

	private void endSplashAnimation() {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_anim_rush);

		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// view.clearAnimation();// 写的话, onAnimationEnd 会 再执行一遍

				Intent intent = new Intent(SplashAnimShowActivity.this, CoverActivity.class);
				startActivity(intent);

				finish();
			}
		});

		show_imgv.startAnimation(animation);
	}

	// public void onWindowFocusChanged(boolean hasFocus) {
	// if (hasFocus) {
	// // 动画是否正在运行
	// if (animationDrawable.isRunning()) {
	// // 停止动画播放
	// animationDrawable.stop();
	// } else {
	// // 开始或者继续动画播放
	// animationDrawable.start();
	// }
	// }
	// }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}
