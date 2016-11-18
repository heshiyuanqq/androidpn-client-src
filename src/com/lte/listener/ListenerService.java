package com.lte.listener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.Time;
import android.widget.Toast;

import com.lte.R;
import com.lte.config.ConfigTest;
import com.lte.util.ToolUtil;

public class ListenerService extends Service {

	private Context context;
	private static final String TAG = "ListenerService";

	public static final String SERVICE_NAME = "com.resarch.listener.Listener.Research";

	// JSON key, 从setup.json文件中读取, log采样间隔
	private static final String JSON_CPULOG_INTERVAL = "CpuLogInterval";
	private static final String JSON_MEMLOG_INTERVAL = "MemLogInterval";
	private static final String JSON_SIGNALLOG_INTERVAL = "SignalLogInterval";
	private static final String JSON_NETWORKLOG_INTERVAL = "NetworkLogInterval";
	private static final String JSON_POWERLOG_INTERVAL = "PowerLogInterval";

	// log文件路径
	private String SIGNALLOGFILE_DIR;
	private String CPULOGFILE_DIR;
	private String MEMLOGFILE_DIR;
	private String NETWORKLOGFILE_DIR;
	private String POWERLOGFILE_DIR;
	private String DEVICEINFOFILE_DIR;

	// log文件名
	private String SIGNALLOGFILE;
	private String CPULOGFILE;
	private String MEMLOGFILE;
	private String NETWORKLOGFILE;
	private String POWERLOGFILE;
	private String DEVICEINFOFILE;

	private String mJSON_SETUP_FILENAME = "/sdcard/testcase/setup.json"; // setup.json文件名和路径

	// 手机参数
	private String mIMEI;
	private String mIMSI;
	private String mMODEL;
	private String mManufacturer;
	private String mIMEISV;
	private String mSerial;
	private String mMSISDN;
	private String mMCCMNC;
	private String mNWOPName;
	private String mSIMMCCMNC;
	private String mSIMOPName;
	private String mSIMSerial;
	private String mRadioSignalStrength = "???"; // for GSM/CDMA/EVDO
	private String mTds = "???";// for TD-SCDMA
	private String mLteSignalStrength = "???"; // for LTE
	private String mLteRsrp = "???"; // for LTE
	private String mLteRsrq = "???"; // for LTE
	private String mLteRssnr = "???"; // for LTE
	private String mLteCqi = "???"; // for LTE
	private String mPowerSource = "???";
	private boolean mIsRoaming;
	private int mCellID;
	private int mNetworkType;
	private int mPhoneType;
	private int mDataState;
	private int mBatteryTemp = -1;
	private int mBatteryLevel = -1;
	private int mCpuLogInterval = 1; // default 1s
	private int mMemLogInterval = 1; // default 1s
	private int mSignalLogInterval = 1; // default 1s
	private int mNetworkLogInterval = 1; // default 1s
	private int mPowerLogInterval = 1; // default 1s
	private int mCpuCount;
	private int mMemCount;
	private int mSignalCount;
	private int mNetworkCount;
	private int mPowerCount;
	private long mLastTotalCPUTime = 0;
	private long mNowTotalCPUTime = 0;
	private long mLastIdleCPUTime = 0;
	private long mNowIdleCPUTime = 0;
	private long mLastTotalCPU0Time = 0;
	private long mNowTotalCPU0Time = 0;
	private long mLastIdleCPU0Time = 0;
	private long mNowIdleCPU0Time = 0;
	private long mLastTotalCPU1Time = 0;
	private long mNowTotalCPU1Time = 0;
	private long mLastIdleCPU1Time = 0;
	private long mNowIdleCPU1Time = 0;
	private long mLastTotalCPU2Time = 0;
	private long mNowTotalCPU2Time = 0;
	private long mLastIdleCPU2Time = 0;
	private long mNowIdleCPU2Time = 0;
	private long mLastTotalCPU3Time = 0;
	private long mNowTotalCPU3Time = 0;
	private long mLastIdleCPU3Time = 0;
	private long mNowIdleCPU3Time = 0;
	private long mLastTotalCPU4Time = 0;
	private long mNowTotalCPU4Time = 0;
	private long mLastIdleCPU4Time = 0;
	private long mNowIdleCPU4Time = 0;
	private long mLastTotalCPU5Time = 0;
	private long mNowTotalCPU5Time = 0;
	private long mLastIdleCPU5Time = 0;
	private long mNowIdleCPU5Time = 0;
	private long mLastTotalCPU6Time = 0;
	private long mNowTotalCPU6Time = 0;
	private long mLastIdleCPU6Time = 0;
	private long mNowIdleCPU6Time = 0;
	private long mLastTotalCPU7Time = 0;
	private long mNowTotalCPU7Time = 0;
	private long mLastIdleCPU7Time = 0;
	private long mNowIdleCPU7Time = 0;
	private long mTotalMem = 0;
	private long mFreeMem = 0;
	private long mMemUsageRatio = 0;
	private float mBatteryVolt = -1;
	private float mCPUUsageRatio = 0;
	private float mCPU0UsageRatio = 0;
	private float mCPU1UsageRatio = 0;
	private float mCPU2UsageRatio = 0;
	private float mCPU3UsageRatio = 0;
	private float mCPU4UsageRatio = 0;
	private float mCPU5UsageRatio = 0;
	private float mCPU6UsageRatio = 0;
	private float mCPU7UsageRatio = 0;
	private MyPhoneStateListener mMyPhoneStateListener;
	private BatteryInfoReceiver mBatteryInfoReceiver;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.context = this;

		// log文件路径
		// SIGNALLOGFILE_DIR = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME +
		// "/";
		CPULOGFILE_DIR = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/";
		MEMLOGFILE_DIR = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/";
		NETWORKLOGFILE_DIR = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/";
		POWERLOGFILE_DIR = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/";
		DEVICEINFOFILE_DIR = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/";

