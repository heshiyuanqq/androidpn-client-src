package com.lte.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.lte.config.ConfigSP;
import com.lte.util.NetUtil;
import com.lte.util.ToolUtil;

public class SignalService extends Service {

	/**
	 * 当前的信号强度
	 */
	private int state_Strength = 0;
	/**
	 * 当前的小区ID（不准确）
	 */
	private int state_CellID = 0;
	/**
	 * 是否停止监听信号强度
	 */
	private boolean isStopListenStrength;

	/**
	 * 取设备状态的时间间隔
	 */
	private int stateSpan = 5;

	/**
	 * 小区ID
	 */
	private String ID;

	// /**
	// * 取设备信号制式
	// */
	String strType;

	// private int i = 0;

	NetUtil netutil = new NetUtil();

	TelephonyManager telephonyManager;
	PhoneStateMonitor phoneStateMonitor;

	Editor editor;// 存储器

	@Override
	public void onStart(Intent intent, int startId) {
		// Toast.makeText(getApplicationContext(), "信号强度测试服务开启",
		// Toast.LENGTH_SHORT).show();
		System.out.println("信号强度测试服务开启");
		Log.i("Signal", "信号强度测试服务开启");
		editor = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
				.edit();// 初始化编辑器
		isStopListenStrength = false;// 开
		ListenPhoneState();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * 测试获取设备状态（小区ID、信号强度）
	 */
	private void ListenPhoneState() {

		// -------------------------------------------------------------
		// 监听（网络制式、小区ID、以及连接状态）
		if (telephonyManager == null) {
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);// 取得手机服务
			phoneStateMonitor = new PhoneStateMonitor();// 创建手机状态监听
		}
		telephonyManager.listen(phoneStateMonitor,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS// 信号强度
						| PhoneStateListener.LISTEN_DATA_CONNECTION_STATE// 数据连接状态
						| PhoneStateListener.LISTEN_SERVICE_STATE// 服务状态
						| PhoneStateListener.LISTEN_CELL_LOCATION);// 本地小区
		// -------------------------------------------------------------
		if (!"未连接".equalsIgnoreCase(getNetType())) {// 如果已连接

			// 开始监听
			new Thread(new StrengthRunnable()).start();
			// StrengthRunnable.run();

		} else {
			Log.i("SignalService", "未连接网络");
		}
	}

	/**
	 * 监听设备状态改变（网络制式、小区ID、以及连接状态）
	 */
	private class PhoneStateMonitor extends PhoneStateListener {
		/**
		 * 信号强度改变时调用
		 */
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			// 获取LTE信号的Dbm
			// Integer strength = 0;
			int strength = 0;
			try {
				Method method = signalStrength.getClass().getDeclaredMethod(
						"getDbm");
				strength = (Integer) method.invoke(signalStrength);
				// System.out.println("Dbm：" + "Signal strength: " +
				// strength);//输出信号强度strength

				// -------------------------------------------------------------
				strType = getNetType();// 网络制式
				editor.putString(ConfigSP.SP_reseach_signalType, strType)
						.commit();
				// -------------------------------------------------------------

				// 若信号强度变化了，则记录当前的信号强度
				if (strength != state_Strength) {
					state_Strength = strength;
					System.out.println("***************************变化后信号强度："
							+ state_Strength);
					editor.putString(ConfigSP.SP_reseach_signalStrength,
							state_Strength + "").commit();
				}

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 小区信息变化
		 */
		@Override
		public void onCellLocationChanged(CellLocation location) {
			super.onCellLocationChanged(location);

			int cellId = ((GsmCellLocation) location).getCid();
			// 当前小区ID
			ID = ToolUtil.CalcCellID(telephonyManager.getNetworkType(), cellId);
			System.out.println("=======================当前小区ID：" + ID
					+ " cellId:" + state_CellID);

			// 若小区ID变化了，则记录当前的小区ID
			if (cellId != state_CellID) {
				state_CellID = cellId;
				ID = ToolUtil.CalcCellID(telephonyManager.getNetworkType(),
						state_CellID);
				System.out.println("***************************变化后小区ID：" + ID
						+ " cellId:" + state_CellID);
				// 记录状态
				editor.putString(ConfigSP.SP_reseach_Cell_ID, ID + "").commit();
			}
		}

	}

