package com.dengdeng123.blowupplane.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

/**
 * 设备信息
 * 
 * @author imknown
 */
public class Device {
	/** 设备名 MOTO ME632 */
	String deviceFullName;

	/** 系统型号, 如 2.3.6 */
	String deviceReleaseVersion;

	/** SDK版本, 如 10 */
	String sdk;

	/** API版本, 如 10 */
	int apiVersion;

	String IMEI;
	String IMSI;
	String SIM;

	public Device(String deviceFullName, String deviceReleaseVersion, String sdk, int apiVersion, String IMEI, String IMSI, String SIM) {
		this.deviceFullName = deviceFullName;
		this.deviceReleaseVersion = deviceReleaseVersion;

		this.sdk = sdk;
		this.apiVersion = apiVersion;
		this.IMEI = IMEI;
		this.IMSI = IMSI;
		this.SIM = SIM;
	}

	public String getDeviceName() {
		return deviceFullName;
	}

	public void setDeviceName(String deviceFullName) {
		this.deviceFullName = deviceFullName;
	}

	public String getSdk() {
		return sdk;
	}

	public void setSdk(String sdk) {
		this.sdk = sdk;
	}

	public String getDeviceNameReleaseVersion() {
		return deviceReleaseVersion;
	}

	public void setDeviceNameReleaseVersion(String deviceReleaseVersion) {
		this.deviceReleaseVersion = deviceReleaseVersion;
	}

	public int getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(int apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String IMEI) {
		this.IMEI = IMEI;
	}

	public String getIMSI() {
		return IMSI;
	}

	public void setIMSI(String IMSI) {
		this.IMSI = IMSI;
	}

	public String getSIM() {
		return SIM;
	}

	public void setSIM(String SIM) {
		this.SIM = SIM;
	}

	/** 获取手机设备型号 */
	public static Device getOSParameters(Context context) {
		String deviceBrand = android.os.Build.BRAND;// 制造商 MOTO
		String deviceName = android.os.Build.MODEL;// 具体型号 ME632
		String deviceReleaseVersion = android.os.Build.VERSION.RELEASE;// 安卓版本 2.3.6

		@SuppressWarnings("deprecation")
		String sdk = android.os.Build.VERSION.SDK;
		int apiVersion = android.os.Build.VERSION.SDK_INT;

		String IMEI = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		String IMSI = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
		String SIM = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();

		Device device = new Device(deviceBrand + " " + deviceName, deviceReleaseVersion, sdk, apiVersion, IMEI, IMSI, SIM);

		return device;
	}

	public static String getAppVersionName(Context context) {
		String version = "1.0.0";

		PackageManager packageManager = context.getPackageManager();

		try {
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return version;
	}
}
