package com.lte.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigPN {

	// ---------------------------------------------------------------------------------------------
	// 公共
//	public static String PNIP;
	public static String PNIP = "http://192.168.0.13";

	// ---------------------------------------------------------------------------------------------
	// 推送
	/**
	 * 注册时信息
	 */
	public static Map<String, String> pnRegisterMap;
	/**
	 * 信鸽返回码含义
	 */
	/**
	 * “功能”对应的[“评分标准表名”, ]
	 */
	public static final Map<String, String> XG_RETURN_MEANING_MAP = new HashMap<String, String>() {
		{
			put("0", "调用成功");
			put("-1", "参数错误");
			put("-2", "请求时间戳不在有效期内");
			put("-3", "sign校验无效，检查access id和secret key（注意不是access key）");
			put("2", "参数错误");
			put("7", "别名/账号绑定的终端数满了（10个）");
			put("14", "收到非法token，例如ios终端没能拿到正确的token");
			put("15", "信鸽逻辑服务器繁忙");
			put("19",
					"操作时序错误\n例如进行tag操作前未获取到deviceToken 没有获取到deviceToken的原因: 1.没有注册信鸽或者苹果推送。 2.provisioning profile制作不正确。");
			put("20", "鉴权错误，可能是由于Access ID和Access Key不匹配");
			put("40", "推送的token没有在信鸽中注册");
			put("48", "推送的账号没有在信鸽中注册");
			put("63", "标签系统忙");
			put("71", "APNS服务器繁忙");
			put("73", "消息字符数超限");
			put("76", "请求过于频繁，请稍后再试");
			put("78", "循环任务参数错误");
			put("100", "APNS证书错误。请重新提交正确的证书");
			put("10000", "起始错误");
			put("10001", "操作类型错误码，例如参数错误时将会发生该错误");
			put("10002", "正在执行注册操作时，又有一个注册操作到来，则回调此错误码");
			put("10003", "权限出错");
			put("10004", "so出错");
			put("10100", "当前网络不可用");
			put("10101", "创建链路失败");
			put("10102", "请求处理过程中， 链路被主动关闭");
			put("10103", "请求处理过程中，服务器关闭链接");
			put("10104", "请求处理过程中，客户端产生异常");
			put("10105", "请求处理过程中，发送或接收报文超时");
			put("10106", "请求处理过程中， 等待发送请求超时");
			put("10107", "请求处理过程中， 等待接收请求超时");
			put("10108", "服务器返回异常报文");
			put("10109", "未知异常，请在QQ群中直接联系管理员，或前往论坛发帖留言");
			put("10110", "创建链路的handler为null");
		}
	};
	// ---------------------------------------------------------------------------------------------
	// txt文件名
	/**
	 * 上传失败结果
	 */
	public static final String TXT_FAILED = "failed.txt";

	// ---------------------------------------------------------------------------------------------
	// SharedPreferences文件名
	public static final String SP_DEPENDSCORE = "dependScore";
	public static final String SP_SET = "set";
	// public static final String SP_XG = "XGPush";
	public static final String SP_PN = "PNPush";
	public static final String SP_LOC = "location";
	public static final String SP_NAME = "name";

	/**
	 * “设备信息”结果中的空格
	 */
	public static final String SPACE = "\040\040\040";

	// ---------------------------------------------------------------------------------------------
	// sharedPreferences
	/**
	 * PN的sharedPreferences的key
	 */
	// public static String SP_PN_ISINFOUPED = "isInfoUped", SP_PN_ISREGISTERED
	// = "isRegistered";
	public static String SP_PN_ISREGISTERED = "isRegistered",
			SP_PN_IMEI = "imei", SP_PN_ACCOUNT = "account", SP_PN_IP = "ip";

	// 设备名称
	public static String SP_NAME_NAME = "name";

	// 手机位置信息
	public static String SP_LOC_BOX = "box_num", SP_LOC_USB = "usb_num";

	/**
	 * set的sharedPreferences的key
	 */
	public static String SP_SET_CALLEDNUM = "calledNum",
			// SP_SET_GLOBALNUM = "global_num", SP_SET_GLOBALAPID =
			// "global_apID", SP_SET_GLOBALAPPW = "global_apPW", SP_SET_USBNUM =
			// "global_usbNum", SP_SET_BOXNUM = "global_boxNum",
			// SP_SET_SIGNALSPAN = "global_signalSpan", //全局设置
			SP_SET_GLOBALNUM = "global_num",
			SP_SET_GLOBALAPID = "global_apID",
			SP_SET_GLOBALAPPW = "global_apPW",
			SP_SET_SIGNALSPAN = "global_signalSpan", // 全局设置
			SP_SET_MMSNUM = "mms_num", // 发送彩信
			SP_SET_OPENWLANAPID = "openwlan_apID",
			SP_SET_OPENWLANAPPW = "openwlan_apPW", // 打开WLAN热点
			SP_SET_LINKWLANAPID = "linkwlan_apID",
			SP_SET_LINKWLANAPPW = "linkwlan_apPW", // 连接WLAN热点
			SP_SET_BTPAIRMAC = "btpair_mac", // 配对蓝牙
			SP_SET_BTOPENTIMEOUT = "btopen_timeout", // 打开蓝牙
			SP_SET_WEBURL = "web_url",
			SP_SET_WABSPAN = "web_span",
			SP_SET_WABOPENTIMES = "web_opentimes", // 打开网页
			SP_SET_PINGURL = "ping_url",
			SP_SET_PINGTIMES = "ping_times",
			SP_SET_PINGSIZE = "ping_size", // ping
			SP_SET_FTPDOWNTIME = "ftpdown_time",
			SP_SET_FTPDOWNCLOSE = "ftpdown_close", // FTP下载
			SP_SET_FTPUPTIMEOUT = "ftpup_timeout", // FTP上传
			SP_SET_VIDEOURL = "video_url",
			SP_SET_VIDEOTIME = "video_time", // 网络视频
			SP_SET_AIRTIME = "air_time", // 飞行模式
			SP_SET_GPSLONGITUDE = "gps_longitude",
			SP_SET_GPSLATITUDE = "gps_latitude",
			SP_SET_GPSTIMEOUT = "gps_out", // GPS定位
			SP_SET_SMSNUM = "sms_edNum",
			SP_SET_SMSCNT = "sms_cnt",
			SP_SET_SMSTIMES = "sms_times", // 发送短信
			SP_SET_SMSEDTIMEOUT = "smsed_timeout", // 接收短信
			SP_SET_MMSEDTIMEOUT = "mmsed_timeout", // 接收彩信
			SP_SET_SMSSEQEDNUM = "smsseq_ednum",
			SP_SET_SMSSEQCNT = "smsseq_cnt",
			SP_SET_SMSSEQTIMES = "smsseq_times",
			SP_SET_SMSSEQSPAN = "smsseq_span",
			SP_SET_SMSSEQTIMEOUT = "smsseq_timeout", // 连发短信
			SP_SET_SMSGROUPEDNUM = "smsgroup_ednum",
			SP_SET_SMSGROUPCNT = "smsgroup_cnt",
			SP_SET_SMSGROUPTIMES = "smsgroup_times",
			SP_SET_SMSGROUPTIMEOUT = "smsgroup_timeout", // 群发短信
			SP_SET_CALLEDTIMEOUT = "called_timeout", // 被叫
			SP_SET_CALLNUM = "call_num", SP_SET_CALLSPAN = "call_span",
			SP_SET_CALLDURATION = "call_duration",
			SP_SET_CALLTIMES = "call_times"; // 主叫

	// ---------------------------------------------------------------------------------------------
	// 功能分类
	/**
	 * 大分类
	 */
	// public static final String[] PARENT_CATEGORY_ARR = {"通话", "短信", "彩信",
	// "数据业务", "无线", "双卡", "其他", };
	public static final String[] PARENT_CATEGORY_ARR = { "通话", "短信", "彩信",
			"数据业务", "无线", "其他", };

	/**
	 * 小分类 与大分类意义对应 ***顺序变动会导致连锁效应***
	 */
	public static final ArrayList<String[]> CHILDREN_CATEGORY_LIST = new ArrayList<String[]>() {
		{
			// add(new String[]{"主叫（响铃）", "被叫", "通话", "主叫（接听）"});
			add(new String[] { "主叫", "被叫" });
			add(new String[] { "发送短信", "接收短信", "连发短信", "群发短信" });
			add(new String[] { "发送彩信", "接收彩信" });
			add(new String[] { "ping", "FTP上传", "FTP下载" });
			// add(new String[]{"打开WLAN热点", "连接WLAN热点", "打开蓝牙", "配对蓝牙",
			// "通过蓝牙发送文件"});
			add(new String[] { "打开WLAN热点", "连接WLAN热点", "打开蓝牙", "配对蓝牙" });
			// add(new String[]{"选择SIM卡打电话", "选择SIM卡发短信"});
			add(new String[] { "GPS定位", "网络状态", "网络视频", "打开网页", "飞行模式" });
		}
	};

	/**
	 * AcSetFn中ListView的所有数据
	 */
	public static final String[] GROUP_CHILD_ARR = { "全局", "全局设置", "通话", "主叫",
			"被叫", "短信", "发送短信", "接收短信", "连发短信", "群发短信", "彩信", "发送彩信", "接收彩信",
			"数据业务", "ping", "FTP上传", "FTP下载", "无线", "打开WLAN热点", "连接WLAN热点",
			"打开蓝牙", "配对蓝牙", "其他", "GPS定位", "网络状态", "网络视频", "打开网页", "飞行模式" };

	// ---------------------------------------------------------------------------------------------
	// 数据库
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "Test.db";

	/**
	 * 所有评分标准表名Array
	 */
	// public static final String[] TABLE_RULE_ARR = {"rule_call",
	// "rule_called", "rule_calling", "rule_call1", "rule_sendSMS",
	// "rule_sendSMSSeq", "rule_sendSMSGroup", "rule_receiveSMS",
	// "rule_sendMMS", "rule_receiveMMS",
	// "rule_ping", "rule_ftpUpload", "rule_ftpDownload", "rule_openWLAN",
	// "rule_connectWLAN", "rule_btOpen", "rule_btPaired", "rule_btSend",
	// "rule_simCall",
	// "rule_simSMS", "rule_gps", "rule_networkType", "rule_networkVideo",
	// "rule_webpage", "rule_airplane"};
	public static final String[] TABLE_RULE_ARR = { "rule_call1",
			"rule_called", "rule_sendSMS", "rule_sendSMSSeq",
			"rule_sendSMSGroup", "rule_receiveSMS", "rule_sendMMS",
			"rule_receiveMMS", "rule_ping", "rule_ftpUpload",
			"rule_ftpDownload", "rule_openWLAN", "rule_connectWLAN",
			"rule_btOpen", "rule_btPaired", "rule_btSend", "rule_simCall",
			"rule_simSMS", "rule_gps", "rule_networkType", "rule_networkVideo",
			"rule_webpage", "rule_airplane" };

	/**
	 * “功能”对应的[“评分标准表名”, ]
	 */
	public static final Map<String, String[]> FUNCTION_RELEVANT_MAP = new HashMap<String, String[]>() {
		{
			// put("主叫（响铃）", new String[]{"rule_call"});
			put("主叫", new String[] { "rule_call1" });
			put("被叫", new String[] { "rule_called" });
			// put("通话", new String[]{"rule_calling"});
			put("发送短信", new String[] { "rule_sendSMS" });
			put("连发短信", new String[] { "rule_sendSMSSeq" });
			put("群发短信", new String[] { "rule_sendSMSGroup" });
			put("接收短信", new String[] { "rule_receiveSMS" });
			put("发送彩信", new String[] { "rule_sendMMS" });
			put("接收彩信", new String[] { "rule_receiveMMS" });
			put("ping", new String[] { "rule_ping" });
			put("FTP上传", new String[] { "rule_ftpUpload" });
			put("FTP下载", new String[] { "rule_ftpDownload" });
			// put("wifi连接", new String[]{"rule_connectWIFI"});
			put("打开WLAN热点", new String[] { "rule_openWLAN" });
			put("连接WLAN热点", new String[] { "rule_connectWLAN" });
			put("打开蓝牙", new String[] { "rule_btOpen" });
			put("配对蓝牙", new String[] { "rule_btPaired" });
			put("通过蓝牙发送文件", new String[] { "rule_btSend" });
			// put("通过蓝牙接收文件", new String[]{"rule_btReceive"});
			put("选择SIM卡打电话", new String[] { "rule_simCall" });
			put("选择SIM卡发短信", new String[] { "rule_simSMS" });
			put("GPS定位", new String[] { "rule_gps" });
			put("网络状态", new String[] { "rule_networkType" });
			put("网络视频", new String[] { "rule_networkVideo" });
			put("打开网页", new String[] { "rule_webpage" });
			put("飞行模式", new String[] { "rule_airplane" });
		}
	};

	/**
	 * 所有“评分标准表”中的字段名<br>
	 * {"自增主键id", "标准名称", "可选结果，用‘,’分开", "得分结果", "比较方式", "分值", "描述"}
	 */
	public static final String[] FIELD_RULE = { "id", "ruleName",
			"optionalResult", "scoreResult", "compareWay", "score",
			"description" };

	/**
	 * 各个rule表需要填写的内容，依次为：<br>
	 * {标准名ruleName, 可选结果optionalResult, 得分结果scoreResult, 比较方式compareWay,
	 * 分值score, 描述description}
	 */
	public static final Map<String, List<String[]>> RULE_TABLE_DATA_MAP = new HashMap<String, List<String[]>>() {
		{
			// 主叫（响铃）
			put("rule_call", new ArrayList<String[]>() {
				{
					add(new String[] { "接通时延(s)", "", "0,10", "><=", "10",
							"0s<?<=10s" });
					add(new String[] { "接通时延(s)", "", "10,15", "><=", "5",
							"10s<?<=15s" });
					add(new String[] { "接通时延(s)", "", "15", ">", "0", "?>15s" });
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// //主叫（接听）
			// 主叫
			put("rule_call1", new ArrayList<String[]>() {
				{
					// add(new String[]{"是否成功", "", "1", "==", "1", "是"});
					add(new String[] { "拨打次数", "", "0", ">=", "0", "无得分" });
					add(new String[] { "接通时延(s)", "", "0,10", "><=", "10",
							"0s<?<=10s" });
					add(new String[] { "接通时延(s)", "", "10,15", "><=", "5",
							"10s<?<=15s" });
					add(new String[] { "接通时延(s)", "", "15", ">", "0", "?>15s" });
					add(new String[] { "成功率", "", "0.6", ">=", "1", "?>=0.6" });
				}
			});
			// 被叫
			put("rule_called", new ArrayList<String[]>() {
				{
					add(new String[] { "接通时延(s)", "", "0,10", "><=", "10",
							"0s<?<=10s" });
					add(new String[] { "接通时延(s)", "", "10,15", "><=", "5",
							"10s<?<=15s" });
					add(new String[] { "接通时延(s)", "", "15", ">", "0", "?>15s" });
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// 通话
			put("rule_calling", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// 发送短信
			put("rule_sendSMS", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
					add(new String[] { "发送时延(s)", "", "10", "<=", "1", "<=10s" });
					add(new String[] { "成功率", "", "1", "==", "1", "100%" });
				}
			});
			// 连发短信
			put("rule_sendSMSSeq", new ArrayList<String[]>() {
				{
					add(new String[] { "成功条数", "", "1", "==", "1", "1" });
					add(new String[] { "成功条数", "", "2", "==", "2", "2" });
					add(new String[] { "成功条数", "", "3", "==", "3", "3" });
					add(new String[] { "成功条数", "", "4", "==", "4", "4" });
					add(new String[] { "成功条数", "", "5", "==", "5", "5" });
					add(new String[] { "成功条数", "", "6", "==", "6", "6" });
					add(new String[] { "成功条数", "", "7", "==", "7", "7" });
					add(new String[] { "成功条数", "", "8", "==", "8", "8" });
					add(new String[] { "成功条数", "", "9", "==", "9", "9" });
					add(new String[] { "成功条数", "", "10", "==", "10", "10" });
				}
			});
			// 群发短信
			put("rule_sendSMSGroup", new ArrayList<String[]>() {
				{
					add(new String[] { "成功条数", "", "1", "==", "1", "1" });
					add(new String[] { "成功条数", "", "2", "==", "2", "2" });
					add(new String[] { "成功条数", "", "3", "==", "3", "3" });
					add(new String[] { "成功条数", "", "4", "==", "4", "4" });
					add(new String[] { "成功条数", "", "5", "==", "5", "5" });
					add(new String[] { "成功条数", "", "6", "==", "6", "6" });
					add(new String[] { "成功条数", "", "7", "==", "7", "7" });
					add(new String[] { "成功条数", "", "8", "==", "8", "8" });
					add(new String[] { "成功条数", "", "9", "==", "9", "9" });
					add(new String[] { "成功条数", "", "10", "==", "10", "10" });
				}
			});
			// 接收短信
			put("rule_receiveSMS", new ArrayList<String[]>() {
				{
					add(new String[] { "接收时延(s)", "", "10", "<=", "1", "?<=10s" });
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// 发送彩信
			put("rule_sendMMS", new ArrayList<String[]>() {
				{
					add(new String[] { "发送时延(s)", "", "30", "<=", "1", "?<=30s" });
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// 接收彩信
			put("rule_receiveMMS", new ArrayList<String[]>() {
				{
					add(new String[] { "接收时延(s)", "", "30", "<=", "1", "?<=30s" });
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// ping
			put("rule_ping", new ArrayList<String[]>() {
				{
					add(new String[] { "ping通时延(ms)", "", "10", "<=", "10",
							"?<=10ms" });
					add(new String[] { "ping通时延(ms)", "", "10,15", "><=", "5",
							"10ms<?<=15ms" });
					add(new String[] { "ping通时延(ms)", "", "15", ">", "0",
							"?>15ms" });
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// FTP上传
			put("rule_ftpUpload", new ArrayList<String[]>() {
				{
					add(new String[] { "平均速度(KB)", "", "640", ">=", "10",
							"?>=5M" });
					add(new String[] { "平均速度(KB)", "", "512,640", ">=<", "8",
							"4M<=?<5M" });
					add(new String[] { "平均速度(KB)", "", "384,512", ">=<", "6",
							"3M<=?<4M" });
					add(new String[] { "平均速度(KB)", "", "256,384", ">=<", "4",
							"2M<=?<3M" });
					add(new String[] { "平均速度(KB)", "", "128,256", ">=<", "2",
							"1M<=?<2M" });
					add(new String[] { "平均速度(KB)", "", "0,128", ">=<", "0",
							"0M<=?<1M" });
				}
			});
			// FTP下载
			put("rule_ftpDownload", new ArrayList<String[]>() {
				{
					add(new String[] { "平均速度(KB)", "", "6400", ">=", "10",
							"?>=50M" });
					add(new String[] { "平均速度(KB)", "", "5120,6400", ">=<", "8",
							"40M<=?<50M" });
					add(new String[] { "平均速度(KB)", "", "3840,5120", ">=<", "6",
							"30M<=?<40M" });
					add(new String[] { "平均速度(KB)", "", "2560,3840", ">=<", "4",
							"20M<=?<30M" });
					add(new String[] { "平均速度(KB)", "", "1280,2560", ">=<", "2",
							"10M<=?<20M" });
					add(new String[] { "平均速度(KB)", "", "1280", "<", "0",
							"?<10M" });
					add(new String[] { "平均速度(KB)", "", "128", ">=", "10",
							"?>=1M" });
					add(new String[] { "平均速度(KB)", "", "121.6,128", ">=<", "8",
							"0.95M<=?<1M" });
					add(new String[] { "平均速度(KB)", "", "115.2,121.6", ">=<",
							"6", "0.9M<=?<0.95M" });
					add(new String[] { "平均速度(KB)", "", "108.8,115.2", ">=<",
							"4", "0.85M<=?<0.9M" });
					add(new String[] { "平均速度(KB)", "", "102.4,108.8", ">=<",
							"2", "0.8M<=?<0.85M" });
					add(new String[] { "平均速度(KB)", "", "102.4", "<", "0",
							"?<0.8M" });
					add(new String[] { "数据量(KB)", "", "0", ">", "0", "无得分" });
					add(new String[] { "下载时长(s)", "", "0", ">", "0", "无得分" });
				}
			});
			// 打开WLAN热点
			put("rule_openWLAN", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// 连接WLAN热点
			put("rule_connectWLAN", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// 打开蓝牙
			put("rule_btOpen", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// 配对蓝牙
			put("rule_btPaired", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// 通过蓝牙发送文件
			put("rule_btSend", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// //通过蓝牙接收文件
			// put("rule_btReceive", new ArrayList<String[]>() {{
			// add(new String[]{"是否成功", "", "1", "==", "10", "是"});
			// }});
			// 选择SIM卡打电话
			put("rule_simCall", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// 选择SIM卡发短信
			put("rule_simSMS", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// GPS定位
			put("rule_gps", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// 网络状态
			put("rule_networkType", new ArrayList<String[]>() {
				{
					add(new String[] { "网络类型", "", "0", "==", "0", "没有网络" });
					add(new String[] { "网络类型", "", "2", "==", "2", "2G" });
					add(new String[] { "网络类型", "", "3", "==", "3", "3G" });
					add(new String[] { "网络类型", "", "4", "==", "4", "4G" });
				}
			});
			// 网络视频
			put("rule_networkVideo", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
			// 打开网页
			put("rule_webpage", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
					add(new String[] { "打开时长(s)", "", "0,10", "><=", "1",
							"0s<?<=10s" });
					add(new String[] { "打开时长(s)", "", "0", "==", "0", "?=0s" });
					add(new String[] { "打开次数", "", "0", ">", "0", "无得分" });
					add(new String[] { "成功率", "", "0.6", ">=", "1", "?>=60%" });
				}
			});
			// 飞行模式
			put("rule_airplane", new ArrayList<String[]>() {
				{
					add(new String[] { "是否成功", "", "1", "==", "1", "是" });
				}
			});
		}
	};

	/**
	 * 保存测试相关表Arr {"项目名", "功能名", "评分标准", "关系"}
	 */
	public static final String[] TABLE_PROJECT_ARR = { "saved_project",
			"saved_fn", "saved_rule", "saved_relation" };

	/**
	 * 保存测试各表字段
	 */
	public static final Map<String, String[]> FIELD_PROJECT_MAP = new HashMap<String, String[]>() {
		{
			// 项目名
			put("saved_project", new String[] { "id", "project", "time",
					"isUp", "createDate" });
			// 功能名
			put("saved_fn", new String[] { "id", "project", "fn" });
			// 评分标准
			put("saved_rule", new String[] { "id", "project", "fn", "ruleId",
					"ruleName", "optionalResult", "scoreResult", "compareWay",
					"score", "description" });
			// 关系
			put("saved_relation", new String[] { "id", "project", "fn",
					"relation", "relatedTo" });
		}
	};

	/**
	 * 结果得分相关表Arr {"得分项目名", "得分详情", "项目得分"}
	 */
	public static final String[] TABLE_SCORE_ARR = { "score_project",
			"score_detail", "score_projectScore" };

	/**
	 * 结果得分各表字段
	 */
	public static final Map<String, String[]> FIELD_SCORE_MAP = new HashMap<String, String[]>() {
		{
			// 项目名
			put("score_project", new String[] { "id", "project", "createDate" });
			// 得分详情
			put("score_detail", new String[] { "id", "project", "fn", "rule",
					"result", "score" });
			// 项目得分
			put("score_projectScore", new String[] { "id", "project",
					"realScore", "totleScore" });
		}
	};

	// ---------------------------------------------------------------------------------------------
	// FTP
	/**
	 * FTP的ip
	 */
	public static String FTP_IP = "211.140.3.250";
	/**
	 * FTP用户名
	 */
	public static String FTP_USERNAME = "ltetest";
	/**
	 * FTP密码
	 */
	public static String FTP_PASSWORD = "zjtest123";
	/**
	 * FTP下载 本地文件夹
	 */
	public static String FTP_DOWN_LOCAL_DIR = "ftpDown";
	/**
	 * FTP下载 本地文件名
	 */
	public static String FTP_DOWN_LOCAL_FILE = "test1g";
	/**
	 * FTP上传 本地文件夹
	 */
	public static String FTP_UP_LOCAL_DIR = "ftpUP";
	/**
	 * FTP上传 本地文件名
	 */
	public static String FTP_UP_LOCAL_FILE = "UpTestFile";
	/**
	 * Ftp下载 服务器文件
	 */
	public static String FTP_DOWN_SERVER_FILE = "download(xia zai)"
			+ File.separator + "test1g";
	/**
	 * Ftp上传 服务器路径
	 */
	public static String FTP_UP_SERVER_PATH = "upload(shang chuan)";

}
