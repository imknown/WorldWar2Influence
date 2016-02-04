package com.dengdeng123.blowupplane.module.game.story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.base.Constant;
import com.dengdeng123.blowupplane.entity.StoryModeStageEntity;
import com.dengdeng123.blowupplane.module.game.MainGameActivity;
import com.dengdeng123.blowupplane.util.DisplayUtil;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_choose_position)
public class ChoosePositionActivity extends BaseActivity {

	@ViewInject(R.id.action_bar_title_txtv)
	private TextView action_bar_title_txtv;

	@ViewInject(R.id.map_bg_imgv)
	private ImageView map_bg_imgv;

	/** 第1小节的图标 */
	@ViewInject(R.id.map_stage_1)
	private ImageView map_stage_1;

	/** 第2小节的图标 */
	@ViewInject(R.id.map_stage_2)
	private ImageView map_stage_2;

	/** 第3小节的图标 */
	@ViewInject(R.id.map_stage_3)
	private ImageView map_stage_3;

	/** 第4小节的图标 */
	@ViewInject(R.id.map_stage_4)
	private ImageView map_stage_4;

	/** 第5小节的图标 */
	@ViewInject(R.id.map_stage_5)
	private ImageView map_stage_5;

	private List<ImageView> mapStageImgVs;

	/** 选择的地图的Id */
	private int mapId;

	private int[][] ussr_germany_position_in_pic = { { 922, 413 }, { 785, 324 }, { 1079, 457 }, { 798, 533 }, { 536, 561 } };
	private int[][] pacific_position_in_pic = { { 386, 567 }, { 1007, 406 }, { 733, 805 }, { 703, 519 }, { 566, 310 } };
	private int[][] china_position_in_pic = { { 969, 628 }, { 610, 360 }, { 878, 518 }, { 821, 440 }, { 704, 693 } };
	private int[][] western_europe_position_in_pic = { { 539, 358 }, { 431, 449 }, { 555, 643 }, { 653, 415 }, { 705, 558 } };

	/** 地图图片的 像素宽 */
	private float picWidth = 1280F;

	/** 地图图片的 像素高 */
	private float picHeight = 960F;

	private List<StoryModeStageEntity> storyModeStageEntities;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mapStageImgVs = new ArrayList<ImageView>();
		mapStageImgVs.add(map_stage_1);
		mapStageImgVs.add(map_stage_2);
		mapStageImgVs.add(map_stage_3);
		mapStageImgVs.add(map_stage_4);
		mapStageImgVs.add(map_stage_5);

		mapId = getIntent().getIntExtra("mapId", 0);

		int titleStringId = R.string.app_name;
		int mapResId = R.drawable.map_ussr_germany;
		int[][] positions = new int[5][2];

		if (mapId == R.id.map_ussr_germany_imgv) {
			titleStringId = R.string.ussr_germany_battlefield;
			mapResId = R.drawable.map_ussr_germany;
			positions = ussr_germany_position_in_pic;
		} else if (mapId == R.id.map_pacific_imgv) {
			titleStringId = R.string.pacific_battlefield;
			mapResId = R.drawable.map_pacific;
			positions = pacific_position_in_pic;
		} else if (mapId == R.id.map_china_imgv) {
			titleStringId = R.string.china_battlefield;
			mapResId = R.drawable.map_china;
			positions = china_position_in_pic;
		} else if (mapId == R.id.map_western_europe_imgv) {
			titleStringId = R.string.western_europe_battlefield;
			mapResId = R.drawable.map_western_europe;
			positions = western_europe_position_in_pic;
		}

		action_bar_title_txtv.setText(getString(titleStringId));
		map_bg_imgv.setImageResource(mapResId);

		initPositions(positions);

		ChooseMapActivity.singleClickLocker = true;
		ChooseMapActivity.ll.setVisibility(View.GONE);

		storyModeStageEntities = new StoryModeStageDao().findStoryModeStageByMapId(mapId);
		int size = storyModeStageEntities.size();

