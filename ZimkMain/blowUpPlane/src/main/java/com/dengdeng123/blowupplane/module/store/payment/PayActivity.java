package com.dengdeng123.blowupplane.module.store.payment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.base.Constant;
import com.dengdeng123.blowupplane.domain.WebUrlDomain;
import com.dengdeng123.blowupplane.entity.StoreCoinItemEntity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_pay)
public class PayActivity extends BaseActivity {

	@ViewInject(R.id.action_bar_title_txtv)
	private TextView action_bar_title_txtv;

	@ViewInject(R.id.price_exchange_rate_txtv)
	private TextView price_exchange_rate_txtv;

	@ViewInject(R.id.item_desc_txtv)
	private TextView item_desc_txtv;

	public static final String PARTNER = "2088011626296872";// 合作商户PID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	public static final String SELLER = "2088011626296872";// 商户收款的支付宝账号
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMMNIMgGOjZgPtDzdMwhgEAX1BBRenjrx2+j1sAx/7qCw+yt6eNizcUj1NyStdEe0UBaV8grnutyiTDDJYT2o+OYuZXJ0MtAzGOSrdFhW9MBpQToUwY22BsQl7JLbFzRHHyl1BJAgYwNp79YQGtWYVHWsN8RsAgFVUm1q4xxu6PbAgMBAAECgYB2V7nDAtxA76ZNOtlrnQvmV07IU9c+FLgdiUebtvpU3FApgt98og79BJ968Ny1s7/8DpRiE+1JJ2YLmuthwA9w0GFq2tQcoSEg3MtUKvZYOfcbNuc1LNkpP49QRUGgwDoIqtbOMKkg6ntdYsYBTwjsTBsGjAD42v6JzOn7FIK9eQJBAPZDE4yaA2oYJlyu5avQKWEhczKWdbZgtSjFXAoup5ONmVXVq96D2C9ZIAT/xOGg28HA43JVjZUHpTHRDbpkiD0CQQDKw6SFSgwK+HHjaZaTYAEbRjz9g0OzaMbDSBKI5fS8ATX1s7/bbN9wFZGAaeLR4nCNHcEAwl13nU9AdDZSjwX3AkBtlT2exG4eiO6RMba79N5k/YiQi0mIRZJ3uRMS6N3jGBCSlIltdAgAUc2gy9vldzozBs1vdEiTd5p4B4nvVosVAkEAmsy2pBQeEZoPyODp1mXrMllzYtB+NVB+vsQdcmqtZ4M9IPI0PV9nTnkI9pLgWgCYkQPwP5YkKZft03Vlq64JjQJAC4grRyjHP3tudRI1w6wotOgG7tnwYFDD2Jos1TpMVHB+grCnE/7SXTs5GQFfVlZv5OcqIJl6amgfSkne2Y5ikA==";// 商户（RSA）私钥，pkcs8格式
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCv2i/dyMC9RTGLYwaYUcyG4Tm+Tokxu2ycQ7Lk Z6MLPq0L6nocRRnde4mqf5tq2fUZM/B+7SraheL0j7Zw7Hv7DoFBirwUWPP1pArcLS4AOsvM9knA uo3+C68eycE08R1JOzYr01Z4c+pG03hbSuC9d6iT0/QDp17OmS4+mACAmQIDAQAB";// 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。

	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;

	private static Context mContext;

	private StoreCoinItemEntity storeCoinItemDomain;

	/** 本次交易的 订单号 */
	private String outTradeNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;

		action_bar_title_txtv.setText(getString(R.string.buy));

		storeCoinItemDomain = (StoreCoinItemEntity) getIntent().getSerializableExtra("storeCoinItemDomain");

