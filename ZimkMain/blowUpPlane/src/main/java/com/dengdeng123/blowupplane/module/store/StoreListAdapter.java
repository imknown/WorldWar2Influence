package com.dengdeng123.blowupplane.module.store;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.base.Constant;
import com.dengdeng123.blowupplane.domain.WebUrlDomain;
import com.dengdeng123.blowupplane.entity.StoreItemEntity;
import com.dengdeng123.blowupplane.module.user.UserDao;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class StoreListAdapter extends BaseAdapter {

	class StoreListDataViewHolder {
		@ViewInject(R.id.store_shoot_type_imgv)
		private ImageView store_shoot_type_imgv;

		@ViewInject(R.id.store_shoot_type_txtv)
		private TextView store_shoot_type_txtv;

		@ViewInject(R.id.store_shoot_type_desc_txtv)
		private TextView store_shoot_type_desc_txtv;

		@ViewInject(R.id.store_shoot_type_coin_txtv)
		private TextView store_shoot_type_coin_txtv;

		@ViewInject(R.id.store_num_buy_lnrl)
		private LinearLayout store_num_buy_lnrl;

		@ViewInject(R.id.store_num_buy_edttxt)
		private EditText store_num_buy_edttxt;

		private StoreItemEntity storeItemEntity;

		/**
		 * 获取真实的 对象
		 * 
		 * @param position
		 *            修改的真实的位置, 不受 getView 滑动影响<br>
		 *            <font style="color:red">不能直接传递对象, 因为对象时引用的, 一变皆变, 而 int 是 值传递</font>
		 */
		public StoreListDataViewHolder(int position) {
			this.storeItemEntity = storeItemEntities.get(position);
		}

		@OnClick(R.id.store_num_buy_minus_btn)
		private void numBuyMinus(View view) {
			int oldBuyNumInt = Integer.parseInt(store_num_buy_edttxt.getText().toString());

			if (1 == oldBuyNumInt) {
				return;
			}

			String newBuyNum = String.valueOf(oldBuyNumInt - 1);

			storeItemEntity.setNumSelected(Integer.parseInt(newBuyNum));
			store_num_buy_edttxt.setText(newBuyNum);
		}

		@OnClick(R.id.store_num_buy_addition_btn)
		private void numBuyAddition(View view) {
			int oldBuyNumInt = Integer.parseInt(store_num_buy_edttxt.getText().toString());// 当前购买的数量
			int newBuyNumInt = oldBuyNumInt + 1;// 要购买的数量
			int unitCoin = Integer.parseInt(store_shoot_type_coin_txtv.getText().toString());// 单价

			int totalMyCoin = BeginApplication.ue.getCoinNum();// 我的金币余额
			int totalCoinToPay = newBuyNumInt * unitCoin;// 总价

			if (totalMyCoin < totalCoinToPay) {
				BeginApplication.showMessageToast(R.string.coin_num_is_not_enough);

				return;
			}

			storeItemEntity.setNumSelected(newBuyNumInt);
			store_num_buy_edttxt.setText(String.valueOf(newBuyNumInt));
		}

		@OnClick(R.id.store_num_buy_btn)
		private void buy(View view) {
			int oldBuyNumInt = Integer.parseInt(store_num_buy_edttxt.getText().toString());// 当前购买的数量
			int unitCoin = Integer.parseInt(store_shoot_type_coin_txtv.getText().toString());// 单价

			int totalMyCoin = BeginApplication.ue.getCoinNum();// 我的金币余额
			int totalCoinToPay = oldBuyNumInt * unitCoin;// 总价

			if (totalMyCoin < totalCoinToPay) {
				BeginApplication.showMessageToast(R.string.coin_num_is_not_enough);

				return;
			}

			BeginApplication.showLoadingPopupWindow(view);

			if (BeginApplication.ue.getUserId().equals(Constant.GUEST_ID)) {
				guestBuy();// 游客的话, 不发送网络请求, 只改本地数据库
			} else {
				signedInUserBuy();// 联网购买
			}
		}

		private void guestBuy() {
			int coinLeft = getCoinLeft();
			String typeName = store_shoot_type_txtv.getText().toString();
			int numBuy = Integer.parseInt(store_num_buy_edttxt.getText().toString());

			boolean successOrNot = new UserDao().updateUserInfo(Constant.GUEST_ID, coinLeft, typeName, numBuy);

			if (successOrNot) {
				showBuyOk();
			} else {
				BeginApplication.closeLoadingPopupWindow();

				BeginApplication.showMessageToast(R.string.buy_failed);
			}
		}

		private void signedInUserBuy() {
			JSONObject requestParameters = WebUrlDomain.getBaseJSONObject(activity);

			setType(requestParameters);

			String url = WebUrlDomain.getCallInterfaceUrl("changeProps", requestParameters);

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

		private void parseJsonFromServer(JSONObject resObject) {
			int state = resObject.optInt("state");

			if (state != 0) {
				int failedResId = 0;// 0成功，1失败 2 余额不足 3 数量不可为0

				if (state == 1) {
					failedResId = R.string.buy_failed;
				} else if (state == 2) {
					failedResId = R.string.buy_balance_not_enough;
				} else if (state == 3) {
					failedResId = R.string.buy_number_not_zero;
				} else {
					failedResId = R.string.buy_failed;
				}

				BeginApplication.closeLoadingPopupWindow();

				BeginApplication.showMessageToast(failedResId);

				return;
			}

			showBuyOk();
		}

		private int getCoinLeft() {
			int buyNumInt = Integer.parseInt(store_num_buy_edttxt.getText().toString());// 购买的数量
			int unitCoin = Integer.parseInt(store_shoot_type_coin_txtv.getText().toString());// 单价

			int totalMyCoin = BeginApplication.ue.getCoinNum();// 我的金币余额
			int totalCoinToPay = buyNumInt * unitCoin;// 总价

			int coinLeft = totalMyCoin - totalCoinToPay;// 购买的余额

			return coinLeft;
		}

		private void showBuyOk() {
			String typeName = store_shoot_type_txtv.getText().toString();
			int numBuy = Integer.parseInt(store_num_buy_edttxt.getText().toString());

			if ("炸弹".equals(typeName)) {
				BeginApplication.ue.setBombNum(numBuy + BeginApplication.ue.getBombNum());
			} else if ("爆头".equals(typeName)) {
				BeginApplication.ue.setHeadshotNum(numBuy + BeginApplication.ue.getHeadshotNum());
			}

			BeginApplication.ue.setCoinNum(getCoinLeft());

			((StoreActivity) activity).showUserInfo();

			BeginApplication.closeLoadingPopupWindow();

			BeginApplication.showMessageToast(R.string.buy_success);
		}

		private void setType(JSONObject requestParameters) {
			int bombNum = 0;
			int headshotNum = 0;

			String typeName = store_shoot_type_txtv.getText().toString();
			int numBuy = Integer.parseInt(store_num_buy_edttxt.getText().toString());

			if ("炸弹".equals(typeName)) {
				bombNum = numBuy;
			} else if ("爆头".equals(typeName)) {
				headshotNum = numBuy;
			}

			try {
				requestParameters.put("userId", BeginApplication.ue.getUserId());
				requestParameters.put("bomb", bombNum);
				requestParameters.put("headShot", headshotNum);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void showError(Exception e) {
		e.printStackTrace();

		BeginApplication.closeLoadingPopupWindow();

		BeginApplication.showMessageToast(R.string.buy_failed);
	}

	private Activity activity;

	private List<StoreItemEntity> storeItemEntities = new ArrayList<StoreItemEntity>();

	public StoreListAdapter(Activity activity) {
		this.activity = activity;
	}

	public void clear() {
		this.storeItemEntities.clear();
	}

	public void setStoreItemList(List<StoreItemEntity> storeItemList) {
		this.storeItemEntities = storeItemList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		StoreListDataViewHolder holder = null;

		StoreItemEntity storeItemEntity = storeItemEntities.get(position);

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.activity_store_lstv_item, (ViewGroup) null, false);

			holder = new StoreListDataViewHolder(position);

			ViewUtils.inject(holder, convertView);

			convertView.setTag(holder);
		} else {
			holder = (StoreListDataViewHolder) convertView.getTag();
		}

		holder.store_shoot_type_imgv.setImageResource(getResId(storeItemEntity.getName()));
		holder.store_shoot_type_txtv.setText(storeItemEntity.getName());
		holder.store_shoot_type_desc_txtv.setText(storeItemEntity.getDesc());
		holder.store_shoot_type_coin_txtv.setText(storeItemEntity.getCoinNum());
		holder.store_num_buy_edttxt.setText(String.valueOf(storeItemEntity.getNumSelected()));
		holder.store_num_buy_lnrl.setVisibility(storeItemEntity.getVisibility());

		return convertView;
	}

	@Override
	public int getCount() {
		return storeItemEntities.size();
	}

	private int getResId(String typeName) {
		int resId = 0;

		if ("炸弹".equals(typeName)) {
			resId = R.drawable.up_icon_shoot_type_bomb;
		} else if ("爆头".equals(typeName)) {
			resId = R.drawable.twinkling_headshot;
		} else if ("金币".equals(typeName)) {
			resId = R.drawable.store_coin;
		}

		return resId;
	}

	@Override
	public StoreItemEntity getItem(int position) {
		return storeItemEntities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
