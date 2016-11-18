package com.lte.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.util.Log;

import com.lte.bean.ActionBean;
import com.lte.config.ConfigSP;
import com.lte.config.ConfigTest;
import com.lte.listener.CallSMSListenerService;
import com.lte.listener.ListenerService;
import com.lte.util.CMDUtil;
import com.lte.util.ConnectWifi;
import com.lte.util.ConnectivityManagerUtil;
import com.lte.util.IOUtil;
import com.lte.util.ReadSql;
import com.lte.util.ToolUtil;

public class LogicService extends Service {

	Context context;
	private static final String TAG = "LogicService";
	ConnectWifi connectWifi;

	/**
	 * action的步骤（默认开始step为1）
	 */
	private int actionStep = 1;

	// 开始的个数
	private int numStart = 0;

	// 结束的个数
	private int numEnd = 0;

	// 当前的behavior的步骤
	public int beIndex = -1;

	// imsi号码
	private String strImsi;

	// 上传imsi号码的结果
	private String imisResult;

	// ActionName
	private String ActionName = "";

	// 当前的运行的actionName（因为页面显示的带有步骤，不适合此地使用）
	private String currentName = "";

	// standard
	private String standard;

	// case执行完毕的标识位
	private boolean flag = false;

	ConnectivityManagerUtil cmu;

	Editor editor;

	long totaltime, totaltime1, totaltime2;

	long runtime, runtime1, runtime2;

	// private List<ActionBean> list = ConfigTest.ACTION_BEAN;

