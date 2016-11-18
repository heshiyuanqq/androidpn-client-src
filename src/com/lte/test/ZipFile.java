package com.lte.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

import com.lte.config.ConfigTest;
import com.lte.util.ToolUtil;
import com.lte.util.ZipUtil;

/**
 * 压缩文件夹 生成压缩文件
 * 
 * @author aaa
 * 
 */
public class ZipFile {

	/**
	 * 
	 * @param filePath
	 *            压缩文件夹的路径
	 */
	public static void zipFile(String filePath) {
		Log.e("开始压缩文件", System.currentTimeMillis() + "");
		SimpleDateFormat format01 = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss:SSS");
		String t01 = format01.format(new Date());
		String timeStart = "开始压缩：" + t01 + "\n";
		ToolUtil.WriteFile(timeStart, "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/"
				+ "compress.txt", true);
		String srcPath = "";// 压缩文件的路径
		String archPath = "";// 压缩包保存的路径
		String[] fileSrcStrings;// 指定压缩源，可以是目录或文件的数组
		String commentString = "Androi Java Zip 测试.";// 压缩包注释
		ZipUtil mZipControl;

		String path = Environment.getExternalStorageDirectory().getAbsolutePath();

		archPath = path + "/testcase";
		File zipFile = new File(archPath);// 创建保存zip文件的文件夹
		if (!zipFile.exists()) {
			zipFile.mkdir();
		}

		srcPath = filePath;
		File srcFile = new File(srcPath);// 创建压缩源的文件夹
		if (!srcFile.exists()) {
			srcFile.mkdir();
		}

		fileSrcStrings = new String[] { srcFile.toString() };
		mZipControl = new ZipUtil();

		try {
			mZipControl.writeByApacheZipOutputStream(fileSrcStrings, archPath + "/"
					+ ConfigTest.LOG_FILE_NAME + ".zip", commentString);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
