package com.dengdeng123.blowupplane;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.domain.WebUrlDomain;
import com.dengdeng123.blowupplane.update.UpdateActivity;
import com.dengdeng123.blowupplane.util.DatabaseUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        copyDatabase();

        checkNeedToUpdateOrNot();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                intentToMain();
            }
        }, 1500);
    }

    /**
     * 如果没有飞机坐标的数据库的话, 进行创建. 没有的话, 不进行操作
     */
    private void copyDatabase() {
        try {
            DatabaseUtil.copyDatabase(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void intentToMain() {
        Intent i = new Intent(SplashActivity.this, SplashAnimShowActivity.class);
        SplashActivity.this.startActivity(i);

        finish();
    }

    private void checkNeedToUpdateOrNot() {

        JSONObject requestParameters = WebUrlDomain.getBaseJSONObject(this);
        String url = WebUrlDomain.getCallInterfaceUrl("getAppversionInfo", requestParameters);

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

                JSONObject resObject = null;

                try {
                    resObject = new JSONObject(responseInfo.result);

                    intentToMain();

                    if (resObject.optInt("state") == -1) {
                        // 已经是最新
                        return;
                    }

                    parseJsonFromServer(resObject);
                } catch (JSONException e) {
                    e.printStackTrace();

                    // Toast.makeText(SplashActivity.this, getString(R.string.checkver_fail), Toast.LENGTH_SHORT).show();
                    BeginApplication.showMessageToast(R.string.checkver_fail);
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                error.printStackTrace();

                // Toast.makeText(SplashActivity.this, getString(R.string.checkver_fail), Toast.LENGTH_SHORT).show();
                BeginApplication.showMessageToast(R.string.checkver_fail);
            }
        });
    }

    private void parseJsonFromServer(JSONObject resObject) {
        String needupdate = resObject.optString("needupdate");
        String resCode;

        if (needupdate.equals("1")) {
            resCode = UpdateActivity.ENFORCE_UPDATING;
        } else {
            resCode = UpdateActivity.RECOMMEND_UPDATING;
        }

        JSONObject info = resObject.optJSONObject("info");
        String file_name = info.optString("file_name");
        String description = info.optString("description");
        String version = info.optString("version");

        Intent i = new Intent(this, UpdateActivity.class);// 检查更新

        // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("resCode", resCode);
        i.putExtra("resDesc", "检测到新版本 " + version + " !\n");
        i.putExtra("update_url", WebUrlDomain.CONNECT_HOST + file_name);
        // i.putExtra("from_start", true);
        i.putExtra("update_content", description);

        startActivity(i);

        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
