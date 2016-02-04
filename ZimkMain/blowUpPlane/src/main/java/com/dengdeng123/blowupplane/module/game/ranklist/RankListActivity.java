package com.dengdeng123.blowupplane.module.game.ranklist;

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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.domain.RankListItemDomain;
import com.dengdeng123.blowupplane.domain.WebUrlDomain;
import com.dengdeng123.blowupplane.module.user.UserInfoActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

@ContentView(R.layout.activity_rank_list)
public class RankListActivity extends BaseActivity implements IXListViewListener {

	@ViewInject(R.id.rank_lstv)
	private XListView rank_lstv;

	@ViewInject(R.id.action_bar_title_txtv)
	private TextView action_bar_title_txtv;

	@ViewInject(R.id.action_bar_right_imgv)
	private ImageView action_bar_right_imgv;

	@ViewInject(R.id.rank_tab_lnrlyt)
	private LinearLayout rank_tab_lnrlyt;

	@ViewInject(R.id.show_score_list_txtv)
	private TextView show_score_list_txtv;

	@ViewInject(R.id.show_infinite_list_txtv)
	private TextView show_infinite_list_txtv;

	/** 起始下标 */
	private int offset = 0;

	/** 获取多少条数据 */
	private final int ROWS = 10;

	/** 名次 */
	private int rank = 1;

	private RankListAdapter mAdapter;

	public static final int SHOW_GLOBAL_SCORE_LIST = 0x1001;
	public static final int SHOW_GLOBAL_INFINITE_LIST = 0x1002;
	public static final int SHOW_FRIEND_SCORE_LIST = 0x1003;
	public static final int SHOW_FRIEND_INFINITE_LIST = 0x1004;

	/** 显示类型 */
	private int showType = SHOW_GLOBAL_SCORE_LIST;

	/** 是否有新数据 */
	@SuppressWarnings("unused")
	private boolean hasMoreData = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action_bar_title_txtv.setText(R.string.ranking_list);
		action_bar_right_imgv.setImageResource(R.drawable.rank_list_friend_toggle_btn);

		mAdapter = new RankListAdapter(this);

		rank_lstv.setAdapter(mAdapter);
		rank_lstv.setPullRefreshEnable(true);
		rank_lstv.setPullLoadEnable(true);
		rank_lstv.setXListViewListener(this);
		rank_lstv.setRefreshTime("无");

