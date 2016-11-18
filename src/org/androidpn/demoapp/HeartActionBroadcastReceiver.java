package org.androidpn.demoapp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.androidpn.client.NotificationService;
import org.androidpn.client.XmppManager;
import org.jivesoftware.smack.XMPPConnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class HeartActionBroadcastReceiver extends BroadcastReceiver {
	public static final String ACTION = "android.intent.action.heartaction.TEST";
	// 创建一个可重用固定线程数的线程池
	ExecutorService pool = Executors.newSingleThreadExecutor();
	XmppManager xmppManager;
	XMPPConnection connection;

	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent != null) {
			xmppManager = NotificationService.getinstance().getXmppManager();
			System.out.println("111111111111111111");
			if (xmppManager != null) {
				connection = xmppManager.getConnection();
				System.out.println("22222222222222222222222222222222");
				if (connection != null) {
					System.out.println("3333333333333333333333333");
//					try {
//						connection
//								.startKeepAliveAlarmManager(xmppManager, pool);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
				}

			}
			
			
//			Thread serviceThread = new Thread(new Runnable() {
//				@Override
//				public void run() {
//					Intent intent = NotificationService.getIntent();
//					context.startService(intent);
//				}
//			});
//			serviceThread.start();
			
			Toast.makeText(context, "toast", Toast.LENGTH_SHORT).show();
			System.out.println("闹钟闹钟闹钟闹钟闹钟闹钟闹钟闹钟闹钟闹钟闹钟闹钟闹钟闹钟闹钟闹钟闹钟闹钟");
		}
	}

}
