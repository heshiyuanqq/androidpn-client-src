package com.lte.test;

import java.io.File;

public class DelFile {

	/**
	 * 
	 * @param filePath
	 *            需要刪除的文件夾的路徑
	 */
	public static void delFile(String filePath) {
		del(filePath);
	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePath
	 *            文件夹路径
	 */
	private static void del(String filePath) {
		File myFile = new File(filePath);
		delAllFile(filePath);
		myFile.delete();// 删除空的文件夹

	}

	/**
	 * 删除指定文件夹路径 删除目录下的所有文件
	 * 
	 * @param filePath
	 *            文件夹路径
	 * @return
	 */
	private static boolean delAllFile(String filePath) {
		boolean flag = false;
		File myFile = new File(filePath);
		if (!myFile.exists()) {
			return flag;
		}
		if (!myFile.isDirectory()) {
			return flag;
		}
		String[] tempList = myFile.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (filePath.endsWith(File.separator)) {
				temp = new File(filePath + tempList[i]);
			} else {
				temp = new File(filePath + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(filePath + "/" + tempList[i]);// 先删除文件夹里面的内容
				del(filePath + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}

		return flag;

	}

}
