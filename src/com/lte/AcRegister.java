package com.lte;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidpn.client.ServiceManager;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lte.config.ConfigPN;
import com.lte.config.ConfigSP;
import com.lte.test.DelFile;
import com.lte.util.ConnectivityManagerUtil;
import com.lte.util.NetUtil;

public class AcRegister extends Activity {

	private Context context;
	/**
	 * IPEditText
	 */
	private EditText ipET;
	/**
	 * Text tv
	 */
	private TextView tv;
	/*
	 * 下次自动登陆CheckBox
	 */
	private CheckBox cb;
	/**
	 * DoubleLine注册Button
	 */
	private Button DoubleLineBtn;
	/**
	 * 手动注册Button
	 */
	private Button registerBtn;
	/** imsi */
	private String strImsi;
	/** ip */
	private String ip;
	/** checkMark是否直接登录的标志 */
	String checkMark;
	/** check存入勾选状态 */
	// boolean check;
	/** 设备名 */
	private String strFactory;
	/** 型号 */
	private String strType;
	/** exitTime初始化 */
	private long exitTime = 0;
	/** 超时反馈 */
	String Dialog = "";
	/** 编辑器 */
	Editor editor;// 编辑器
	/** 缺省电量下限初始化值(可从这直接修改) */
	String Dpowerlow = "90";
	/**
	 * 注册服务器返回的结果
	 */
	public static String result;
	/**
	 * 注册流程执行完毕的标识位
	 */
	public static int wait = 0;

	boolean flag = true;