		price_exchange_rate_txtv.setText(getString(R.string.price_exchange_rate, storeCoinItemDomain.getHowMuchRmb(), storeCoinItemDomain.getHowManyCoins()));
		item_desc_txtv.setText(storeCoinItemDomain.getDesc());
	}

	/** check whether the device has authentication alipay account. 查询终端设备是否存在支付宝认证账户 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(PayActivity.this);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();
	}

	/** call alipay sdk pay. 调用SDK支付 */
	@OnClick(R.id.pay)
	public void pay(View v) {
		if (BeginApplication.ue.getUserId().equals(Constant.GUEST_ID)) {
			BeginApplication.showMessageToast(R.string.sign_in_first);
			return;
		}

		getOnlineUserInfo();
	}

	private static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				// String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				if (TextUtils.equals(resultStatus, "9000")) {
					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					BeginApplication.showMessageToast(R.string.pay_success);
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						BeginApplication.showMessageToast(R.string.pay_to_be_confirmed);
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						BeginApplication.showMessageToast(R.string.pay_failed);
					}
				}

				BeginApplication.closeLoadingPopupWindow();

				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(mContext, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};

	/** get the sdk version. 获取SDK版本号 */
	@SuppressWarnings("unused")
	private void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 * @param subject
	 *            商品名称
	 * @param body
	 *            商品详情
	 * @param price
	 *            商品金额
	 * @return 订单信息
	 */
	private String getOrderInfo(String subject, String body, String price) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + outTradeNo + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + WebUrlDomain.CONNECT_HOST + "buyGold" + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值（需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	// /** get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范） */
	// private String getOutTradeNo() {
	// SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
	// Date date = new Date();
	// String key = format.format(date);
	//
	// Random r = new Random();
	// key = key + r.nextInt();
	// key = key.substring(0, 15);
	// return key;
	// }

	/** get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范） */
	private void getOutTradeNo() {
		BeginApplication.showLoadingPopupWindow(price_exchange_rate_txtv);

		JSONObject requestParameters = WebUrlDomain.getBaseJSONObject(this);
		try {
			requestParameters.put("uid", BeginApplication.ue.getUserId());
			requestParameters.put("cash", storeCoinItemDomain.getHowMuchRmb());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = WebUrlDomain.getCallInterfaceUrl("createRechargeOrder", requestParameters);

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
					parseOutTradeNoJsonFromServer(new JSONObject(responseInfo.result));
				} catch (JSONException e) {
					showGetOutTradeNoError(e);
				}
			}

			@Override
			public void onFailure(HttpException e, String msg) {
				showGetOutTradeNoError(e);
			}
		});
	}

	/** 获取订单号 失败 */
	private void showGetOutTradeNoError(Exception e) {
		e.printStackTrace();

		BeginApplication.closeLoadingPopupWindow();

		BeginApplication.showMessageToast(R.string.get_out_trade_no_fail);
	}

	/** 获取订单号 成功 之后的数据解析 */
	private void parseOutTradeNoJsonFromServer(JSONObject resObject) {
		// {
		// 　"state": "0",
		// 　"rechargingRecordsId": "201502110000005429"
		// }

		int state = resObject.optInt("state");

		if (state != 0) {
			int failedResId = 0;

			if (state == 1) {
				failedResId = R.string.get_out_trade_no_fail;
			} else {
				failedResId = R.string.get_out_trade_no_fail;
			}

			BeginApplication.closeLoadingPopupWindow();

			BeginApplication.showMessageToast(failedResId);

			return;
		}

		outTradeNo = resObject.optString("rechargingRecordsId");

		// ===================================================

		// 订单
		String orderInfo = getOrderInfo(storeCoinItemDomain.getName(), storeCoinItemDomain.getDesc(), storeCoinItemDomain.getHowMuchRmb());

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

		Runnable payRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(PayActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();

		// BeginApplication.showMessageToast(R.string.sign_in_success);
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/** get the sign type we use. 获取签名方式 */
	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	// ==================================================================

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

					getOutTradeNo();
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
			BeginApplication.showMessageToast(R.string.get_user_info_fail);
		}
	}

	@OnClick(R.id.action_bar_left_back_imgv)
	private void leftBack(View view) {
		finish();
	}
}