		// log文件名
		// SIGNALLOGFILE = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/"
		// + "signallog.csv";
		CPULOGFILE = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/" + "cpulog.csv";
		MEMLOGFILE = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/" + "memlog.csv";
		NETWORKLOGFILE = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/" + "networklog.csv";
		POWERLOGFILE = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/" + "powerlog.csv";
		DEVICEINFOFILE = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/" + "deviceinfo.csv";

		this.initial();
		intervalHandler.sendEmptyMessage(2);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// 完成测试, 退出agent时解除注册监控电池信息receiver
		// unregister battery state receiver
		if (mBatteryInfoReceiver != null) {
			context.unregisterReceiver(mBatteryInfoReceiver);
			mBatteryInfoReceiver = null;
		}
		// 退出agent时解除注册监听手机状态的listener
		// unregister phone state listener
		if (mMyPhoneStateListener != null) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			tm.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_NONE);
			tm = null;
			mMyPhoneStateListener = null;
		}
		// 停止定时输出
		intervalHandler.removeMessages(2);
	}

	/**
	 * 定时执行Handler
	 */
	Handler intervalHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// Log.d(TAG, "定时执行");
			if (msg.what == 2) {
				try {
					getDeviceInfo();
					writeLogFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			intervalHandler.sendEmptyMessageDelayed(2, 1000);
		}
	};

	/**
	 * 初始化时执行
	 */
	private void initial() {
		// check and prepare the log files
		// ---cpu log file
		ArrayList<String> als;
		String titleLine;
		als = ToolUtil.readTXTFile(CPULOGFILE);
		if (als == null) {
			titleLine = "TimeStamp(ms),DateTime,CPU(%),CPU0,CPU1(%),CPU2(%),CPU3(%),CPU4(%),CPU5(%),CPU6(%),CPU7(%)";
			als = new ArrayList<String>();
			als.add(titleLine);
			ToolUtil.writeTXTFile(als, CPULOGFILE_DIR, CPULOGFILE);
		}
		// ---memory log file
		als = ToolUtil.readTXTFile(MEMLOGFILE);
		if (als == null) {
			titleLine = "TimeStamp(ms),DateTime,TotalMem(MB),FreeMem(MB),MemUsageRatio(%)";
			als = new ArrayList<String>();
			als.add(titleLine);
			ToolUtil.writeTXTFile(als, MEMLOGFILE_DIR, MEMLOGFILE);
		}
		// ---signal log file
		/*
		 * als = ToolUtil.readTXTFile(SIGNALLOGFILE); if (als == null) {
		 * titleLine =
		 * "TimeStamp(ms),DateTime,GSM(dbm),LTE(dbm),LTE-RSRP,LTE-RSRQ,LTE-RSSNR,LTE-CQI"
		 * ; als = new ArrayList<String>(); als.add(titleLine);
		 * ToolUtil.writeTXTFile(als, SIGNALLOGFILE_DIR, SIGNALLOGFILE); }
		 */
		// ---network log file
		// als = ToolUtil.readTXTFile(NETWORKLOGFILE);
		// if (als == null) {
		// // titleLine =
		// //
		// "TimeStamp(ms),DateTime,MSISDN,MCCMNC,NWOPName,CellID,NetworkType,PhoneType,DataState,Roaming,GSM(dbm),TDS(dbm),LTE(dbm),LTE-RSRP,LTE-RSRQ,LTE-RSSNR,LTE-CQI";
		// titleLine =
		// "TimeStamp(ms),DateTime,MCCMNC,NWOPName,CellID,NetworkType,PhoneType,DataState,Roaming,GSM(dbm),TDS(dbm),LTE(dbm)";
		//
		// als = new ArrayList<String>();
		// als.add(titleLine);
		// ToolUtil.writeTXTFile(als, NETWORKLOGFILE_DIR, NETWORKLOGFILE);
		// }
		// ---power log file
		als = ToolUtil.readTXTFile(POWERLOGFILE);
		if (als == null) {
			titleLine = "TimeStamp(ms),DateTime,PowerSource,BatteryLevel(%),BatteryVolt(V),BatteryTemp(C)";
			als = new ArrayList<String>();
			als.add(titleLine);
			ToolUtil.writeTXTFile(als, POWERLOGFILE_DIR, POWERLOGFILE);
		}
		// ---device info file
		titleLine = "Manufacturer,Model,AndroidVersion,Serial,IMEI,IMEI/SV,IMSI,SIMMCCMNC,SIMOPName,SIMSerial";
		// mMODEL = mMODEL.replaceAll(" ", "-");
		TelephonyManager teleMa = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		mManufacturer = ToolUtil.getDeviceManufacturer();
		mMODEL = ToolUtil.getDeviceModel();
		mSerial = ToolUtil.getDeviceSerial();
		mIMEI = teleMa.getDeviceId();
		mIMEISV = teleMa.getDeviceSoftwareVersion();
		mIMSI = teleMa.getSubscriberId();
		mSIMMCCMNC = teleMa.getSimOperator();
		mSIMOPName = teleMa.getSimOperatorName();
		mSIMSerial = teleMa.getSimSerialNumber();
		String contentLine = mManufacturer + "," + mMODEL + "," + Build.VERSION.RELEASE + ","
				+ mSerial + "," + mIMEI + "," + mIMEISV + "," + mIMSI + "," + mSIMMCCMNC + ","
				+ mSIMOPName + "," + mSIMSerial;
		als = new ArrayList<String>();
		als.add(titleLine);
		als.add(contentLine);
		ToolUtil.writeTXTFile(als, DEVICEINFOFILE_DIR, DEVICEINFOFILE);
		// read the log record interval from setup.json
		JSONArray array = ToolUtil.readJSONFile(mJSON_SETUP_FILENAME);
		if (array == null) {
			Toast.makeText(context, "setup.json为空", Toast.LENGTH_SHORT).show();
		} else {
			try {
				JSONObject json = array.getJSONObject(0);
				mCpuLogInterval = json.getInt(JSON_CPULOG_INTERVAL);
				mMemLogInterval = json.getInt(JSON_MEMLOG_INTERVAL);
				mSignalLogInterval = json.getInt(JSON_SIGNALLOG_INTERVAL);
				mNetworkLogInterval = json.getInt(JSON_NETWORKLOG_INTERVAL);
				mPowerLogInterval = json.getInt(JSON_POWERLOG_INTERVAL);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
			}
		}
		// register phone state listener
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		mMyPhoneStateListener = new MyPhoneStateListener();
		tm.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		try {
			// 完成初始化后, 开始测试前注册receiver监控电池信息
			// register battery state receiver
			mBatteryInfoReceiver = new BatteryInfoReceiver();
			context.registerReceiver(mBatteryInfoReceiver, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取手机参数的方法, 每秒调用一次
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void getDeviceInfo() throws IOException {
		// get the device infomation
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation gcl = (GsmCellLocation) tm.getCellLocation();
		// get phone IMEI
		mIMEI = tm.getDeviceId(); // SAMSUNG NOTE 2 OK! HUAWEI MATE 2 FAIL!
		// mIMEI = android.os.SystemProperties.get("gsm.imei");
		// get phone IMSI
		mIMSI = tm.getSubscriberId(); // SAMSUNG NOTE 2 OK! HUAWEI MATE 2 FAIL!
		// mIMSI = android.os.SystemProperties.get("gsm.sim.imsi");
		// get phone model
		mMODEL = ToolUtil.getDeviceModel();
		// get phone manufacture
		mManufacturer = ToolUtil.getDeviceManufacturer();
		// get phone IMEI SV
		mIMEISV = tm.getDeviceSoftwareVersion();
		// get phone serial
		mSerial = ToolUtil.getDeviceSerial();
		// get phone MSISDN
		mMSISDN = tm.getLine1Number();
		// get phone MCC+MNC
		mMCCMNC = tm.getNetworkOperator();
		// get phone Network Operator Name
		mNWOPName = tm.getNetworkOperatorName();
		// get cell id
		if (gcl != null) {
			mCellID = gcl.getCid();
		} else {
			mCellID = -1;
		}
		// get SIM MCC+MNC
		mSIMMCCMNC = tm.getSimOperator();
		// get SIM operator name
		mSIMOPName = tm.getSimOperatorName();
		// get SIM serial
		mSIMSerial = tm.getSimSerialNumber();
		// get phone network roaming state
		mIsRoaming = tm.isNetworkRoaming();
		// get network type
		mNetworkType = tm.getNetworkType();
		// get phone type
		mPhoneType = tm.getPhoneType();
		// get data connection state
		mDataState = tm.getDataState();
		// read the CPU usage time from the file: /proc/stat
		File file = new File("/proc/stat");
		FileReader fr = new FileReader(file);
		BufferedReader reader = new BufferedReader(fr);
		String[] sa = new String[9];
		for (int i = 0; i < 9; i++) {
			sa[i] = reader.readLine();
		}
		reader.close();
		String[] cpuInfo;
		String[] cpuInfo0;
		String[] cpuInfo1;
		String[] cpuInfo2;
		String[] cpuInfo3;
		String[] cpuInfo4;
		String[] cpuInfo5;
		String[] cpuInfo6;
		String[] cpuInfo7;
		cpuInfo = sa[0].split("\\s+");
		cpuInfo0 = sa[1].split("\\s+");
		cpuInfo1 = sa[2].split("\\s+");
		cpuInfo2 = sa[3].split("\\s+");
		cpuInfo3 = sa[4].split("\\s+");
		cpuInfo4 = sa[5].split("\\s+");
		cpuInfo5 = sa[6].split("\\s+");
		cpuInfo6 = sa[7].split("\\s+");
		cpuInfo7 = sa[8].split("\\s+");
		// parse total CPU usage ratio
		if ((cpuInfo[0].contains("cpu")) && (cpuInfo[0].length() <= 4)) {
			mNowTotalCPUTime = Long.parseLong(cpuInfo[1]) + Long.parseLong(cpuInfo[2])
					+ Long.parseLong(cpuInfo[3]) + Long.parseLong(cpuInfo[4])
					+ Long.parseLong(cpuInfo[5]) + Long.parseLong(cpuInfo[6])
					+ Long.parseLong(cpuInfo[7]);
			mNowIdleCPUTime = Long.parseLong(cpuInfo[4]); // cpu idle time
			mCPUUsageRatio = (((float) (mNowTotalCPUTime - mLastTotalCPUTime) - (float) (mNowIdleCPUTime - mLastIdleCPUTime)) * 100)
					/ (float) (mNowTotalCPUTime - mLastIdleCPUTime);
			mLastTotalCPUTime = mNowTotalCPUTime;
			mLastIdleCPUTime = mNowIdleCPUTime;
		}
		// parse CPU0 usage ratio
		if ((cpuInfo0[0].contains("cpu")) && (cpuInfo0[0].length() <= 4)) {
			mNowTotalCPU0Time = Long.parseLong(cpuInfo0[1]) + Long.parseLong(cpuInfo0[2])
					+ Long.parseLong(cpuInfo0[3]) + Long.parseLong(cpuInfo0[4])
					+ Long.parseLong(cpuInfo0[5]) + Long.parseLong(cpuInfo0[6])
					+ Long.parseLong(cpuInfo0[7]);
			mNowIdleCPU0Time = Long.parseLong(cpuInfo0[4]); // cpu idle time
			mCPU0UsageRatio = (((float) (mNowTotalCPU0Time - mLastTotalCPU0Time) - (float) (mNowIdleCPU0Time - mLastIdleCPU0Time)) * 100)
					/ (float) (mNowTotalCPU0Time - mLastIdleCPU0Time);
			mLastTotalCPU0Time = mNowTotalCPU0Time;
			mLastIdleCPU0Time = mNowIdleCPU0Time;
		} else {
			mNowTotalCPU0Time = 0;
			mNowIdleCPU0Time = 0;
			mLastTotalCPU0Time = 0;
			mLastIdleCPU0Time = 0;
			mCPU0UsageRatio = 0;
		}
		// parse CPU1 usage ratio
		if ((cpuInfo1[0].contains("cpu")) && (cpuInfo1[0].length() <= 4)) {
			mNowTotalCPU1Time = Long.parseLong(cpuInfo1[1]) + Long.parseLong(cpuInfo1[2])
					+ Long.parseLong(cpuInfo1[3]) + Long.parseLong(cpuInfo1[4])
					+ Long.parseLong(cpuInfo1[5]) + Long.parseLong(cpuInfo1[6])
					+ Long.parseLong(cpuInfo1[7]);
			mNowIdleCPU1Time = Long.parseLong(cpuInfo1[4]); // cpu idle time
			mCPU1UsageRatio = (((float) (mNowTotalCPU1Time - mLastTotalCPU1Time) - (float) (mNowIdleCPU1Time - mLastIdleCPU1Time)) * 100)
					/ (float) (mNowTotalCPU1Time - mLastIdleCPU1Time);
			mLastTotalCPU1Time = mNowTotalCPU1Time;
			mLastIdleCPU1Time = mNowIdleCPU1Time;
		} else {
			mNowTotalCPU1Time = 0;
			mNowIdleCPU1Time = 0;
			mLastTotalCPU1Time = 0;
			mLastIdleCPU1Time = 0;
			mCPU1UsageRatio = 0;
		}
		// parse CPU2 usage ratio
		if ((cpuInfo2[0].contains("cpu")) && (cpuInfo2[0].length() <= 4)) {
			mNowTotalCPU2Time = Long.parseLong(cpuInfo2[1]) + Long.parseLong(cpuInfo2[2])
					+ Long.parseLong(cpuInfo2[3]) + Long.parseLong(cpuInfo2[4])
					+ Long.parseLong(cpuInfo2[5]) + Long.parseLong(cpuInfo2[6])
					+ Long.parseLong(cpuInfo2[7]);
			mNowIdleCPU2Time = Long.parseLong(cpuInfo2[4]); // cpu idle time
			mCPU2UsageRatio = (((float) (mNowTotalCPU2Time - mLastTotalCPU2Time) - (float) (mNowIdleCPU2Time - mLastIdleCPU2Time)) * 100)
					/ (float) (mNowTotalCPU2Time - mLastIdleCPU2Time);
			mLastTotalCPU2Time = mNowTotalCPU2Time;
			mLastIdleCPU2Time = mNowIdleCPU2Time;
		} else {
			mNowTotalCPU2Time = 0;
			mNowIdleCPU2Time = 0;
			mLastTotalCPU2Time = 0;
			mLastIdleCPU2Time = 0;
			mCPU2UsageRatio = 0;
		}
		// parse CPU3 usage ratio
		if ((cpuInfo3[0].contains("cpu")) && (cpuInfo3[0].length() <= 4)) {
			mNowTotalCPU3Time = Long.parseLong(cpuInfo3[1]) + Long.parseLong(cpuInfo3[2])
					+ Long.parseLong(cpuInfo3[3]) + Long.parseLong(cpuInfo3[4])
					+ Long.parseLong(cpuInfo3[5]) + Long.parseLong(cpuInfo3[6])
					+ Long.parseLong(cpuInfo3[7]);
			mNowIdleCPU3Time = Long.parseLong(cpuInfo3[4]); // cpu idle time
			mCPU3UsageRatio = (((float) (mNowTotalCPU3Time - mLastTotalCPU3Time) - (float) (mNowIdleCPU3Time - mLastIdleCPU3Time)) * 100)
					/ (float) (mNowTotalCPU3Time - mLastIdleCPU3Time);
			mLastTotalCPU3Time = mNowTotalCPU3Time;
			mLastIdleCPU3Time = mNowIdleCPU3Time;
		} else {
			mNowTotalCPU3Time = 0;
			mNowIdleCPU3Time = 0;
			mLastTotalCPU3Time = 0;
			mLastIdleCPU3Time = 0;
			mCPU3UsageRatio = 0;
		}
		// parse CPU4 usage ratio
		if ((cpuInfo4[0].contains("cpu")) && (cpuInfo4[0].length() <= 4)) {
			mNowTotalCPU4Time = Long.parseLong(cpuInfo4[1]) + Long.parseLong(cpuInfo4[2])
					+ Long.parseLong(cpuInfo4[3]) + Long.parseLong(cpuInfo4[4])
					+ Long.parseLong(cpuInfo4[5]) + Long.parseLong(cpuInfo4[6])
					+ Long.parseLong(cpuInfo4[7]);
			mNowIdleCPU4Time = Long.parseLong(cpuInfo4[4]); // cpu idle time
			mCPU4UsageRatio = (((float) (mNowTotalCPU4Time - mLastTotalCPU4Time) - (float) (mNowIdleCPU4Time - mLastIdleCPU4Time)) * 100)
					/ (float) (mNowTotalCPU4Time - mLastIdleCPU4Time);
			mLastTotalCPU4Time = mNowTotalCPU4Time;
			mLastIdleCPU4Time = mNowIdleCPU4Time;
		} else {
			mNowTotalCPU4Time = 0;
			mNowIdleCPU4Time = 0;
			mLastTotalCPU4Time = 0;
			mLastIdleCPU4Time = 0;
			mCPU4UsageRatio = 0;
		}
		// parse CPU5 usage ratio
		if ((cpuInfo5[0].contains("cpu")) && (cpuInfo5[0].length() <= 4)) {
			mNowTotalCPU5Time = Long.parseLong(cpuInfo5[1]) + Long.parseLong(cpuInfo5[2])
					+ Long.parseLong(cpuInfo5[3]) + Long.parseLong(cpuInfo5[4])
					+ Long.parseLong(cpuInfo5[5]) + Long.parseLong(cpuInfo5[6])
					+ Long.parseLong(cpuInfo5[7]);
			mNowIdleCPU5Time = Long.parseLong(cpuInfo5[4]); // cpu idle time
			mCPU5UsageRatio = (((float) (mNowTotalCPU5Time - mLastTotalCPU5Time) - (float) (mNowIdleCPU5Time - mLastIdleCPU5Time)) * 100)
					/ (float) (mNowTotalCPU5Time - mLastIdleCPU5Time);
			mLastTotalCPU5Time = mNowTotalCPU5Time;
			mLastIdleCPU5Time = mNowIdleCPU5Time;
		} else {
			mNowTotalCPU5Time = 0;
			mNowIdleCPU5Time = 0;
			mLastTotalCPU5Time = 0;
			mLastIdleCPU5Time = 0;
			mCPU5UsageRatio = 0;
		}
		// parse CPU6 usage ratio
		if ((cpuInfo6[0].contains("cpu")) && (cpuInfo6[0].length() <= 4)) {
			mNowTotalCPU6Time = Long.parseLong(cpuInfo6[1]) + Long.parseLong(cpuInfo6[2])
					+ Long.parseLong(cpuInfo6[3]) + Long.parseLong(cpuInfo6[4])
					+ Long.parseLong(cpuInfo6[5]) + Long.parseLong(cpuInfo6[6])
					+ Long.parseLong(cpuInfo6[7]);
			mNowIdleCPU6Time = Long.parseLong(cpuInfo6[4]); // cpu idle time
			mCPU6UsageRatio = (((float) (mNowTotalCPU6Time - mLastTotalCPU6Time) - (float) (mNowIdleCPU6Time - mLastIdleCPU6Time)) * 100)
					/ (float) (mNowTotalCPU6Time - mLastIdleCPU6Time);
			mLastTotalCPU6Time = mNowTotalCPU6Time;
			mLastIdleCPU6Time = mNowIdleCPU6Time;
		} else {
			mNowTotalCPU6Time = 0;
			mNowIdleCPU6Time = 0;
			mLastTotalCPU6Time = 0;
			mLastIdleCPU6Time = 0;
			mCPU6UsageRatio = 0;
		}
		// parse CPU7 usage ratio
		if ((cpuInfo7[0].contains("cpu")) && (cpuInfo7[0].length() <= 4)) {
			mNowTotalCPU7Time = Long.parseLong(cpuInfo7[1]) + Long.parseLong(cpuInfo7[2])
					+ Long.parseLong(cpuInfo7[3]) + Long.parseLong(cpuInfo7[4])
					+ Long.parseLong(cpuInfo7[5]) + Long.parseLong(cpuInfo7[6])
					+ Long.parseLong(cpuInfo7[7]);
			mNowIdleCPU7Time = Long.parseLong(cpuInfo7[4]); // cpu idle time
			mCPU7UsageRatio = (((float) (mNowTotalCPU7Time - mLastTotalCPU7Time) - (float) (mNowIdleCPU7Time - mLastIdleCPU7Time)) * 100)
					/ (float) (mNowTotalCPU7Time - mLastIdleCPU7Time);
			mLastTotalCPU7Time = mNowTotalCPU7Time;
			mLastIdleCPU7Time = mNowIdleCPU7Time;
		} else {
			mNowTotalCPU7Time = 0;
			mNowIdleCPU7Time = 0;
			mLastTotalCPU7Time = 0;
			mLastIdleCPU7Time = 0;
			mCPU7UsageRatio = 0;
		}
		// get totle memory in MB
		file = new File("/proc/meminfo");
		fr = new FileReader(file);
		reader = new BufferedReader(fr);
		String line = reader.readLine(); // read the first line which contains
											// the total mem
		String[] totalMemInfo = line.split("\\s+");
		mTotalMem = Long.valueOf(totalMemInfo[1]) / 1024; // total mem in MB
		// get free memory in MB
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		/*
		 * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		 * mTotalMemFromMI = mi.totalMem / (1024 * 1024); // B -> MB } else {
		 * mTotalMemFromMI = 0; }
		 */
		mFreeMem = mi.availMem / (1024 * 1024); // B -> MB
		// get memory usage ratio
		if (mTotalMem > 0) {
			mMemUsageRatio = ((mTotalMem - mFreeMem) * 100) / mTotalMem;
		} else {
			mMemUsageRatio = 0;
		}
	}

	/**
	 * 写手机参数log的方法, 每秒调用一次, 在getDeviceInfo方法调用后调用
	 */
	@SuppressWarnings("deprecation")
	private void writeLogFile() {
		// called 1 time per second
		Time t = new Time();
		t.setToNow();
		long timeStamp = t.toMillis(false);
		String dateTime = ToolUtil.timeStamp2DateTime(t, true);
		ArrayList<String> al;
		// write cpu log file
		if (mCpuCount == 0) {
			al = new ArrayList<String>();
			String s = String.valueOf(timeStamp) + "," + dateTime + ","
					+ String.format("%.5f", mCPUUsageRatio) + ","
					+ String.format("%.5f", mCPU0UsageRatio) + ","
					+ String.format("%.5f", mCPU1UsageRatio) + ","
					+ String.format("%.5f", mCPU2UsageRatio) + ","
					+ String.format("%.5f", mCPU3UsageRatio) + ","
					+ String.format("%.5f", mCPU4UsageRatio) + ","
					+ String.format("%.5f", mCPU5UsageRatio) + ","
					+ String.format("%.5f", mCPU6UsageRatio) + ","
					+ String.format("%.5f", mCPU7UsageRatio);
			al.add(s);
			ToolUtil.appendTXTFile(al, CPULOGFILE);
		}
		mCpuCount++;
		if (mCpuCount >= mCpuLogInterval) {
			mCpuCount = 0;
		}
		// write mem log file
		if (mMemCount == 0) {
			al = new ArrayList<String>();
			String s = String.valueOf(timeStamp) + "," + dateTime + "," + mTotalMem + ","
					+ mFreeMem + "," + mMemUsageRatio;
			al.add(s);
			ToolUtil.appendTXTFile(al, MEMLOGFILE);
		}
		mMemCount++;
		if (mMemCount >= mMemLogInterval) {
			mMemCount = 0;
		}
		// write signal log file
		/*
		 * if (mSignalCount == 0) { al = new ArrayList<String>(); String s =
		 * String.valueOf(timeStamp) + "," + dateTime + "," +
		 * mRadioSignalStrength + "," + mLteSignalStrength + "," + mLteRsrp +
		 * "," + mLteRsrq + "," + mLteRssnr + "," + mLteCqi; al.add(s);
		 * ToolUtil.appendTXTFile(al, SIGNALLOGFILE); }
		 * 
		 * mSignalCount++;if (mSignalCount >= mSignalLogInterval) {mSignalCount
		 * = 0;}
		 */
		// write network log file
		if (mNetworkCount == 0) {
			al = new ArrayList<String>();
			// calculate cell ID
			String cellID = ToolUtil.CalcCellID(mNetworkType, mCellID);
			if (cellID == null) {
				cellID = "unknown";
			}
			// String s = String.valueOf(timeStamp) + "," + dateTime + "," +
			// mMSISDN + "," + mMCCMNC
			// + "," + mNWOPName + "," + cellID + "," +
			// parseNetworkType(mNetworkType) + ","
			// + parsePhoneType(mPhoneType) + "," + parseDataState(mDataState) +
			// ","
			// + mIsRoaming + "," + mRadioSignalStrength + "," + mTds + ","
			// + mLteSignalStrength + "," + mLteRsrp + "," + mLteRsrq + "," +
			// mLteRssnr + ","
			// + mLteCqi;
			String s = String.valueOf(timeStamp) + "," + dateTime + "," + mMCCMNC + "," + mNWOPName
					+ "," + cellID + "," + parseNetworkType(mNetworkType) + ","
					+ parsePhoneType(mPhoneType) + "," + parseDataState(mDataState) + ","
					+ mIsRoaming + "," + mRadioSignalStrength + "," + mTds + ","
					+ mLteSignalStrength;

			// al.add(s);
			// ToolUtil.appendTXTFile(al, NETWORKLOGFILE);

			NetworkLog(String.valueOf(timeStamp), dateTime, mMCCMNC, mNWOPName, cellID,
					mNetworkType, mPhoneType, mDataState, String.valueOf(mIsRoaming),
					mRadioSignalStrength, mTds, mLteSignalStrength);
		}
		mNetworkCount++;
		if (mNetworkCount >= mNetworkLogInterval) {
			mNetworkCount = 0;
		}
		// write power log file
		if (mPowerCount == 0) {
			al = new ArrayList<String>();
			String s = String.valueOf(timeStamp) + "," + dateTime + "," + mPowerSource + ","
					+ mBatteryLevel + "," + String.format("%.3f", mBatteryVolt) + ","
					+ mBatteryTemp;
			if (!mPowerSource.equals("???")) {
				al.add(s);
				ToolUtil.appendTXTFile(al, POWERLOGFILE);
			}
		}
		mPowerCount++;
		if (mPowerCount >= mPowerLogInterval) {
			mPowerCount = 0;
		}
	}

	/**
	 * listener, 监听手机信号强度变化
	 */
	private class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			// TODO Auto-generated method stub
			super.onSignalStrengthsChanged(signalStrength);
			int gsmSignalStrength = signalStrength.getGsmSignalStrength();

			String signal = signalStrength.toString();
			String[] parts = signal.split(" ");
			Build b = new Build();
			String model = b.MODEL;
			String manufacturer = b.MANUFACTURER;
			if (manufacturer.equals("HUAWEI")) {// 华为P6
				// 2G：index=1
				// 3G：index=3
				// 4G：index=11
				mRadioSignalStrength = parts[1];// 2G
				mTds = parts[3];// 3G
				mLteSignalStrength = parts[11];// 4G
			} else if (manufacturer.equals("Xiaomi")) {// 小米4
				// 2G- index=1（需要换算）
				// 3G- index=13
				// 4G-index=9
				mRadioSignalStrength = String.valueOf(-113 + (2 * Integer.parseInt((parts[1]))));// 2G
				mTds = parts[13];// 3G
				mLteSignalStrength = parts[9];// 4G
			} else if (manufacturer.equals("samsung")) {// 三星S5
				// 2G-使用原API，需要换算
				// 3G- index=13 (dbm，无需换算）
				// 4G-index=9 (dbm，无需换算）
				mRadioSignalStrength = String.valueOf(-113 + (2 * gsmSignalStrength));
				mTds = parts[13];
				mLteSignalStrength = parts[9];
			} else if (manufacturer.equals("CMDC")) {// 移动N1
				// 2G-使用原API，需要换算
				// 3G- index=13 (dbm，无需换算）
				// 4G- index=9 (dbm，无需换算）
				mRadioSignalStrength = String.valueOf(-113 + (2 * gsmSignalStrength));
				mTds = parts[13];
				mLteSignalStrength = parts[9];
			}

			/*
			 * String radioSignalStrength = String.valueOf(0); int
			 * lteSignalStrength = 0; int lteRsrp = 0; int lteRsrq = 0; int
			 * lteRssnr = 0; int lteCqi = 0; TelephonyManager tm =
			 * (TelephonyManager) context
			 * .getSystemService(Context.TELEPHONY_SERVICE); if
			 * (signalStrength.isGsm() || (tm.getNetworkType() ==
			 * TelephonyManager.NETWORK_TYPE_LTE)) { // get GSM signal strength
			 * int gsmSignalStrength = signalStrength.getGsmSignalStrength(); if
			 * (signalStrength.isGsm() && (gsmSignalStrength != 99)) {
			 * radioSignalStrength = String.valueOf(-113 + (2 *
			 * gsmSignalStrength)); // dbm } else { radioSignalStrength = "???";
			 * } mRadioSignalStrength = radioSignalStrength; // get LTE signal
			 * strength lteSignalStrength =
			 * Integer.parseInt(getSpecifiedFieldValues(SignalStrength.class,
			 * signalStrength, "mLteSignalStrength")); lteRsrp =
			 * Integer.parseInt(getSpecifiedFieldValues(SignalStrength.class,
			 * signalStrength, "mLteRsrp")); lteRsrq =
			 * Integer.parseInt(getSpecifiedFieldValues(SignalStrength.class,
			 * signalStrength, "mLteRsrq")); lteRssnr =
			 * Integer.parseInt(getSpecifiedFieldValues(SignalStrength.class,
			 * signalStrength, "mLteRssnr")); lteCqi =
			 * Integer.parseInt(getSpecifiedFieldValues(SignalStrength.class,
			 * signalStrength, "mLteCqi")); mLteSignalStrength =
			 * String.valueOf(-113 + (2 * lteSignalStrength)); // dbm mLteRsrp =
			 * String.valueOf(lteRsrp); mLteRsrq = String.valueOf(lteRsrq);
			 * mLteRssnr = String.format("%.1f", (float) lteRssnr / 10); mLteCqi
			 * = String.valueOf(lteCqi); } else if (tm.getNetworkType() ==
			 * TelephonyManager.NETWORK_TYPE_CDMA) { mRadioSignalStrength =
			 * String.valueOf(signalStrength.getCdmaDbm()); mLteSignalStrength =
			 * "???"; mLteRsrp = "???"; mLteRsrq = "???"; mLteRssnr = "???";
			 * mLteCqi = "???"; } else if ((tm.getNetworkType() ==
			 * TelephonyManager.NETWORK_TYPE_EVDO_0) || (tm.getNetworkType() ==
			 * TelephonyManager.NETWORK_TYPE_EVDO_A) || (tm.getNetworkType() ==
			 * TelephonyManager.NETWORK_TYPE_EVDO_B)) { mRadioSignalStrength =
			 * String.valueOf(signalStrength.getEvdoDbm()); mLteSignalStrength =
			 * "???"; mLteRsrp = "???"; mLteRsrq = "???"; mLteRssnr = "???";
			 * mLteCqi = "???"; }
			 */

		}
	}

	/**
	 * 信号强度变化时，利用反射获取各个值
	 * 
	 * @param mClass
	 * @param mInstance
	 * @param fieldName
	 * @return
	 */
	private final String getSpecifiedFieldValues(Class<?> mClass, Object mInstance, String fieldName) {
		String fieldValue = "";

		if (mClass == null || mInstance == null || fieldName == null)
			return fieldValue;

		try {
			final Field field = mClass.getDeclaredField(fieldName);

			if (field != null) {
				field.setAccessible(true);
				fieldValue = field.get(mInstance).toString();
			}

		} catch (NoSuchFieldException exp) {
			fieldValue = "";
		} catch (IllegalAccessException ile) {
			fieldValue = "";
		}
		return fieldValue;
	}

	/**
	 * 根据读取参数解析网络类型的方法
	 * 
	 * @param nwtype
	 * @return
	 */
	private String parseNetworkType(int nwtype) {
		String networkType;
		switch (nwtype) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:// 7
			networkType = context.getString(R.string.network_type_1xrtt);
			break;
		case TelephonyManager.NETWORK_TYPE_CDMA:// 4
			networkType = context.getString(R.string.network_type_cdma);
			break;
		case TelephonyManager.NETWORK_TYPE_EDGE:// 2
			networkType = context.getString(R.string.network_type_edge);
			break;
		case TelephonyManager.NETWORK_TYPE_EHRPD:// 14
			networkType = context.getString(R.string.network_type_ehrpd);
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:// 5
			networkType = context.getString(R.string.network_type_evdo_0);
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:// 6
			networkType = context.getString(R.string.network_type_evdo_a);
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:// 12
			networkType = context.getString(R.string.network_type_evdo_b);
			break;
		case TelephonyManager.NETWORK_TYPE_GPRS:// 1
			networkType = context.getString(R.string.network_type_gprs);
			break;
		case TelephonyManager.NETWORK_TYPE_HSDPA:// 8
			networkType = context.getString(R.string.network_type_hsdpa);
			break;
		case TelephonyManager.NETWORK_TYPE_HSPA:// 10
			networkType = context.getString(R.string.network_type_hspa);
			break;
		case TelephonyManager.NETWORK_TYPE_HSPAP:// 15
			networkType = context.getString(R.string.network_type_hspap);
			break;
		case TelephonyManager.NETWORK_TYPE_HSUPA:// 9
			networkType = context.getString(R.string.network_type_hsupa);
			break;
		case TelephonyManager.NETWORK_TYPE_IDEN:// 11
			networkType = context.getString(R.string.network_type_iden);
			break;
		case TelephonyManager.NETWORK_TYPE_LTE:// 13
			networkType = context.getString(R.string.network_type_lte);
			break;
		case TelephonyManager.NETWORK_TYPE_UMTS:// 3
			networkType = context.getString(R.string.network_type_umts);
			break;
		case 17:// 17
			// networkType = context.getString(R.string.network_type_td_scdma);
			networkType = "TD-SCDMA";
			break;
		case 18:// 18
			networkType = "TD-SCDMA";
			break;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:// 0
			networkType = context.getString(R.string.network_type_unknown);
			break;
		default:
			// networkType = "???";
			// 打电话时默认为2G
			networkType = "gsm";
		}
		return networkType;
	}

	/**
	 * 根据读取参数解析手机类型的方法
	 * 
	 * @param ptype
	 * @return
	 */
	private String parsePhoneType(int ptype) {
		String phoneType;
		switch (ptype) {
		case TelephonyManager.PHONE_TYPE_CDMA:
			phoneType = context.getString(R.string.phone_type_cdma);
			break;
		case TelephonyManager.PHONE_TYPE_GSM:
			phoneType = context.getString(R.string.phone_type_gsm);
			break;
		case TelephonyManager.PHONE_TYPE_NONE:
			phoneType = context.getString(R.string.phone_type_none);
			break;
		case TelephonyManager.PHONE_TYPE_SIP:
			phoneType = context.getString(R.string.phone_type_sip);
			break;
		default:
			phoneType = "???";
		}
		return phoneType;
	}

	/**
	 * 根据读取参数解析数据连接状态的方法
	 * 
	 * @param dstate
	 * @return
	 */
	private String parseDataState(int dstate) {
		String dataState;
		switch (dstate) {
		case TelephonyManager.DATA_CONNECTED:
			dataState = context.getString(R.string.data_connected);
			break;
		case TelephonyManager.DATA_CONNECTING:
			dataState = context.getString(R.string.data_connecting);
			break;
		case TelephonyManager.DATA_DISCONNECTED:
			dataState = context.getString(R.string.data_disconnected);
			break;
		case TelephonyManager.DATA_SUSPENDED:
			dataState = context.getString(R.string.data_suspended);
			break;
		default:
			dataState = "???";
		}
		return dataState;
	}

	/**
	 * receiver, 监听电池信息
	 */
	private class BatteryInfoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			mBatteryTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10; // C
			int rawlevel;
			int scale;
			rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			if ((rawlevel >= 0) && (scale > 0)) {
				mBatteryLevel = (rawlevel * 100) / scale;
			} else {
				mBatteryLevel = -1;
			}
			mBatteryVolt = ((float) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)) / 1000; // V
			int status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			if (status != -1) {
				switch (status) {
				case 0:
					mPowerSource = "0 means it is on battery";
					break;
				case BatteryManager.BATTERY_PLUGGED_AC:
					mPowerSource = "交流充电";
					break;
				case BatteryManager.BATTERY_PLUGGED_USB:
					mPowerSource = "USB充电";
					break;
				}
			}
		}
	}

	private void NetworkLog(String timeStamp, String dateTime, String mMCCMNC, String mNWOPName,
			String cellID, int mNetworkType, int mPhoneType, int mDataState, String mIsRoaming,
			String mRadioSignalStrength, String mTds, String mLteSignalStrength) {

		// NETWORKLOGFILE = "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/"
		// + "networklog.csv";
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/sdcard/testcase/"
				+ ConfigTest.LOG_FILE_NAME + ".db", null);

		try {
			db = SQLiteDatabase.openDatabase(
					"/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + ".db", null,
					SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);

			Cursor cursor = db.rawQuery("select  *  from networklog", null);
			if (cursor.moveToNext()) {
				cursor.close();
				insertNetworklog(db, String.valueOf(timeStamp), dateTime + "", mMCCMNC, mNWOPName,
						mCellID + "", parseNetworkType(mNetworkType), parsePhoneType(mPhoneType),
						parseDataState(mDataState), mIsRoaming + "", mRadioSignalStrength, mTds,
						mLteSignalStrength);// Long.toString(timeStamp)
			}
		} catch (Exception e) {

			db = SQLiteDatabase.openDatabase(
					"/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + ".db", null,
					SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);

			db.execSQL("create table networklog("
					+ "TimeStamp"
					+ " varchar(50), DateTime varchar(50),"
					+ " MCCMNC varchar(50), NWOPName varchar(50), CellID varchar(50), NetworkType varchar(50),"
					+ "PhoneType varchar(50),DataState varchar(50),Roaming varchar(50)," + "GSM"
					+ " varchar(50)," + "TDS" + " varchar(50)," + "LTE" + " varchar(50))");

			// insertNetworklog(db, "TimeStamp", "DateTime",
			// "MCCMNC", "NWOPName", "CellID",
			// "NetworkType", "mPhoneType",
			// "DataState", "Roaming",
			// "GSM", "TDS", "LTE");// Long.toString(timeStamp)

			db.close();

			db = SQLiteDatabase.openDatabase(
					"/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + ".db", null,
					SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);

			insertNetworklog(db, String.valueOf(timeStamp), dateTime + "", mMCCMNC, mNWOPName,
					mCellID + "", parseNetworkType(mNetworkType), parsePhoneType(mPhoneType),
					parseDataState(mDataState), mIsRoaming + "", mRadioSignalStrength, mTds,
					mLteSignalStrength);// Long.toString(timeStamp)
		}
	}

	// 添加操作
	public void insertNetworklog(SQLiteDatabase db, String TimeStamp, String DateTime,
			String MCCMNC, String NWOPName, String CellID, String NetworkType, String PhoneType,
			String DataState, String Roaming, String GSM, String TDS, String LTE) {
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put("TimeStamp", TimeStamp);
		cv.put("DateTime", DateTime);
		cv.put("MCCMNC", MCCMNC);
		cv.put("NWOPName", NWOPName);
		cv.put("CellID", CellID);
		cv.put("NetworkType", NetworkType);
		cv.put("PhoneType", PhoneType);
		cv.put("DataState", DataState);
		cv.put("Roaming", Roaming);
		cv.put("GSM", GSM);
		cv.put("TDS", TDS);
		cv.put("LTE", LTE);
		// SQLiteDatabase db = this.getWritableDatabase();
		db.insert("networklog", null, cv);
		db.close();

		// System.out.println("插入一条");
	}

	// public class MyDatabaseUtil extends SQLiteOpenHelper{
	//
	// public MyDatabaseUtil(Context context, String name,
	// CursorFactory factory, int version) {
	// super(context, name, factory, version);
	// // TODO Auto-generated constructor stub
	// }
	//
	// @Override
	// public void onCreate(SQLiteDatabase db) {
	// // TODO Auto-generated method stub
	// db.execSQL("create table networklog("
	// + "[TimeStamp(ms)]"
	// + " varchar(50), DateTime varchar(50),"
	// +
	// " MCCMNC varchar(50), NWOPName varchar(50), CellID varchar(50), NetworkType varchar(50),"
	// + "PhoneType varchar(50),DataState varchar(50),Roaming varchar(50),"
	// + "[GSM(dbm)]" + " varchar(50)," + "[TDS(dbm)] "
	// + "varchar(50)," + "[LTE(dbm)]" + " varchar(50))");
	// }
	//
	// @Override
	// public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	// // TODO Auto-generated method stub
	// }
	//
	// /**
	// * 判断某张表是否存在
	// * @param tabName 表名
	// * @return
	// */
	// public boolean tabIsExist(String tabName){
	// boolean result = false;
	// if(tabName == null){
	// return false;
	// }
	// SQLiteDatabase db = null;
	// Cursor cursor = null;
	// try {
	// db = this.getReadableDatabase();//此this是继承SQLiteOpenHelper类得到的
	// String sql =
	// "select count(*) as c from sqlite_master where type ='table' and name ='"
	// + tabName.trim() + "'" ;
	// cursor = db.rawQuery(sql, null);
	// if(cursor.moveToNext()){
	// int count = cursor.getInt(0);
	// if(count>0){
	// result = true;
	// }
	// }
	//
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// return result;
	// }
	//
	// }
}