		StoryModeStageEntity storyModeStageEntitiy = null;

		for (int i = 0; i < size; i++) {
			storyModeStageEntitiy = storyModeStageEntities.get(i);

			ImageView iv = mapStageImgVs.get(i);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", storyModeStageEntitiy.getId());
			map.put("idNext", (i < size - 1) ? storyModeStageEntities.get(i + 1).getId() : StoryModeStageEntity.NO_NEXT_STAGE);
			map.put("fatherId", storyModeStageEntitiy.getFatherId());
			iv.setTag(map);

			if (storyModeStageEntitiy.getLockedState() == 1) {
				continue;
			} else {
				iv.setVisibility(View.GONE);// == 0, 未解锁, 不显示
			}
		}
	}

	private void initPositions(int[][] positions) {
		DisplayUtil du = new DisplayUtil(this);

		float offset = du.dip2px_M1(30 / 2);// sniper 控件的一半儿
		int bgWidth = du.getDisplayWidth() * 2;
		int viewHeight = du.getDisplayHeight() - DisplayUtil.getStatusBarHeight(this) - (int) du.dip2px_M1(40);// action_bar 的 40dip

		FrameLayout.LayoutParams bgLp = (FrameLayout.LayoutParams) map_bg_imgv.getLayoutParams();
		bgLp.width = bgWidth;
		bgLp.gravity = Gravity.TOP | Gravity.LEFT;
		map_bg_imgv.setLayoutParams(bgLp);
		map_bg_imgv.requestLayout();

		int size = mapStageImgVs.size();
		for (int i = 0; i < size; i++) {
			ImageView iv = mapStageImgVs.get(i);

			FrameLayout.LayoutParams sniperLp = (FrameLayout.LayoutParams) iv.getLayoutParams();
			sniperLp.leftMargin = (int) (positions[i][0] / picWidth * bgWidth - offset);
			sniperLp.topMargin = (int) (positions[i][1] / picHeight * viewHeight - offset);
			sniperLp.gravity = Gravity.TOP | Gravity.LEFT;
			iv.setLayoutParams(sniperLp);
			iv.requestLayout();
		}
	}

	int REQUEST_CODE = 0;

	@OnClick({ R.id.map_stage_1, R.id.map_stage_2, R.id.map_stage_3, R.id.map_stage_4, R.id.map_stage_5 })
	private void gotoStoryMode(View view) {
		BeginApplication.showLoadingPopupWindow(view);

		int stageOrder = 0;
		int viewId = view.getId();
		if (viewId == R.id.map_stage_1) {
			stageOrder = 1;
		} else if (viewId == R.id.map_stage_2) {
			stageOrder = 2;
		} else if (viewId == R.id.map_stage_3) {
			stageOrder = 3;
		} else if (viewId == R.id.map_stage_4) {
			stageOrder = 4;
		} else if (viewId == R.id.map_stage_5) {
			stageOrder = 5;
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) view.getTag();
		int id = (Integer) map.get("id");
		int idNext = (Integer) map.get("idNext");
		int fatherId = (Integer) map.get("fatherId");

		Intent intent = new Intent(this, MainGameActivity.class);
		intent.putExtra("id", id);
		intent.putExtra("idNext", idNext);
		intent.putExtra("fatherId", fatherId);
		intent.putExtra("mapId", mapId);
		intent.putExtra("stageOrder", stageOrder);
		intent.putExtra("gameMode", Constant.STORY_MODE);
		startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	// 当结果返回后判断并执行操作
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Bundle extras = intent.getExtras();

				if (extras != null) {
					int stageOrder = extras.getInt("stageOrder");
					int nextStageOrder = stageOrder + 1;

					int size = mapStageImgVs.size();
					if (stageOrder == size) {
						return;// 没有下一关了
					}

					ImageView iv = mapStageImgVs.get(nextStageOrder - 1);
					iv.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	@OnClick(R.id.action_bar_left_back_imgv)
	private void leftBack(View view) {
		finish();

		// overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}
}