	/**
	 * 循环间隔一定时间获取一次信号强度
	 */
	// Runnable StrengthRunnable = new Runnable(){
	//
	// @Override ToolUtil.CalcCellID(telephonyManager.getNetworkType(), cellId);
	// public void run() {
	// // TODO Auto-generated method stub
	// while (true) {
	// // 若停止监听，则退出循环
	// if (isStopListenStrength == true) {
	// break;
	// }
	//
	// // -------------------------------------------------------------
	//
	// // -------------------------------------------------------------
	//
	// strType = getNetType();// 网络制式
	// editor.putString(ConfigSP.SP_reseach_signalType,
	// strType).commit();
	// editor.putString(ConfigSP.SP_reseach_signalStrength,
	// state_Strength + "").commit();
	// editor.putString(ConfigSP.SP_reseach_Cell_ID,
	// state_CellID + "").commit();;
	// // Toast.makeText(getApplicationContext(), "信号强度dbm："+
	// // state_Strength + ",小区ID：" +
	// // state_CellID,Toast.LENGTH_SHORT).show();
	// System.out.println("信号强度：" + state_Strength + ",小区ID"
	// + state_CellID + ",信号制式" + strType);
	//
	// String strImsi = getSharedPreferences(ConfigSP.SP_reseach,
	// Context.MODE_PRIVATE).getString(
	// ConfigSP.SP_reseach_Imsi, "");// 手机卡唯一标识
	//
	// String ip = getApplicationContext().getSharedPreferences(
	// ConfigSP.SP_reseach,// 表名
	// Context.MODE_PRIVATE).getString(ConfigSP.SP_reseach_ip,// key名
	// "null_ip");
	// // String ip = "192.168.1.15";
	// String serverIp = "http://" + ip;
	// String url = serverIp
	// + ":8080/ResearchProject/terminal/setSignalValue";
	//
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// NameValuePair postKeyValueState = new BasicNameValuePair(
	// "strSignalValue", String.valueOf(state_Strength));
	// NameValuePair postKeyValueId = new BasicNameValuePair(
	// "strCellID", String.valueOf(state_CellID));
	// NameValuePair postKeyValueImsi = new BasicNameValuePair(
	// "strImsi", strImsi);
	// NameValuePair postKeyValueType = new BasicNameValuePair(
	// "strNetType", strType);
	//
	// params.add(postKeyValueState);
	// params.add(postKeyValueId);
	// params.add(postKeyValueImsi);
	// params.add(postKeyValueType);
	//
	// String s = netutil.httpUpload(getApplicationContext(), url,
	// params);
	//
	// Log.i("**re**", s);
	// // 每隔5秒取一次信号强度
	// try {
	// Thread.sleep(stateSpan * 1000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// };

