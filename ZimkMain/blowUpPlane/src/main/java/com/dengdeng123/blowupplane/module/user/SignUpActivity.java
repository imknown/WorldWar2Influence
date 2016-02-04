package com.dengdeng123.blowupplane.module.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.domain.WebUrlDomain;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_sign_up)
public class SignUpActivity extends BaseActivity {

	@ViewInject(R.id.action_bar_title_txtv)
	private TextView action_bar_title_txtv;

	@ViewInject(R.id.username_edttxt)
	private EditText username_edttxt;

	@ViewInject(R.id.password_edttxt)
	private EditText password_edttxt;

	@ViewInject(R.id.confirm_password_edttxt)
	private EditText confirm_password_edttxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action_bar_title_txtv.setText(getString(R.string.sign_up));
	}

	/** 校验输入的数据是否有问题 */
	private boolean validateInput() {
		boolean flag = false;
		int errorStringResId = 0;

		String username_edttxt_string = username_edttxt.getText().toString();
		String password_edttxt_string = password_edttxt.getText().toString();
		String confirm_password_edttxt_string = confirm_password_edttxt.getText().toString();

		if (TextUtils.isEmpty(username_edttxt_string)) {
			errorStringResId = R.string.username_disallow_empty;
		} else if (TextUtils.isEmpty(password_edttxt_string)) {
			errorStringResId = R.string.password_disallow_empty;
		} else if (TextUtils.isEmpty(confirm_password_edttxt_string)) {
			errorStringResId = R.string.confirm_password_disallow_empty;
		} else if (!confirm_password_edttxt_string.equals(password_edttxt_string)) {
			errorStringResId = R.string.confirm_password_is_not_equals_password;
		} else {
			flag = true;
		}

		if (!flag) {
			BeginApplication.showMessageToast(errorStringResId);
		}

		return flag;
	}

	@OnClick(R.id.sign_up_btn)
	private void signUp(View view) {

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

		String url = WebUrlDomain.getCallInterfaceUrl("registerBeUserInfo", requestParameters);

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
			public void onFailure(HttpException error, String msg) {
				showError(error);
			}
		});
	}

	private void showError(Exception e) {
		e.printStackTrace();
		BeginApplication.closeLoadingPopupWindow();
		BeginApplication.showMessageToast(R.string.sign_up_failed);
	}

	private void parseJsonFromServer(JSONObject resObject) {
		int state = resObject.optInt("state");

		if (state != 0) {
			int failedResId = 0;

			if (state == 2) {
				failedResId = R.string.sign_user_has_exist_failed_2;
			} else {
				failedResId = R.string.sign_in_failed;
			}

			BeginApplication.closeLoadingPopupWindow();
			BeginApplication.showMessageToast(failedResId);
			return;
		}

		BeginApplication.closeLoadingPopupWindow();
		BeginApplication.showMessageToast(R.string.sign_up_success);

		finish();
	}

	@OnClick(R.id.action_bar_left_back_imgv)
	private void leftBack(View view) {
		finish();

		// overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}
}