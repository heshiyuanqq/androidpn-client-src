package com.lte;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidpn.client.ServiceManager;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lte.R;
import com.lte.R.id;
import com.lte.R.layout;
import com.lte.config.ConfigPN;
import com.lte.config.ConfigSP;
import com.lte.doubleline.ShellUtil;
import com.lte.doubleline.TimeService;
import com.lte.doubleline.ShellUtil.CommandResult;
import com.lte.util.ConnectivityManagerUtil;
import com.lte.util.NetUtil;

public class AcDoubleLine extends Activity {
	private static final String TAG = "AcGprsAndWifi";
	private Context context;
	Method dataConnSwitchmethod;
	// Class telephonyManagerClass;
	Object ITelephonyStub;
	// Class ITelephonyClass;
	TelephonyManager telephonyManager;
	String[] strs;
	StringBuffer sb = new StringBuffer();
	String dataNDS1;
	String dataNDS2;
	
	String dialog = "";
	
	private TextView text;
	private ProgressDialog pd;

	int delay = 1000;
	int i = 0;
	String ip;
	// boolean stopThread = false;
	int j = 0;

	Thread myThread;

	/**
	 * openDoubleLink()内部循环控制位
	 */
	boolean flag = true;
	boolean isWifiEnabled = false;
	boolean isConnected = false;
	boolean start = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doubleline);
		text = (TextView) findViewById(R.id.text);

		this.context = this;
		// ConnectivityManagerUtil cmu = new ConnectivityManagerUtil(context);
		// Toast.makeText(getApplicationContext(), "正在初始化数据，请稍等。。",
		// Toast.LENGTH_SHORT).show();
		Intent intent = getIntent();
		ip = intent.getStringExtra("IP");
		closeDoubleLink();
		openDoubleLink();
		myThread = new Thread() {
			public void run() {
				try {
					sleep(60 * 1000);
					System.out.println("60s");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				// stopThread = true;
				while(start){					
					flag = false;
					isWifiEnabled = true;
					isConnected = true;
					closeDoubleLink();
					try {
						sleep(2 * 1000);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					pd.dismiss();// 关闭ProgressDialog
					
					dialog = "双通道开启超时，请重试！";
					Intent skipRegisterIntent = new Intent(context,
							AcRegister.class);
					skipRegisterIntent.putExtra("Dialog", dialog);
					startActivity(skipRegisterIntent);
					AcDoubleLine.this.finish();
					break;
				}
			}
		};
		myThread.start();
	}

	@Override
	protected void onDestroy() {
		pd.dismiss();// 关闭ProgressDialog
		super.onDestroy();
	}

	@Override
	public void finish() {
		pd.dismiss();// 关闭ProgressDialog
		super.finish();
	}

	/**
	 * 打开双通道
	 */
	private void openDoubleLink() {
		pd = ProgressDialog.show(AcDoubleLine.this, "请稍候", "双通道加载中……");
		pd.setTitle("正在开启双通道");
		new Thread() {
			public void run() {
				// while (!stopThread) {
				// 1. 打开数据网络
				CommandResult opendata = ShellUtil.execCommand(
						"svc data enable", true);
				 sb.append("1. svc data enable(打开数据网络)\n");
				sentMesage();
				// if (opendata.errorMsg == null ||
				// opendata.errorMsg.equals("")) {// 数据开关打开命令执行成功
				// 等待数据网络连接成功
				while (flag) {
					Log.d(TAG, "等待数据网络连接成功");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					boolean isMobile = ConnectivityManagerUtil.getInstance(
							context).isMobile();
					j++;
					System.out.println("循环" + j);
					if (isMobile == true) {
						break;
					}
				}
				 sb.append("数据网络连接成功\n");
				sentMesage();
				// 2. 获取数据DNS
				CommandResult getdns1 = ShellUtil.execCommand(
						"getprop net.dns1", true);

				dataNDS1 = getdns1.successMsg;
				 sb.append("2.获取数据DNS\n数据DNS1:" + dataNDS1 + "\n");
				sentMesage();
				CommandResult getdns2 = ShellUtil.execCommand(
						"getprop net.dns2", true);

				dataNDS2 = getdns2.successMsg;
				 sb.append("数据DNS2:" + dataNDS2 + "\n");
				sentMesage();

				// 3. 打开wifi
				CommandResult openwifi = ShellUtil.execCommand(
						"svc wifi enable", true);

				WifiManager wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				// isWifiEnabled = false;
				while (isWifiEnabled == false) {
					Log.d(TAG, "打开Wifi");
					// 停500毫秒，再次判断Wifi是否关闭
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					isWifiEnabled = wifiManager.isWifiEnabled();
				}
				 sb.append("3. svc wifi enable(打开Wifi)\n");
				sentMesage();

				// isConnected = false;
				while (isConnected == false) {
					Log.d(TAG, "判断Wifi是否连接成功");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// 每次获取连接状态，都需要重新初始化ConnectivityManager，否则始终返回false
					ConnectivityManager connMgr = (ConnectivityManager) ((Activity) context)
							.getApplicationContext().getSystemService(
									Context.CONNECTIVITY_SERVICE);
					NetworkInfo networkInfo = connMgr
							.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					isConnected = networkInfo.isConnected();
				}
				 sb.append("connected to wifi(连接WiFi成功)\n");
				sentMesage();

				// 4. 定时保持双通道
				startService(new Intent(AcDoubleLine.this, TimeService.class));
				 sb.append("4. 定时保持双通道\n");
				sentMesage();

				// 5. 删除默认路由表
				CommandResult delDefault = ShellUtil.execCommand(
						"ip route del default;ip route del default", true);
				 sb.append("5. ip route del default;ip route del default (删除默认路由表)\n");
				sentMesage();
				// Log.e("delDefault_S", delDefault.successMsg);
				// Log.e("delDefault_E", delDefault.errorMsg);
				try {
					new Thread().sleep(5 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// 6. 添加数据路由表
				CommandResult addDefault = ShellUtil
						.execCommand(
								"ip route add 0.0.0.0/0 dev rmnet1 ;ip route add default dev rmnet1",
								true);
				CommandResult addDefault2 = ShellUtil
						.execCommand(
								"ip route add 0.0.0.0/0 dev rmnet0 ;ip route add default dev rmnet0",
								true);
				 sb.append("6. ip route add 0.0.0.0/0 dev rmnet1 ;ip route add default dev rmnet1 (添加数据路由表)\n");
				sentMesage();

				// 7. 添加DNS
				CommandResult addDns = ShellUtil.execCommand(
						"ndc resolver setifdns eth0 \"\" " + dataNDS1 + " "
								+ dataNDS2, true);
				CommandResult refreshDns = ShellUtil.execCommand(
						"ndc resolver setdefaultif eth0", true);
				 sb.append("7. ndc resolver setifdns eth0 (添加DNS)\n");
				sentMesage();
				// 8.提示完成
				sb.append("请稍等！");
				sentMesage();
				// Log.e("addDefault_S", addDefault.successMsg);
				// Log.e("addDefault_E", addDefault.errorMsg);
				// }
				// }
				// }

				System.out.println("循环结束" + j);

				if (i == 11
				// && ConnectivityManagerUtil.getInstance(context)
				// .isMobile()
						&& wifiManager.isWifiEnabled()) {
					System.out.println("开始注册" + j);
					start = false;
//					if (myThread != null && myThread.isAlive()) {
//						System.out
//								.println("status:" + myThread.isInterrupted() + "," + myThread.isAlive());
//						myThread.interrupt();
//						System.out
//								.println("status:" + myThread.isInterrupted());
//					}

					new RegisterPNAsyncTask().execute();
					try {
						new Thread().sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// stopThread = true;
					// Intent skipRegisterIntent = new
					// Intent(AcDoubleLine.this,
					// AcMain.class);
					// startActivity(skipRegisterIntent);
					// AcDoubleLine.this.finish();
				}
				// }

			};
		}.start();
	}

	/**
	 * 关闭双通道
	 */
	public void closeDoubleLink() {
		this.stopService(new Intent(AcDoubleLine.this, TimeService.class)); // 关闭双通道
		ShellUtil.execCommand("svc data disable", true); // 关闭数据
		ShellUtil.execCommand("svc wifi disable", true); // 关闭wifi
	}

	/**
	 * 保留wifi
	 */
	public void retainWifi() {
		this.stopService(new Intent(AcDoubleLine.this, TimeService.class)); // 关闭双通道
		ShellUtil.execCommand("svc data disable", true); // 关闭data
	}

	/**
	 * 保留数据
	 */
	public void retainData() {
		this.stopService(new Intent(AcDoubleLine.this, TimeService.class)); // 关闭双通道
		ShellUtil.execCommand("svc wifi disable", true); // 关闭wifi
	}

	Handler han = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				text.setText(msg.getData().getString("data"));

			}

		}
	};

	private void sentMesage() {
		Message msg = han.obtainMessage();
		msg.what = 1;
		Bundle bun = new Bundle();
		bun.putString("data", sb.toString());
		msg.what = 1;
		msg.setData(bun);
		han.sendMessage(msg);
		i++;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------
	// 功能
	/**
	 * 注册PN（判断是否已经有该用户）
	 * 
	 */
	private class RegisterPNAsyncTask extends AsyncTask<Void, Void, String> {
		String strImsi;
		String strFactory;
		String strType;
		String serverIp;

		@Override
		protected String doInBackground(Void... params) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(context.TELEPHONY_SERVICE);
			strImsi = tm.getSubscriberId();// 手机卡唯一标识

			strFactory = Build.MANUFACTURER;// 手机制造厂商
			strType = Build.MODEL;// 手机型号
			serverIp = "http://" + ip;

			// String accontName = alias;
			NameValuePair pairImsi = new BasicNameValuePair("strImsi", strImsi); // Imsi
			NameValuePair pairFactory = new BasicNameValuePair("strFactory",
					strFactory);
			NameValuePair pairType = new BasicNameValuePair("strType", strType);

			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			paramList.add(pairImsi);
			paramList.add(pairFactory);
			paramList.add(pairType);

			String url = serverIp + ":8080/ResearchProject/terminal/Register";
			String result = NetUtil.getInstance().httpUpload(context, url,
					paramList);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.contains("SUCCESS")) {
				// 写入sp，供pn注册时调用
				Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();

				ConfigPN.PNIP = ip;

				Intent skipMainIntent = new Intent(context, AcMain.class);
				startActivity(skipMainIntent);
				// Start the service
				ServiceManager serviceManager = new ServiceManager(context);
				// serviceManager.setNotificationIcon(R.drawable.pn_notification);
				Map<String, String> pnMap = new HashMap<String, String>();

				pnMap.put("strImsi", strImsi);
				pnMap.put("userName", strImsi);
				pnMap.put("strFactory", strFactory);
				pnMap.put("strType", strType);
				ConfigPN.pnRegisterMap = pnMap;
				serviceManager.startService();
				Editor editor = getSharedPreferences(ConfigSP.SP_reseach,
						Context.MODE_PRIVATE).edit();

				editor.putString(ConfigSP.SP_reseach_ip, ip);
				editor.putString(ConfigSP.SP_reseach_Imsi, strImsi);
				editor.putString(ConfigSP.SP_phone_id, result.substring(7));// 把手机的ID保存
				editor.commit();
				AcDoubleLine.this.finish();
			}

			else if (result.equals("FAILED")) {
//				start = false;
				System.out.println("注册失败!");
				dialog = "注册失败!";
				closeDoubleLink();
				Intent skipMainIntent = new Intent(context, AcRegister.class);
				skipMainIntent.putExtra("Dialog", dialog);
				startActivity(skipMainIntent);
				AcDoubleLine.this.finish();

			} else {
//				start = false;
				System.out.println("注册出现问题，重新注册!");
				dialog = "注册出现问题，重新注册!";
				closeDoubleLink();
				Intent skipMainIntent = new Intent(context, AcRegister.class);
				skipMainIntent.putExtra("Dialog", dialog);
				startActivity(skipMainIntent);
				AcDoubleLine.this.finish();

			}
		}
	}
}