		getMainListData(false);
	}

	/** 顶部下拉刷新 */
	@Override
	public void onRefresh() {
		offset = 0;
		rank = 1;

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

	// @OnScrollStateChanged(R.id.rank_lstv)
	// private void onScrollStateChanged(AbsListView view, int scrollState) {
	// if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
	// if (hasMoreData && view.getLastVisiblePosition() == view.getCount() - 1) {
	// rank_lstv.setPullLoadEnable(false);
	//
	// onLoadMore();
	// }
	// }
	// }

	// @OnScroll(R.id.rank_lstv)
	// public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	// }

	private void getMainListData(boolean showLoading) {
		if (showLoading) {
			BeginApplication.showLoadingPopupWindow(action_bar_title_txtv);
		}

		hasMoreData = true;

		String methodCalled = "";

		JSONObject requestParameters = WebUrlDomain.getBaseJSONObject(this);
		try {
			requestParameters.put("pageFirst", offset);
			requestParameters.put("pageSize", ROWS);

			if (showType == SHOW_GLOBAL_SCORE_LIST) {
				methodCalled = "queryRankingOfIntegral";
			} else if (showType == SHOW_GLOBAL_INFINITE_LIST) {
				methodCalled = "queryRankingOfEndless";
			} else if (showType == SHOW_FRIEND_SCORE_LIST) {
				methodCalled = "queryFriendOfIntegral";
				requestParameters.put("userId", BeginApplication.ue.getUserId());
			} else if (showType == SHOW_FRIEND_INFINITE_LIST) {
				methodCalled = "queryFriendOfEndless";
				requestParameters.put("userId", BeginApplication.ue.getUserId());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = WebUrlDomain.getCallInterfaceUrl(methodCalled, requestParameters);

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

					mAdapter.notifyDataSetChanged();

					onLoadFinish(true);
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

	private void parseJsonFromServer(JSONObject resObject) {
		// {
		// 　"state": "0",
		// 　"rows": [
		// 　　{
		// 　　　"passBarrier": 2,
		// 　　　"userId": 100002,
		// 　　　"shotdownTotal": 2,
		// 　　　"userName": "333"
		// 　　}
		// 　]
		// }

		int state = resObject.optInt("state");

		if (state != 0) {
			int failedResId = 0;

			if (state == 1) {
				failedResId = R.string.no_more_info;

			} else {
				failedResId = R.string.tips_network_error_2;
			}

			hasMoreData = false;

			BeginApplication.showMessageToast(failedResId);

			return;
		}

		try {
			hasMoreData = true;

			List<RankListItemDomain> tempList = new ArrayList<RankListItemDomain>();
			RankListItemDomain tempDomain;

			JSONArray jsonArray = resObject.getJSONArray("rows");

			int arrayLength = jsonArray.length();

			if (arrayLength < ROWS) {
				hasMoreData = false;
			}

			for (int i = 0; i < arrayLength; i++) {
				JSONObject tempJson = jsonArray.getJSONObject(i);

				tempDomain = new RankListItemDomain();
				tempDomain.setRank(String.valueOf(rank));
				tempDomain.setUserId(tempJson.optString("userId"));
				tempDomain.setNickname(tempJson.optString("userName"));
				tempDomain.setShotdownTotal(tempJson.optString("shotdownTotal"));

				tempList.add(tempDomain);

				rank++;
			}

			mAdapter.addList(tempList);
		} catch (JSONException e) {
			e.printStackTrace();
			BeginApplication.showMessageToast(R.string.get_ranking_list_fail);
		}
	}

	private void showError(Exception e) {
		e.printStackTrace();

		BeginApplication.showMessageToast(R.string.get_ranking_list_fail);

		mAdapter.notifyDataSetChanged();

		onLoadFinish(false);
	}

	private void onLoadFinish(boolean refreshTime) {
		rank_lstv.stopRefresh();
		rank_lstv.stopLoadMore();

		if (refreshTime) {
			rank_lstv.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
		}

		BeginApplication.closeLoadingPopupWindow();
	}

	@OnItemClick(R.id.rank_lstv)
	private void seeOthersUserInfo(AdapterView<?> parent, View view, int position, long id) {
		BeginApplication.showLoadingPopupWindow(view);

		Intent intent = new Intent(this, UserInfoActivity.class);
		intent.putExtra("userId", mAdapter.getItem(position - 1).getUserId());
		intent.putExtra("whatToDo", UserInfoActivity.SEE_OTHERS_USER_INFO);
		startActivity(intent);
	}

	@OnClick({ R.id.show_score_list_txtv, R.id.show_infinite_list_txtv })
	private void showList(View view) {
		int viewId = view.getId();

		if (viewId == R.id.show_score_list_txtv) {
			if (showType == SHOW_GLOBAL_SCORE_LIST || showType == SHOW_GLOBAL_INFINITE_LIST) {
				showType = SHOW_GLOBAL_SCORE_LIST;
			} else if (showType == SHOW_FRIEND_SCORE_LIST || showType == SHOW_FRIEND_INFINITE_LIST) {
				showType = SHOW_FRIEND_SCORE_LIST;
			}

			rank_tab_lnrlyt.setBackgroundResource(R.drawable.friend_list_tab);
		} else if (viewId == R.id.show_infinite_list_txtv) {
			if (showType == SHOW_GLOBAL_SCORE_LIST || showType == SHOW_GLOBAL_INFINITE_LIST) {
				showType = SHOW_GLOBAL_INFINITE_LIST;
			} else if (showType == SHOW_FRIEND_SCORE_LIST || showType == SHOW_FRIEND_INFINITE_LIST) {
				showType = SHOW_FRIEND_INFINITE_LIST;
			}

			rank_tab_lnrlyt.setBackgroundResource(R.drawable.friend_apply_tab);
		}

		onRefresh();
	}

	@OnClick(R.id.action_bar_left_back_imgv)
	private void leftBack(View view) {
		finish();
	}

	@OnClick(R.id.action_bar_right_imgv)
	private void changeType(View view) {
		if (showType == SHOW_GLOBAL_SCORE_LIST || showType == SHOW_GLOBAL_INFINITE_LIST) {
			showType = SHOW_FRIEND_SCORE_LIST;
			action_bar_right_imgv.setImageResource(R.drawable.rank_list_global_toggle_btn);
			show_score_list_txtv.setText(R.string.friend_scroe);
			show_infinite_list_txtv.setText(R.string.friend_infinite);
		} else if (showType == SHOW_FRIEND_SCORE_LIST || showType == SHOW_FRIEND_INFINITE_LIST) {
			showType = SHOW_GLOBAL_SCORE_LIST;
			action_bar_right_imgv.setImageResource(R.drawable.rank_list_friend_toggle_btn);
			show_score_list_txtv.setText(R.string.global_scroe);
			show_infinite_list_txtv.setText(R.string.global_infinite);
		}

		rank_tab_lnrlyt.setBackgroundResource(R.drawable.friend_list_tab);

		onRefresh();
	}
}