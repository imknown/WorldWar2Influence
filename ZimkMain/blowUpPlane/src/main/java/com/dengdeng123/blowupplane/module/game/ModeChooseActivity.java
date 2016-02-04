package com.dengdeng123.blowupplane.module.game;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.base.Constant;
import com.dengdeng123.blowupplane.module.CoverActivity;
import com.dengdeng123.blowupplane.module.game.friendlist.FriendListActivity;
import com.dengdeng123.blowupplane.module.game.ranklist.RankListActivity;
import com.dengdeng123.blowupplane.module.game.story.ChooseMapActivity;
import com.dengdeng123.blowupplane.module.store.StoreActivity;
import com.dengdeng123.blowupplane.module.user.FeedbackActivity;
import com.dengdeng123.blowupplane.module.user.UserInfoActivity;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_mode_choose)
public class ModeChooseActivity extends BaseActivity {

	@OnClick(R.id.story_mode_btn)
	private void gotoStoryMode(View view) {
		Intent intent = new Intent(this, ChooseMapActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.infinite_mode_btn)
	private void gotoInfiniteMode(View view) {
		BeginApplication.showLoadingPopupWindow(view);

		Intent intent = new Intent(this, MainGameActivity.class);
		intent.putExtra("gameMode", Constant.INFINITE_MODE);
		startActivity(intent);
	}

	@OnClick(R.id.play_together_btn)
	private void gotoPlayTogether(View view) {
		BeginApplication.showMessageToast(R.string.under_construction);
	}

	@OnClick(R.id.show_user_info_btn)
	private void gotoShowUserInfo(View view) {
		BeginApplication.showLoadingPopupWindow(view);

		Intent intent = new Intent(this, UserInfoActivity.class);
		intent.putExtra("userId", BeginApplication.ue.getUserId());
		startActivity(intent);
	}

	@ViewInject(R.id.show_rank_list_btn)
	private TextView show_rank_list_btn;

	@OnClick(R.id.show_rank_list_btn)
	private void gotoShowRankList(View view) {
		BeginApplication.showLoadingPopupWindow(view);

		Intent intent = new Intent(this, RankListActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.show_store_btn)
	private void gotoStore(View view) {
		BeginApplication.showLoadingPopupWindow(view);

		Intent intent = new Intent(this, StoreActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.show_friend_list_btn)
	private void gotoFriendList(View view) {
		BeginApplication.showLoadingPopupWindow(view);

		Intent intent = new Intent(this, FriendListActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.show_feedback_btn)
	private void gotoFeedback(View view) {
		Intent intent = new Intent(this, FeedbackActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 用户注销之后, 回到本页, 需要重新进行注册
		if (BeginApplication.ue.getUserId().equals("")) {
			Intent intent = new Intent(this, CoverActivity.class);
			startActivity(intent);

			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		BeginApplication.canExit = false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			BeginApplication.readyToExit();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}