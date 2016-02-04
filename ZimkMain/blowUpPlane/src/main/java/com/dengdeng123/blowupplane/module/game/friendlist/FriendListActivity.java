package com.dengdeng123.blowupplane.module.game.friendlist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.base.Constant;
import com.dengdeng123.blowupplane.domain.FriendListItemDomain;
import com.dengdeng123.blowupplane.domain.WebUrlDomain;
import com.dengdeng123.blowupplane.module.user.UserInfoActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

@ContentView(R.layout.activity_friend_list)
public class FriendListActivity extends BaseActivity implements IXListViewListener {

	@ViewInject(R.id.action_bar_title_txtv)
	private TextView action_bar_title_txtv;

	@ViewInject(R.id.action_bar_right_imgv)
	private ImageView action_bar_right_imgv;

	@ViewInject(R.id.friend_tab_lnrlyt)
	private LinearLayout friend_tab_lnrlyt;

	@ViewInject(R.id.friend_lstv)
	private XListView friend_lstv;

	/** 起始下标 */
	private int offset = 0;

	/** 获取多少条数据 */
	private final int ROWS = 10;

	/** 序号 */
	private int sequenceNumber = 1;

	private FriendListAdapter mAdapter;

	public static final int SHOW_FRIEND_LIST = 0x1001;
	public static final int SHOW_APPLY_LIST = 0x1002;

	/** 显示类型 */
	private int showType = SHOW_FRIEND_LIST;

	/** 是否有新数据 */
	@SuppressWarnings("unused")
	private boolean hasMoreData = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action_bar_title_txtv.setText(getString(R.string.friend_list));
		action_bar_right_imgv.setImageResource(R.drawable.add_friend_btn);

		mAdapter = new FriendListAdapter(this);

		friend_lstv.setAdapter(mAdapter);
		friend_lstv.setPullRefreshEnable(true);
		friend_lstv.setPullLoadEnable(true);
		friend_lstv.setXListViewListener(this);
		friend_lstv.setRefreshTime("无");

