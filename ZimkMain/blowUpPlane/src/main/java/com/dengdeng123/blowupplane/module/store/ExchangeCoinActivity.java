package com.dengdeng123.blowupplane.module.store;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.entity.StoreCoinItemEntity;
import com.dengdeng123.blowupplane.module.store.payment.PayActivity;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

@ContentView(R.layout.activity_store_coin)
public class ExchangeCoinActivity extends BaseActivity {

	@ViewInject(R.id.action_bar_title_txtv)
	private TextView action_bar_title_txtv;

	@ViewInject(R.id.store_coin_lstv)
	private ListView store_coin_lstv;

	private ExchangeCoinListAdapter storeCoinListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action_bar_title_txtv.setText(getString(R.string.payment));

		List<StoreCoinItemEntity> storeItemEntities = new ArrayList<StoreCoinItemEntity>();

		// StoreCoinItemDomain storeCoinItemDomain = new StoreCoinItemDomain();
		// storeCoinItemDomain.setImageResId(R.drawable.store_coin);
		// storeCoinItemDomain.setHowManyCoins("1000");
		// storeCoinItemDomain.setHowMuchRmb("1");
		// storeCoinItemDomain.setName("金幣");
		// storeCoinItemDomain.setDesc("可以用來購買道具");
		// storeItemEntities.add(storeCoinItemDomain);

		try {
			storeItemEntities = BeginApplication.dbUtils.findAll(Selector.from(StoreCoinItemEntity.class).where("PD_key", "=", "Money"));
		} catch (DbException e) {
			e.printStackTrace();
		}

		// try {
		// storeItemEntities = BeginApplication.dbUtils.findAll(Selector.from(StoreItemEntity.class));
		// } catch (DbException e) {
		// e.printStackTrace();
		// }

		storeCoinListAdapter = new ExchangeCoinListAdapter(this);
		storeCoinListAdapter.setStoreCoinItemList(storeItemEntities);

		store_coin_lstv.setAdapter(storeCoinListAdapter);
	}

	@OnItemClick(R.id.store_coin_lstv)
	private void gotoPayment(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, PayActivity.class);
		StoreCoinItemEntity storeCoinItemDomain = storeCoinListAdapter.getItem(position);
		intent.putExtra("storeCoinItemDomain", storeCoinItemDomain);
		startActivity(intent);
	}

	@OnClick(R.id.action_bar_left_back_imgv)
	private void leftBack(View view) {
		finish();
	}
}