	ConnectivityManagerUtil cmu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.register);
		// 视图
		ipET = (EditText) this.findViewById(R.id.pn_ET_IP);
		cb = (CheckBox) this.findViewById(R.id.check);
		tv = (TextView) this.findViewById(R.id.tv);

		editor = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).edit();// 初始化编辑器
		editor.putString(ConfigSP.SP_reseach_power_low_default, Dpowerlow).commit();

		try {
			checkMark = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).getString(
					ConfigSP.SP_reseach_checkMark, "");
		} catch (Exception e) {
			// TODO: handle exception
			checkMark = "unchecked";
		}
		if ("checked".equals(checkMark)) {
			cb.setChecked(true);
		} else {
			cb.setChecked(false);
		}

		try {
			ip = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).getString(
					ConfigSP.SP_reseach_ip, "");
			ipET.setText(ip);// 将SP_reseach_ip的值写入ipET
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("ip:" + e);
			ip = "";
			ipET.setText("null");// 将SP_reseach_ip的值写入ipET
		}

		try {
			Intent intentD = getIntent();
			Dialog = intentD.getStringExtra("Dialog");
			if (Dialog.equals("") || Dialog == null) {
				System.out.println("Dialog无值");
			} else {
				Toast.makeText(getApplicationContext(), Dialog, Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Dialog无传递值");
		}

		DoubleLineBtn = (Button) this.findViewById(R.id.pn_BTN_register1);
		DoubleLineBtn.setTextColor(Color.rgb(255, 255, 255));
		DoubleLineBtn.setText("双通道连接注册");
		DoubleLineBtn.setClickable(true);
		// aliasET = (EditText) this.findViewById(R.id.pn_ET_alias);
		registerBtn = (Button) this.findViewById(R.id.pn_BTN_register2);
		// String registerName = getSharedPreferences(ConfigSP.SP_reseach,
		// Context.MODE_PRIVATE).getString(ConfigSP.SP_reseach_name, "");
		// cb.setClickable(false);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {// 勾选才存储checkMark
					checkMark = "checked";
				} else {
					checkMark = "unchecked";
				}
			}
		});

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);
		strImsi = tm.getSubscriberId();// 手机卡唯一标识
		System.out.println("------------------手机------" + strImsi);

		cmu = new ConnectivityManagerUtil(getApplicationContext());

		/**
		 * 判断下次登录checkIp是否存在，如果存在直接进入注册PN
		 */
		if ("checked".equals(checkMark) &&
		// ip != null &&
				!"".equals(ip)) {
			new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					while (flag) {
						wait = 0;
						try {
							if (cmu.isConnectivity()) {// wifi连接成功
								new RegisterPNAsyncTask().execute();
								while (true) {
									if (wait == 1) {// 说明注册流程执行完毕
										if (result != null && result.contains("SUCCESS")) {// 返回成功，登录成功
											flag = false;
											break;
										} else {// 注册失败
											sleep(5 * 1000);
											break;
										}
									}

								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} catch (Error e) {
							e.printStackTrace();
						}
					}
				}
			}.start();

			// ipET.setClickable(false);
			// // ipET.setClickable(false);
			// // aliasET.setText(registerName);
			// registerBtn.setText("登录");
		}
		DoubleLineBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ip = ipET.getText().toString().trim();

				if (!ip.matches("(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}")) {
					Toast.makeText(context, "填写不完整或格式不正确", Toast.LENGTH_SHORT).show();
					delayClick(DoubleLineBtn);
				} else {
					editor.putString(ConfigSP.SP_reseach_checkMark, checkMark);
					editor.putString(ConfigSP.SP_reseach_ip, ip);
					editor.commit();

					DoubleLineBtn.setTextColor(Color.rgb(150, 150, 150));
					DoubleLineBtn.setText("双通道开启中...");
					DoubleLineBtn.setClickable(false);

					Intent intent = new Intent();
					intent.putExtra("IP", ip);
					intent.setClass(AcRegister.this, AcDoubleLine.class);
					startActivity(intent);
					AcRegister.this.finish();
				}
			}
		});
		registerBtn.setOnClickListener(new RegisterOnClickListener());

		// 程序启动遍历sdcard目录下testcase文件夹中超过7天的文件 如果超过七天则删除
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/testcase";
		// 先进行判断SD卡下testcase文件夹是否存在，不存在进行创建
		File testcaseFile = new File(path);
		if (!testcaseFile.exists()) {
			testcaseFile.mkdir();
		}
		// 获得手机当前的时间
		long curTime = System.currentTimeMillis();// 获取当前时间

		File files[] = new File(path).listFiles();
		for (File ff : files) {
			String filesName = ff.getName();// 得到文件夹及文件名字
			if (!filesName.contains("null")) {
				if (filesName.contains("_")) {// 得到有时间戳的文件及文件夹
					if (ff.isDirectory()) {// 说明是个文件夹
						String filesNameList[] = filesName.split("_");
						System.out.println(filesNameList.length);
						if (curTime - Long.parseLong(filesNameList[2]) > 24 * 60 * 60 * 1000) {// 超过规定时间(暂定1天)执行删除
							DelFile.delFile(path + "/" + ff.getName());
						}
					} else {// 说明是个文件
						String subName = ff.getName().substring(0, ff.getName().length() - 4);
						String filesNameList[] = subName.split("_");
						if (curTime - Long.parseLong(filesNameList[2]) > 24 * 60 * 60 * 1000) {// 超过规定时间(暂定一天)执行删除
							DelFile.delFile(path + "/" + ff.getName());
						}
					}
				}

			} else {
				DelFile.delFile(path + "/" + ff.getName());
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		doSU();
	}

	private void doSU() {
		try {
			@SuppressWarnings("unused")
			Process process = Runtime.getRuntime().exec("su");// 判断手机是否root
			tv.setText("已root");
			DoubleLineBtn.setClickable(true);
		} catch (IOException e) {
			System.out.println("root e.getMessage()" + e.getMessage());
			tv.setText("未root");
			DoubleLineBtn.setClickable(false);
			DoubleLineBtn.setTextColor(Color.rgb(150, 150, 150));// 字体转灰
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * 点击手动注册
	 */
	private class RegisterOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// System.out.println("-" + aliasET.getText().toString().trim() +
			// "-");
			ip = ipET.getText().toString().trim();

			cmu = new ConnectivityManagerUtil(getApplicationContext());// 连接管理实例

			// accontName = aliasET.getText().toString().trim();
			if (!ip.matches("(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}")) {
				Toast.makeText(context, "填写不完整或格式不正确", Toast.LENGTH_SHORT).show();
				delayClick(registerBtn);
			} else {
				editor.putString(ConfigSP.SP_reseach_checkMark, checkMark);
				editor.putString(ConfigSP.SP_reseach_ip, ip);
				editor.commit();

				if (cmu.isConnectivity()) {
					registerBtn.setTextColor(Color.rgb(150, 150, 150));// 字体转灰
					registerBtn.setText("正在注册...");
					registerBtn.setClickable(false);
					// Map<String, String> pnRegisterMap = new HashMap<String,
					// String>();
					new RegisterPNAsyncTask().execute();
				} else {
					Toast.makeText(context, "网络未连接！请手动打开网络", Toast.LENGTH_SHORT).show();
				}
			}
		}

	}

	// -----------------------------------------------------------------------------------------------------------------------------------------
	// 功能
	/**
	 * 注册PN（判断是否已经有该用户）
	 * 
	 */
	private class RegisterPNAsyncTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			strImsi = tm.getSubscriberId();// 手机卡唯一标识

			strFactory = Build.MANUFACTURER;// 手机制造厂商
			strType = Build.MODEL;// 手机型号
			// ip = checkIp;
			String serverIp = "http://" + ip;

			// String accontName = alias;
			NameValuePair pairImsi = new BasicNameValuePair("strImsi", strImsi); // Imsi
			NameValuePair pairFactory = new BasicNameValuePair("strFactory", strFactory);
			NameValuePair pairType = new BasicNameValuePair("strType", strType);

			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			paramList.add(pairImsi);
			paramList.add(pairFactory);
			paramList.add(pairType);
			// String url = serverIp + ":8080/LTETestProject/terminal/Register";
			String url = serverIp + ":8080/ResearchProject/terminal/Register";

			System.out.println(url);
			result = NetUtil.getInstance().httpUpload(context, url, paramList);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.contains("SUCCESS")) {
				// 写入sp，供pn注册时调用
				Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();

				ConfigPN.PNIP = ip;
				registerBtn.setTextColor(Color.rgb(255, 255, 255));
				registerBtn.setText("手动连接注册");
				registerBtn.setClickable(true);
				// ipET.setText("");
				Intent skipMainIntent = new Intent(context, AcMain.class);
				startActivity(skipMainIntent);
				// Start the service
				ServiceManager serviceManager = new ServiceManager(context);
				// serviceManager.setNotificationIcon(R.drawable.pn_notification);
				Map<String, String> pnMap = new HashMap<String, String>();
				// pnMap.put("strImei", strImei);
				// pnMap.put("userName", strImei);
				// pnMap.put("strName", accontName);
				pnMap.put("strImsi", strImsi);
				pnMap.put("userName", strImsi);
				pnMap.put("strFactory", strFactory);
				pnMap.put("strType", strType);
				ConfigPN.pnRegisterMap = pnMap;
				serviceManager.startService();
				Editor editor = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
						.edit();
				editor.putString(ConfigSP.SP_reseach_checkMark, checkMark);
				editor.putString(ConfigSP.SP_reseach_ip, ipET.getText().toString());
				editor.putString(ConfigSP.SP_reseach_Imsi, strImsi);
				editor.putString(ConfigSP.SP_phone_id, result.substring(7));// 把手机的ID保存
				editor.commit();
				AcRegister.this.finish();
			}
			// else if (result.equals("EXIST")) {
			// Toast.makeText(context, "账号已经存在", Toast.LENGTH_SHORT).show();
			// registerBtn.setText("注册");
			// registerBtn.setClickable(true);
			// }
			else if (result.equals("FAILED")) {
				Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
				registerBtn.setTextColor(Color.rgb(255, 255, 255));
				registerBtn.setText("手动连接注册");
				registerBtn.setClickable(true);

			} else {
				System.out.println(result);
				Toast.makeText(context, "注册出现问题，重新注册" + result, Toast.LENGTH_SHORT).show();
				registerBtn.setTextColor(Color.rgb(255, 255, 255));
				registerBtn.setText("手动连接注册");
				registerBtn.setClickable(true);
			}
			wait = 1;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				this.finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void delayClick(final Button bt) {
		// TODO Auto-generated method stub
		bt.setTextColor(Color.rgb(150, 150, 150));// 字体转灰
		bt.setText("请稍等...");
		bt.setClickable(false);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// execute the task
				bt.setText("手动连接注册");
				bt.setClickable(true);
				bt.setTextColor(Color.rgb(255, 255, 255));// 字体转白
			}
		}, 2500);
	}
}
