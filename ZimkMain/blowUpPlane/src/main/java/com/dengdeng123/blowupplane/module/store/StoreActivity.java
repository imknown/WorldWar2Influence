package com.dengdeng123.blowupplane.module.store;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.base.Constant;
import com.dengdeng123.blowupplane.domain.WebUrlDomain;
import com.dengdeng123.blowupplane.entity.StoreItemEntity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

@ContentView(R.layout.activity_store)
public class StoreActivity extends BaseActivity {

	@ViewInject(R.id.store_lstv)
	private ListView store_lstv;

	@ViewInject(R.id.action_bar_title_txtv)
	private TextView action_bar_title_txtv;

	@ViewInject(R.id.store_coin_num_txtv)
	private TextView store_coin_num_txtv;

	@ViewInject(R.id.store_bomb_num_txtv)
	private TextView store_bomb_num_txtv;

	@ViewInject(R.id.store_headshot_num_txtv)
	private TextView store_headshot_num_txtv;

	private List<StoreItemEntity> storeItemEntities;
	private StoreListAdapter storeListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			storeItemEntities = BeginApplication.dbUtils.findAll(Selector.from(StoreItemEntity.class).where("PD_key", "<>", "Money"));
		} catch (DbException e) {
			e.printStackTrace();
		}

		storeListAdapter = new StoreListAdapter(this);
		storeListAdapter.setStoreItemList(storeItemEntities);

		store_lstv.setAdapter(storeListAdapter);

		action_bar_title_txtv.setText(getString(R.string.store));

		if (BeginApplication.ue.getUserId().equals(Constant.GUEST_ID)) {
			// BeginApplication.ue = new UserDao().findUserById(Constant.GUEST_ID); // 这句话写不写, 其实作用不大

			showUserInfo();
		} else {
			getOnlineUserInfo();
		}
	}

	private void getOnlineUserInfo() {
		JSONObject requestParameters = WebUrlDomain.getBaseJSONObject(this);
		try {
			requestParameters.put("user_id", BeginApplication.ue.getUserId());
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

		BeginApplication.closeLoadingPopupWindow();

		BeginApplication.showMessageToast(R.string.get_user_info_fail);

		finish();
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

			BeginApplication.closeLoadingPopupWindow();

			BeginApplication.showMessageToast(failedResId);

			finish();

			return;
		}

		try {
			JSONArray jsonArray = resObject.getJSONArray("rows");

			JSONObject tempJson = jsonArray.getJSONObject(0);

			BeginApplication.ue.setBombNum(tempJson.optInt("bomb"));
			BeginApplication.ue.setHeadshotNum(tempJson.optInt("headShot"));
			BeginApplication.ue.setCoinNum(tempJson.optInt("money"));
			BeginApplication.ue.setNickname(tempJson.optString("userName"));
			BeginApplication.ue.setShotdownTotal(tempJson.optInt("shotdown"));
		} catch (JSONException e) {
			e.printStackTrace();

			BeginApplication.closeLoadingPopupWindow();

			BeginApplication.showMessageToast(R.string.get_user_info_fail);

			finish();
		}
	}

	public void showUserInfo() {
		store_coin_num_txtv.setText(String.valueOf(BeginApplication.ue.getCoinNum()));
		store_bomb_num_txtv.setText(String.valueOf(BeginApplication.ue.getBombNum()));
		store_headshot_num_txtv.setText(String.valueOf(BeginApplication.ue.getHeadshotNum()));

		BeginApplication.closeLoadingPopupWindow();
	}

	@OnItemClick(R.id.store_lstv)
	private void changeVisibility(AdapterView<?> parent, View view, int position, long id) {
		// final LinearLayout store_num_buy_lnrl = (LinearLayout) view.findViewById(R.id.store_num_buy_lnrl);
		//
		// Animation animation = (Animation) AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		// LayoutAnimationController lac = new LayoutAnimationController(animation);
		// lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
		// lac.setDelay(0F);// 单位 秒
		// store_num_buy_lnrl.setLayoutAnimation(lac);

		StoreItemEntity storeItemEntity = storeItemEntities.get(position);// position 从 0 开始

		if (storeItemEntity.getVisibility() == View.GONE) {
			storeItemEntity.setVisibility(View.VISIBLE);
		} else if (storeItemEntity.getVisibility() == View.VISIBLE) {
			storeItemEntity.setVisibility(View.GONE);
		}

		storeListAdapter.notifyDataSetChanged();
	}

	@OnClick(R.id.action_bar_left_back_imgv)
	private void leftBack(View view) {
		finish();
	}

	@OnClick(R.id.store_exchange_coin_imgv)
	private void exchangeCoin(View view) {
		Intent intent = new Intent(this, ExchangeCoinActivity.class);
		startActivity(intent);
	}
}