	// // 测试list
	// private List<ActionBean> list = new ArrayList<ActionBean>();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		editor = getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).edit();// 初始化存储器
		cmu = new ConnectivityManagerUtil(getApplicationContext());

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		context = this;
		LogicThread logicThread = new LogicThread();
		// 设置守护线程
		logicThread.setDaemon(true);
		logicThread.start();

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 
	 * 开始运行的线程
	 * 
	 */
	public class LogicThread extends Thread {
		public void run() {

			try {
				if (!ConfigTest.CLOCK.equals("")) {
					// 现在的时间
					long curTime = System.currentTimeMillis();
					// 将定时转换成long型
					SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					// String curDate = sdf.format(new Date());
					String clock = ConfigTest.CLOCK;

					long clockTime = 0;
					try {
						clockTime = sdfDateFormat.parse(clock).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (curTime > clockTime) {
						new Thread() {
							public void run() {
								editor.putString(ConfigSP.SP_reseach_status, "测试中").commit();
								testLogic();
							};
						}.start();
					} else {
						while (true) {
							// 设定好日期的格式
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
							String nowTime = sdf.format(new Date());
							if (nowTime.equals(ConfigTest.CLOCK)) {
								new Thread() {
									public void run() {
										testLogic();
									};
								}.start();
								break;
							}
						}
					}

				} else {
					new Thread() {
						public void run() {
							testLogic();
						};
					}.start();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}

	/**
	 * 按步骤测试
	 */
	private void testLogic() {
		ConfigTest.fileNum = 0;
		flag = false;
		// 启动生成手机各项标准的服务
		Intent logIntent = new Intent(context, ListenerService.class);
		context.startService(logIntent);

		// 启动检测手机短信接收的服务
		Intent smLogIntent = new Intent(context, CallSMSListenerService.class);
		context.startService(smLogIntent);

		// 按顺序解析case
		for (int caseIndex = 0; caseIndex < ConfigTest.CASE_LIST.size(); caseIndex++) {

			beIndex++;
			// 将该case的参数保存到“jar包所需参数路径”
			String nowParamStr = IOUtil.readStringFromFile("sdcard/testcase", "parameter"
					+ caseIndex + ".json");
			IOUtil.writeStringToFile(nowParamStr, "sdcard/testcase",
					"sdcard/testcase/parameter.json", false);
			// 遍历behivor，按照step解析执行behivor
			OUT: while (true) {
				final List<ActionBean> behivorList = ConfigTest.CASE_LIST.get(caseIndex);
				// final List<ActionBean> behivorList = ConfigTest.CASE_LIST;
				for (int i = 0; i < behivorList.size(); i++) {
					final int j = i;

					editor.putString(
							ConfigSP.SP_reseach_actionNumber,
							(i + 1) + "/" + behivorList.size() + ";" + (caseIndex + 1) + "/"
									+ ConfigTest.CASE_LIST.size()).commit();
					// 若该action的step为当前step则测试
					if (Integer.parseInt(behivorList.get(i).getStep()) == actionStep) {

						editor.putString(ConfigSP.SP_reseach_etc_actionStep, actionStep + "")
								.commit();

						// 取出文件中step与当前的step进行比较，是否为当前步骤
						if (Integer.parseInt(getSharedPreferences(ConfigSP.SP_reseach,
								Context.MODE_PRIVATE)
								.getString(ConfigSP.SP_reseach_actionStep, "1")) == Integer
								.parseInt(behivorList.get(i).getStep())
								&& caseIndex == beIndex) {
							// editor.putString(ConfigSP.SP_reseach_etc_actionStep,
							// behivorList.get(i).getStep()).commit();

							ActionName = ActionName + (j + 1) + behivorList.get(j).getAction();
						} else {
							ActionName = (j + 1) + behivorList.get(j).getAction();
						}

						new Thread() {
							public void run() {
								numStart++;
								try {
									// 延时时间表示在上一步（step）执行完后延时多长时间后执行当前步骤。
									Thread.sleep(Integer.parseInt(behivorList.get(j).getDelay()) * 1000);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}

								totaltime1 = System.currentTimeMillis();// 系统当前时间
								// 判断当前步骤是根据时间执行还是根据重复次数，当前为时间
								if (Integer.parseInt(behivorList.get(j).getTime()) == 0) {
									String time = behivorList.get(j).getDuration();
									standard = "time:" + time;// 时间
									editor.putString(ConfigSP.SP_reseach_standard, standard)
											.commit();
									runtime1 = System.currentTimeMillis();// jar包调用前
									String[] cmd = new String[] {
											"su",
											"uiautomator runtest /sdcard/testcase/UiAutomatorPrjDemo.jar -c com."
													+ behivorList.get(j).getAction(), time };
									// 获取当前的action的名字
									currentName = behivorList.get(j).getAction();
									Log.e("当前运行的action", currentName);
									try {
										CMDUtil.execShellCMD(cmd, 1);
									} catch (Exception e) {
										e.printStackTrace();
									} catch (Error e) {
										e.printStackTrace();
									}
									// 按照次数执行
								} else {
									String time = behivorList.get(j).getDuration();
									standard = "times:" + time;// 次数
									editor.putString(ConfigSP.SP_reseach_standard, standard)
											.commit();
									System.out.println("******进行次数执行");
									// String[] cmd = new String[] {
									// "su",
									// "uiautomator runtest /sdcard/testcase/LearningUiAutomatorDemo.jar -c com.WeiXinText"
									// };
									runtime1 = System.currentTimeMillis();// jar包调用前
									String[] cmd = new String[] {
											"su",
											"uiautomator runtest /sdcard/testcase/UiAutomatorPrjDemo.jar -c com."
													+ behivorList.get(j).getAction(), time };
									// 获取当前的action的名字
									currentName = behivorList.get(j).getAction();
									try {
										if (time.equals("0")) {
											System.out.println("******进行次数执行***time 为0");
											CMDUtil.execShellCMD(cmd, 3);
										} else {
											CMDUtil.execShellCMD(cmd, 2);
										}

									} catch (Exception e) {
										e.printStackTrace();
									} catch (Error e) {
										e.printStackTrace();
									}
								}
								numEnd++;
							};

						}.start();
					}
				}
				// 循环判断该step是否结束
				while (true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("当前step：" + actionStep);

					if (numStart == 0 && numEnd == 0) {
						System.out.println("全部测试完毕");
					
						editor.putString(ConfigSP.SP_ISUpload, "true").commit();
						
						SimpleDateFormat format02 = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss:SSS");
						String t02 = format02.format(new Date());
						String timeEnd = "测试完毕：" + t02 + "\n";
						ToolUtil.WriteFile(timeEnd, "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME
								+ "/" + "compress.txt", true);
						editor.putString(ConfigSP.SP_reseach_status, "等待测试").commit();

						Intent sgintent = new Intent(LogicService.this, SignalService.class);
//						stopService(sgintent);// 先停止测试信号服务，避免开启两个相同的服务
//						startService(sgintent);// 再启动测试信号服务

						editor.putString(ConfigSP.SP_reseach_actionName, "执行完毕").commit();

						actionStep = 1;
						beIndex = -1;
						ActionName = "";
						break OUT;
					} else {
						// 每隔15秒钟压缩上报一次
						try {
							Thread.sleep(15 * 1000);
							// TODO 压缩
							// 每次导出一次数据之后编号加一
							ConfigTest.fileNum = ConfigTest.fileNum + 1;
							// 从数据库读取数据并压缩
							Log.e("文件号码", ConfigTest.fileNum + "");
							ReadSql.readAndWrite(context, currentName, ConfigTest.fileNum, false);

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Error e) {
							e.printStackTrace();
						}
					}
					if (numStart == numEnd) {
						System.out.println("step" + actionStep + "测试完毕！");
						runtime2 = System.currentTimeMillis();// jar包调用后，即"测试完毕！"
						runtime = runtime2 - runtime1;

						editor.putString(ConfigSP.SP_reseach_runtime, runtime + "").commit();

						actionStep++;
						numStart = 0;
						numEnd = 0;
						Log.i("---info---", "---下一步开始---");
						// testLogic();
						break;
					} else {
						editor.putString(ConfigSP.SP_reseach_actionName, ActionName).commit();
					}
				}
			}
		}

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		totaltime2 = System.currentTimeMillis();// 系统当前时间
		totaltime = totaltime2 - totaltime1;
		editor.putString(ConfigSP.SP_reseach_totaltime, totaltime + "").commit();

		// 检查文件中的无用文件,读取smslog内容
		String contentSms = IOUtil.readStringFromFile("/sdcard/testcase/"
				+ ConfigTest.LOG_FILE_NAME, "/smslog.csv");
		if (contentSms.equals("TimeStamp(ms),DateTime Phone,SMS/MMS,Content")) {// 如果文件是空的话删除文件
			DelFile.delFile("/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/smslog.csv");
		}
		// 检查文件中的无用文件，读取calllog内容
		String contentCall = IOUtil.readStringFromFile("/sdcard/testcase/"
				+ ConfigTest.LOG_FILE_NAME, "/calllog.csv");
		if (!contentCall.equals("文件不存在")) {// 如果case中没有接电话测试 则把calllog文件删除
			File callFile = new File("/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME, "/calllog.csv");
			FileReader fr;
			try {
				fr = new FileReader(callFile);
				BufferedReader br = new BufferedReader(fr);
				String temp = null;
				String s = "";
				int num = 0;
				while ((temp = br.readLine()) != null) {
					s += temp + "\n";
				}
				String[] ss = s.split("\n");
				for (int i = 0; i < ss.length; i++) {
					String sss = ss[i];
					String[] hang = sss.split(",");
					if (hang[2].equals("IDLE")) {
						num++;
					}
					if (num == ss.length - 1) {
						DelFile.delFile("/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME
								+ "/calllog.csv");
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String[] cmd = new String[] { "su", "am start -n com.lte/com.lte.AcMain" };

		try {
			CMDUtil.execShellCMD(cmd, 3);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// TODO 压缩
		ZipFile.zipFile("/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME);
		Log.d("TAG", "压缩完毕！");
		SimpleDateFormat format02 = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss:SSS");
		String t02 = format02.format(new Date());
		String timeEnd = "压缩完毕：" + t02 + "\n";
		ToolUtil.WriteFile(timeEnd, "/sdcard/testcase/" + ConfigTest.LOG_FILE_NAME + "/"
				+ "compress.txt", true);
		Log.e("压缩文件完毕", System.currentTimeMillis() + "");
		new Thread() {
			public void run() {
				try {
					ConfigTest.fileNum = ConfigTest.fileNum + 1;
					ReadSql.readAndWrite(context, currentName, ConfigTest.fileNum, true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();

		// 修改任务执行标识
		ConfigTest.isDo = 0;

		// 停止监听Service
		context.stopService(logIntent);
		// 停止监听Service
		context.stopService(smLogIntent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
