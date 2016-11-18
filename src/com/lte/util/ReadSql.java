package com.lte.util;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lte.config.ConfigTest;
import com.lte.test.LogUpLoad;
import com.lte.test.ZipFile;

public class ReadSql {
	/**
	 * 
	 * @param context
	 * @param actionName
	 *            当前运行的action的名字
	 * @param fileNum
	 *            当前上传到第几段了
	 * @param isEnd
	 *            是否上传结束
	 */
	public static void readAndWrite(final Context context, String actionName, final int fileNum,
			final Boolean isEnd) {
		Cursor cAction = null, cNetWork = null;
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("sdcard/testcase/"
				+ ConfigTest.LOG_FILE_NAME + ".db", null);
		Log.e("数据库的名字", ConfigTest.LOG_FILE_NAME);
		/*
		 * jdk1.7版本可以直接switch（String）
		 */
		if (actionName.equals("WeiXinText")) {// 微信文本
			try {
				cAction = db.rawQuery("select * from WeiXinTextLog", null);
				cNetWork = db.rawQuery("select * from networklog", null);
				// 导出文件
				ExportToCSVUtil.ExportToCSV(cAction,
						"/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME, "WeiXinTextLog");
				ExportToCSVUtil.ExportToCSV(cNetWork, "/sdcard/testcase/"
						+ ConfigTest.LOG_FILE_NAME, "networklog");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// 压缩
			try {
				new Thread().sleep(3 * 1000);
				ZipFile.zipFile("/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME);
				new Thread() {
					public void run() {
						try {
							String response = LogUpLoad.newUpLoad(context, fileNum, isEnd);
							if (response != "SUCCESS") {// 如果上传失败，再次重传
								LogUpLoad.newUpLoad(context, fileNum, isEnd);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (actionName.equals("WeiXinImage")) {// 微信照片
			try {
				cAction = db.rawQuery("select * from WeiXinImageLog", null);
				cNetWork = db.rawQuery("select * from networklog", null);
				// 导出文件
				ExportToCSVUtil.ExportToCSV(cAction,
						"/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME, "WeiXinImageLog");
				ExportToCSVUtil.ExportToCSV(cNetWork, "/sdcard/testcase/"
						+ ConfigTest.LOG_FILE_NAME, "networklog");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// 压缩
			try {
				new Thread().sleep(3 * 1000);
				ZipFile.zipFile("/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME);
				new Thread() {
					public void run() {
						try {
							String response = LogUpLoad.newUpLoad(context, fileNum, isEnd);
							if (response != "SUCCESS") {// 如果上传失败，再次重传
								LogUpLoad.newUpLoad(context, fileNum, isEnd);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (actionName.equals("WeiXinVoice")) {// 微信语音
			try {
				cAction = db.rawQuery("select * from WeiXinVoiceLog", null);
				cNetWork = db.rawQuery("select * from networklog", null);
				// 导出文件
				ExportToCSVUtil.ExportToCSV(cAction,
						"/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME, "WeiXinVoiceLog");
				ExportToCSVUtil.ExportToCSV(cNetWork, "/sdcard/testcase/"
						+ ConfigTest.LOG_FILE_NAME, "networklog");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// 压缩
			try {
				new Thread().sleep(3 * 1000);
				ZipFile.zipFile("/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME);
				new Thread() {
					public void run() {
						try {
							String response = LogUpLoad.newUpLoad(context, fileNum, isEnd);
							if (response != "SUCCESS") {// 如果上传失败，再次重传
								LogUpLoad.newUpLoad(context, fileNum, isEnd);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (actionName.equals("WeiXinVideo")) {// 微信小视频
			try {
				cAction = db.rawQuery("select * from WeiXinVideoLog", null);
				cNetWork = db.rawQuery("select * from networklog", null);
				// 导出文件
				ExportToCSVUtil.ExportToCSV(cAction,
						"/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME, "WeiXinVideoLog");
				ExportToCSVUtil.ExportToCSV(cNetWork, "/sdcard/testcase/"
						+ ConfigTest.LOG_FILE_NAME, "networklog");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// 压缩
			try {
				new Thread().sleep(3 * 1000);
				ZipFile.zipFile("/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME);
				new Thread() {
					public void run() {
						try {
							String response = LogUpLoad.newUpLoad(context, fileNum, isEnd);
							if (response != "SUCCESS") {// 如果上传失败，再次重传
								LogUpLoad.newUpLoad(context, fileNum, isEnd);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (actionName.equals("FTPUpload")) {
			try {// ftp上传
				cAction = db.rawQuery("select * from FTPUpLog", null);
				cNetWork = db.rawQuery("select * from networklog", null);
				// 导出文件
				ExportToCSVUtil.ExportToCSV(cAction,
						"/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME, "FTPUpLog");
				ExportToCSVUtil.ExportToCSV(cNetWork, "/sdcard/testcase/"
						+ ConfigTest.LOG_FILE_NAME, "networklog");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// 压缩
			try {
				new Thread().sleep(3 * 1000);
				ZipFile.zipFile("/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME);
				new Thread() {
					public void run() {
						try {
							String response = LogUpLoad.newUpLoad(context, fileNum, isEnd);
							if (response != "SUCCESS") {// 如果上传失败，再次重传
								LogUpLoad.newUpLoad(context, fileNum, isEnd);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (actionName.equals("FTPDownload")) {// ftp下载
			try {
				cAction = db.rawQuery("select * from FTPDownLog", null);
				cNetWork = db.rawQuery("select * from networklog", null);
				// 导出文件
				ExportToCSVUtil.ExportToCSV(cAction,
						"/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME, "FTPDownLog");
				ExportToCSVUtil.ExportToCSV(cNetWork, "/sdcard/testcase/"
						+ ConfigTest.LOG_FILE_NAME, "networklog");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// 压缩
			try {
				new Thread().sleep(3 * 1000);
				ZipFile.zipFile("/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME);
				new Thread() {
					public void run() {
						try {
							String response = LogUpLoad.newUpLoad(context, fileNum, isEnd);
							if (response != "SUCCESS") {// 如果上传失败，再次重传
								LogUpLoad.newUpLoad(context, fileNum, isEnd);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (actionName.equals("TelCaseUI")) {// 电话拨打
		}
		if (actionName.equals("TelAnswerCase")) {// 电话接听
		}
		if (actionName.equals("SMSCaseUI")) {// 短信接收
		}
		if (actionName.equals("WebBrowser")) {// Web浏览
			try {
				cAction = db.rawQuery("select * from WebLog", null);
				cNetWork = db.rawQuery("select * from networklog", null);
				// 导出文件
				ExportToCSVUtil.ExportToCSV(cAction,
						"/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME, "WebLog");

				ExportToCSVUtil.ExportToCSV(cNetWork, "/sdcard/testcase/"
						+ ConfigTest.LOG_FILE_NAME, "networklog");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// 压缩
			try {
				new Thread().sleep(3 * 1000);
				ZipFile.zipFile("/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME);
				new Thread() {
					public void run() {
						try {
							String response = LogUpLoad.newUpLoad(context, fileNum, isEnd);
							if (response != "SUCCESS") {// 如果上传失败，再次重传
								LogUpLoad.newUpLoad(context, fileNum, isEnd);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (actionName.equals("LBSHedituSearch")) {// 地图搜索
		}
		if (actionName.equals("LBSHedituPath")) {// 地图查询
		}
		if (actionName.equals("Ping")) {// Ping
		}
		if (actionName.equals("TencentVideo")) {// 腾讯视频
		}
		if (actionName.equals("AirPlaneMode")) {// 飞行模式
		}

		// Cursor c = db.rawQuery("select * from" + "news_inf", null);
	}
}
