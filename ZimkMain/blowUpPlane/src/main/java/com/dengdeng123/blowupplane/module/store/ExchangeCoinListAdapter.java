package com.dengdeng123.blowupplane.module.store;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.entity.StoreCoinItemEntity;
import com.dengdeng123.blowupplane.util.BitmapHelp;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ExchangeCoinListAdapter extends BaseAdapter {

	private class StoreCoinListDataViewHolder {
		@ViewInject(R.id.store_coin_lstv_item_img_imgv)
		private ImageView store_coin_lstv_item_img_imgv;

		@ViewInject(R.id.store_coin_lstv_item_how_many_coins_txtv)
		private TextView store_coin_lstv_item_how_many_coins_txtv;

		@ViewInject(R.id.store_coin_lstv_item_how_much_rmb_txtv)
		private TextView store_coin_lstv_item_how_much_rmb_txtv;
	}

	private Activity activity;
	private List<StoreCoinItemEntity> storeCoinItemEntities = new ArrayList<StoreCoinItemEntity>();

	BitmapUtils bu;

	public ExchangeCoinListAdapter(Activity activity) {
		this.activity = activity;
		this.bu = BitmapHelp.getBitmapUtils(activity);
	}

	public void clear() {
		this.storeCoinItemEntities.clear();
	}

	public void setStoreCoinItemList(List<StoreCoinItemEntity> storeCoinItemList) {
		this.storeCoinItemEntities = storeCoinItemList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		StoreCoinListDataViewHolder holder = null;

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.activity_store_coin_lstv_item, (ViewGroup) null, false);

			holder = new StoreCoinListDataViewHolder();

			ViewUtils.inject(holder, convertView);

			convertView.setTag(holder);
		} else {
			holder = (StoreCoinListDataViewHolder) convertView.getTag();
		}

		StoreCoinItemEntity storeCoinItemEntity = storeCoinItemEntities.get(position);

		holder.store_coin_lstv_item_img_imgv.setImageResource(storeCoinItemEntity.getItemImg());
		holder.store_coin_lstv_item_how_many_coins_txtv.setText(activity.getString(R.string.how_many_coins, storeCoinItemEntity.getHowManyCoins()));
		holder.store_coin_lstv_item_how_much_rmb_txtv.setText(activity.getString(R.string.how_much_rmb, storeCoinItemEntity.getHowMuchRmb()));

		return convertView;
	}

	@Override
	public int getCount() {
		return storeCoinItemEntities.size();
	}

	@Override
	public StoreCoinItemEntity getItem(int position) {
		return storeCoinItemEntities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
