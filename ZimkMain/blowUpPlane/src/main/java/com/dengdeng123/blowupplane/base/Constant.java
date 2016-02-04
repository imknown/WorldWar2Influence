package com.dengdeng123.blowupplane.base;

public class Constant {
	/** 炮弹 */
	public final static int SHOOT_TYPE_BULLET = 0x10000;

	/** 炸弹 */
	public final static int SHOOT_TYPE_BOMB = 0x10001;

	/** 爆头 */
	public final static int SHOOT_TYPE_HEADSHOT = 0x10002;

	/** 默认无尽模式子弹数量 */
	public final static int SHOOT_TYPE_BULLET_INFINITE_MODE_INIT_NUM = 60;

	/** 默认闯关模式子弹数量 */
	public final static int SHOOT_TYPE_BULLET_STORY_MODE_INIT_NUM = 20;

	/** 击落多少后, 奖励炸弹 */
	public final static int AWARD_RANGE = 5;

	/** 击落若干飞机之后, 奖励炸弹的数量 */
	public final static int HOW_MANY_BOMB_TO_AWARD_WHEN_SHOOTDOWN_ENOUGH = 1;

	/** 爆头之后, 炮弹的数量 */
	public final static int HOW_MANY_BULLET_TO_AWARD_WHEN_HEADSHOT = 5;

	/** 炸开之后的坐标 */
	public final static String SHOT_BLOCK_OFFSET = "-1";

	/** 存放坐标 */
	public final static String ITEMTEXT = "itemText";

	/** 存放展示的图片 */
	public final static String ITEMIMAGE = "itemImage";

	/** 存放展示的GIF动画 */
	public final static String ITEMIMAGE_GIF = "itemImage_gif";

	/** 不播放Gif 的 FLAG */
	public final static int NOT_PLAY_GIF = 0x10010;

	/** 游客 UserId */
	public final static String GUEST_ID = "-1";

	/** 闯关模式 */
	public final static String STORY_MODE = "story_mode";

	/** 无尽模式 */
	public final static String INFINITE_MODE = "infinite_mode";
}