package com.lte.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class IOUtil {

	/**
	 * 将数据流写入文件
	 * @param dirString 文件夹
	 * @param inputStream
	 * @param frpUpFile 目标文件
	 */
	public static void writeStreamToFile(String dirString, InputStream inputStream, File frpUpFile) {
		//文件不存在则创建
		File dirFile = new File(dirString);
		if (dirFile.exists() == false) {
			dirFile.mkdirs();
		}
		FileOutputStream fileOS = null;
		try {
			fileOS = new FileOutputStream(frpUpFile);
			byte[] buffer = new byte[1024];				// 缓冲区
			int byteCount = 0;
			while ((byteCount = inputStream.read(buffer)) > 0) {
				fileOS.write(buffer, 0, byteCount);
			}
			fileOS.flush();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将字符串写入文件
	 * @param str	字符串
	 * @param dirString	文件夹
	 * @param pathString 文件
	 * @param isAppend 是否追加
	 */
	public static void writeStringToFile(String str, String dirString, String pathString, boolean isAppend) {
		//文件不存在则创建
		File dirFile = new File(dirString);
		if (dirFile.exists() == false) {
			dirFile.mkdirs();
		}
		InputStream inputStream = new ByteArrayInputStream(str.getBytes());
		FileOutputStream fileOS = null;
		try {
			fileOS = new FileOutputStream(pathString, isAppend);				//true表示在文件末尾追加
			byte[] buffer = new byte[1024];				// 缓冲区
			int byteCount = 0;
			while ((byteCount = inputStream.read(buffer)) > 0) {
				fileOS.write(buffer, 0, byteCount);
			}
			fileOS.flush();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取文件
	 * @param dirString	文件夹
	 * @param faleName 文件名
	 */
	public static String readStringFromFile(String dirString, String faleName) {
		// 获取扩展SD卡设备状态  并判断是否可写或可读
		String sdStateString = android.os.Environment.getExternalStorageState(); 
		if (sdStateString.equals(android.os.Environment.MEDIA_MOUNTED_READ_ONLY) || sdStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File file = new File(dirString+ File.separator+ faleName);
			if (file.exists() == false) {
				return "文件不存在";
			}
			String resultStr = "";
			try {
				FileInputStream fi = new FileInputStream(file);
				int length = (int) file.length();
				System.out.println("文件大小："+length);
				Reader reader = new InputStreamReader(fi);
				BufferedReader br = new BufferedReader(reader);
				try {
					String lineStr;
					StringBuilder sb = new StringBuilder();
					while ((lineStr = br.readLine()) != null) {
						sb.append(lineStr);
					}
					resultStr = sb.toString();
					System.out.println(resultStr);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return resultStr;
		} else {
			System.out.println("不可读");
			return "文件不可读";
		}
	}

}
