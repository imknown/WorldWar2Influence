package com.dengdeng123.blowupplane.module.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.base.Constant;
import com.dengdeng123.blowupplane.domain.WebUrlDomain;
import com.dengdeng123.blowupplane.entity.PlaneOffsetEntity;
import com.dengdeng123.blowupplane.entity.StoryModeStageEntity;
import com.dengdeng123.blowupplane.module.game.story.HelperActivity;
import com.dengdeng123.blowupplane.module.game.story.StoryModeStageDao;
import com.dengdeng123.blowupplane.module.user.UserDao;
import com.dengdeng123.blowupplane.util.DisplayUtil;
import com.dengdeng123.blowupplane.util.ImageSplitter;
import com.dengdeng123.blowupplane.widget.ProcessDashboardImageView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.lidroid.xutils.view.annotation.event.OnTouch;

public class MainGameActivity extends BaseActivity {

	/** 用来展示Blocks */
	@ViewInject(R.id.gridView)
	private GridView gridView;

	/** 显示的炮弹类型 */
	@ViewInject(R.id.number_left_imgv)
	private ImageView number_left_imgv;

	/** 显示已击落的数量 */
	@ViewInject(R.id.plane_number_shootdown_real_txtv)
	private TextView plane_number_shootdown_real_txtv;

	/** 显示攻击类型的剩余数量 */
	@ViewInject(R.id.number_left_real_txtv)
	private TextView number_left_real_txtv;

	/** 显示任务进度 */
	@ViewInject(R.id.mission_real_txtv)
	private TextView mission_real_txtv;

	/** 显示奖励的详情 */
	@ViewInject(R.id.award_real_txtv)
	private TextView award_real_txtv;

	/** 显示奖励的详情 */
	@ViewInject(R.id.ordnance_imgv)
	private ImageView ordnance_imgv;

	/** 爆头剩余的数量 */
	@ViewInject(R.id.shoot_type_headshot_num_txtv)
	private TextView shoot_type_headshot_num_txtv;

	@ViewInject(R.id.up_icon_shoot_type_bomb_img)
	private ImageView up_icon_shoot_type_bomb_img;

	@ViewInject(R.id.shoot_type_bullet_imgv)
	private ImageView shoot_type_bullet_imgv;

	@ViewInject(R.id.shoot_type_bomb_imgv)
	private ImageView shoot_type_bomb_imgv;

	@ViewInject(R.id.shoot_type_imgv)
	private ImageView shoot_type_imgv;

	/** 动画图层 */
	@ViewInject(R.id.anim_lyt)
	private FrameLayout anim_lyt;

	private StoryModeStageDao stageDao = new StoryModeStageDao();
	private UserDao userDao = new UserDao();

	// ===================================================================

	/** 当前的炸弹类型 */
	private int shootType = Constant.SHOOT_TYPE_BULLET;

	/** 展示的数据 */
	private List<Map<String, Object>> gridviewDataList;

	/** 当前的机头坐标集合 */
	private List<String> mapHandpieceList = new ArrayList<String>();

	/** 当前的机身坐标集合 */
	private List<String> mapAirframeList = new ArrayList<String>();

	/** 当前击落了多少飞机 */
	private int planeNumShootdown;

	/** 当前炮弹剩余多少 */
	private int bulletNumLeft = 0;

	/** 当前炮弹用了多少 */
	private int bulletNumUsed = 0;

	/** 当前炮弹剩余多少 */
	private int bombNumLeft = BeginApplication.ue.getBombNum();

	/** 当前炮弹用了多少 */
	private int bombNumUsed = 0;

	/** 累积的奖励的炮弹数量 */
	private int bombAwardNumLeft = 0;

	// /** 累积的奖励的炮弹的使用数量 */
	// private int bombAwardNumUsed = 0;

	/** 当前爆头剩余多少 */
	private int headshotNumLeft = BeginApplication.ue.getHeadshotNum();

	/** 当前爆头用了多少 */
	private int headshotNumUsed = 0;

	/** 当前无尽模式闯过多少关 */
	private int infiniteModeStageNumRushacross = 0;

	/** 当前击落多少飞机. 击落了一定数量之后, 奖励若干炸弹 */
	private int awardNum;

	/** 适配器 */
	private MainGameGridAdapter mainGridAdapter;

	/** 横多少个 */
	private int rangeX;

	/** 纵多少个 */
	private int rangeY;

	/** 分解之后的小图片集合 */
	private List<ImageSplitter.ImagePiece> imagePieces;

	/** 后台处理地图加载, 之后显示 */
	private MapMakerTask mapMakerTask;

	// ===================================================================

	/** 游戏模式 */
	private String gameMode;

	/** 选择的地图的Id */
	private int mapId;

	/** 这一小节在数据库中的id */
	private int id;

	/** 下一小节在数据库中的id */
	private int idNext;

	/** 地图 */
	private int fatherId;

	/** 闯关模式的第几小节 */
	private int stageOrder;

	// ===================================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gameMode = getIntent().getStringExtra("gameMode");
		mapId = getIntent().getIntExtra("mapId", 0);
		stageOrder = getIntent().getIntExtra("stageOrder", 0);
		id = getIntent().getIntExtra("id", 0);
		idNext = getIntent().getIntExtra("idNext", 0);
		fatherId = getIntent().getIntExtra("fatherId", 0);

		if (Constant.STORY_MODE.equals(gameMode)) {
			setContentView(R.layout.activity_story_mode);
			bulletNumLeft = Constant.SHOOT_TYPE_BULLET_STORY_MODE_INIT_NUM;
		} else if (Constant.INFINITE_MODE.equals(gameMode)) {
			setContentView(R.layout.activity_infinite_mode);
			bulletNumLeft = Constant.SHOOT_TYPE_BULLET_INFINITE_MODE_INIT_NUM;
		}

		ViewUtils.inject(this);

		showBaseViewReals();

