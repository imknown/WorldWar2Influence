package com.dengdeng123.blowupplane.module.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.domain.WebUrlDomain;
import com.dengdeng123.blowupplane.module.CoverActivity;
import com.dengdeng123.blowupplane.module.game.ModeChooseActivity;
import com.dengdeng123.blowupplane.util.SharePre;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_sign_in)
public class SignInActivity extends BaseActivity {

	@ViewInject(R.id.action_bar_title_txtv)
	private TextView action_bar_title_txtv;

	@ViewInject(R.id.username_edttxt)
	private EditText username_edttxt;

	@ViewInject(R.id.password_edttxt)
	private EditText password_edttxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action_bar_title_txtv.setText(getString(R.string.sign_in));

		username_edttxt.setText(SharePre.getUsername(this));
		password_edttxt.setText(SharePre.getPwd(this));
	}

	@OnClick(R.id.sign_up_goto_imgv)
	private void gotoSignUp(View view) {
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);

		// overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	public static CoverActivity ca;

	/** 校验输入的数据是否有问题 */
	private boolean validateInput() {
		boolean flag = false;
		int errorStringResId = 0;

		String username_edttxt_string = username_edttxt.getText().toString();
		String password_edttxt_string = password_edttxt.getText().toString();

		if (TextUtils.isEmpty(username_edttxt_string)) {
			errorStringResId = R.string.username_disallow_empty;
		} else if (TextUtils.isEmpty(password_edttxt_string)) {
			errorStringResId = R.string.password_disallow_empty;
		} else {
			flag = true;
		}

		if (!flag) {
			BeginApplication.showMessageToast(errorStringResId);
		}

		return flag;
	}

	@OnClick(R.id.sign_in_btn)
	private void signIn(View view) {

		if (!validateInput()) {
			return;
		}

		BeginApplication.showLoadingPopupWindow(view);

		JSONObject requestParameters = WebUrlDomain.getBaseJSONObject(this);
		try {
			requestParameters.put("un", username_edttxt.getText().toString());
			requestParameters.put("pw", password_edttxt.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = WebUrlDomain.getCallInterfaceUrl("loginBeUserInfo", requestParameters);

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
					parseJsonFromServer(new JSONObject(responseInfo.result));
				} catch (JSONException e) {
					showError(e);
				}
			}

			@Override
			public void onFailure(HttpException e, String msg) {
				showError(e);
			}
		});
	}

	private void showError(Exception e) {
		e.printStackTrace();

		BeginApplication.closeLoadingPopupWindow();

		BeginApplication.showMessageToast(R.string.sign_in_failed);
	}

	private void parseJsonFromServer(JSONObject resObject) {
		// {
		// 　"headShot": "3",
		// 　"updatetime": null,
		// 　"shotdown": "13",
		// 　"passWord": "cV9cc+26OCrBq6FK8JJphQ==",
		// 　"userType": "",
		// 　"createtime": null,
		// 　"nickName": "imknown",
		// 　"userId": 100017,
		// 　"diamond": "0",
		// 　"bomb": "4",
		// 　"money": "236",
		// 　"userName": "imknown",
		// 　"bullet": "0",
		// 　"delFlag": ""
		// }

		int state = resObject.optInt("state");

		if (state != 0) {
			int failedResId = 0;

			if (state == 1 || state == 2) {
				failedResId = R.string.sign_user_not_exist_failed_blur;
			} else {
				failedResId = R.string.sign_in_failed;
			}

			BeginApplication.closeLoadingPopupWindow();

			BeginApplication.showMessageToast(failedResId);

			return;
		}

		JSONObject temp = null;

		try {
			temp = resObject.getJSONArray("rows").getJSONObject(0);
		} catch (JSONException e) {
			showError(e);
			return;
		}

		BeginApplication.ue.setNickname(temp.optString("nickName"));
		BeginApplication.ue.setUserId(temp.optString("userId"));

		BeginApplication.ue.setCoinNum(temp.optInt("money"));
		BeginApplication.ue.setBombNum(temp.optInt("bomb"));
		BeginApplication.ue.setHeadshotNum(temp.optInt("headShot"));
		BeginApplication.ue.setShotdownTotal(temp.optInt("shotdown"));

		SharePre.setUsername(this, username_edttxt.getText().toString());
		SharePre.setPwd(this, password_edttxt.getText().toString());
		SharePre.setLoginState(this, true);

		BeginApplication.closeLoadingPopupWindow();
		BeginApplication.showMessageToast(R.string.sign_in_success);

		Intent intent = new Intent(SignInActivity.this, ModeChooseActivity.class);
		startActivity(intent);

		finish();

		if (ca != null) {
			ca.finish();
			ca = null;
		}
	}

	@OnClick(R.id.action_bar_left_back_imgv)
	private void leftBack(View view) {
		finish();
	}
}