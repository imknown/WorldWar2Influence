package com.dengdeng123.blowupplane.module;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.base.Constant;
import com.dengdeng123.blowupplane.entity.UserEntity;
import com.dengdeng123.blowupplane.module.game.ModeChooseActivity;
import com.dengdeng123.blowupplane.module.user.SignInActivity;
import com.dengdeng123.blowupplane.util.SharePre;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_cover)
public class CoverActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// copyDatabase();
	}

	// /** 如果没有飞机坐标的数据库的话, 进行创建. 没有的话, 不进行操作 */
	// private void copyDatabase() {
	// try {
	// DatabaseUtil.copyDatabase(this);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	@OnClick(R.id.guest_login_btn)
	private void guestLogin(View view) {

		try {
			BeginApplication.ue = BeginApplication.dbUtils.findFirst(Selector.from(UserEntity.class).where("UserId", "=", Constant.GUEST_ID).orderBy("id").limit(10));
			BeginApplication.ue.setUserId(Constant.GUEST_ID);
		} catch (DbException e) {
			e.printStackTrace();
		}

		SharePre.setLoginState(this, true);
		
		BeginApplication.canExit = false;

		Intent intent = new Intent(this, ModeChooseActivity.class);
		startActivity(intent);

		finish();
	}

	@OnClick(R.id.sign_in_btn)
	private void signIn(View view) {
		SignInActivity.ca = this;
		Intent intent = new Intent(this, SignInActivity.class);
		startActivity(intent);
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