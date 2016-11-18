package com.lte;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.androidpn.client.NotificationService;
import org.androidpn.client.ServiceManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.lte.config.ConfigSP;
import com.lte.doubleline.ShellUtil;
import com.lte.doubleline.TimeService;
import com.lte.listener.CallSMSListenerService;
import com.lte.listener.ListenerService;
import com.lte.test.LogicService;
import com.lte.test.SignalService;
import com.lte.util.PowerResponce;

public class AcMain extends Activity {

	Context context;

	private NotificationManager nm;
	static final int NOTIFICATION_ID = 0x123;

	int i = 0;
	int j = -1;
	// String Etxt;
	String Ttxt;
	Editor editor;
	// private int MIN_MARK = 1;
	// private int MAX_MARK = 100;
	private long exitTime = 0;

	TextView status_tv, powerstatus_tv, PNstatus_tv, IP_tv, P_IP_tv, IMSI_tv, now_IMSI_tv, PID_tv,
			powerlower_tv, power_tv, signal_tv, cell_ID_tv, task_tv, step_tv, plan_tv, standard_tv,
			runtime_tv, totaltime_tv;
	String status, PNstatus, powerstatus, IP, P_IP, IMSI, now_IMSI, PID, powerlower, power, signal,
			cell_ID, ActionName, etc_Step, plan, standard, runtime, totaltime;

	String Dpowerlow;
	int powerlow;
	int powerhigh = 95;

	Boolean exist = false;
	Boolean stop = false;

	WifiManager wifiManager;

	Handler handler = new Handler();

	Runnable myRunnable = new Runnable() {
		public void run() {

			TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(
					Context.TELEPHONY_SERVICE);
			now_IMSI = tm.getSubscriberId();// 手机卡唯一标识

			// ServiceManager serviceManager = new ServiceManager(AcMain.this);
			// if(now_IMSI == null){
			// serviceManager.stopService();
			// stop = true;
			// }else if(!IMSI.equals(now_IMSI)){
			// exist = true;
			// }else{
			// exist = true;
			// }
			// if(exist && stop){
			// serviceManager.startService();
			// stop = false;
			// exist = false;
			// }

			try {
				ActionName = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
						.getString(ConfigSP.SP_reseach_actionName, "");
			} catch (Exception e) {
				// TODO: handle exception
				ActionName = "无";
			}
			// System.out.println("当前任务：" + ActionName);
			try {
				etc_Step = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
						.getString(ConfigSP.SP_reseach_etc_actionStep, "");
			} catch (Exception e) {
				// TODO: handle exception
				etc_Step = "0";
			}

			try {
				standard = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
						.getString(ConfigSP.SP_reseach_standard, "");
			} catch (Exception e) {
				// TODO: handle exception
				standard = "null";
			}

			try {
				plan = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).getString(
						ConfigSP.SP_reseach_actionNumber, "");
			} catch (Exception e) {
				// TODO: handle exception
				plan = "0";
			}

			try {
				runtime = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
						.getString(ConfigSP.SP_reseach_runtime, "");
			} catch (Exception e) {
				// TODO: handle exception
				runtime = "0";
			}

			try {
				totaltime = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
						.getString(ConfigSP.SP_reseach_totaltime, "");
			} catch (Exception e) {
				// TODO: handle exception
				totaltime = "0";
			}

			try {
				signal = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).getString(
						ConfigSP.SP_reseach_signalType, "")
						+ " / "
						+ getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
								.getString(ConfigSP.SP_reseach_signalStrength, "");
			} catch (Exception e) {
				// TODO: handle exception
				signal = "null,0";
			}

			try {
				cell_ID = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
						.getString(ConfigSP.SP_reseach_Cell_ID, "");
			} catch (Exception e) {
				// TODO: handle exception
				cell_ID = "0";
			}