	private class StrengthRunnable implements Runnable {
		@Override
		public void run() {
			while (true) {
				// 若停止监听，则退出循环
				if (isStopListenStrength == true) {
					break;
				}

				// -------------------------------------------------------------
				// 每隔5秒取一次信号强度
				try {
					Thread.sleep(stateSpan * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// -------------------------------------------------------------

				// Toast.makeText(getApplicationContext(), "信号强度dbm："+
				// state_Strength + ",小区ID：" +
				// state_CellID,Toast.LENGTH_SHORT).show();
				System.out.println("第一:信号强度" + state_Strength + ",小区ID" + ID
						+ ",信号制式" + strType + " cellId:" + state_CellID);

				String strImsi = getSharedPreferences(ConfigSP.SP_reseach,
						Context.MODE_PRIVATE).getString(
						ConfigSP.SP_reseach_Imsi, "");// 手机卡唯一标识

				// String strth = getSharedPreferences(ConfigSP.SP_reseach,
				// Context.MODE_PRIVATE).getString(
				// ConfigSP.SP_reseach_signalStrength, "");// 手机卡唯一标识
				//
				// String cid = getSharedPreferences(ConfigSP.SP_reseach,
				// Context.MODE_PRIVATE).getString(
				// ConfigSP.SP_reseach_Cell_ID, "");// 手机卡唯一标识
				//
				// String tp = getSharedPreferences(ConfigSP.SP_reseach,
				// Context.MODE_PRIVATE).getString(
				// ConfigSP.SP_reseach_signalType, "");// 手机卡唯一标识
				//
				// System.out.println("第二:信号强度：" + strth + ",小区ID："
				// + cid + ",信号制式：" + tp);

				String ip = getApplicationContext().getSharedPreferences(
						ConfigSP.SP_reseach,// 表名
						Context.MODE_PRIVATE).getString(ConfigSP.SP_reseach_ip,// key名
						"null_ip");
				// String ip = "192.168.1.15";
				String serverIp = "http://" + ip;
				String url = serverIp
						+ ":8080/ResearchProject/terminal/setSignalValue";

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				NameValuePair postKeyValueState = new BasicNameValuePair(
						"strSignalValue", String.valueOf(state_Strength));
				NameValuePair postKeyValueId = new BasicNameValuePair(
						"strCellId", String.valueOf(ID));
				NameValuePair postKeyValueImsi = new BasicNameValuePair(
						"strImsi", strImsi);
				NameValuePair postKeyValueType = new BasicNameValuePair(
						"strNetType", strType);

				params.add(postKeyValueState);
				params.add(postKeyValueId);
				params.add(postKeyValueImsi);
				params.add(postKeyValueType);

				String isUpload = getSharedPreferences(ConfigSP.SP_reseach,
						Context.MODE_PRIVATE).getString(ConfigSP.SP_ISUpload,
						"");
				if ("true".equals(isUpload)) {
					String s = netutil.httpUpload(getApplicationContext(), url,
							params);
					System.out.println("**re**" + s);
					Log.i("**re**", s);
				} else {
					String s = "null";//未上传
					System.out.println("**re**" + s);
					Log.i("**re**", s);
				}

			}
		}
	}

	/**
	 * 停止监听设备状态（网络制式、小区ID、以及连接状态）（信号强度）<br>
	 * 上传状态信息
	 */
	private void stopListenPhoneState() {
		if (telephonyManager != null) {
			// 停止监听（网络制式、小区ID、以及连接状态）
			telephonyManager.listen(phoneStateMonitor,
					PhoneStateListener.LISTEN_NONE);
		}

	}

	/**
	 * 获取数据连接制式 是否连接 2G/3G/4G/未知/其他 其他：getNetworkType()存在值，但不在我的判断范围内，如：16
	 */
	private String getNetType() {

		String netType;
		// ConnectivityManagerUtil cmu = new ConnectivityManagerUtil(
		// getApplicationContext());
		// if (cmu.isConnectivity()) {

		if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_GPRS
				|| telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EDGE
				|| telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_CDMA
				|| telephonyManager.getNetworkType() == 16) {
			netType = "2G";
			Log.i("signalNetType", netType + telephonyManager.getNetworkType());
		} else if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS
				|| telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EHRPD
				|| telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_0
				|| telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_A
				|| telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_B
				|| telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA
				|| telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA
				|| telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPAP// Level
																							// 13
				|| telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA
				// || telephonyManager.getNetworkType() == 16
				|| telephonyManager.getNetworkType() == 17
				|| telephonyManager.getNetworkType() == 18) {
			netType = "3G";
			// Log.i("signalNetType", netType +
			// telephonyManager.getNetworkType());
		} else if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE)// Level
																							// 11
		{
			netType = "4G";
			// Log.i("signalNetType", netType +
			// telephonyManager.getNetworkType());
		} else if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
			netType = "未知";
			// Log.i("signalNetType", netType +
			// telephonyManager.getNetworkType());
		} else {
			netType = "其他";
			// Log.i("signalNetType", netType +
			// telephonyManager.getNetworkType());
		}
		// }

		// } else {
		// netType = "未连接";
		// Log.i("signaltype", netType);
		// }
		return netType;
	}

	@Override
	public void onDestroy() {
		System.out.println("信号强度测试服务关闭");
		super.onDestroy(); // 退出测试线程
		// 停止监听设备状态
		isStopListenStrength = true;// 关
		stopListenPhoneState();// 停止监听
		// new Thread(new StrengthRunnable()).stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
