package com.lte.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

public class ReadLogUtil extends Thread {

	private Context ctx;
	private int logCount;

	private static final String TAG = "LogInfo OutGoing Call";

	/**
	 * 前后台电话
	 * 
	 * @author sdvdxl
	 * 
	 */
	private static class CallViewState {
		public static final String FORE_GROUND_CALL_STATE = "mForeground";
	}

	/**
	 * 呼叫状态
	 * 
	 * @author sdvdxl
	 * 
	 */
	private static class CallState {
		public static final String DIALING = "DIALING";
		public static final String ALERTING = "ALERTING";
		public static final String ACTIVE = "ACTIVE";
		public static final String IDLE = "IDLE";
		public static final String DISCONNECTED = "DISCONNECTED";
	}

	public ReadLogUtil(Context ctx) {
		this.ctx = ctx;
	}

	/**
	 * 读取Log流 取得呼出状态的log 从而得到转换状态
	 */
	@Override
	public void run() {
		Log.d(TAG, "开始读取日志记录");
//adb logcat ActivityManager:I MyApp:d *:S 
//      最后的元素 *:S ,，是设置所有的标 签为”silent”，所有日志只显示有”View” and “MyApp”的，
//		用 *:S 的另一个用处是 能够确保日志输出的时候是按照过滤器的说明限制的，也让过滤器也作为一项输出到日志中. 
//		String[] catchParams = { "logcat", "InCallScreen *:s" };
		String[] catchParams = { "logcat", "DotMatrix *:s" };
		String[] clearParams = { "logcat", "-c" };

		try {
			Process process = Runtime.getRuntime().exec(catchParams);
			InputStream is = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String line = null;
			while ((line = reader.readLine()) != null) {
				logCount++;
				// 输出所有
				Log.d(TAG, line);

				// 日志超过512条就清理
				if (logCount > 512) {
					// 清理日志
					Runtime.getRuntime().exec(clearParams).destroy();// 销毁进程，释放资源
					logCount = 0;
					Log.v(TAG, "-----------清理日志---------------");
				}

				/*---------------------------------前台呼叫-----------------------*/
				// 空闲
				if (line.contains(ReadLogUtil.CallViewState.FORE_GROUND_CALL_STATE)
						|| line.contains(ReadLogUtil.CallState.IDLE)) {
					Log.d(TAG, ReadLogUtil.CallState.IDLE);
				}

				// 正在拨号，等待建立连接，即已拨号，但对方还没有响铃，
				if (line.contains(ReadLogUtil.CallViewState.FORE_GROUND_CALL_STATE)
						|| line.contains(ReadLogUtil.CallState.DIALING)) {
					Log.d(TAG, ReadLogUtil.CallState.DIALING);
				}

				// 呼叫对方 正在响铃
				if (line.contains(ReadLogUtil.CallViewState.FORE_GROUND_CALL_STATE)
						|| line.contains(ReadLogUtil.CallState.ALERTING)) {
					Log.d(TAG, ReadLogUtil.CallState.ALERTING);
				}

				// 已接通，通话建立
				if (line.contains(ReadLogUtil.CallViewState.FORE_GROUND_CALL_STATE)
						|| line.contains(ReadLogUtil.CallState.ACTIVE)) {
					Log.d(TAG, ReadLogUtil.CallState.ACTIVE);
				}

				// 断开连接，即挂机
				if (line.contains(ReadLogUtil.CallViewState.FORE_GROUND_CALL_STATE)
						|| line.contains(ReadLogUtil.CallState.DISCONNECTED)) {
					Log.d(TAG, ReadLogUtil.CallState.DISCONNECTED);
				}

			} // END while

		} catch (IOException e) {
			e.printStackTrace();
		} // END try-catch
	} // END run

}
