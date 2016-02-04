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
import com.dengdeng123.blowupplane.base.Constant;
import com.dengdeng123.blowupplane.domain.WebUrlDomain;
import com.dengdeng123.blowupplane.util.KeyboardUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_feedback)
public class FeedbackActivity extends BaseActivity {

	@ViewInject(R.id.action_bar_title_txtv)
	private TextView action_bar_title_txtv;

	@ViewInject(R.id.action_bar_right_txtv)
	private TextView action_bar_right_txtv;

	@ViewInject(R.id.input_feedback_content_edttxt)
	private EditText input_feedback_content_edttxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action_bar_title_txtv.setText(R.string.feedback);
		action_bar_right_txtv.setText(R.string.send);
	}

	@OnClick(R.id.action_bar_right_txtv)
	private void sendFeedback(View view) {

		if (!validateInput()) {
			return;
		}

		KeyboardUtil.hideSystemKeyBoard(this, view);

		BeginApplication.showLoadingPopupWindow(view);

		JSONObject requestParameters = WebUrlDomain.getBaseJSONObject(this);
		try {

			String userId = BeginApplication.ue.getUserId();

			if (!userId.equals(Constant.GUEST_ID)) {
				requestParameters.put("user_id", userId);
			}

			requestParameters.put("feedback_content", input_feedback_content_edttxt.getText().toString());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = WebUrlDomain.getCallInterfaceUrl("editFeedBack", requestParameters);

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

		BeginApplication.showMessageToast(R.string.feedback_failed);
	}

	private void parseJsonFromServer(JSONObject resObject) {
		int state = resObject.optInt("state");

		if (state != 0) {
			int failedResId = 0;

			if (state == 1) {
				failedResId = R.string.feedback_failed;
			} else {
				failedResId = R.string.feedback_failed;
			}

			BeginApplication.closeLoadingPopupWindow();

			BeginApplication.showMessageToast(failedResId);

			return;
		}

		BeginApplication.closeLoadingPopupWindow();
		BeginApplication.showMessageToast(R.string.feedback_success);

		finish();
	}

	/** 校验输入的数据是否有问题 */
	private boolean validateInput() {
		boolean flag = false;
		int errorStringResId = 0;

		String input_feedback_content = input_feedback_content_edttxt.getText().toString();

		if (TextUtils.isEmpty(input_feedback_content)) {
			errorStringResId = R.string.feedback_disallow_empty;
		} else {
			flag = true;
		}

		if (!flag) {
			BeginApplication.showMessageToast(errorStringResId);
		}

		return flag;
	}

	@OnClick(R.id.action_bar_left_back_imgv)
	private void leftBack(View view) {
		finish();
	}
}