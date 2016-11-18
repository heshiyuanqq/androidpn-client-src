package com.lte.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lidroid.xutils.http.RequestParams;
import com.lte.config.ConfigSP;
import com.lte.config.ConfigTest;
import com.lte.util.ToolUtil;
import com.lte.util.XutilsUploader;

public class LogUpLoad {
	// 不带有重传机制，手机端不主动上传log，平台自己手动选择上传
	public static String newUpLoad(Context context, int fileNum, Boolean isEnd) throws Exception {
		// TODO Auto-generated method stub
		// 上传测试的新的文件包
		Log.e("准备上传压缩包", System.currentTimeMillis() + "");
		SimpleDateFormat format01 = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss:SSS");
		String t01 = format01.format(new Date());
		String timeStart = "开始上传：" + t01 + "\n";
		ToolUtil.WriteFile(timeStart, "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/"
				+ "compress.txt", true);
		// NetUtil netUpLoad = new NetUtil();
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);
		String strImsi = tm.getSubscriberId();// 手机卡唯一标识

		// List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		String pathString = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/testcase/" + ConfigTest.LOG_FILE_NAME + ".zip";

		// NameValuePair pairImsi = new BasicNameValuePair("imsino", strImsi);
		// // Imsi
		// NameValuePair pairFlag = new BasicNameValuePair("finishFlag", isEnd +
		// "");
		// NameValuePair pairNo = new BasicNameValuePair("sequenceNo", fileNum +
		// "");

		// 上传之前 设置
		// String pathStringEncode = URLEncoder.encode(pathString, "UTF-8");

		/**
		 * old upload method
		 */
		// NameValuePair postKeyValueZIP = new BasicNameValuePair("zipfile",
		// pathString);
		// paramList.add(postKeyValueZIP);
		// paramList.add(pairImsi);
		// paramList.add(pairNo);
		// paramList.add(pairFlag);
		// System.out.println("ip================>"
		// + context.getSharedPreferences(ConfigSP.SP_reseach,
		// Context.MODE_PRIVATE)
		// .getString(ConfigSP.SP_reseach_ip, "") + "=========>" +
		// postKeyValueZIP);
		// String response = netUpLoad.httpUpload(context,
		// "http://"
		// + context.getSharedPreferences(ConfigSP.SP_reseach,
		// Context.MODE_PRIVATE)
		// .getString(ConfigSP.SP_reseach_ip, "")
		// + ":8080/LTETestProject/log/receivelog", paramList);
		// SimpleDateFormat format02 = new
		// SimpleDateFormat("yyyy/MM/dd  HH:mm:ss:SSS");
		// String t02 = format02.format(new Date());
		// String timeEnd = "上传完毕：" + t02 + "\n";
		// ToolUtil.WriteFile(timeEnd, "/sdcard/testcase/" +
		// ConfigTest.LOG_FILE_NAME + "/"
		// + "compress.txt", true);
		// Log.e("上传压缩包完毕", System.currentTimeMillis() + "");
		// if (response.equals("SUCCESS")) {
		// Log.i("Log=====>", "上传成功");
		// } else {
		// Log.i("Log=====>", "上传失败");
		// }
		// return response;

		/**
		 * Xutils 方式上传
		 */
		String uri = "http://"
				+ context.getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE)
						.getString(ConfigSP.SP_reseach_ip, "")
				+ ":8080/ResearchProject/log/receivelog";
		RequestParams params = new RequestParams();
		params.addBodyParameter("imsino", strImsi);
		params.addBodyParameter("zipfile", new File(pathString));
		params.addBodyParameter("sequenceNo", fileNum + "");
		params.addBodyParameter("finishFlag", isEnd + "");
		String response = XutilsUploader.uploadSynMethod(params, uri);
		return response;
	}

}