		initMap();
	}

	// 游戏中不可以进入商店了, 所以注释掉
	// @Override
	// protected void onResume() {
	// super.onResume();
	//
	// number_left_real_txtv.setText(String.valueOf(getCurrentShootTypeNumLeft()));
	// // headshotNumLeft;
	// }

	/** 初始化地图数据 */
	private void initMap() {
		mapMakerTask = new MapMakerTask();
		mapMakerTask.execute();
	}

	/** 后台处理地图加载, 之后显示 */
	private class MapMakerTask extends AsyncTask<Void, Void, Void> {

		/** 运行在UI线程中, 在调用 doInBackground() 之前执行 */
		@Override
		protected void onPreExecute() {
		}

		/** 后台运行的方法, 可以运行非UI线程，可以执行耗时的方法 */
		@Override
		protected Void doInBackground(Void... params) {
			startMakeMap();

			// publishProgress();
			return null;
		}

		/** 在 publishProgress() 被调用以后执行, 用于更新进度 */
		@Override
		protected void onProgressUpdate(Void... progresses) {
		}

		/** 运行在ui线程中, 在doInBackground()执行完毕后执行 */
		@Override
		protected void onPostExecute(Void result) {
			stopMakeMap();
		}

		/** 在取消执行中的任务时更改UI */
		@Override
		protected void onCancelled() {
		}
	}

	/** 显示基本数据 */
	private void showBaseViewReals() {
		plane_number_shootdown_real_txtv.setText(String.valueOf(planeNumShootdown));
		number_left_real_txtv.setText(String.valueOf(bulletNumLeft));

		if (Constant.INFINITE_MODE.equals(gameMode)) {
			shoot_type_headshot_num_txtv.setText(String.valueOf(headshotNumLeft));
		}

		if (Constant.INFINITE_MODE.equals(gameMode)) {
			mission_real_txtv.setText(0 + "/" + Constant.AWARD_RANGE);
			award_real_txtv.setText(String.valueOf(Constant.HOW_MANY_BOMB_TO_AWARD_WHEN_SHOOTDOWN_ENOUGH));
		}

		shoot_type_bullet_imgv.setVisibility(View.INVISIBLE);
		shoot_type_bomb_imgv.setVisibility(View.INVISIBLE);
	}

	/** 创建飞机坐标集合 */
	private void startMakeMap() {
		getMapDataLists();
		// getTestMapDataLists();
		makeBlocks();
	}

	/** 初始化创建 GridView 块儿 */
	private void makeBlocks() {
		splitBitmap();

		gridviewDataList = new ArrayList<Map<String, Object>>();

		Map<String, Object> map;
		String offset;// 坐标
		String position;// 位置

		for (int i = 0; i < rangeX; i++) {
			for (int j = 0; j < rangeY; j++) {
				map = new HashMap<String, Object>();

				offset = j + "," + i;// 0,1
				position = i + "" + j;// 10

				map.put(Constant.ITEMTEXT, offset);
				map.put(Constant.ITEMIMAGE_GIF, Constant.NOT_PLAY_GIF);
				map.put(Constant.ITEMIMAGE, imagePieces.get(Integer.parseInt(position)).bitmap);

				// showHandpieces(map, offset);// FIXME 发行的时候, 务必注释掉这一行!

				gridviewDataList.add(map);
			}
		}

		mainGridAdapter = new MainGameGridAdapter(this, gridviewDataList, R.layout.activity_main_game_gridv_item);
	}

	/** 分解封面的图片 */
	private void splitBitmap() {
		Bitmap frontCoverBitmap = BitmapFactory.decodeResource(getResources(), getSplitBitmapResId());

		int xPiece = this.rangeX;
		int yPiece = this.rangeY;
		int pieceWidth = frontCoverBitmap.getWidth() / xPiece;
		int pieceHeight = frontCoverBitmap.getHeight() / yPiece;
		imagePieces = ImageSplitter.split(frontCoverBitmap, xPiece, yPiece, pieceWidth, pieceHeight);

		if (frontCoverBitmap != null && !frontCoverBitmap.isRecycled()) {
			frontCoverBitmap.recycle();
			frontCoverBitmap = null;
		}
		System.gc();
	}

	/** 把机头标注出来, 调试用 */
	@SuppressWarnings("unused")
	private void showHandpieces(Map<String, Object> map, String offset) {
		if (mapHandpieceList.contains(offset)) {
			map.put(Constant.ITEMIMAGE, R.drawable.block_red_unclicked);
		}
	}

	private int getSplitBitmapResId() {
		int rid = 0;

		if (Constant.STORY_MODE.equals(gameMode)) {
			if (mapId == R.id.map_ussr_germany_imgv) {
				if (stageOrder == 1) {
					rid = R.drawable.story_mode_map_ussr_germany_1;
				} else if (stageOrder == 2) {
					rid = R.drawable.story_mode_map_ussr_germany_2;
				} else if (stageOrder == 3) {
					rid = R.drawable.story_mode_map_ussr_germany_3;
				} else if (stageOrder == 4) {
					rid = R.drawable.story_mode_map_ussr_germany_4;
				} else if (stageOrder == 5) {
					rid = R.drawable.story_mode_map_ussr_germany_5;
				}
			} else if (mapId == R.id.map_pacific_imgv) {
				if (stageOrder == 1) {
					rid = R.drawable.story_mode_map_pacific_1;
				} else if (stageOrder == 2) {
					rid = R.drawable.story_mode_map_pacific_2;
				} else if (stageOrder == 3) {
					rid = R.drawable.story_mode_map_pacific_3;
				} else if (stageOrder == 4) {
					rid = R.drawable.story_mode_map_pacific_4;
				} else if (stageOrder == 5) {
					rid = R.drawable.story_mode_map_pacific_5;
				}
			} else if (mapId == R.id.map_china_imgv) {
				if (stageOrder == 1) {
					rid = R.drawable.story_mode_map_china_1;
				} else if (stageOrder == 2) {
					rid = R.drawable.story_mode_map_china_2;
				} else if (stageOrder == 3) {
					rid = R.drawable.story_mode_map_china_3;
				} else if (stageOrder == 4) {
					rid = R.drawable.story_mode_map_china_4;
				} else if (stageOrder == 5) {
					rid = R.drawable.story_mode_map_china_5;
				}
			} else if (mapId == R.id.map_western_europe_imgv) {
				if (stageOrder == 1) {
					rid = R.drawable.story_mode_map_western_europe_1;
				} else if (stageOrder == 2) {
					rid = R.drawable.story_mode_map_western_europe_2;
				} else if (stageOrder == 3) {
					rid = R.drawable.story_mode_map_western_europe_3;
				} else if (stageOrder == 4) {
					rid = R.drawable.story_mode_map_western_europe_4;
				} else if (stageOrder == 5) {
					rid = R.drawable.story_mode_map_western_europe_5;
				}
			}
		} else if (Constant.INFINITE_MODE.equals(gameMode)) {
			rid = R.drawable.infinite_mode_game_page_grdv_front_cover;
		}

		return rid;
	}

	/** 获取飞机坐标的数据 */
	private void getMapDataLists() {
		try {
			StringBuilder sb = new StringBuilder();

			if (Constant.STORY_MODE.equals(gameMode)) {
				sb.append("SELECT A.* FROM Flydate AS A ");
				sb.append("INNER JOIN FlySonDate AS B ON (A.id = B.FatherID) ");
				sb.append("WHERE A.id = " + fatherId + " ");
				sb.append("GROUP BY B.FatherID;");
			} else if (Constant.INFINITE_MODE.equals(gameMode)) {
				sb.append("SELECT A.* FROM Flydate AS A ");
				sb.append("INNER JOIN FlySonDate AS B ON (A.id = B.FatherID) ");
				sb.append("GROUP BY B.FatherID ");
				sb.append("ORDER BY random() LIMIT 1;");// 随机取一条有飞机坐标的数据
			}

			Cursor cursor = BeginApplication.dbUtils.execQuery(sb.toString()); // Cursor cursor = db.execQuery(new SqlInfo(sb.toString(), new Object[] {}));

			int id = -10;// 主键
			// String mapName = "";// 地图名字
			// int planeNum = 0;// 飞机数量
			// String createPersonName = "";// 创建人名字

			if (cursor.moveToNext()) {
				id = cursor.getInt(cursor.getColumnIndex("id"));// 主键
				// mapName = cursor.getString(cursor.getColumnIndex("name"));// 地图名字
				// planeNum = cursor.getInt(cursor.getColumnIndex("num"));// 飞机数量
				// createPersonName = cursor.getString(cursor.getColumnIndex("createmen"));// 创建人名字
				this.rangeX = cursor.getInt(cursor.getColumnIndex("SeparateX"));// 横向范围
				this.rangeY = cursor.getInt(cursor.getColumnIndex("SeparateY"));// 纵向范围
			}

			cursor.close();

			List<PlaneOffsetEntity> planeOffsetEntities = BeginApplication.dbUtils.findAll(Selector.from(PlaneOffsetEntity.class).where("FatherID", "=", id));
			// List<PlaneOffsetEntity> planeOffsetEntities = findAllBySdkOnly(id);

			for (PlaneOffsetEntity planeOffsetEntity : planeOffsetEntities) {
				offsetMaker(planeOffsetEntity);
			}

			// dbUtils.close();
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/** 创建飞机坐标 */
	private void offsetMaker(PlaneOffsetEntity planeOffsetEntity) {
		String handpieceOffset = planeOffsetEntity.getHandpieceOffset();
		mapHandpieceList.add(handpieceOffset);

		String[] handpieceOffsets = handpieceOffset.split(",");
		String direction = planeOffsetEntity.getDirection();

		int handpieceOffsetX = Integer.parseInt(handpieceOffsets[0]);
		int handpieceOffsetY = Integer.parseInt(handpieceOffsets[1]);

		if (direction.equals("L")) {// 朝左飞
			mapAirframeList.add((handpieceOffsetX + 1) + "," + (handpieceOffsetY - 2));
			mapAirframeList.add((handpieceOffsetX + 1) + "," + (handpieceOffsetY - 1));
			mapAirframeList.add((handpieceOffsetX + 1) + "," + (handpieceOffsetY + 0));
			mapAirframeList.add((handpieceOffsetX + 1) + "," + (handpieceOffsetY + 1));
			mapAirframeList.add((handpieceOffsetX + 1) + "," + (handpieceOffsetY + 2));

			mapAirframeList.add((handpieceOffsetX + 2) + "," + (handpieceOffsetY + 0));

			mapAirframeList.add((handpieceOffsetX + 3) + "," + (handpieceOffsetY - 1));
			mapAirframeList.add((handpieceOffsetX + 3) + "," + (handpieceOffsetY + 0));
			mapAirframeList.add((handpieceOffsetX + 3) + "," + (handpieceOffsetY + 1));
		} else if (direction.equals("U")) {// 朝左上
			mapAirframeList.add((handpieceOffsetX - 2) + "," + (handpieceOffsetY + 1));
			mapAirframeList.add((handpieceOffsetX - 1) + "," + (handpieceOffsetY + 1));
			mapAirframeList.add((handpieceOffsetX + 0) + "," + (handpieceOffsetY + 1));
			mapAirframeList.add((handpieceOffsetX + 1) + "," + (handpieceOffsetY + 1));
			mapAirframeList.add((handpieceOffsetX + 2) + "," + (handpieceOffsetY + 1));

			mapAirframeList.add((handpieceOffsetX + 0) + "," + (handpieceOffsetY + 2));

			mapAirframeList.add((handpieceOffsetX - 1) + "," + (handpieceOffsetY + 3));
			mapAirframeList.add((handpieceOffsetX + 0) + "," + (handpieceOffsetY + 3));
			mapAirframeList.add((handpieceOffsetX + 1) + "," + (handpieceOffsetY + 3));
		} else if (direction.equals("R")) {// 朝左右
			mapAirframeList.add((handpieceOffsetX - 1) + "," + (handpieceOffsetY - 2));
			mapAirframeList.add((handpieceOffsetX - 1) + "," + (handpieceOffsetY - 1));
			mapAirframeList.add((handpieceOffsetX - 1) + "," + (handpieceOffsetY + 0));
			mapAirframeList.add((handpieceOffsetX - 1) + "," + (handpieceOffsetY + 1));
			mapAirframeList.add((handpieceOffsetX - 1) + "," + (handpieceOffsetY + 2));

			mapAirframeList.add((handpieceOffsetX - 2) + "," + (handpieceOffsetY + 0));

			mapAirframeList.add((handpieceOffsetX - 3) + "," + (handpieceOffsetY - 1));
			mapAirframeList.add((handpieceOffsetX - 3) + "," + (handpieceOffsetY + 0));
			mapAirframeList.add((handpieceOffsetX - 3) + "," + (handpieceOffsetY + 1));
		} else if (direction.equals("D")) {// 朝左下
			mapAirframeList.add((handpieceOffsetX - 2) + "," + (handpieceOffsetY - 1));
			mapAirframeList.add((handpieceOffsetX - 1) + "," + (handpieceOffsetY - 1));
			mapAirframeList.add((handpieceOffsetX + 0) + "," + (handpieceOffsetY - 1));
			mapAirframeList.add((handpieceOffsetX + 1) + "," + (handpieceOffsetY - 1));
			mapAirframeList.add((handpieceOffsetX + 2) + "," + (handpieceOffsetY - 1));

			mapAirframeList.add((handpieceOffsetX + 0) + "," + (handpieceOffsetY - 2));

			mapAirframeList.add((handpieceOffsetX - 1) + "," + (handpieceOffsetY - 3));
			mapAirframeList.add((handpieceOffsetX + 0) + "," + (handpieceOffsetY - 3));
			mapAirframeList.add((handpieceOffsetX + 1) + "," + (handpieceOffsetY - 3));
		}
	}

	/** 创建数据完成, 刷新、展示 */
	private void stopMakeMap() {
		gridView.setAdapter(mainGridAdapter);

		BeginApplication.closeLoadingPopupWindow();
	}

	/** 选择攻击类型的事件 */
	@OnClick({ R.id.shoot_type_bullet_imgv, R.id.shoot_type_bomb_imgv })
	private void selectShootType(View view) {
		int viewId = view.getId();

		if (viewId == R.id.shoot_type_bullet_imgv) {
			shootType = Constant.SHOOT_TYPE_BULLET;
			number_left_imgv.setImageResource(R.drawable.up_icon_shoot_type_bullet);
			number_left_real_txtv.setText(String.valueOf(bulletNumLeft));

			shoot_type_imgv.setImageResource(R.drawable.twinkling_bullet_white);
		} else if (viewId == R.id.shoot_type_bomb_imgv) {
			shootType = Constant.SHOOT_TYPE_BOMB;
			number_left_imgv.setImageResource(R.drawable.up_icon_shoot_type_bomb);
			number_left_real_txtv.setText(String.valueOf(bombNumLeft + bombAwardNumLeft));

			shoot_type_imgv.setImageResource(R.drawable.twinkling_bomb_white);
		}

		selectShootTypeStateController(view);
	}

	private boolean shootTypeIsShown = false;

	/** 控制选择攻击类型的弹出或者关闭 */
	@OnClick(R.id.shoot_type_imgv)
	private void selectShootTypeStateController(View view) {
		if (shootTypeIsShown) {
			shoot_type_bullet_imgv.setVisibility(View.INVISIBLE);
			shoot_type_bomb_imgv.setVisibility(View.INVISIBLE);

			shootTypeIsShown = false;
		} else {
			// shoot_type_bullet_imgv.setVisibility(View.VISIBLE);
			// shoot_type_bomb_imgv.setVisibility(View.VISIBLE);

			shootTypeIsShown = true;

			selectShootTypeAnimController(shoot_type_bullet_imgv);
			selectShootTypeAnimController(shoot_type_bomb_imgv);
		}
	}

	private void selectShootTypeAnimController(final View view) {
		int[] start_location = new int[2];// 高度的话, 会包含 StatusBar 和 TilteBar/ActionBar/AppBar
		int[] end_location = new int[2];

		shoot_type_imgv.getLocationInWindow(start_location);
		view.getLocationInWindow(end_location);

		// 坐标好像都是以左上角为基准的, 所以要补上控件的半长差
		int endX = end_location[0] - start_location[0] - (shoot_type_imgv.getWidth() / 2 - view.getWidth() / 2);
		int endY = end_location[1] - start_location[1] - (shoot_type_imgv.getHeight() / 2 - view.getHeight() / 2);

		final ImageView bombTemp = new ImageView(this);
		int imgResId = 0;
		if (view.getId() == R.id.shoot_type_bullet_imgv) {
			imgResId = R.drawable.twinkling_bullet_white;
		} else if (view.getId() == R.id.shoot_type_bomb_imgv) {
			imgResId = R.drawable.twinkling_bomb_white;
		}
		bombTemp.setImageResource(imgResId);

		// =======================
		int statusBarHeight = DisplayUtil.getStatusBarHeight(this);// 状态栏高度
		int titleBarHeight = DisplayUtil.getTitleBarHeight(this);// 标题栏高度
		// =======================

		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(view.getWidth(), view.getHeight());
		lp.leftMargin = start_location[0] + (shoot_type_imgv.getWidth() / 2 - view.getWidth() / 2);
		lp.topMargin = start_location[1] - statusBarHeight - titleBarHeight + (shoot_type_imgv.getHeight() / 2 - view.getHeight() / 2);
		lp.gravity = Gravity.TOP | Gravity.LEFT;
		bombTemp.setLayoutParams(lp);
		bombTemp.requestLayout();

		anim_lyt.addView(bombTemp);

		Animation selectShootTypeTranslateAnimation = new TranslateAnimation(0, endX, 0, endY);
		selectShootTypeTranslateAnimation.setDuration(200);
		selectShootTypeTranslateAnimation.setFillAfter(false);
		selectShootTypeTranslateAnimation.setInterpolator(new DecelerateInterpolator());
		selectShootTypeTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				bombTemp.clearAnimation();
				bombTemp.setVisibility(View.GONE);

				if (shootTypeIsShown) {
					view.setVisibility(View.VISIBLE);
				} else {
					view.setVisibility(View.INVISIBLE);
				}
			}
		});

		bombTemp.startAnimation(selectShootTypeTranslateAnimation);
	}

	/** 点击菜单事件 */
	@OnClick(R.id.menu_imgv)
	private void clickMenu(View view) {
		if (menuDialog == null) {
			menuDialog = new Dialog(this, R.style.my_dialog);
			menuDialog.setContentView(R.layout.dialog_menu);
		}

		MenuHolder menuHolder = new MenuHolder();

		ViewUtils.inject(menuHolder, menuDialog.getWindow().getDecorView());

		if (Constant.STORY_MODE.equals(gameMode)) {
			menuHolder.back_to_last_page.setText(R.string.back_to_choose_map);
		} else if (Constant.INFINITE_MODE.equals(gameMode)) {
			menuHolder.back_to_last_page.setText(R.string.back_to_menu);
			menuHolder.paly_help_txtv.setVisibility(View.INVISIBLE);
		}

		menuDialog.show();
	}

	private Dialog menuDialog;

	/** 存放菜单中的控件 */
	private class MenuHolder {
		// private Dialog menuDialog;
		//
		// public MenuHolder(Dialog menuDialog) {
		// this.menuDialog = menuDialog;
		// }

		// @ViewInject(R.id.goto_store_btn)
		// private TextView goto_store_btn;

		@ViewInject(R.id.paly_help_txtv)
		private TextView paly_help_txtv;

		@ViewInject(R.id.back_to_last_page)
		private TextView back_to_last_page;

		@ViewInject(R.id.back_to_game)
		private TextView back_to_game;

		// @OnClick(R.id.goto_store_btn)
		// public void gotoStore(View view) {
		// dialog.dismiss();
		// Intent intent = new Intent(dialog.getContext(), StoreActivity.class);
		// startActivity(intent);
		// }

		@OnClick(R.id.paly_help_txtv)
		public void showHelper(View view) {
			menuDialog.dismiss();

			Intent intent = new Intent(menuDialog.getContext(), HelperActivity.class);
			startActivity(intent);
		}

		@OnClick(R.id.back_to_last_page)
		public void backToLastPage(View view) {
			menuDialog.dismiss();
			finish();
		}

		@OnClick(R.id.back_to_game)
		public void backToGame(View view) {
			menuDialog.dismiss();
		}
	}

	/** 点击了一个块儿之后的事件 */
	@OnItemClick(R.id.gridView)
	private void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		if (Constant.STORY_MODE.equals(gameMode)) {
			return;
		}

		readyForShoot(position);
	}

	/**
	 * 校验等 准备工作<br>
	 * 其实 用多线程 调用这个方法, 可能会保证 快速点击的 时候, 操作流畅<br>
	 * <b style="color:red">但是务必 注意 线程同步 的问题</b>
	 * 
	 * @param position
	 *            控件的位置
	 */
	void readyForShoot(int position) {
		Map<String, Object> item = gridviewDataList.get(position);// position 是下标, 从0开始, 到 list.size()-1 结束
		String itemText = (String) item.get(Constant.ITEMTEXT);

		if (itemText.equals(Constant.SHOT_BLOCK_OFFSET)) {
			if (Constant.INFINITE_MODE.equals(gameMode)) {
			} else if (Constant.STORY_MODE.equals(gameMode)) {
				useOneShootType();
			}

			return;
		}

		if (getCurrentShootTypeNumLeft() <= 0) {
			// 子弹没有了, 直接结束, 不需要提示
			if (shootType != Constant.SHOOT_TYPE_BULLET) {
				BeginApplication.showMessageToast(R.string.no_prop_left);
			}

			return;
		}

		if (shootType == Constant.SHOOT_TYPE_BULLET) {
			--bulletNumLeft;
			++bulletNumUsed;

			beginShoot(position);
		} else if (shootType == Constant.SHOOT_TYPE_BOMB) {

			if (bombAwardNumLeft > 0) {
				--bombAwardNumLeft;
				// ++bombAwardNumUsed;
			} else {
				--bombNumLeft;
				++bombNumUsed;
			}

			String[] offsets = itemText.split(",");

			int offsetX = Integer.parseInt(offsets[0]); // 横坐标
			int offsetY = Integer.parseInt(offsets[1]); // 纵坐标

			if (offsetX != 0) {
				int leftPosition = position - 1;

				Map<String, Object> itemLeft = gridviewDataList.get(leftPosition);
				String itemLeftText = (String) itemLeft.get(Constant.ITEMTEXT);

				if (!itemLeftText.equals(Constant.SHOT_BLOCK_OFFSET)) {
					beginShoot(leftPosition);
				}
			}

			if (offsetY != 0) {
				int topPosition = position - rangeY;

				Map<String, Object> itemTop = gridviewDataList.get(topPosition);
				String itemTopText = (String) itemTop.get(Constant.ITEMTEXT);

				if (!itemTopText.equals(Constant.SHOT_BLOCK_OFFSET)) {
					beginShoot(topPosition);
				}
			}

			if (offsetX != rangeX - 1) {
				int rightPosition = position + 1;

				Map<String, Object> itemRight = gridviewDataList.get(rightPosition);
				String itemRightText = (String) itemRight.get(Constant.ITEMTEXT);

				if (!itemRightText.equals(Constant.SHOT_BLOCK_OFFSET)) {
					beginShoot(rightPosition);
				}
			}

			if (offsetY != rangeY - 1) {
				int bottomPosition = position + rangeY;

				Map<String, Object> itemRight = gridviewDataList.get(bottomPosition);
				String itemBottomText = (String) itemRight.get(Constant.ITEMTEXT);

				if (!itemBottomText.equals(Constant.SHOT_BLOCK_OFFSET)) {
					beginShoot(bottomPosition);
				}
			}

			beginShoot(position);
		}
	}

	/** 射击头部 */
	@OnClick(R.id.shoot_type_headshot_imgv)
	private void shootHead(View view) {
		if (headshotNumLeft <= 0) {
			BeginApplication.showMessageToast(R.string.no_prop_left);
			return;
		}

		// shootType = Constant.SHOOT_TYPE_HEADSHOT;
		// number_left_imgv.setImageResource(R.drawable.block_red_unclicked);
		// number_left_real_txtv.setText(String.valueOf(headshotNumLeft));

		// 极特殊的情况下, 例如快速地点击, 才可能会遇到这个情况, 应该不可能 小于0, 小于0的话, 应该有运行时异常
		if (mapHandpieceList.size() == 0) {
			return;
		}

		--headshotNumLeft;
		++headshotNumUsed;

		shoot_type_headshot_num_txtv.setText(String.valueOf(headshotNumLeft));

		String[] offsets = mapHandpieceList.get(0).split(",");
		int position = Integer.parseInt(offsets[1] + offsets[0]);

		boolean isHeadshot = true;
		beginShoot(position, isHeadshot);

		if (headshotNumLeft <= 0) {
			BeginApplication.showMessageToast(R.string.no_prop_left);
			return;
		}
	}

	/** 进行非爆头的攻击 */
	private void beginShoot(int position) {
		boolean isHeadshot = false;

		beginShoot(position, isHeadshot);
	}

	/** 进行攻击 */
	private void beginShoot(int position, boolean isHeadshot) {
		Map<String, Object> item = gridviewDataList.get(position);
		String itemText = (String) item.get(Constant.ITEMTEXT);

		int imgaeview_resId = R.drawable.hole_nothing;
		int imgaeview_gif_resId = Constant.NOT_PLAY_GIF;

		if (mapHandpieceList.contains(itemText)) {
			mapHandpieceList.remove(itemText);

			imgaeview_gif_resId = R.drawable.explode;

			if (Constant.INFINITE_MODE.equals(gameMode)) {
				bulletNumLeft += Constant.HOW_MANY_BULLET_TO_AWARD_WHEN_HEADSHOT;
				awardNum++;
			}

			planeNumShootdown++;

			// 增加炸弹
			if (Constant.INFINITE_MODE.equals(gameMode) && awardNum == Constant.AWARD_RANGE) {
				awardNum = 0;

				bombAwardNumLeft += Constant.HOW_MANY_BOMB_TO_AWARD_WHEN_SHOOTDOWN_ENOUGH;

				translateBombImg();
			}

			if (mapHandpieceList.size() == 0) {
				mapHandpieceList.clear();
				mapAirframeList.clear();

				if (Constant.INFINITE_MODE.equals(gameMode)) {
					BeginApplication.showLoadingPopupWindow(plane_number_shootdown_real_txtv);
					++infiniteModeStageNumRushacross;
					initMap();
				} else if (Constant.STORY_MODE.equals(gameMode)) {
					boolean stageClearedOrNot = true;// 闯关成功

					String userId = BeginApplication.ue.getUserId();

					if (userId.equals(Constant.GUEST_ID)) {
						boolean successOrNot = false;

						if (stageClearedOrNot) {
							successOrNot = unlockStage();
						}

						showSettlement(successOrNot, stageClearedOrNot);
					} else {
						settlementOnline(stageClearedOrNot);
					}
				}
			}
		} else if (mapAirframeList.contains(itemText)) {
			mapAirframeList.remove(itemText);

			imgaeview_resId = R.drawable.hole_blue;
		}

		item.put(Constant.ITEMTEXT, Constant.SHOT_BLOCK_OFFSET);
		item.put(Constant.ITEMIMAGE, imgaeview_resId);
		item.put(Constant.ITEMIMAGE_GIF, imgaeview_gif_resId);

		mainGridAdapter.notifyDataSetChanged();

		plane_number_shootdown_real_txtv.setText(String.valueOf(planeNumShootdown));
		number_left_real_txtv.setText(String.valueOf(getCurrentShootTypeNumLeft()));

		if (Constant.INFINITE_MODE.equals(gameMode)) {
			mission_real_txtv.setText(awardNum + "/" + Constant.AWARD_RANGE);
		}

		if (bulletNumLeft <= 0) {
			boolean stageClearedOrNot = false;

			settlementSelector(stageClearedOrNot);// 无尽结束 或者 闯关失败
		} else if (bombNumLeft + bombAwardNumLeft <= 0 && !isHeadshot && Constant.INFINITE_MODE.equals(gameMode)) {
			BeginApplication.showMessageToast(R.string.no_prop_left);
			return;
		}
	}

	/** 解锁 下一关 */
	private boolean unlockStage() {
		boolean unlockSuccessOrNot = false;

		if (idNext != StoryModeStageEntity.NO_NEXT_STAGE) {
			// 写的好的话, 这句话不应该 写在主线程, 但是 通常执行会很快, 不会阻塞 主线程
			unlockSuccessOrNot = stageDao.updateStoryModeStageById(String.valueOf(idNext));// 修改数据库 进度
			if (!unlockSuccessOrNot) {
				BeginApplication.showMessageToast(R.string.unlocker_next_stage_fail);
			}
		}

		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		bundle.putInt("stageOrder", stageOrder);

		Intent intent = new Intent();
		intent.putExtras(bundle);

		setResult(RESULT_OK, intent);

		return unlockSuccessOrNot;
	}

	/** 根据模式 选择 结算方式 */
	private void settlementSelector(boolean stageClearedOrNot) {
		boolean successOrNot = true;

		String userId = BeginApplication.ue.getUserId();

		// FIXME 一旦静态变量被系统或者360等释放, 就悲剧了
		if (userId.equals(Constant.GUEST_ID)) {
			BeginApplication.ue.setShotdownTotal(BeginApplication.ue.getShotdownTotal() + planeNumShootdown);

			if (Constant.STORY_MODE.equals(gameMode)) {
				boolean a = userDao.updateShotdownTotal(BeginApplication.ue.getShotdownTotal());
				boolean b = userDao.updateBombNum(BeginApplication.ue.getBombNum());
				successOrNot = a && b;// 游客闯关失败
			} else if (Constant.INFINITE_MODE.equals(gameMode)) {
				// 写的好的话, 这句话不应该 写在主线程, 但是 通常执行会很快, 不会阻塞 主线程
				boolean a = userDao.updateShotdownTotal(BeginApplication.ue.getShotdownTotal());
				boolean b = userDao.updateBombNum(BeginApplication.ue.getBombNum());
				boolean c = userDao.updateHeadshotNum(BeginApplication.ue.getHeadshotNum());

				successOrNot = a && b && c;// 有一個 失敗 就算結算失敗
			}

			showSettlement(successOrNot, stageClearedOrNot);
		} else {
			if (Constant.STORY_MODE.equals(gameMode)) {
				settlementOnline(stageClearedOrNot);// 闯关失败
			} else if (Constant.INFINITE_MODE.equals(gameMode)) {
				settlementOnline(stageClearedOrNot);// 无尽模式 游戏结束
			}
		}
	}

	/**
	 * 在线 结算
	 * 
	 * @param stageClearedOrNot
	 *            如果是闯关模式的话, 传闯关是否成功; 无尽模式的话, 传什么都行
	 */
	private void settlementOnline(final boolean stageClearedOrNot) {
		BeginApplication.showLoadingPopupWindow(plane_number_shootdown_real_txtv);

		JSONObject requestParameters = WebUrlDomain.getBaseJSONObject(this);
		try {
			requestParameters.put("userId", BeginApplication.ue.getUserId());// 当前登录用户id

			if (Constant.STORY_MODE.equals(gameMode)) {
				requestParameters.put("shotdown", planeNumShootdown);// 无尽模式击落飞机数
			} else if (Constant.INFINITE_MODE.equals(gameMode)) {
				requestParameters.put("shotdownEndless", planeNumShootdown);// 无尽模式击落飞机数
				requestParameters.put("passBarrier", infiniteModeStageNumRushacross);// 无尽模式闯关数
			}

			requestParameters.put("bullet", bulletNumLeft);// 剩余子弹数

			if (bombNumUsed > bombAwardNumLeft) {
				requestParameters.put("bomb", bombNumUsed - bombAwardNumLeft);// 消耗炸弹数
			} else {
				requestParameters.put("bomb", 0);// 消耗炸弹数
			}

			requestParameters.put("headShot", headshotNumUsed);// 消耗爆头数
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = WebUrlDomain.getCallInterfaceUrl("settlement", requestParameters);

		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.POST, url, new RequestCallBack<String>() {
			@Override
			public void onStart() {
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					parseSettlementJsonFromServer(new JSONObject(responseInfo.result), stageClearedOrNot);
				} catch (JSONException e) {
					showSettlementOnlineError(e, stageClearedOrNot);
				}
			}

			@Override
			public void onFailure(HttpException e, String msg) {
				showSettlementOnlineError(e, stageClearedOrNot);
			}
		});
	}

	private void showSettlementOnlineError(Exception e, boolean stageClearedOrNot) {
		e.printStackTrace();

		BeginApplication.closeLoadingPopupWindow();

		// BeginApplication.showMessageToast(R.string.settlement_fail);

		if (stageClearedOrNot) {
			unlockStage();
		}

		boolean successOrNot = false;
		showSettlement(successOrNot, stageClearedOrNot);
	}

	private void parseSettlementJsonFromServer(JSONObject resObject, boolean stageClearedOrNot) {
		// {
		// 　"state": "0",
		// 　"rows":
		// 　[
		// 　　{
		// 　　　"bomb": 6,
		// 　　　"bullet": 0,
		// 　　　"diamond": 0,
		// 　　　"headShot": 4,
		// 　　　"logId": 0,
		// 　　　"money": 852,
		// 　　　"passBarrier": 1,
		// 　　　"props_id": 17,
		// 　　　"shotdown": 4,
		// 　　　"shotdownEndless": 1,
		// 　　　"type": 0,
		// 　　　"userId": 100000,
		// 　　　"userName": "111"
		// 　　}
		// 　]
		// }

		int state = resObject.optInt("state");

		if (state != 0) {
			// int failedResId = 0;
			//
			// if (state == 1) {
			// failedResId = R.string.settlement_fail;
			// } else {
			// failedResId = R.string.settlement_fail;
			// }

			BeginApplication.closeLoadingPopupWindow();

			// BeginApplication.showMessageToast(failedResId);

			boolean successOrNot = false;
			showSettlement(successOrNot, stageClearedOrNot);

			return;
		}

		JSONObject temp = null;

		try {
			temp = resObject.getJSONArray("rows").getJSONObject(0);
		} catch (JSONException e) {
			showSettlementOnlineError(e, stageClearedOrNot);
			return;
		}

		BeginApplication.ue.setNickname(temp.optString("userName"));
		BeginApplication.ue.setUserId(temp.optString("userId"));

		BeginApplication.ue.setCoinNum(temp.optInt("money"));
		BeginApplication.ue.setBombNum(temp.optInt("bomb"));
		BeginApplication.ue.setHeadshotNum(temp.optInt("headShot"));

		BeginApplication.ue.setShotdownTotal(temp.optInt("shotdownEndless") + temp.optInt("shotdown"));

		if (stageClearedOrNot) {
			unlockStage();
		}

		BeginApplication.closeLoadingPopupWindow();

		boolean successOrNot = true;
		showSettlement(successOrNot, stageClearedOrNot);
	}

	/** 显示的 结算 对话空 */
	private Dialog settlementDialog;

	/**
	 * 显示结算的对话框
	 * 
	 * @param successOrNot
	 *            後台是否結算成功
	 * @param stageClearedOrNot
	 *            如果是闯关模式的话, 传闯关是否成功; 无尽模式的话, 传什么都行
	 */
	private void showSettlement(boolean successOrNot, final boolean stageClearedOrNot) {
		// AlertDialog.Builder b = new AlertDialog.Builder(MainGameActivity.this);
		// b.setIcon(R.drawable.ic_launcher).setTitle("消息");
		// b.setMessage("游戏结束, 击落了 " + planeNumShootdown + " 架飞机.");
		// b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// finish();
		// }
		// });
		// b.create();
		// b.show();

		if (settlementDialog != null && settlementDialog.isShowing()) {
			if (successOrNot) {
				settlementDialog.dismiss();// 重新 结算成功
				finish();
			}

			return;// 显示一个就够了
		}

		if (settlementDialog == null) {
			settlementDialog = new Dialog(this, R.style.my_dialog);
			settlementDialog.setContentView(R.layout.dialog_settlement);
			settlementDialog.setCanceledOnTouchOutside(false);// 点击对话框 外侧不允许消失
			settlementDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH) {
						return true; // 必须点击窗体的 返回按钮 才可以
					} else {
						return false; // 默认返回 false
					}
				}
			});
		}

		View v = settlementDialog.getWindow().getDecorView();
		if (successOrNot) {
			v.findViewById(R.id.retry_settlement_txtv).setVisibility(View.GONE);
			v.findViewById(R.id.settlement_fail_txtv).setVisibility(View.GONE);
		} else {
			v.findViewById(R.id.retry_settlement_txtv).setVisibility(View.VISIBLE);
			v.findViewById(R.id.retry_settlement_txtv).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					settlementSelector(stageClearedOrNot);
				}
			});
		}

		SettlementHolder holder = new SettlementHolder();

		ViewUtils.inject(holder, v);

		holder.bullet_real_used_txtv.setText(String.valueOf(bulletNumUsed));
		holder.bomb_real_used_txtv.setText(String.valueOf(bombNumUsed));
		holder.shotdown_real_num_txtv.setText(String.valueOf(planeNumShootdown));
		if (Constant.STORY_MODE.equals(gameMode)) {
			holder.award_coin_real_num_txtv.setText(String.valueOf(bulletNumLeft));

			if (stageClearedOrNot) {
				holder.menu_title_txtv.setText(R.string.stage_cleared_success);
			} else {
				holder.menu_title_txtv.setText(R.string.stage_cleared_fail);
			}
		} else if (Constant.INFINITE_MODE.equals(gameMode)) {
			v.findViewById(R.id.award_coin_num_lnrlyt).setVisibility(View.GONE);
		}

		settlementDialog.show();
	}

	/** 存放无尽结算中的控件 */
	private class SettlementHolder {
		// private Dialog settlementDialog;
		//
		// public SettlementHolder(Dialog settlementDialog) {
		// this.settlementDialog = settlementDialog;
		// }

		@ViewInject(R.id.bullet_real_used_txtv)
		private TextView bullet_real_used_txtv;

		@ViewInject(R.id.bomb_real_used_txtv)
		private TextView bomb_real_used_txtv;

		@ViewInject(R.id.shotdown_real_num_txtv)
		private TextView shotdown_real_num_txtv;

		@ViewInject(R.id.award_coin_real_num_txtv)
		private TextView award_coin_real_num_txtv;

		@ViewInject(R.id.menu_title_txtv)
		private TextView menu_title_txtv;

		@OnClick(R.id.back_to_last_page)
		public void backToLastPage(View view) {
			settlementDialog.dismiss();
			finish();
		}
	}

	/** 奖励的炸弹, 位移动画 */
	private void translateBombImg() {
		int[] start_location = new int[2];// 高度的话, 会包含 StatusBar 和 TilteBar/ActionBar/AppBar
		int[] end_location = new int[2];

		up_icon_shoot_type_bomb_img.getLocationInWindow(start_location);
		shoot_type_imgv.getLocationInWindow(end_location);

		// 坐标好像都是以左上角为基准的, 所以要补上控件的半长差
		int endX = end_location[0] - start_location[0] + (shoot_type_imgv.getWidth() / 2 - up_icon_shoot_type_bomb_img.getWidth() / 2);
		int endY = end_location[1] - start_location[1] + (shoot_type_imgv.getHeight() / 2 - up_icon_shoot_type_bomb_img.getHeight() / 2);

		final ImageView bombTemp = new ImageView(this);
		bombTemp.setImageResource(R.drawable.up_icon_shoot_type_bomb);

		// =======================
		int statusBarHeight = DisplayUtil.getStatusBarHeight(this);// 状态栏高度
		int titleBarHeight = DisplayUtil.getTitleBarHeight(this);// 标题栏高度
		// =======================

		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(up_icon_shoot_type_bomb_img.getWidth(), up_icon_shoot_type_bomb_img.getHeight());
		lp.leftMargin = start_location[0];
		lp.topMargin = start_location[1] - statusBarHeight - titleBarHeight;
		lp.gravity = Gravity.TOP | Gravity.LEFT;
		bombTemp.setLayoutParams(lp);
		bombTemp.requestLayout();

		anim_lyt.addView(bombTemp);

		Animation translateAnimation = new TranslateAnimation(0, endX, 0, endY);
		translateAnimation.setDuration(3000);
		translateAnimation.setFillAfter(false);
		translateAnimation.setInterpolator(new DecelerateInterpolator());
		translateAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				bombTemp.clearAnimation();
				bombTemp.setVisibility(View.GONE);
			}
		});

		bombTemp.startAnimation(translateAnimation);
	}

	/** 获取当前攻击类型的子弹的剩余数量 */
	private int getCurrentShootTypeNumLeft() {
		int shootTypeNumLeft = 0;

		if (shootType == Constant.SHOOT_TYPE_BULLET) {
			shootTypeNumLeft = bulletNumLeft;
		} else if (shootType == Constant.SHOOT_TYPE_BOMB) {
			shootTypeNumLeft = bombNumLeft + bombAwardNumLeft;
		}

		return shootTypeNumLeft;
	}

	// =======================================================
	// 大炮旋转相关
	// =======================================================

	/** 大炮旋转的角度 */
	private float ordnanceDegrees = 0;

	/** 原始的大炮 */
	private Bitmap originalOrdnanceBitmap;

	/** 旋转之后的大炮 */
	private Bitmap rotatedOrdnanceBitmap;

	/** 显示旋转之后大炮 */
	private void showRotatedOrdnance(float degrees) {
		if (originalOrdnanceBitmap == null) {
			// bitmap_ordnance = ((BitmapDrawable) getResources().getDrawable(R.drawable.ordnance)).getBitmap();
			originalOrdnanceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordnance);
		}

		Matrix matrixOrdnance = new Matrix();
		matrixOrdnance.postRotate(degrees);

		rotatedOrdnanceBitmap = Bitmap.createBitmap(originalOrdnanceBitmap, 0, 0, originalOrdnanceBitmap.getWidth(), originalOrdnanceBitmap.getHeight(), matrixOrdnance, true);// 创建新图片

		ordnance_imgv.setImageBitmap(rotatedOrdnanceBitmap);

		// bitmap_ordnance = null;
		// resizedBitmap = null;
	}

	/** 是否可以 旋转大炮, [true]可以 [false]不可以 */
	private boolean canOrdnanceRotateOrNot = false;

	/** 大炮旋转的方向, [-1]逆时针 [1]顺时针 */
	private float ordnanceRotateDirection = 0;

	/** 旋转大炮 */
	@OnTouch({ R.id.left_arrow_imgv, R.id.right_arrow_imgv })
	private boolean rotateOrdnance(final View v, MotionEvent event) {
		int eventId = event.getAction();

		// 按下
		if (eventId == MotionEvent.ACTION_DOWN) {

			if (v.getId() == R.id.left_arrow_imgv) {
				ordnanceRotateDirection = -1;
			} else if (v.getId() == R.id.right_arrow_imgv) {
				ordnanceRotateDirection = 1;
			}

			if (checkIfHasRotateToMax()) {
				return false;
			}

			canOrdnanceRotateOrNot = true;

			new RotateTask().execute();

			return true;
		}

		// 移动
		if (eventId == MotionEvent.ACTION_MOVE) {
		}

		// 松开
		if (eventId == MotionEvent.ACTION_UP) {
			canOrdnanceRotateOrNot = false;
		}

		// 移出了控件
		if (eventId == MotionEvent.ACTION_CANCEL) {
			canOrdnanceRotateOrNot = false;
		}

		return super.onTouchEvent(event);
	}

	/** 右侧最大的角度 */
	private static final int ORDNANCE_DEGREES_MAX = 90;

	/** 左侧最小的角度 */
	private static final int ORDNANCE_DEGREES_MIN = -ORDNANCE_DEGREES_MAX;

	/** 中立/临界的角度, 也是默认的角度 */
	private static final int ORDNANCE_DEGREES_SPINODAL = (ORDNANCE_DEGREES_MIN + ORDNANCE_DEGREES_MAX) / 2;

	/**
	 * 是否旋转到了最大角度
	 * 
	 * @return [true]旋转到了最大角度, 不可以再进行旋转, [false]没有旋转到最大角度, 可以继续旋转
	 */
	private boolean checkIfHasRotateToMax() {
		boolean left = ordnanceDegrees <= ORDNANCE_DEGREES_MIN && ordnanceRotateDirection < ORDNANCE_DEGREES_SPINODAL;
		boolean right = ordnanceDegrees >= ORDNANCE_DEGREES_MAX && ordnanceRotateDirection > ORDNANCE_DEGREES_SPINODAL;

		boolean b = left || right;

		return b;
	}

	/** 旋转大炮的线程 */
	private class RotateTask extends AsyncTask<Void, Void, Void> {
		/** 后台运行的方法, 可以运行非UI线程，可以执行耗时的方法 */
		@Override
		protected Void doInBackground(Void... params) {
			while (canOrdnanceRotateOrNot) {

				if (checkIfHasRotateToMax()) {
					break;
				}

				try {
					Thread.sleep(10);// 不写的话, 会直接旋转, 不平滑
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				ordnanceDegrees += ordnanceRotateDirection;

				publishProgress();
			}

			return null;
		}

		/** 在 publishProgress() 被调用以后执行, 用于更新进度 */
		@Override
		protected void onProgressUpdate(Void... progresses) {
			showRotatedOrdnance(ordnanceDegrees);
		}
	}

	// =======================================================
	// 按住 显示 的 力度条
	// =======================================================

	@ViewInject(R.id.dashboard_imgv)
	private ProcessDashboardImageView dashboard_imgv;

	/** 是否可以 在进行蓄力大炮, [true]在蓄力 [false]没在蓄力 */
	private boolean prepareToProcessOrNot = false;

	/** 当前的进度 */
	private int processDegrees = ProcessDashboardImageView.MIN_PROCESS;

	/** 蓄力 */
	@OnTouch(R.id.ordnance_imgv)
	private boolean processDashboard(final View v, MotionEvent event) {

		int eventId = event.getAction();

		// 按下
		if (eventId == MotionEvent.ACTION_DOWN) {

			if (getCurrentShootTypeNumLeft() <= 0) {
				// 子弹没有了, 直接结束, 不需要提示
				if (shootType != Constant.SHOOT_TYPE_BULLET) {
					BeginApplication.showMessageToast(R.string.no_prop_left);
				}

				return super.onTouchEvent(event);
			}

			processDegrees = ProcessDashboardImageView.MIN_PROCESS;

			prepareToProcessOrNot = true;

			new ProcessTask().execute();

			return true;
		}

		// 移动
		if (eventId == MotionEvent.ACTION_MOVE) {
		}

		// 松开
		if (eventId == MotionEvent.ACTION_UP) {
			prepareToProcessOrNot = false;
		}

		// 移出了控件
		if (eventId == MotionEvent.ACTION_CANCEL) {
			prepareToProcessOrNot = false;
		}

		return super.onTouchEvent(event);
	}

	/** 蓄力的线程 */
	private class ProcessTask extends AsyncTask<Void, Void, Void> {
		/** 后台运行的方法, 可以运行非UI线程，可以执行耗时的方法 */
		@Override
		protected Void doInBackground(Void... params) {

			// runOnUiThread(new Runnable() {
			// public void run() {
			// Toast.makeText(getApplicationContext(), "开始了", Toast.LENGTH_LONG).show();
			// }
			// });

			while (prepareToProcessOrNot) {

				try {
					Thread.sleep(10);// 不写的话,蓄力控件刷新不平滑
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				publishProgress();
			}

			// runOnUiThread(new Runnable() {
			// public void run() {
			// Toast.makeText(getApplicationContext(), "结束了", Toast.LENGTH_LONG).show();
			// }
			// });

			return null;
		}

		/** 在 publishProgress() 被调用以后执行, 用于更新进度 */
		@Override
		protected void onProgressUpdate(Void... progresses) {
			if (processDegrees >= ProcessDashboardImageView.MAX_PROCESS) {
				processDegrees = ProcessDashboardImageView.MIN_PROCESS;
			} else {
				processDegrees++;
			}

			dashboard_imgv.setProcessDegrees(processDegrees);
			dashboard_imgv.postInvalidate();// 调用 onDraw(), 刷新界面
		}

		@Override
		protected void onPostExecute(Void result) {
			fire();
		}
	}

	/** 大炮开火 */
	private void fire() {
		try {
			Thread.sleep(10);// 这样可以避免while 没有执行完成, 就开火了
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int[] start_location = new int[2];// 暂时定为炮口, 高度的话, 会包含 StatusBar 和 TilteBar/ActionBar/AppBar
		int[] end_location = new int[] { 0, 0 };// 屏幕 最左上角, 注意是物理屏幕, 不是应用可见范围的最上角

		ordnance_imgv.getLocationInWindow(start_location);

		int w = ordnance_imgv.getWidth();
		int h = ordnance_imgv.getHeight();

		double x = start_location[0] + 1.0D * w / 2;// 大炮中心坐标 x
		double y = start_location[1] + 1.0D * h / 2;// 大炮中心坐标 y

		int[] start_location_radius = DisplayUtil.getDestinationLocation(new double[] { x, y }, ordnanceDegrees, w / 2);// 炮口的坐标

		double currentDistance = getCurrentDistance(start_location, end_location);// 根据蓄力程度, 计算出来的距离

		int[] end_location_fly_to = DisplayUtil.getDestinationLocation(new double[] { start_location_radius[0], start_location_radius[1] }, ordnanceDegrees, currentDistance);// 炮弹最终落下的地方 的坐标

		translateFire(start_location_radius, end_location_fly_to);
	}

	/**
	 * 获取射程长度
	 * 
	 * @param start_location
	 *            起始坐标
	 * @param end_location
	 *            结束坐标
	 * @return
	 */
	private double getCurrentDistance(int[] start_location, int[] end_location) {
		double maxDistance = Math.sqrt(Math.pow(start_location[0] - end_location[0], 2) + Math.pow(start_location[1] - end_location[1], 2));// 最大射程

		int sweepCurrentProcessDegrees = processDegrees - ProcessDashboardImageView.MIN_PROCESS;// 当前扫过的角度(°)
		int sweepTheWholeProcessDegrees = ProcessDashboardImageView.MAX_PROCESS - ProcessDashboardImageView.MIN_PROCESS;// 最大扫过的角度(°)
		double percentProcess = 1.0D * sweepCurrentProcessDegrees / sweepTheWholeProcessDegrees;

		double currentDistance = maxDistance * percentProcess;

		return currentDistance;
	}

	/**
	 * 发射大炮的动画
	 * 
	 * @param start_location_radius
	 *            起始位置
	 * @param end_location_fly_to
	 *            结束位置
	 */
	private void translateFire(int[] start_location_radius, final int[] end_location_fly_to) {
		int[] start_location = start_location_radius;// 高度的话, 会包含 StatusBar 和 TilteBar/ActionBar/AppBar
		int[] end_location = end_location_fly_to;

		final int[] wheresTheCannonballFiredEnd = wheresTheCannonballFiredEnd(end_location_fly_to);

		int w = number_left_imgv.getWidth();
		int h = number_left_imgv.getHeight();

		int endX = end_location[0] - start_location[0];
		int endY = end_location[1] - start_location[1];

		final ImageView cannonball_imgv = new ImageView(this);
		cannonball_imgv.setImageResource(R.drawable.cannonball);

		// =======================
		int statusBarHeight = DisplayUtil.getStatusBarHeight(this);// 状态栏高度
		int titleBarHeight = DisplayUtil.getTitleBarHeight(this);// 标题栏高度
		// =======================

		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
		lp.leftMargin = start_location[0] - w / 2;
		lp.topMargin = start_location[1] - statusBarHeight - titleBarHeight - h / 2;
		lp.gravity = Gravity.TOP | Gravity.LEFT;
		cannonball_imgv.setLayoutParams(lp);
		cannonball_imgv.requestLayout();

		anim_lyt.addView(cannonball_imgv);

		Animation translateAnimation = new TranslateAnimation(0, endX, 0, endY);
		translateAnimation.setDuration(500);
		translateAnimation.setFillAfter(true);
		translateAnimation.setInterpolator(new DecelerateInterpolator(1.5F));
		translateAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				cannonball_imgv.clearAnimation();
				cannonball_imgv.setVisibility(View.GONE);

				int positionX = wheresTheCannonballFiredEnd[0];
				int positionY = wheresTheCannonballFiredEnd[1];

				if (positionX == ILLEGAL_POSITION_FLAG || positionY == ILLEGAL_POSITION_FLAG) {
					return;
				}

				String position = positionY + "" + positionX;// 10
				readyForShoot(Integer.parseInt(position));
			}
		});

		cannonball_imgv.startAnimation(translateAnimation);
	}

	/** 闯关模式 大炮 的 无效射击 */
	private static final int ILLEGAL_POSITION_FLAG = -1;

	/**
	 * 炮弹落在什么地方
	 * 
	 * @return
	 */
	private int[] wheresTheCannonballFiredEnd(int[] end_location_fly_to) {
		int[] start_location = new int[2];// 高度的话, 会包含 StatusBar 和 TilteBar/ActionBar/AppBar

		int positionX = ILLEGAL_POSITION_FLAG, positionY = ILLEGAL_POSITION_FLAG;
		int[] illegalPosition = new int[] { ILLEGAL_POSITION_FLAG, ILLEGAL_POSITION_FLAG };

		gridView.getLocationInWindow(start_location);
		int w = gridView.getWidth();
		int h = gridView.getHeight();

		float wInRange = 1F * w / rangeX;
		float hInRange = 1F * h / rangeY;

		if (end_location_fly_to[0] < start_location[0]) {
			useOneShootType();
			return illegalPosition;// 从 GridView 左侧飞出去了
		}

		if (end_location_fly_to[0] > start_location[0] + w) {
			useOneShootType();
			return illegalPosition;// 从 GridView 右侧飞出去了
		}

		if (end_location_fly_to[1] < start_location[1]) {
			useOneShootType();
			return illegalPosition;// 从 GridView 上侧飞出去了
		}

		if (end_location_fly_to[1] > start_location[1] + h) {
			useOneShootType();
			return illegalPosition;// 从 GridView 下侧飞出去了
		}

		for (int x = 0; x < rangeX; x++) {
			if (start_location[0] + wInRange * x <= end_location_fly_to[0] && end_location_fly_to[0] < start_location[0] + wInRange * (x + 1)) {
				positionX = x;
			}
		}

		for (int y = 0; y < rangeY; y++) {
			if (start_location[1] + hInRange * y <= end_location_fly_to[1] && end_location_fly_to[1] < start_location[1] + hInRange * (y + 1)) {
				positionY = y;
			}
		}

		if (positionX == ILLEGAL_POSITION_FLAG || positionY == ILLEGAL_POSITION_FLAG) {
			useOneShootType();
			return illegalPosition; // 炮弹打出去了
		}

		return new int[] { positionX, positionY };
	}

	/** 闯关模式 消耗一下射击的道具 */
	private void useOneShootType() {
		if (shootType == Constant.SHOOT_TYPE_BULLET) {
			--bulletNumLeft;
			++bulletNumUsed;

			number_left_real_txtv.setText(String.valueOf(getCurrentShootTypeNumLeft()));

			if (bulletNumLeft <= 0) {
				boolean stageClearedOrNot = false;// 最后一发 打到了无效的地方, 肯定是 失败了

				settlementSelector(stageClearedOrNot);// 闯关失败
			}
		} else if (shootType == Constant.SHOOT_TYPE_BOMB) {
			--bombNumLeft;
			++bombNumUsed;

			number_left_real_txtv.setText(String.valueOf(getCurrentShootTypeNumLeft()));

			if (bombNumLeft + bombAwardNumLeft <= 0) {
				BeginApplication.showMessageToast(R.string.no_prop_left);
				return;
			}
		}
	}
}
