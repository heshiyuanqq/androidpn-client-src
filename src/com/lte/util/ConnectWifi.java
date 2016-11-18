package com.lte.util;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

public class ConnectWifi {

	/**
	 * 需要root权限 需要配置AndroidManifest权限
	 */
	public ConnectWifi(String SSID, Context context) {
		String[] cmd = new String[] { "su", "svc wifi enable" };

		try {
			CMDUtil.execShellCMD(cmd, 3);// 打开wifi
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		WifiAdmin mWifiAdmin = new WifiAdmin(context);
		// mWifiAdmin.openWifi();
		mWifiAdmin.addNetWork(mWifiAdmin.CreateWifiInfo(SSID, null, 1));
	}

	public ConnectWifi(String SSID, String password, Context context) {
		String[] cmd = new String[] { "su", "svc wifi enable" };

		try {
			CMDUtil.execShellCMD(cmd, 3);// 打开wifi
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		WifiAdmin mWifiAdmin = new WifiAdmin(context);
		// mWifiAdmin.openWifi();
		mWifiAdmin.addNetWork(mWifiAdmin.CreateWifiInfo(SSID, password, 3));
	}

	public class WifiAdmin {
		// 定义WifiManager对象
		private WifiManager mWifiManager;
		// 定义WifiInfo对象
		private WifiInfo mWifiInfo;
		// 扫描出的网络连接列表,ScanResult主要用来描述已经检测出的接入点，包括介入点的地址，介入点的名称，身份认证，频率，信号强度等信息
		private List<ScanResult> mWifiList;
		// 网络连接列表
		private List<WifiConfiguration> mWifiConfiguration;
		// 定义一个WifiLock
		WifiLock mWifiLock;

		// 构造器
		public WifiAdmin(Context context) {
			// 取得WifiManager对象
			mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			// 取得WifiInfo对象
			mWifiInfo = mWifiManager.getConnectionInfo();
		}

		/**
		 * 创建一个WIFI信息：
		 * 
		 * */
		public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
			WifiConfiguration config = new WifiConfiguration();
			config.allowedAuthAlgorithms.clear();
			config.allowedGroupCiphers.clear();
			config.allowedKeyManagement.clear();
			config.allowedPairwiseCiphers.clear();
			config.allowedProtocols.clear();
			config.SSID = "\"" + SSID + "\"";
			// WifiConfiguration tempConfig = this.IsExsits(SSID);
			// if (tempConfig != null) {
			// mWifiManager.removeNetwork(tempConfig.networkId);
			// }
			if (Type == 1) // WIFICIPHER_NOPASS//不加密
			{
				config.wepKeys[0] = "";
				config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				config.wepTxKeyIndex = 0;
			}
			if (Type == 2) // WIFICIPHER_WEP//WEP加密方式
			{
				config.hiddenSSID = true;
				config.wepKeys[0] = "\"" + Password + "\"";
				config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
				config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
				config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
				config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
				config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
				config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				config.wepTxKeyIndex = 0;
			}
			if (Type == 3) // WIFICIPHER_WPA//WPA加密方式
			{
				config.hiddenSSID = true;
				config.preSharedKey = "\"" + Password + "\"";
				config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
				config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
				config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
				config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
				// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
				config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
				config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
				config.status = WifiConfiguration.Status.ENABLED;
			}
			return config;
		}

		// 打开WIFI
		public void openWifi() {
			if (!mWifiManager.isWifiEnabled()) {
				mWifiManager.setWifiEnabled(true);
				System.out.println("wifi打开成功!");
			}
			// if (mWifiManager.disconnect()) {
			// mWifiManager.setWifiEnabled(true);
			// System.out.println("wifi打开成功!!");
			// }
		}

		// 关闭WIFI
		public void closeWifi() {
			if (mWifiManager.isWifiEnabled()) {
				mWifiManager.setWifiEnabled(false);
			}
		}

		// 检查当前WIFI状态
		public int checkState() {
			return mWifiManager.getWifiState();
		}

		// 锁定WifiLock
		public void acquireWifiLock() {
			mWifiLock.acquire();
		}

		// 解锁WifiLock
		public void releaseWifiLock() {
			// 判断时候锁定
			if (mWifiLock.isHeld()) {
				mWifiLock.acquire();
			}
		}

		// 创建一个WifiLock
		public void creatWifiLock() {
			mWifiLock = mWifiManager.createWifiLock("Test");
		}

		// 得到配置好的网络
		public List<WifiConfiguration> getConfiguration() {
			return mWifiConfiguration;
		}

		// 指定配置好的网络进行连接
		public void connectConfiguration(int index) {
			// 索引大于配置好的网络索引返回
			if (index > mWifiConfiguration.size()) {
				System.out.println("连接失败!");
				return;
			}
			// 连接配置好的指定ID的网络
			mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
			System.out.println(index + "连接成功!");
		}

		public void startScan() {
			mWifiManager.startScan();
			// 得到扫描结果
			mWifiList = mWifiManager.getScanResults();
			// 得到配置好的网络连接
			mWifiConfiguration = mWifiManager.getConfiguredNetworks();
		}

		// 得到网络列表
		public List<ScanResult> getWifiList() {
			return mWifiList;
		}

		// 查看扫描结果
		public StringBuffer lookUpScan() {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mWifiList.size(); i++) {
				sb.append("Index_" + new Integer(i + 1).toString() + ":");
				// 将ScanResult信息转换成一个字符串包
				// 其中把包括：BSSID、SSID、capabilities、frequency、level
				sb.append((mWifiList.get(i)).toString()).append("\n");
			}
			return sb;
		}

		public String getMacAddress() {
			return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
		}

		public String getBSSID() {
			return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
		}

		public int getIpAddress() {
			return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
		}

		// 得到连接的ID
		public int getNetWordId() {
			return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
		}

		// 得到wifiInfo的所有信息
		public String getWifiInfo() {
			return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
		}

		// 添加一个网络并连接
		public void addNetWork(WifiConfiguration configuration) {
			int wcgId = mWifiManager.addNetwork(configuration);
			boolean b = mWifiManager.enableNetwork(wcgId, true);
			System.out.println("a--" + wcgId);
			System.out.println("b--" + b);
		}

		// /**
		// *添加一个网络并连接：
		// *
		// * */
		// public void addNetwork(WifiConfiguration wcg) {
		// int wcgID = mWifiManager.addNetwork(wcg);
		// boolean b = mWifiManager.enableNetwork(wcgID, true);
		// System.out.println("a--" + wcgID);
		// System.out.println("b--" + b);
		// }

		// 断开指定ID的网络
		public void disConnectionWifi(int netId) {
			mWifiManager.disableNetwork(netId);
			mWifiManager.disconnect();
		}
	}
}
