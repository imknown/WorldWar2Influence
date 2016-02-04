package com.dengdeng123.blowupplane.module.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.base.Constant;
import com.dengdeng123.blowupplane.domain.WebUrlDomain;
import com.dengdeng123.blowupplane.entity.UserEntity;
import com.dengdeng123.blowupplane.util.SharePre;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_user_info)
public class UserInfoActivity extends BaseActivity {

	@ViewInject(R.id.user_id_txtv)
	private TextView user_id_txtv;

	@ViewInject(R.id.user_name_txtv)
	private TextView user_name_txtv;

	@ViewInject(R.id.coin_num_txtv)
	private TextView coin_num_txtv;

	@ViewInject(R.id.headshot_num_txtv)
	private TextView headshot_num_txtv;

	@ViewInject(R.id.bomb_num_txtv)
	private TextView bomb_num_txtv;

	@ViewInject(R.id.plane_num_shootdown)
	private TextView plane_num_shootdown;

	@ViewInject(R.id.action_bar_title_txtv)
	private TextView action_bar_title_txtv;

	@ViewInject(R.id.sign_out_btn)
	private TextView sign_out_btn;

	/** 要做什么, 可以理解为 从哪里进来的 */
	private int whatToDo;

	/** 要查看的用户的 Id */
	private String userId;

	/** 当前登录的用户的 Id */
	private String loginedUserId = BeginApplication.ue.getUserId();

	/** 只负责 展示数据 */
	private UserEntity ue = new UserEntity();

	/** 查看别人的信息 */
	public static final int SEE_OTHERS_USER_INFO = 0x1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action_bar_title_txtv.setText(getString(R.string.user_info));

		whatToDo = getIntent().getIntExtra("whatToDo", 0);
		if (whatToDo == SEE_OTHERS_USER_INFO) {
			sign_out_btn.setVisibility(View.INVISIBLE);
		}

		userId = getIntent().getStringExtra("userId");
		if (userId.equals(Constant.GUEST_ID)) {
			// BeginApplication.ue = new UserDao().findUserById(Constant.GUEST_ID); // 这句话写不写, 其实作用不大
			this.ue = BeginApplication.ue;

			showUserInfo();
		} else {
			getOnlineUserInfo();
		}
	}

	private void getOnlineUserInfo() {
		JSONObject requestParameters = WebUrlDomain.getBaseJSONObject(this);
		try {
			requestParameters.put("user_id", userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = WebUrlDomain.getCallInterfaceUrl("searchBeUserInfo", requestParameters);

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
					parseUserInfoJsonFromServer(new JSONObject(responseInfo.result));

					showUserInfo();
				} catch (JSONException e) {
					showGetUserInfoError(e);
				}
			}

			@Override
			public void onFailure(HttpException e, String msg) {
				showGetUserInfoError(e);
			}
		});
	}

	private void showGetUserInfoError(Exception e) {
		e.printStackTrace();

		BeginApplication.showMessageToast(R.string.get_user_info_fail);

		showUserInfo();
	}

	private void parseUserInfoJsonFromServer(JSONObject resObject) {
		// {
		// 　"state": "0",
		// 　"rows": [ {
		// 　　"bomb": "5",
		// 　　"bullet": "0",
		// 　　"createtime": {
		// 　　　"date": 6,
		// 　　　"day": 5,
		// 　　　"hours": 19,
		// 　　　"minutes": 24,
		// 　　　"month": 1,
		// 　　　"seconds": 46,
		// 　　　"time": 1423221886000,
		// 　　　"timezoneOffset": -480,
		// 　　　"year": 115
		// 　　},
		// 　　"delFlag": "0",
		// 　　"diamond": "0",
		// 　　"headShot": "3",
		// 　　"money": "1100",
		// 　　"nickName": "",
		// 　　"passWord": "111111",
		// 　　"shotdown": "3",
		// 　　"updatetime": null,
		// 　　"userId": 100000,
		// 　　"userName": "111",
		// 　　"userType": ""
		// 　} ]
		// }
		int state = resObject.optInt("state");

		if (state != 0) {
			int failedResId = 0;

			if (state == 1) {
				failedResId = R.string.get_user_info_fail;
			} else {
				failedResId = R.string.get_user_info_fail;
			}

			BeginApplication.showMessageToast(failedResId);

			showUserInfo();

			return;
		}

		try {
			JSONArray jsonArray = resObject.getJSONArray("rows");

			JSONObject tempJson = jsonArray.getJSONObject(0);

			if (loginedUserId.equals(userId)) {
				BeginApplication.ue.setBombNum(tempJson.optInt("bomb"));
				BeginApplication.ue.setHeadshotNum(tempJson.optInt("headShot"));
				BeginApplication.ue.setCoinNum(tempJson.optInt("money"));
				BeginApplication.ue.setNickname(tempJson.optString("userName"));
				BeginApplication.ue.setShotdownTotal(tempJson.optInt("shotdown"));

				this.ue = BeginApplication.ue;
			} else {
				this.ue.setUserId(userId);
				this.ue.setBombNum(tempJson.optInt("bomb"));
				this.ue.setHeadshotNum(tempJson.optInt("headShot"));
				this.ue.setCoinNum(tempJson.optInt("money"));
				this.ue.setNickname(tempJson.optString("userName"));
				this.ue.setShotdownTotal(tempJson.optInt("shotdown"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			BeginApplication.showMessageToast(R.string.get_user_info_fail);
		}
	}

	private void showUserInfo() {
		user_name_txtv.setText(getString(R.string.user_name_is, ue.getNickname()));
		user_id_txtv.setText(getString(R.string.user_id_is, ue.getUserId()));

		coin_num_txtv.setText(getString(R.string.coin_num_is, ue.getCoinNum()));
		bomb_num_txtv.setText(getString(R.string.bomb_num_is, ue.getBombNum()));
		headshot_num_txtv.setText(getString(R.string.headshot_num_is, ue.getHeadshotNum()));
		plane_num_shootdown.setText(getString(R.string.plane_num_shootdown_is, ue.getShotdownTotal()));

		BeginApplication.closeLoadingPopupWindow();
	}

	@OnClick(R.id.sign_out_btn)
	private void signOut(View view) {
		BeginApplication.ue = new UserEntity();

		SharePre.setLoginState(this, false);

		BeginApplication.showMessageToast(R.string.sign_out_success);

		finish();
	}

	@OnClick(R.id.action_bar_left_back_imgv)
	private void leftBack(View view) {
		finish();
	}
}