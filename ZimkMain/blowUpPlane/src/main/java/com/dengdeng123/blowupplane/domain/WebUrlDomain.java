package com.dengdeng123.blowupplane.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.dengdeng123.blowupplane.util.Device;

public class WebUrlDomain {
	// private static final String HTTP_IP = "192.168.1.102";// Httpd IP
	// private static final String HTTP_PORT = "82";// Httpd 端口

	// private final static String TOMCAT_IP = "192.168.1.127";// Tomcat IP
	// private final static String TOMCAT_PORT = "8081";// Tomcat 端口

	private final static String TOMCAT_IP = "42.96.190.106";// Tomcat IP
	private final static String TOMCAT_PORT = "81";// Tomcat 端口

	/** 调用接口用这个 */
	public final static String CONNECT_HOST = "http://" + TOMCAT_IP + ":" + TOMCAT_PORT + "/BlowUpPlane/foreground/";

	public static JSONObject getBaseJSONObject(Context c) {
		JSONObject requestParameters = new JSONObject();

		Device d = Device.getOSParameters(c);

		try {
			requestParameters.put("imei", d.getIMEI());// 手机型号
			requestParameters.put("phonemodels", d.getDeviceName());// 手机型号
			requestParameters.put("sysinfo", d.getDeviceNameReleaseVersion());// 系统版本
			requestParameters.put("equipment", "1");// [1]android, [2]ios
			requestParameters.put("version", Device.getAppVersionName(c));
			requestParameters.put("client_type", 2);// [1]等等???, [2]快递商户版
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return requestParameters;
	}

	public static String getCallInterfaceUrl(String actionName, JSONObject requestParameters) {
		String urlEncoded = "";

		try {
			urlEncoded = CONNECT_HOST + actionName + "?rd=" + URLEncoder.encode(requestParameters.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return urlEncoded;
	}
}
