package com.dengdeng123.blowupplane.module.game.story;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_choose_map)
public class ChooseMapActivity extends BaseActivity {

	@ViewInject(R.id.action_bar_title_txtv)
	private TextView action_bar_title_txtv;

	@ViewInject(R.id.map_ussr_germany_imgv)
	private ImageView map_ussr_germany_imgv;

	@ViewInject(R.id.map_ussr_germany_drop_down_curtain_imgv)
	private LinearLayout map_ussr_germany_drop_down_curtain_imgv;

	@ViewInject(R.id.map_western_europe_drop_down_curtain_imgv)
	private LinearLayout map_western_europe_drop_down_curtain_imgv;

	@ViewInject(R.id.map_pacific_drop_down_curtain_imgv)
	private LinearLayout map_pacific_drop_down_curtain_imgv;

	@ViewInject(R.id.map_china_drop_down_curtain_imgv)
	private LinearLayout map_china_drop_down_curtain_imgv;

	/** 是否可以点击, 防止同时点击多个 */
	public static boolean singleClickLocker = true;

	/** 帷幕落下的控件 */
	public static LinearLayout ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action_bar_title_txtv.setText(getString(R.string.story_mode));
	}

	@OnClick({ R.id.map_ussr_germany_imgv, R.id.map_pacific_imgv, R.id.map_china_imgv, R.id.map_western_europe_imgv })
	private void gotoChoosePosition(final View view) {

		if (!singleClickLocker) {
			return;
		}

		singleClickLocker = false;

		Animation animation = AnimationUtils.loadAnimation(this, R.anim.drop_down_curtain);

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

				Intent intent = new Intent(ChooseMapActivity.this, ChoosePositionActivity.class);
				intent.putExtra("mapId", view.getId());
				startActivity(intent);
			}
		});

		int mapId = view.getId();
		int mapCurtainId = 0;

		if (mapId == R.id.map_ussr_germany_imgv) {
			ll = map_ussr_germany_drop_down_curtain_imgv;
			mapCurtainId = R.string.ussr_germany_battlefield_vertical;
		} else if (mapId == R.id.map_pacific_imgv) {
			ll = map_pacific_drop_down_curtain_imgv;
			mapCurtainId = R.string.pacific_battlefield_vertical;
		} else if (mapId == R.id.map_china_imgv) {
			ll = map_china_drop_down_curtain_imgv;
			mapCurtainId = R.string.china_battlefield_vertical;
		} else if (mapId == R.id.map_western_europe_imgv) {
			ll = map_western_europe_drop_down_curtain_imgv;
			mapCurtainId = R.string.western_europe_battlefield_vertical;
		}

		((TextView) (ll.findViewById(R.id.map_curtain_txtv))).setText(mapCurtainId);

		ll.setVisibility(View.VISIBLE);
		ll.startAnimation(animation);
	}

	@OnClick(R.id.action_bar_left_back_imgv)
	private void leftBack(View view) {
		finish();

		// overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		singleClickLocker = true;// 防止点击了一个地图之后, 快速点击了返回
	}
}