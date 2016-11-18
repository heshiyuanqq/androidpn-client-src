package com.lte.util;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;

public class ConnectivityManagerUtil {
	private static ConnectivityManagerUtil instance = null;
	private ConnectivityManager connManager = null;
	private State state;// 网络状态
	private NetworkInfo info;
	public boolean is2G;
	public boolean is3G;
	public boolean is4G;

	public ConnectivityManagerUtil(Context activity) {
		connManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * 单例模式获取TelephonyManager
	 * 
	 * @param activity
	 * @return
	 */
	public static ConnectivityManagerUtil getInstance(Context activity) {
		if (instance == null) {
			instance = new ConnectivityManagerUtil(activity);
		}
		return instance;
	}

	/**
	 * 是否有网络连接
	 * 
	 * @return false 为否， true 为是
	 */
	public boolean isConnectivity() {
		if (isWifi() || isMobile()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否是wifi网络
	 * 
	 * @return false 为否， true 为是
	 */
	public boolean isWifi() {
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (state == State.CONNECTED) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否是手机网络
	 * 
	 * @return false 为否， true 为是
	 */
	public boolean isMobile() {
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (state == State.CONNECTED) {
			return true;
		} else {
			return false;
		}
	}

	// info.getSubtype()取值列表如下：
	// int NETWORK_TYPE_1xRTT Current network is 1xRTT
	// int NETWORK_TYPE_UNKNOWN Network type is unknown
	// int NETWORK_TYPE_IDEN Current network is iDen

	// NETWORK_TYPE_CDMA Current network is CDMA: Either IS95A or
	// IS95B 电信2G
	// NETWORK_TYPE_EDGE Current network is EDGE 移动或联通2G
	// NETWORK_TYPE_GPRS Current network is GPRS 移动或联通2G
	/**
	 * 是否是手机2G网络
	 * 
	 * @return false 为否， true 为是 移动和联通的2G为GPRS或EDGE 电信的2G为CDMA
	 */
	public boolean is2G() {
		if (isMobile()) {
			info = connManager.getActiveNetworkInfo();
			if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS
					|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE
					|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA) {
				is2G = true;
			} else {
				is2G = false;
			}
		}
		return is2G;
	}

	// NETWORK_TYPE_UMTS Current network is UMTS 联通3G
	// NETWORK_TYPE_EHRPD Current network is eHRPD
	// eHRPD是cdma的演进技术，大概就是3.75g， 如果你在中国，那就是电信使用的3g技术的一种演进技术。
	// NETWORK_TYPE_EVDO_0 Current network is EVDO revision 0 电信3G
	// NETWORK_TYPE_EVDO_A Current network is EVDO revision A 电信3G
	// NETWORK_TYPE_EVDO_B Current network is EVDO revision B 电信3G

	// NETWORK_TYPE_HSDPA Current network is HSDPA 联通3G
	// NETWORK_TYPE_HSPA Current network is HSPA 联通3G
	// NETWORK_TYPE_HSPAP Current network is HSPA+ 联通3G
	// NETWORK_TYPE_HSUPA Current network is HSUPA 联通3G

	/**
	 * 是否是手机3G网络
	 * @return false 为否， true 为是 联通的3G为UMTS或HSDPA， 电信的3G为EVDO
	 */
	public boolean is3G() {
		if (isMobile()) {
			info = connManager.getActiveNetworkInfo();
			int typeId=info.getSubtype();
//			Logger.e("typeID", typeId+"");
			if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_UMTS
					|| typeId == TelephonyManager.NETWORK_TYPE_EHRPD
					|| typeId == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| typeId == TelephonyManager.NETWORK_TYPE_EVDO_A
					|| typeId == TelephonyManager.NETWORK_TYPE_EVDO_B
					|| typeId == TelephonyManager.NETWORK_TYPE_HSDPA
					|| typeId == TelephonyManager.NETWORK_TYPE_HSPA
					|| typeId == TelephonyManager.NETWORK_TYPE_HSPAP
					|| typeId == TelephonyManager.NETWORK_TYPE_HSUPA) {
				is3G = true;
			} else {
				is3G = false;
			}
		}
		return is3G;
	}

	// NETWORK_TYPE_LTE Current network is LTE 4G（LTE就是俗称的3.9G）
	/**
	 * 是否是手机4G网络
	 * 
	 * @return false 为否， true 为是
	 */
	public boolean is4G() {
		if (isMobile()) {
			info = connManager.getActiveNetworkInfo();
			if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE) {
				is4G = true;
			} else {
				is4G = false;
			}
		}
		return is4G;
	}
	
	/***
	 * 打开/关闭移动网络
	 * @param context
	 * @param paramBoolean
	 * @return
	 */
	public boolean switchMobileNet(Context context, boolean paramBoolean) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			Method localMethod = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			localMethod.setAccessible(true);
			localMethod.invoke(connectivityManager, paramBoolean);
			return true;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return false;
	}
	 
}