		getMainListData(false);
	}

	/** 顶部下拉刷新 */
	@Override
	public void onRefresh() {
		offset = 0;
		sequenceNumber = 1;

		mAdapter.clear();

		getMainListData();
	}

	/** 底部加载更多 */
	@Override
	public void onLoadMore() {
		offset = mAdapter.getCount();

		getMainListData();
	}

	private void getMainListData() {
		getMainListData(true);
	}

	private void getMainListData(boolean showLoading) {
		String userId = BeginApplication.ue.getUserId();

		if (userId.equals(Constant.GUEST_ID)) {
			BeginApplication.showMessageToast(R.string.sign_in_first);

			mAdapter.notifyDataSetChanged();

			onLoadFinish(false);

			return;
		}

		if (showLoading) {
			BeginApplication.showLoadingPopupWindow(action_bar_title_txtv);
		}

		hasMoreData = true;

		JSONObject requestParameters = WebUrlDomain.getBaseJSONObject(this);
		try {
			requestParameters.put("pageFirst", offset);
			requestParameters.put("pageSize", ROWS);
			requestParameters.put("userId", userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = WebUrlDomain.getCallInterfaceUrl("queryFriendList", requestParameters);

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

					// String test = "{\"state\": \"0\",\"friendList\": [],\"applyList\": ["
					// + "{\"userId\": 1,\"userName\": \"1\"},{\"userId\": 2,\"userName\": \"2\"},{\"userId\": 3,\"userName\": \"3\"},{\"userId\": 4,\"userName\": \"4\"},{\"userId\": 5,\"userName\": \"5\"},"
					// + "{\"userId\": 6,\"userName\": \"6\"},{\"userId\": 7,\"userName\": \"7\"},{\"userId\": 8,\"userName\": \"8\"},{\"userId\": 9,\"userName\": \"9\"},{\"userId\": 10,\"userName\": \"10\"},"
					// + "{\"userId\": 11,\"userName\": \"11\"},{\"userId\": 12,\"userName\": \"12\"},{\"userId\": 13,\"userName\": \"13\"},{\"userId\": 14,\"userName\": \"14\"},{\"userId\": 15,\"userName\": \"15\"}"
					// + "]}";
					// parseFriendListJsonFromServer(new JSONObject(test));

					parseFriendListJsonFromServer(new JSONObject(responseInfo.result));

					mAdapter.notifyDataSetChanged();

					onLoadFinish(true);
				} catch (JSONException e) {
					showFriendListError(e);
				}
			}

			@Override
			public void onFailure(HttpException e, String msg) {
				showFriendListError(e);
			}
		});
	}

	// ===============================

	private void parseFriendListJsonFromServer(JSONObject resObject) {
		// {
		// 　"state": "0",
		// 　"friendList":
		// 　[
		// 　　{
		// 　　　"id": 4,
		// 　　　"userId": 100000,
		// 　　　"state": 2,
		// 　　　"userName": "111",
		// 　　　"friendName": "100",
		// 　　　"friendId": 100001
		// 　　},
		// 　　{
		// 　　　"id": 9,
		// 　　　"userId": 100000,
		// 　　　"state": 2,
		// 　　　"userName": "111",
		// 　　　"friendName": "333",
		// 　　　"friendId": 100002
		// 　　}
		// 　],
		// 　"applyList":
		// 　[
		// 　　{
		// 　　　"id": 10,
		// 　　　"userId": 100002,
		// 　　　"state": 1,
		// 　　　"userName": "333",
		// 　　　"friendName": "111",
		// 　　　"friendId": 100000
		// 　　}
		// 　]
		// }

		int state = resObject.optInt("state");

		if (state != 0) {
			int failedResId = 0;

			if (state == 1) {
				failedResId = R.string.no_more_info;

			} else {
				failedResId = R.string.get_friend_fail;
			}

			hasMoreData = false;

			BeginApplication.showMessageToast(failedResId);

			return;
		}

		try {
			hasMoreData = true;

			List<FriendListItemDomain> tempList = new ArrayList<FriendListItemDomain>();
			FriendListItemDomain tempDomain;

			JSONArray jsonArray = null;

			if (showType == SHOW_FRIEND_LIST) {
				jsonArray = resObject.getJSONArray("friendList");
			} else if (showType == SHOW_APPLY_LIST) {
				jsonArray = resObject.getJSONArray("applyList");
			}

			int arrayLength = jsonArray.length();

			if (arrayLength < ROWS) {
				hasMoreData = false;
			}

			if (arrayLength == 0) {
				hasMoreData = false;

				BeginApplication.showMessageToast(R.string.no_more_info);

				return;
			}

			for (int i = 0; i < arrayLength; i++) {
				JSONObject tempJson = jsonArray.getJSONObject(i);

				tempDomain = new FriendListItemDomain();
				tempDomain.setSequenceNumber(String.valueOf(sequenceNumber));

				if (showType == SHOW_FRIEND_LIST) {
					tempDomain.setFriendId(tempJson.optString("friendId"));
					tempDomain.setFriendName(tempJson.optString("friendName"));
				} else if (showType == SHOW_APPLY_LIST) {
					tempDomain.setFriendId(tempJson.optString("id"));
					tempDomain.setFriendName(tempJson.optString("userName"));
				}

				tempList.add(tempDomain);

				sequenceNumber++;
			}

			mAdapter.setShowType(showType);
			mAdapter.addList(tempList);
		} catch (JSONException e) {
			e.printStackTrace();
			BeginApplication.showMessageToast(R.string.get_friend_fail);
		}
	}

	private void showFriendListError(Exception e) {
		e.printStackTrace();

		BeginApplication.showMessageToast(R.string.get_friend_fail);

		mAdapter.notifyDataSetChanged();

		onLoadFinish(false);
	}

	private void onLoadFinish(boolean refreshTime) {
		friend_lstv.stopRefresh();
		friend_lstv.stopLoadMore();

		if (refreshTime) {
			friend_lstv.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
		}

		BeginApplication.closeLoadingPopupWindow();
	}

	@OnItemClick(R.id.friend_lstv)
	private void seeOthersUserInfo(AdapterView<?> parent, View view, int position, long id) {
		BeginApplication.showLoadingPopupWindow(view);

		Intent intent = new Intent(this, UserInfoActivity.class);
		intent.putExtra("userId", mAdapter.getItem(position - 1).getFriendId());
		intent.putExtra("whatToDo", UserInfoActivity.SEE_OTHERS_USER_INFO);
		startActivity(intent);
	}

	@OnClick(R.id.action_bar_left_back_imgv)
	private void leftBack(View view) {
		finish();

		// overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	@OnClick({ R.id.show_friend_list_txtv, R.id.show_apply_list_txtv })
	private void showFriendList(View view) {
		int viewId = view.getId();

		if (viewId == R.id.show_friend_list_txtv) {
			showType = SHOW_FRIEND_LIST;
			friend_tab_lnrlyt.setBackgroundResource(R.drawable.friend_list_tab);
		} else if (viewId == R.id.show_apply_list_txtv) {
			showType = SHOW_APPLY_LIST;
			friend_tab_lnrlyt.setBackgroundResource(R.drawable.friend_apply_tab);
		}

		onRefresh();
	}

	// ===============================

	private Dialog addFriendDialog;

	class AddFriendHolder {
		@ViewInject(R.id.send_add_request)
		private TextView send_add_request;

		@ViewInject(R.id.userid_input_edttxt)
		private EditText userid_input_edttxt;

		@OnClick(R.id.send_add_request)
		public void sendAddRequest(View view) {

			if (!validateInput()) {
				return;
			}

			BeginApplication.showLoadingPopupWindow(view);

			JSONObject requestParameters = WebUrlDomain.getBaseJSONObject(FriendListActivity.this);
			try {
				requestParameters.put("userId", BeginApplication.ue.getUserId());
				requestParameters.put("friendId", userid_input_edttxt.getText().toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			String url = WebUrlDomain.getCallInterfaceUrl("addFriend", requestParameters);

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
						parseAddFriendJsonFromServer(new JSONObject(responseInfo.result));
					} catch (JSONException e) {
						showAddFriendError(e);
					}
				}

				@Override
				public void onFailure(HttpException e, String msg) {
					showAddFriendError(e);
				}
			});
		}

		/** 校验输入的数据是否有问题 */
		private boolean validateInput() {
			boolean flag = false;
			int errorStringResId = 0;

			String userid_input = userid_input_edttxt.getText().toString();

			if (TextUtils.isEmpty(userid_input)) {
				errorStringResId = R.string.input_userid;
			} else if (userid_input.equals(BeginApplication.ue.getUserId())) {
				errorStringResId = R.string.add_friend_fail_2_not_allow_self;
			} else {
				flag = true;
			}

			if (!flag) {
				BeginApplication.showMessageToast(errorStringResId);
			}

			return flag;
		}
	}

	private void showAddFriendError(Exception e) {
		e.printStackTrace();

		BeginApplication.closeLoadingPopupWindow();

		BeginApplication.showMessageToast(R.string.add_friend_fail);
	}

	private void parseAddFriendJsonFromServer(JSONObject resObject) {
		// {
		// 　"state": "0"
		// }
		int state = resObject.optInt("state");

		if (state != 0) {
			int failedResId = 0;

			if (state == 1) {
				failedResId = R.string.add_friend_fail;
			} else if (state == 2) {
				failedResId = R.string.add_friend_fail_2_not_allow_self;
			} else if (state == 3) {
				failedResId = R.string.add_friend_fail_3_userid_not_exist;
			} else if (state == 4) {
				failedResId = R.string.add_friend_fail_4_already_friend;
			} else if (state == 5) {
				failedResId = R.string.add_friend_fail_5_already_send_add_request;
			} else {
				failedResId = R.string.add_friend_fail;
			}

			BeginApplication.closeLoadingPopupWindow();
			BeginApplication.showMessageToast(failedResId);

			return;
		}

		BeginApplication.closeLoadingPopupWindow();
		BeginApplication.showMessageToast(R.string.add_friend_success);
		addFriendDialog.dismiss();// 没有问题才应该关闭
	}

	@OnClick(R.id.action_bar_right_imgv)
	private void showAddFriend(View view) {
		if (BeginApplication.ue.getUserId().equals(Constant.GUEST_ID)) {
			BeginApplication.showMessageToast(R.string.sign_in_first);

			return;
		}

		if (addFriendDialog != null && addFriendDialog.isShowing()) {
			return;// 显示一个就够了
		}

		if (addFriendDialog == null) {
			addFriendDialog = new Dialog(this, R.style.my_dialog);
			addFriendDialog.setContentView(R.layout.dialog_add_friend);
		}

		View v = addFriendDialog.getWindow().getDecorView();

		AddFriendHolder holder = new AddFriendHolder();

		ViewUtils.inject(holder, v);

		holder.userid_input_edttxt.setText("");

		addFriendDialog.show();
	}
}