			try {
				power = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).getString(
						ConfigSP.SP_reseach_power, "");
			} catch (Exception e) {
				// TODO: handle exception
				power = "0";
			}

			try {
				status = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).getString(
						ConfigSP.SP_reseach_status, "");
			} catch (Exception e) {
				// TODO: handle exception
				status = "可以测试了";
			}

			try {
				PNstatus = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
						.getString(ConfigSP.SP_reseach_PNstatus, "");
			} catch (Exception e) {
				// TODO: handle exception
				PNstatus = "未知";
			}

			try {
				IP = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).getString(
						ConfigSP.SP_reseach_ip, "");
			} catch (Exception e) {
				// TODO: handle exception
				IP = "0.0.0";
			}

			try {
				IMSI = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).getString(
						ConfigSP.SP_reseach_Imsi, "");
			} catch (Exception e) {
				// TODO: handle exception
				IMSI = "0";
			}

			try {
				PID = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).getString(
						ConfigSP.SP_phone_id, "");
			} catch (Exception e) {
				// TODO: handle exception
				PID = "0";
			}

			try {
				powerlower = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
						.getString(ConfigSP.SP_reseach_power_low, Dpowerlow);
			} catch (Exception e) {
				// TODO: handle exception
				powerlower = "0";
			}

			try {
				powerstatus = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
						.getString(ConfigSP.SP_reseach_BatteryResponse, "");
			} catch (Exception e) {
				// TODO: handle exception
				powerstatus = "请稍等...";
			}

			/**
			 * 刷新手机端WiFi与外网的IP
			 */
			ConnectivityManager mConnectivityManager = (ConnectivityManager) getApplicationContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {

				if (wifiManager.isWifiEnabled()) {
					// try {
					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					int ipAddress = wifiInfo.getIpAddress();
					P_IP = intToIp(ipAddress);
					// } catch (Exception ex) {
					// Log.e("WifiPreference IpAddress", ex.toString());
					// P_IP = "0.0.0.0";
					// }
				} else {
					try {
						for (Enumeration<NetworkInterface> en = NetworkInterface
								.getNetworkInterfaces(); en.hasMoreElements();) {
							NetworkInterface intf = en.nextElement();
							for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
									.hasMoreElements();) {
								InetAddress inetAddress = enumIpAddr.nextElement();
								if (!inetAddress.isLoopbackAddress()) {
									P_IP = inetAddress.getHostAddress().toString();
								}
							}
						}
					} catch (SocketException ex) {
						Log.e("WifiPreference IpAddress", ex.toString());
						P_IP = "0.0.0.0";
					}
				}
			} else {
				// System.out.println("网络连接失败");
				P_IP = "0.0.0.0";
			}

			status_tv.setText(status);
			powerstatus_tv.setText(powerstatus);
			PNstatus_tv.setText(PNstatus);
			IP_tv.setText(IP);
			P_IP_tv.setText(P_IP);
			IMSI_tv.setText(IMSI);
			now_IMSI_tv.setText(now_IMSI);
			PID_tv.setText(PID);
			powerlower_tv.setText(powerlower + "%");
			power_tv.setText(power + "%");
			signal_tv.setText(signal);
			cell_ID_tv.setText(cell_ID);
			task_tv.setText(ActionName);
			step_tv.setText(etc_Step);
			plan_tv.setText(plan);
			standard_tv.setText(standard);
			runtime_tv.setText(runtime + "ms");
			totaltime_tv.setText(totaltime + "ms");

			handler.postDelayed(this, 1000);

		}
	};

	BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			final int level = intent.getIntExtra("level", 0); // 电池电量等级
			// int scale = intent.getIntExtra("scale", 100); // 电池满时百分比
			final int status = intent.getIntExtra("status", 0); // 电池状态

			System.out.println("当前电量：" + level);
			editor.putString(ConfigSP.SP_reseach_power, level + "").commit();

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

								String re = PowerResponce.getInstance().responce(AcMain.this,
										"high");

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

								String re = PowerResponce.getInstance()
										.responce(AcMain.this, "low");

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);

		wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);

		// 屏幕常亮
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// 设置通知栏
		// 获取系统的notificationManager服务
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent intent = new Intent(this, AcMain.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		Notification notify = new Notification.Builder(this).setSmallIcon(R.drawable.ic_launcher)
		// 设置通知常驻，不会通过左右滑动消除，只能通过cancel消除
				.setOngoing(true)
				// 设置通知将要启动程序的Intent
				.setContentIntent(pi)
				// 设置打开该通知，该通知不会自动消失
				.setAutoCancel(false).build();
		// 发送通知
		nm.notify(NOTIFICATION_ID, notify);
		/** 在4.0后主线程里使用Http请求应加上下面两句代码 */
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectDiskReads().detectDiskWrites().detectNetwork()
		// .penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
		// .penaltyLog().penaltyDeath().build());

		// final EditText et = (EditText) findViewById(R.id.main_et);
		// Button bt = (Button) findViewById(R.id.main_bt);
		// Button rbt = (Button) findViewById(R.id.reRegister_bt);

		status_tv = (TextView) this.findViewById(R.id.status_tv);
		PNstatus_tv = (TextView) this.findViewById(R.id.PNstatus_tv);
		powerstatus_tv = (TextView) this.findViewById(R.id.powerstatus_tv);
		IP_tv = (TextView) this.findViewById(R.id.IP_tv);
		P_IP_tv = (TextView) findViewById(R.id.Phone_IP_tv);
		IMSI_tv = (TextView) this.findViewById(R.id.IMSI_tv);
		now_IMSI_tv = (TextView) this.findViewById(R.id.now_IMSI_tv);
		PID_tv = (TextView) this.findViewById(R.id.phone_id_tv);
		powerlower_tv = (TextView) this.findViewById(R.id.main_tv);
		power_tv = (TextView) this.findViewById(R.id.power_tv);
		signal_tv = (TextView) this.findViewById(R.id.signal_tv);
		cell_ID_tv = (TextView) this.findViewById(R.id.cell_ID_tv);
		task_tv = (TextView) this.findViewById(R.id.taskNameId);
		step_tv = (TextView) this.findViewById(R.id.step_tv);
		plan_tv = (TextView) this.findViewById(R.id.plan_tv);
		standard_tv = (TextView) this.findViewById(R.id.standard_tv);
		runtime_tv = (TextView) this.findViewById(R.id.runtime_tv);
		totaltime_tv = (TextView) this.findViewById(R.id.totaltime_tv);

		editor = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).edit();

		editor.putString(ConfigSP.SP_reseach_status, "等待测试").commit();

		editor.putString(ConfigSP.SP_ISUpload, "true").commit();//初始化ConfigSP.SP_ISUpload
		
		Dpowerlow = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).getString(
				ConfigSP.SP_reseach_power_low_default, "0");

		handler.postDelayed(myRunnable, 1000);

		// 定义电池电量更新广播的过滤器,只接受带有ACTION_BATTERRY_CHANGED事件的Intent
		IntentFilter batteryChangedReceiverFilter = new IntentFilter();
		batteryChangedReceiverFilter.addAction(Intent.ACTION_BATTERY_CHANGED);//
		// 向系统注册batteryChangedReceiver接收器，本接收器的实现见代码字段处
		registerReceiver(batteryChangedReceiver, batteryChangedReceiverFilter);
		System.out.println("++注册batteryChangedReceiver");

		Intent intent1 = new Intent(AcMain.this, SignalService.class);
