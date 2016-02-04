package com.dengdeng123.blowupplane.module.game.ranklist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.domain.RankListItemDomain;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class RankListAdapter extends BaseAdapter {
	private Activity activity;
	private List<RankListItemDomain> mList = new ArrayList<RankListItemDomain>();

	public RankListAdapter(Activity activity) {
		this.activity = activity;
	}

	public void clear() {
		this.mList.clear();
	}

	public void addList(List<RankListItemDomain> list) {
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
		RankListViewHolder holder = null;

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.activity_rank_list_listv_item, (ViewGroup) null, false);

			holder = new RankListViewHolder();

			ViewUtils.inject(holder, convertView);

			convertView.setTag(holder);
		} else {
			holder = (RankListViewHolder) convertView.getTag();
		}

		// holder.viewPosition = position;
		holder.rank_txtv.setText(mList.get(position).getRank());
		holder.nickname_txtv.setText(mList.get(position).getNickname());
		holder.shotdown_total_txtv.setText(mList.get(position).getShotdownTotal());

		return convertView;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public RankListItemDomain getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class RankListViewHolder {
		@ViewInject(R.id.rank_txtv)
		private TextView rank_txtv;

		@ViewInject(R.id.nickname_txtv)
		private TextView nickname_txtv;

		@ViewInject(R.id.shotdown_total_txtv)
		private TextView shotdown_total_txtv;

		// public int viewPosition;
	}
}
