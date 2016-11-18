package com.lte.config;

import java.util.List;

import com.lte.bean.ActionBean;

public class ConfigTest {

	/**
	 * setup.json的内容--测试项log保存的路径
	 */
	public static String SETUP_PATH;

	/**
	 * 得到的逻辑顺序的List
	 */
	public static List<List<ActionBean>> CASE_LIST;
	// public static List<List<ActionBean>> CASE_LIST = new
	// ArrayList<List<ActionBean>>();
	// public static List<ActionBean> CASE_LIST;

	/**
	 * log文件夹的名字
	 */
	public static String LOG_FILE_NAME;
	/**
	 * 测试action的名字
	 */
	public static String ACTION_NAME;
	/**
	 * log 文件夹的时间值
	 */
	public static String LOG_FILE_TIME;
	/**
	 * 定时器
	 */
	public static String CLOCK;

	/**
	 * 下发指令编号 LOGIC_ORDER 为逻辑任务编号 POWER_ORDER 为电量指令编号 SIGNAL_ORDER 为信号指令编号
	 */
	public static final int LOGIC_ORDER = 0;
	public static final int POWERLOW_SET = 1;
	public static final int SIGNAL_ORDER = 2;
	public static final int LOG_ORDER = 3;
	public static final int ADB_ORDER = 4;

	/**
	 * 文件分段上传的编号
	 */
	public static int fileNum = 1;

	/**
	 * 当前是否有任务执行,0代表无任务执行，1代表任务正在执行
	 */
	public static int isDo = 0;

}
