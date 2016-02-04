package com.dengdeng123.blowupplane.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.module.CoverActivity;
import com.dengdeng123.blowupplane.util.SharePre;
import com.lidroid.xutils.ViewUtils;

public class BaseActivity extends Activity // FragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ViewUtils.inject(this);

		BeginApplication.addActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// JPushInterface.onResume(this);

		if ((BeginApplication.ue == null || TextUtils.isEmpty(BeginApplication.ue.getUserId())) && SharePre.getLoginState(this)) {

			BeginApplication.showLongTimeMessageToast(R.string.sign_in_first_when_ram_info_lost);

			SharePre.setLoginState(this, false);

			BeginApplication.finishAllActivity();

			Intent intent = new Intent(this, CoverActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// JPushInterface.onPause(this);
	}
}
