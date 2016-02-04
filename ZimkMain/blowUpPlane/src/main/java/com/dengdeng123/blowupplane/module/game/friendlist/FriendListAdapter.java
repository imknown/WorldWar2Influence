package com.dengdeng123.blowupplane.module.game.friendlist;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.domain.FriendListItemDomain;
import com.dengdeng123.blowupplane.domain.WebUrlDomain;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class FriendListAdapter extends BaseAdapter {
	private Activity activity;
	private List<FriendListItemDomain> mList = new ArrayList<FriendListItemDomain>();

	private int showType;

	private static final int ACCEPT_APPLY = 2;
	private static final int REFUSE_APPLY = 3;

	public FriendListAdapter(Activity activity) {
		this.activity = activity;
	}

	public void setShowType(int showType) {
		this.showType = showType;
	}

	public void clear() {
		this.mList.clear();
	}

	public void addList(List<FriendListItemDomain> list) {
		if (list == null) {
			return;
		}

		int size = list.size();

		for (int i = 0; i < size; i++) {
			this.mList.add(list.get(i));
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		FriendListViewHolder holder = null;

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.activity_friend_list_listv_item, (ViewGroup) null, false);

			holder = new FriendListViewHolder();

			ViewUtils.inject(holder, convertView);

			convertView.setTag(holder);
		} else {
			holder = (FriendListViewHolder) convertView.getTag();
		}

		holder.viewPosition = position;
		holder.sequence_number_txtv.setText(mList.get(position).getSequenceNumber());
		holder.friend_name_txtv.setText(mList.get(position).getFriendName());

		if (showType == FriendListActivity.SHOW_FRIEND_LIST) {
			holder.accept_apply_txtv.setVisibility(View.GONE);
			holder.refuse_apply_txtv.setVisibility(View.GONE);
		} else if (showType == FriendListActivity.SHOW_APPLY_LIST) {
			holder.accept_apply_txtv.setVisibility(View.VISIBLE);
			holder.refuse_apply_txtv.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public FriendListItemDomain getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class FriendListViewHolder {
		@ViewInject(R.id.sequence_number_txtv)
		private TextView sequence_number_txtv;

		@ViewInject(R.id.friend_name_txtv)
		private TextView friend_name_txtv;

		@ViewInject(R.id.accept_apply_txtv)
		private TextView accept_apply_txtv;

		@ViewInject(R.id.refuse_apply_txtv)
		private TextView refuse_apply_txtv;

		public int viewPosition;

		@OnClick(R.id.accept_apply_txtv)
		private void acceptApply(View view) {
			handlApply(ACCEPT_APPLY);
		}

		@OnClick(R.id.refuse_apply_txtv)
		private void refuseApply(View view) {
			handlApply(REFUSE_APPLY);
		}

		private void handlApply(final int stateSelected) {
			JSONObject requestParameters = WebUrlDomain.getBaseJSONObject(activity);

			try {
				requestParameters.put("id", mList.get(viewPosition).getFriendId());
				requestParameters.put("state", stateSelected);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			String url = WebUrlDomain.getCallInterfaceUrl("confirmAddFriend", requestParameters);

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
						parseJsonFromServer(new JSONObject(responseInfo.result), stateSelected);

					} catch (JSONException e) {
						showError(e, stateSelected);
					}
				}

				@Override
				public void onFailure(HttpException e, String msg) {
					showError(e, stateSelected);
				}
			});
		}

		private void parseJsonFromServer(JSONObject resObject, int stateSelected) {
			int state = resObject.optInt("state");

			if (state != 0) {
				int failedResId = 0;

				if (state == 1) {
					if (stateSelected == ACCEPT_APPLY) {
						failedResId = R.string.accept_friend_apply_fail;
					} else if (stateSelected == REFUSE_APPLY) {
						failedResId = R.string.refuse_friend_apply_fail;
					}
				} else {
					if (stateSelected == ACCEPT_APPLY) {
						failedResId = R.string.accept_friend_apply_fail;
					} else if (stateSelected == REFUSE_APPLY) {
						failedResId = R.string.refuse_friend_apply_fail;
					}
				}

				BeginApplication.closeLoadingPopupWindow();

				BeginApplication.showMessageToast(failedResId);

				return;
			}

			int successResId = 0;

			if (stateSelected == ACCEPT_APPLY) {
				successResId = R.string.accept_friend_apply_success;
			} else if (stateSelected == REFUSE_APPLY) {
				successResId = R.string.refuse_friend_apply_success;
			}

			mList.remove(viewPosition);
			notifyDataSetChanged();

			BeginApplication.showMessageToast(successResId);

			BeginApplication.closeLoadingPopupWindow();
		}

		private void showError(Exception e, int stateSelected) {
			e.printStackTrace();

			int failedResId = 0;

			if (stateSelected == ACCEPT_APPLY) {
				failedResId = R.string.accept_friend_apply_fail;
			} else if (stateSelected == REFUSE_APPLY) {
				failedResId = R.string.refuse_friend_apply_fail;
			}

			BeginApplication.showMessageToast(failedResId);

			BeginApplication.closeLoadingPopupWindow();
		}
	}
}
