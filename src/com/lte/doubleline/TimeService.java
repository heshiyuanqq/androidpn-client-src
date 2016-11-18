package com.lte.doubleline;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Security;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class TimeService extends Service {

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
//		Notification notification = new Notification();
//		startForeground(1, notification);
		handler.sendEmptyMessage(2);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeMessages(2);		//停止循环启动双通道
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 2) {
				set();
			}
			handler.sendEmptyMessageDelayed(2, 5 * 1000);
		}
	};

	private void set() {
		Security.setProperty("networkaddress.cache.ttl", String.valueOf(0));
		Security.setProperty("networkaddress.cache.negative.ttl", String.valueOf(0));
		ConnectivityManager conMan = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		int resultInt = conMan.startUsingNetworkFeature(
				ConnectivityManager.TYPE_MOBILE, "enableHIPRI");
//		conMan.requestRouteToHost(ConnectivityManager.TYPE_MOBILE_HIPRI,
//				lookupHost(null));
		conMan.requestRouteToHost(ConnectivityManager.TYPE_MOBILE_HIPRI,
				lookupHost(null));
//		Toast.makeText(getApplicationContext(), "定时执行", Toast.LENGTH_LONG)
//				.show();
		Log.i("TimeService", "TimeService定时执行");
	}

	private int lookupHost(String paramString) {
		try {
			InetAddress localInetAddress = InetAddress.getByName(paramString);
			byte[] arrayOfByte = localInetAddress.getAddress();
			return ((0xFF & arrayOfByte[3]) << 24
					| (0xFF & arrayOfByte[2]) << 16
					| (0xFF & arrayOfByte[1]) << 8 | 0xFF & arrayOfByte[0]);
		} catch (UnknownHostException localUnknownHostException) {
		}
		return -1;
	}
	
}
