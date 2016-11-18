package com.lte.test;

import org.androidpn.client.ServiceManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lte.AcMain;
import com.lte.R;
import com.lte.config.ConfigSP;
import com.lte.util.PowerResponce;

//import android.widget.Toast;

public class BatteryService extends Service {
	int j = -1, i = 0;
	String Dpowerlow;
	int powerlow;
	int powerhigh = 95;

	Editor editor;// 编辑器

	Boolean exist = false;
	Boolean stop = false;
	String now_IMSI;
	Boolean start = true;

	Runnable myRunnable = new Runnable() {
		public void run() {
			while (start) {
				try {
					Thread.sleep(2 * 1000);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(
						Context.TELEPHONY_SERVICE);
				now_IMSI = tm.getSubscriberId();// 手机卡唯一标识

				ServiceManager serviceManager = new ServiceManager(BatteryService.this);

				System.out.println("******BatteryService我的测试开始********");

				if (now_IMSI == null) {
					serviceManager.stopService();
					stop = true;
				} else {
					exist = true;
				}
				if (exist && stop) {
					serviceManager.startService();
					stop = false;
					exist = false;
				}
			}
		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		// 定义电池电量更新广播的过滤器,只接受带有ACTION_BATTERRY_CHANGED事件的Intent
		IntentFilter batteryChangedReceiverFilter = new IntentFilter();
		batteryChangedReceiverFilter.addAction(Intent.ACTION_BATTERY_CHANGED);//

		System.out.println("电量测试服务开启");

		// new Thread(myRunnable).start();

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		String appName;
		try {
			appName = getPackageManager().getPackageInfo(getApplication().getPackageName(), 0).applicationInfo
					.loadLabel(getPackageManager()).toString();// appName
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			appName = "";
		}
		mBuilder.setContentTitle(appName);// 设置通知栏标题
		mBuilder.setContentText(this.getClass().getName());
		Intent intent1 = new Intent(getApplicationContext(), AcMain.class);
		// intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
		// Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
				intent1, 0);
		mBuilder.setContentIntent(pendingIntent) // 设置通知栏点击意图
				// .setNumber(number) //设置通知集合的数量
				// .setTicker("测试通知来啦") // 通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
				// .setPriority(Notification.PRIORITY_DEFAULT) // 设置该通知优先级
				// .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
				.setOngoing(true)// true，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
				// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND //添加声音
				// requires VIBRATE permission
				.setSmallIcon(R.drawable.ic_launcher);// 设置通知小图标
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		mNotificationManager.notify(1, notify);
		// startForeground(1, notify);

		// Toast.makeText(getApplicationContext(), "电量测试服务开启",
		editor = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).edit();// 初始化编辑器
		// Toast.LENGTH_SHORT).show();
		// Log.i("Power", "电量测试服务开启");

		// 向系统注册batteryChangedReceiver接收器，本接收器的实现见代码字段处
		registerReceiver(batteryChangedReceiver, batteryChangedReceiverFilter);

		// 存入电量上限
		editor.putString(ConfigSP.SP_reseach_power_high, powerhigh + "").commit();
	}

	private BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			final int level = intent.getIntExtra("level", 0); // 电池电量等级
			// int scale = intent.getIntExtra("scale", 100); // 电池满时百分比
			final int status = intent.getIntExtra("status", 0); // 电池状态

			System.out.println("当前电量：" + level);
			editor.putString(ConfigSP.SP_reseach_power, level + "").commit();

			Dpowerlow = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).getString(
					ConfigSP.SP_reseach_power_low_default, "0");
			powerlow = Integer.parseInt(getSharedPreferences(ConfigSP.SP_reseach,
					Context.MODE_PRIVATE).getString(ConfigSP.SP_reseach_power_low, Dpowerlow));// 缺省值为30
			System.out.println("powerlow = " + powerlow);

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					if (status == BatteryManager.BATTERY_STATUS_CHARGING) {

						editor.putString(ConfigSP.SP_reseach_charging, "true").commit();
					} else {
						editor.putString(ConfigSP.SP_reseach_charging, "false").commit();
					}
					if (level == j || j == -1) {// level初始化,电量相等和-1的情况
						j = level;// 相等就把level赋值给j
						// Toast.makeText(getApplicationContext(), j + "," +
						// level,
						// Toast.LENGTH_SHORT).show();

					} else {// 排除电量相等和-1的情况,即:电量大小level改变的时候
						// Toast.makeText(getApplicationContext(), j +
						// "变化后的值*****," +
						// level, Toast.LENGTH_SHORT)
						// .show();

						// 若正在充电
						if (status == BatteryManager.BATTERY_STATUS_CHARGING) {

							editor.putString(ConfigSP.SP_reseach_charging, "true").commit();
							if (level < powerhigh) {
								System.out.println("电量：" + level + "，正在充电!");

								// -----------------------------------------------------------------------------------------------------------
								editor.putString(ConfigSP.SP_reseach_BatteryResponse, "正在充电!")
										.commit();
								// -----------------------------------------------------------------------------------------------------------
							} else {// 若正在充电且电量变化值大于等于scale请求断电
								System.out.println("电量：" + level + "，已充满，请断电!");

								String re = PowerResponce.getInstance().responce(
										BatteryService.this, "high");

								// -----------------------------------------------------------------------------------------------------------
								editor.putString(ConfigSP.SP_reseach_BatteryResponse,
										level + "，" + re + ":已充满，请断电!").commit();
								// -----------------------------------------------------------------------------------------------------------

								Log.i("batteryTestInfo", re);
								System.out.println("batteryTestInfo:" + re);
							}

							// 若未充电
						} else {
							editor.putString(ConfigSP.SP_reseach_charging, "false").commit();
							// 若未充电且电量低于电量警告下限
							if (level >= 0 && level < powerlow) {
								System.out
										.println("电量：" + level + "，电量低于" + powerlow + "，未充电。请充电!");

								String re = PowerResponce.getInstance().responce(
										BatteryService.this, "low");

								// -----------------------------------------------------------------------------------------------------------
								editor.putString(ConfigSP.SP_reseach_BatteryResponse,
										level + "，" + re + ":未充电,请充电!").commit();
								// -----------------------------------------------------------------------------------------------------------

								Log.i("BatteryService", re);
							} else {
								System.out.println("电量：" + level + "，未充电!");

								// -----------------------------------------------------------------------------------------------------------
								editor.putString(ConfigSP.SP_reseach_BatteryResponse, "未充电!")
										.commit();
								// -----------------------------------------------------------------------------------------------------------
							}
						}

						j = level;
					}
				}
			};
			new Thread(runnable).start();

		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		ServiceManager serviceManager = new ServiceManager(this);
		serviceManager.stopService();
		stop = true;
		start = false;
		super.onDestroy();
	}

}
