package com.lte.listener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.TextView;
import android.widget.Toast;

import com.lte.R;
import com.lte.util.FnUtil;
import com.lte.util.PluginUtil;
import com.lte.util.ReadLogUtil;
import com.lte.util.ZipUtil;

public class Abd_AcListener extends Activity {

	private Context context;
	/**
	 * 监听手机状态<br>
	 * 呼叫状态的时间点<br>
	 * 信号强度<br>
	 * 网络类型<br>
	 * 小区id<br>
	 */
	private PhoneStateListener phoneStateListener;
	private TelephonyManager telephonyManager;
	// /**
	// * 来电监听
	// */
	// private BroadcastReceiver_tel telReceiver;
	/**
	 * 输出状态的TextView
	 */
	private TextView stateTV;
	/**
	 * 状态文字
	 */
	private StringBuffer stateSB;
	/**
	 * 读取通讯录QueryHandler
	 */
	private AsyncQueryHandler asyncQuery;
	private Uri uri = android.provider.CallLog.Calls.CONTENT_URI; // 查询的列
	String[] fieldArr = { CallLog.Calls.DATE, // 日期
			CallLog.Calls.NUMBER, // 号码
			CallLog.Calls.TYPE, // 类型
			CallLog.Calls.CACHED_NAME, // 名字
			CallLog.Calls._ID, // id
			CallLog.Calls.DURATION, };
	/**
	 * 去电挂断时间（减去通话时长来计算被叫接听时间）
	 */
	private long idleTime;
	/** 是否停止监听信号强度 */
	private boolean isStopListenStrength = false;
	/** 是否停止监听CpuRam */
	private boolean isStopListenCpuRam = false;
	/** 上一次的信号强度 */
	private int state_lastStrength;
	/** 上一次的networkType */
	private int state_lastNetworkType;
	/** 上一次的小区ID */
	private int state_lastCellID;
	/** 上一次的连接状态 */
	private int state_lastConnect;
	/** phoneState数据列表 */
	private List<String> phoneStateList = new ArrayList<String>();
	/** cpu和ram数据列表 */
	private List<String> cpuRamList = new ArrayList<String>();
	/** log保存目录 */
	private String logDir = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator + "testcase";
	/** log压缩目录 */
	private String logZipDir = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator + "testcaseZip";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		this.setContentView(R.layout.listener);
		stateTV = (TextView) this.findViewById(R.id.listener_TV);
		stateSB = new StringBuffer();
		asyncQuery = new CalllogAsyncQueryHandler(context.getContentResolver());
		// //设置来电监听
		// if (phoneStateListener == null) {
		// phoneStateListener = new PhoneStateListener_ETD();
		// telephonyManager = (TelephonyManager)
		// context.getSystemService(Service.TELEPHONY_SERVICE);
		// telephonyManager.listen(phoneStateListener,
		// PhoneStateListener.LISTEN_CALL_STATE |
		// PhoneStateListener.LISTEN_SIGNAL_STRENGTHS |
		// PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
		// PhoneStateListener.LISTEN_SERVICE_STATE |
		// PhoneStateListener.LISTEN_CELL_LOCATION);
		// }
		// //定时输出 信号强度、网络类型、小区id
		// new Thread(new Runnable_state()).start();
		// //定时输出 cpu占用率/内存占用率
		// new Thread(new Runnable_cpuRam()).start();
		// 监听去电
		new ReadLogUtil(context).start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// this.compress();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// System.out.println("onDestroy");
		// if (telReceiver != null) {
		// context.unregisterReceiver(telReceiver);
		// }
		isStopListenStrength = true; // 停止输出信号强度、网络类型、小区id等
		isStopListenCpuRam = true; // 停止输出cpu
		if (telephonyManager != null) {
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------
	// 呼叫状态的时间点
	// /**
	// * 监听来电/去电
	// */
	// private class BroadcastReceiver_tel extends BroadcastReceiver {
	// @Override
	// public void onReceive(final Context context, Intent intent) {
	// if (phoneStateListener == null) {
	// phoneStateListener = new PhoneStateListener_ETD();
	// telephonyManager = (TelephonyManager)
	// context.getSystemService(Service.TELEPHONY_SERVICE);
	// telephonyManager.listen(phoneStateListener,
	// PhoneStateListener.LISTEN_CALL_STATE);
	// }
	// }
	// }

	/**
	 * 监听手机状态
	 */
	private class PhoneStateListener_ETD extends PhoneStateListener {

		/**
		 * 通话状态改变
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// state 当前状态 incomingNumber,貌似没有去电的API
			super.onCallStateChanged(state, incomingNumber);
			String type = incomingNumber == null || incomingNumber.equals("") ? "去电" : "来电";
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				System.out.println(type + " 挂断：" + getSystemTime() + "\n");
				stateSB.append(type + " 挂断：" + getSystemTime() + "\n");
				if (type.equals("去电")) {
					idleTime = System.currentTimeMillis();
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							asyncQuery.startQuery(0, null, uri, fieldArr, null, null,
									CallLog.Calls.DEFAULT_SORT_ORDER);
						}
					}).start();
				}
				sendMesage();
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				System.out.println(type + " 接听：" + getSystemTime() + "\n");
				stateSB.append(type + " 接听：" + getSystemTime() + "\n");
				sendMesage();
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println(type + " 响铃 - 来电号码：" + incomingNumber + " - " + getSystemTime()
						+ "\n"); // 输出来电号码
				stateSB.append(type + " 响铃 - 来电号码：" + incomingNumber + getSystemTime() + "\n");
				sendMesage();
				break;
			}
		}

		/**
		 * 信号强度改变时调用
		 */
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			// 获取LTE信号的Dbm
			Integer strength = 0;
			try {
				Method method = signalStrength.getClass().getDeclaredMethod("getDbm");
				strength = (Integer) method.invoke(signalStrength);
				// System.out.println("Dbm："+ "Signal strength: " + strength);
				// 若信号强度变化了，则记录当前的信号强度
				if (strength != state_lastStrength) {
					state_lastStrength = strength;
					// System.out.println("***************************总状态："+
					// state_lastNetworkType+ " - "+ state_lastCellID+ " - "+
					// state_lastConnect);
				}
				// Toast.makeText(context, "dbm："+ strength,
				// Toast.LENGTH_SHORT).show();
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

		@Override
		public void onDataActivity(int direction) {
			super.onDataActivity(direction);
		}

		@Override
		public void onDataConnectionStateChanged(int state) {
			super.onDataConnectionStateChanged(state);
		}

		/**
		 * networkType改变时调用（一部华为双卡手机测试无效）<br>
		 * 必须有数据交互才能调用
		 */
		@Override
		public void onDataConnectionStateChanged(int state, int networkType) {
			super.onDataConnectionStateChanged(state, networkType);
			// System.out.println("=======================连接状态："+ state);
			// 若连接状态变化了，则记录当前的连接状态
			if (state != state_lastConnect) {
				state_lastConnect = state;
				// System.out.println("***************************总状态："+
				// state_lastNetworkType+ " - "+ state_lastCellID+ " - "+
				// state_lastConnect);
				// 记录状态
				// writeStateToFile();
			}
		}

		/**
		 * 小区信息变化
		 */
		@Override
		public void onCellLocationChanged(CellLocation location) {
			super.onCellLocationChanged(location);
			int lac = ((GsmCellLocation) location).getLac();
			int cellId = ((GsmCellLocation) location).getCid();
			// System.out.println("=======================小区ID：监听LAC = " + lac +
			// "\t CID = " + cellId);
			// 若小区ID变化了，则记录当前的小区ID
			if (cellId != state_lastCellID) {
				state_lastCellID = cellId;
				// System.out.println("***************************总状态："+
				// state_lastNetworkType+ " - "+ state_lastCellID+ " - "+
				// state_lastConnect);
				// 记录状态
				// writeStateToFile();
			}
		}

		/**
		 * 设备服务状态改变时调用
		 */
		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			super.onServiceStateChanged(serviceState);
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			int state = tm.getNetworkType();
			// System.out.println("=======================网络制式："+ state);
			// 若网络制式发生改变，则记录当前的网络制式
			if (state != state_lastNetworkType) {
				state_lastNetworkType = state;
				// System.out.println("***************************总状态："+
				// state_lastNetworkType+ " - "+ state_lastCellID+ " - "+
				// state_lastConnect);
				// 记录状态
				// writeStateToFile();
			}
		}

	}

	/**
	 * 读取通话记录
	 */
	private class CalllogAsyncQueryHandler extends AsyncQueryHandler {
		public CalllogAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			// 若已经有通话记录，则取最近一次通话记录
			if (cursor != null && cursor.getCount() > 0) {
				// System.out.println("================= 读取 =================");
				// SimpleDateFormat sfd = new
				// SimpleDateFormat("MM-dd hh:mm:ss");
				SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date;
				cursor.moveToFirst(); // 游标移动到第一项
				cursor.moveToPosition(0);
				date = new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
				String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
				int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
				String cachedName = cursor.getString(cursor
						.getColumnIndex(CallLog.Calls.CACHED_NAME));// 缓存的名称与电话号码，如果它的存在
				int id = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
				if (null == cachedName || "".equals(cachedName)) {
				}
				// StringBuilder sb = new StringBuilder();
				// sb.append("最近一次通话记录：\n");
				// sb.append("DATE："+ sfd.format(date)+ "\n");
				// sb.append("NUMBER："+ number+ "\n");
				// sb.append("TYPE（INCOMING = 1；OUTGOING = 2；MISSED = 3）："+
				// type+ "\n");
				// sb.append("CACHED_NAME："+ cachedName+ "\n");
				// sb.append("_ID："+ id+ "\n");
				// sb.append("DURATION："+
				// cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))+
				// "\n");
				String thisCallTime = sfd.format(date);
				String thisDuration = cursor.getString(cursor
						.getColumnIndex(CallLog.Calls.DURATION));
				long offhookTime = idleTime - Integer.parseInt(thisDuration) * 1000;
				String offhookDateTime = sfd.format(offhookTime);
				System.out.println("去电通话记录：" + offhookDateTime);
			}
			super.onQueryComplete(token, cookie, cursor);
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------
	// 信号强度
	/**
	 * 循环间隔一定时间获取一次信号强度
	 */
	private class Runnable_state implements Runnable {
		@Override
		public void run() {
			while (true) {
				// 若停止监听，则退出循环
				if (isStopListenStrength == true) {
					break;
				}
				String phoneStateString = getSystemTime() + " ::::: 信号强度：" + state_lastStrength
						+ "；小区ID：" + state_lastCellID + "；networkType 网络类型："
						+ state_lastNetworkType;
				System.out.println(phoneStateString);
				if (phoneStateList.size() < 20) {
					phoneStateList.add(phoneStateString);
				} else {
					writeDataToFile(phoneStateList, "phoneState.txt");
					phoneStateList.clear();
				}
				// state_signalList.add(state_lastStrength);
				// 每隔10秒取一次信号强度
				try {
					Thread.sleep(3 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 循环间隔一定时间获取cpu占用率/内存占用率
	 */
	private class Runnable_cpuRam implements Runnable {
		@Override
		public void run() {
			while (true) {
				// 若停止监听，则退出循环
				if (isStopListenCpuRam == true) {
					break;
				}
				long[] CupSizeArr = PluginUtil.getInstance().getCupSizes();
				String cpuRate = Math.round(CupSizeArr[2]) + "%";
				// G转换为M
				double totleMem = Double.parseDouble(FnUtil.getInstance().getNumFromString(
						PluginUtil.getInstance().getRamList(context).get(0)));
				if (PluginUtil.getInstance().getRamList(context).get(0).contains("G")) {
					totleMem = totleMem * 1024;
				} else if (PluginUtil.getInstance().getRamList(context).get(0).contains("M")) {
					totleMem = totleMem * 1;
				} else {
					totleMem = totleMem / 1024;
				}
				String getAvailMem = PluginUtil.getInstance().getRamList(context).get(1);
				double availMem = Double.parseDouble(FnUtil.getInstance().getNumFromString(
						getAvailMem));
				// System.out.println("totleMem："+ totleMem+ "； - availMem："+
				// availMem);
				if (getAvailMem.contains("G")) {
					availMem = availMem * 1024;
				} else if (getAvailMem.contains("M")) {
					availMem = availMem * 1;
				} else {
					availMem = availMem / 1024;
				}
				String ramRate = Math.round((totleMem - availMem) / totleMem * 100.0f) + "%";
				// System.out.println("cpu占用率："+ cpuRate+ "；内存占用率："+ ramRate);
				// state_cpuList.add(Math.round(CupSizeArr[2]));
				// state_ramList.add((int) Math.round((totleMem-
				// availMem)/totleMem*100.0f));
				// FnUtil.getInstance().showToastOnUIThread(context, "占用率："+
				// cpuRate);
				String cpuRamString = getSystemTime() + " ::::: cpu占用率：" + cpuRate + "；内存占用率："
						+ ramRate;
				System.out.println(cpuRamString);
				if (cpuRamList.size() < 20) {
					cpuRamList.add(cpuRamString);
				} else {
					writeDataToFile(cpuRamList, "cpuRam.txt");
					cpuRamList.clear();
				}
				// 每隔5秒取一次
				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------
	// 功能方法
	/**
	 * 页面输出
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				stateTV.setText(msg.getData().getString("data"));
			}
		}
	};

	/**
	 * 发送刷新页面命令
	 */
	private void sendMesage() {
		Message msg = handler.obtainMessage();
		msg.what = 1;
		Bundle bun = new Bundle();
		bun.putString("data", stateSB.toString());
		msg.what = 1;
		msg.setData(bun);
		handler.sendMessage(msg);
	}

	/**
	 * 获取当前时间
	 */
	private String getSystemTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}

	/**
	 * 将信息保存进文件
	 */
	private boolean writeDataToFile(List<String> cuslist, String fileName) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		File file = new File(logDir + File.separator + fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				return false;
			}
		}
		try {
			fw = new FileWriter(file, true); // true表示追加
			bw = new BufferedWriter(fw);
			for (String str : cuslist) {
				bw.write(str + "\n");
			}
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 压缩点击事件
	 * 
	 * @param v
	 */
	private void compress() {
		File zipFile = new File(logZipDir);// 创建保存zip文件
		if (!zipFile.exists()) {
			zipFile.mkdirs();
		}
		ZipUtil mZipControl = new ZipUtil();
		try {
			String[] logDirArr = { logDir };
			mZipControl.writeByApacheZipOutputStream(logDirArr, logZipDir + "/testcase.zip",
					"压缩包注释");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Toast.makeText(getApplicationContext(), "已经压缩成功!", 0).show();
	}

}