//		stopService(intent1);
		startService(intent1);// 启动测试信号服务

	}

	/**
	 * 重新注册，关闭所有服务
	 * 
	 * @param view
	 */
	public void reRegister(View view) {

		AlertDialog.Builder alertbBuilder = new AlertDialog.Builder(AcMain.this);
		alertbBuilder.setTitle("提示").setMessage("重新注册会关闭双通道及所有网络;确认执行重新注册？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 执行重新注册操作
						// editor.putString(ConfigSP.SP_reseach_ip,
						// "").commit();
						editor.putString(ConfigSP.SP_reseach_checkMark, "").commit();

						/**
						 * 关闭网络并停止双通道
						 */
						stopService(new Intent(AcMain.this, TimeService.class));// 停止双通道服务
						ShellUtil.execCommand("svc data disable", true); // 关闭数据
						ShellUtil.execCommand("svc wifi disable", true); // 关闭wifi

						/**
						 * 关闭所有的服务
						 */
						editor.remove(ConfigSP.SP_reseach_PNstatus);

						editor.remove(ConfigSP.SP_reseach_actionName);
						editor.remove(ConfigSP.SP_reseach_etc_actionStep);
						editor.remove(ConfigSP.SP_reseach_actionNumber);
						editor.remove(ConfigSP.SP_reseach_standard);
						editor.remove(ConfigSP.SP_reseach_runtime);
						editor.remove(ConfigSP.SP_reseach_totaltime);
						editor.commit();

						Intent registerIntent = new Intent(AcMain.this, AcRegister.class);
						startActivity(registerIntent);

						ServiceManager serviceManager = new ServiceManager(getApplicationContext());
						serviceManager.stopService();

						Intent intent1 = new Intent(AcMain.this, SignalService.class);
						Intent intent2 = new Intent(AcMain.this, LogicService.class);
						Intent intent3 = new Intent(AcMain.this, ListenerService.class);
						Intent intent4 = new Intent(AcMain.this, NotificationService.class);
						Intent intent5 = new Intent(AcMain.this, CallSMSListenerService.class);

						stopService(intent1);
						stopService(intent2);
						stopService(intent3);
						stopService(intent4);
						System.out.println("+++++++++");
						stopService(intent5);
						nm.cancel(NOTIFICATION_ID);
						AcMain.this.finish();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				}).create();
		alertbBuilder.show();

	}

	private String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
				+ (i >> 24 & 0xFF);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 定义电池电量更新广播的过滤器,只接受带有ACTION_BATTERRY_CHANGED事件的Intent
		IntentFilter batteryChangedReceiverFilter = new IntentFilter();
		batteryChangedReceiverFilter.addAction(Intent.ACTION_BATTERY_CHANGED);//
		// 向系统注册batteryChangedReceiver接收器，本接收器的实现见代码字段处
		registerReceiver(batteryChangedReceiver, batteryChangedReceiverFilter);
		System.out.println("++注册batteryChangedReceiver");
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(batteryChangedReceiver);
		System.out.println("--注销batteryChangedReceiver");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				// editor.putString(ConfigSP.SP_reseach_actionName,
				// "").commit();
				// editor.putString(ConfigSP.SP_reseach_etc_actionStep,
				// "").commit();
				// editor.putString(ConfigSP.SP_reseach_actionNumber,
				// "").commit();
				// editor.putString(ConfigSP.SP_reseach_standard,
				// "").commit();
				// editor.putString(ConfigSP.SP_reseach_runtime,
				// "").commit();
				// editor.putString(ConfigSP.SP_reseach_totaltime,
				// "").commit();
				editor.remove(ConfigSP.SP_reseach_PNstatus);

				editor.remove(ConfigSP.SP_reseach_actionName);
				editor.remove(ConfigSP.SP_reseach_etc_actionStep);
				editor.remove(ConfigSP.SP_reseach_actionNumber);
				editor.remove(ConfigSP.SP_reseach_standard);
				editor.remove(ConfigSP.SP_reseach_runtime);
				editor.remove(ConfigSP.SP_reseach_totaltime);
				editor.commit();

				unregisterReceiver(batteryChangedReceiver);
				System.out.println("--注销batteryChangedReceiver");

				Intent intent1 = new Intent(AcMain.this, SignalService.class);
				Intent intent2 = new Intent(AcMain.this, LogicService.class);
				Intent intent3 = new Intent(AcMain.this, ListenerService.class);
				Intent intent4 = new Intent(AcMain.this, NotificationService.class);
				Intent intent5 = new Intent(AcMain.this, CallSMSListenerService.class);

				stopService(intent1);
				stopService(intent2);
				stopService(intent3);
				stopService(intent4);
				stopService(intent5);

				// ActivityManager am = (ActivityManager)
				// getSystemService(Context.ACTIVITY_SERVICE);
				// am.restartPackage(getPackageName());
				nm.cancel(NOTIFICATION_ID);

				AcMain.this.finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		editor.remove(ConfigSP.SP_reseach_PNstatus);

		editor.remove(ConfigSP.SP_reseach_actionName);
		editor.remove(ConfigSP.SP_reseach_etc_actionStep);
		editor.remove(ConfigSP.SP_reseach_actionNumber);
		editor.remove(ConfigSP.SP_reseach_standard);
		editor.remove(ConfigSP.SP_reseach_runtime);
		editor.remove(ConfigSP.SP_reseach_totaltime);
		editor.commit();
	}
}
