package com.lte.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

public class CMDUtil {

	public static void execShellCMD(String[] s, int execType) throws IOException,
			InterruptedException {
		if (s.length != 0) {
			Process p = Runtime.getRuntime().exec(s[0]);
			// PROBLEM: only first cmd in the array can be implemented, the
			// other can not be implemented(or we can't see)
			if (s.length > 1) {
				OutputStream outputStream = p.getOutputStream();
				DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
				int i = 1;
				// while (i <= s.length - 1) {
				dataOutputStream.writeBytes(s[1]);
				// i = i + 1;
				// }
				dataOutputStream.flush();
				dataOutputStream.close();
				outputStream.close();
			}
			switch (execType) {
			// 按照duration时间执行
			case 1:
				synchronized (s) {
					s.wait(Integer.parseInt(s[2]) * 1000);
					ToolUtil.killProcess("uiautomator");
					String[] cmd = new String[] { "su",
							"uiautomator runtest /sdcard/testcase/UiAutomatorPrjDemo.jar -c com.StopCase" };
					CMDUtil.execShellCMD(cmd, 3);
				}
				break;
			// 按照timeout执行
			case 2:
				synchronized (s) {
					Worker worker = new Worker(p);
					worker.start();
					try {
						worker.join(Integer.parseInt(s[2]) * 1000);
						if (worker.exit != null) {
							Log.e("超时", "未超时");
						} else {
							// 超时操作
							ToolUtil.killProcess("uiautomator");

							Process sh = Runtime.getRuntime().exec("su");
							DataOutputStream os = new DataOutputStream(sh.getOutputStream());
							final String Command = "uiautomator runtest /sdcard/testcase/UiAutomatorPrjDemo.jar -c com.StopCase";
							os.writeBytes(Command);
							os.flush();
							os.close();
							sh.waitFor();
							Log.e("超时", "超时处理结束");
						}
					} catch (InterruptedException ex) {
						worker.interrupt();
						Thread.currentThread().interrupt();
						throw ex;
					} finally {
						// p.destroy();
					}
				}
				break;
			// 按顺序执行
			case 3:
				p.waitFor();
				break;
			default:
				p.waitFor();
				break;
			}
		}
	}

	private static class Worker extends Thread {
		private final Process process;
		private Integer exit;

		private Worker(Process process) {
			this.process = process;
		}

		public void run() {
			try {
				exit = process.waitFor();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}
}
