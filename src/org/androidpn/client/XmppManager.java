/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lte.config.ConfigPN;
import com.lte.config.ConfigSP;

/**
 * This class is to manage the XMPP connection between client and server.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class XmppManager {
	/**
	 * 是否连接成功
	 */
	private boolean isConnect = false;

	public boolean getIsConnect() {
		return isConnect;
	}

	public void setIsConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}

	/**
	 * 是否测试中
	 */
	private boolean isTesting;

	public boolean getIsTesting() {
		return isTesting;
	}

	public void setIsTesting(boolean isTesting) {
		this.isTesting = isTesting;
	}

	private static boolean IsFirstRegister = true;

	private static final String LOGTAG = LogUtil.makeLogTag(XmppManager.class);

	private static final String XMPP_RESOURCE_NAME = "AndroidpnClient";

	private Context context;

	private NotificationService.TaskSubmitter taskSubmitter;

	private NotificationService.TaskTracker taskTracker;

	private SharedPreferences sharedPrefs;

	private String xmppHost;

	private int xmppPort;

	private XMPPConnection connection;

	private String username;

	private String password;

	private ConnectionListener connectionListener;

	/**
	 * ֪ͨ��ݰ������
	 */
	private PacketListener notificationPacketListener;

	private Handler handler;

	private List<Runnable> taskList;

	private boolean running = false;

	private Future<?> futureTask;

	private Thread reconnection;
	// private NotificationService notificationService;
	private Editor editor;
	String Imsi, Username;

	public XmppManager(NotificationService notificationService) {
		isTesting = false;

		context = notificationService;
		// -------------------------------------------------
		editor = notificationService
				.getSharedPreferences(ConfigSP.SP_reseach, Context.MODE_PRIVATE).edit();
		// -------------------------------------------------
		// this.notificationService = notificationService;
		taskSubmitter = notificationService.getTaskSubmitter();
		taskTracker = notificationService.getTaskTracker();
		sharedPrefs = notificationService.getSharedPreferences();

		// xmppHost = sharedPrefs.getString(Constants.XMPP_HOST, "localhost");
		xmppHost = ConfigPN.PNIP;
		xmppPort = sharedPrefs.getInt(Constants.XMPP_PORT, 5111);
		username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
		// password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");
		// username = Config.pnRegisterMap.get("userName");
		// password = "teng123456789";
		// username = "4b95f2fa5e4711e4ab8200163e00180a";
		// password = "69e96448750758942068614e810c471b";
		connectionListener = new PersistentConnectionListener(this);
		notificationPacketListener = new NotificationPacketListener(this);

		handler = new Handler();
		taskList = new ArrayList<Runnable>();
		reconnection = new ReconnectionThread(this);

	}

	public Context getContext() {
		return context;
	}

	public void connect() {

		Log.d(LOGTAG, "connect()...");
		// -------------------------------------------------
		System.out.println("连接...");
		// -------------------------------------------------
		// zjg�Լ�ע��
		submitLoginTask();

		// if(isRegistered()){
		// Log.d(LOGTAG, "connect()...");
		// // -------------------------------------------------
		// System.out.println("连接...");
		// // -------------------------------------------------
		// // zjg�Լ�ע��
		// submitLoginTask();
		// }else{
		// submitRegisterTask();
		// }
	}

	public void disconnect() {
		Log.d(LOGTAG, "disconnect()...");
		// -------------------------------------------------
		System.out.println("断开连接...");
		// -------------------------------------------------
		terminatePersistentConnection();
	}

	/**
	 * ��ֹ��������
	 */
	public void terminatePersistentConnection() {
		Log.d(LOGTAG, "terminatePersistentConnection()...");// 结束持久连接
		// -------------------------------------------------
		System.out.println("结束持久连接");
		// -------------------------------------------------

		Runnable runnable = new Runnable() {

			final XmppManager xmppManager = XmppManager.this;

			public void run() {
				if (xmppManager.isConnected()) {
					// -------------------------------------------------
					// editor.putString(ConfigSP.SP_reseach_PNstatus,
					// "已连接").commit();
					// System.out.println("PN连接成功");
					// -------------------------------------------------
					Log.d(LOGTAG, "terminatePersistentConnection()... run()");
					xmppManager.getConnection().removePacketListener(
							xmppManager.getNotificationPacketListener());
					xmppManager.getConnection().disconnect();
				}
				xmppManager.runTask();
			}

		};
		addTask(runnable);
	}

	public XMPPConnection getConnection() {
		return connection;
	}

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ConnectionListener getConnectionListener() {
		return connectionListener;
	}

	public PacketListener getNotificationPacketListener() {
		return notificationPacketListener;
	}

	public void startReconnectionThread() {
		// -------------------------------------------------
		editor.putString(ConfigSP.SP_reseach_PNstatus, "重新连接中...").commit();
		// -------------------------------------------------
		synchronized (reconnection) {
			/*
			 * if (reconnection == null || !reconnection.isAlive()) {
			 * //在这里增加reconnection的初始化问题 reconnection = new
			 * ReconnectionThread(this);
			 * reconnection.setName("Xmpp Reconnection Thread");
			 * reconnection.start(); }
			 */
			// -------------------------------------------------
			editor.putString(ConfigSP.SP_reseach_PNstatus, "重新连接中...").commit();
			// -------------------------------------------------
			synchronized (reconnection) {
				try {
					if (reconnection == null || !reconnection.isAlive()) {
						// 在这里增加reconnection的初始化问题
						reconnection = new ReconnectionThread(this);
						reconnection.setName("Xmpp Reconnection Thread");
						reconnection.start();
					}
				} catch (Exception e) {
					Log.e("TAG", "Reconnection 异常： " + e.toString());
					reconnection = new ReconnectionThread(this);
					reconnection.setName("Xmpp Reconnection Thread");
					reconnection.start();
				} catch (Error e) {
					Log.e("TAG", "Reconnection 错误： " + e.toString());
					reconnection = new ReconnectionThread(this);
					reconnection.setName("Xmpp Reconnection Thread");
					reconnection.start();
				}
			}

		}
	}

	public Handler getHandler() {
		return handler;
	}

	public void reregisterAccount() {
		removeAccount();
		submitLoginTask();
		runTask();
	}

	public List<Runnable> getTaskList() {
		return taskList;
	}

	public Future<?> getFutureTask() {
		return futureTask;
	}

	public void runTask() {
		Log.d(LOGTAG, "runTask()...");
		// -------------------------------------------------
		System.out.println("运行任务");
		// -------------------------------------------------
		synchronized (taskList) {
			running = false;
			futureTask = null;
			if (!taskList.isEmpty()) {
				Runnable runnable = (Runnable) taskList.get(0);
				taskList.remove(0);
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			}
		}
		taskTracker.decrease();
		Log.d(LOGTAG, "runTask()...done");
	}

	private String newRandomUUID() {
		String uuidRaw = UUID.randomUUID().toString();
		return uuidRaw.replaceAll("-", "");
	}

	private boolean isConnected() {
		// -------------------------------------------------
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		Imsi = tm.getSubscriberId();// 手机卡唯一标识

		Username = getUsername();

		System.out.println("isAuthenticated:" + Imsi + ";" + Username);
		// -------------------------------------------------
		return connection != null && connection.isConnected()
		// -------------------------------------------------
		// && Imsi.equals(Username)
		// -------------------------------------------------
		;
	}

	private boolean isAuthenticated() {// 已认证
		// -------------------------------------------------
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		Imsi = tm.getSubscriberId();// 手机卡唯一标识

		Username = getUsername();

		System.out.println("isAuthenticated:" + Imsi + ";" + Username);
		// -------------------------------------------------
		return connection != null && connection.isConnected() && connection.isAuthenticated()
		// -------------------------------------------------
		// && Imsi.equals(Username)
		// -------------------------------------------------
		;
	}

	private boolean isRegistered() {
		// -------------------------------------------------
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		Imsi = tm.getSubscriberId();// 手机卡唯一标识
		Username = getUsername();
		System.out.println("isRegistered:" + Imsi + ";" + Username);

		/**
		 * 换卡，删除旧用户名和密码
		 */
		Editor pnEditor = sharedPrefs.edit();
		if (sharedPrefs.contains(Constants.XMPP_USERNAME)) {
			pnEditor.remove(Constants.XMPP_USERNAME);
			pnEditor.commit();
		}
		if (sharedPrefs.contains(Constants.XMPP_PASSWORD)) {
			pnEditor.remove(Constants.XMPP_PASSWORD);
			pnEditor.commit();
		}

		Log.e("TAG", "IMSI=" + Imsi);
		Log.e("TAG", "RegisterIMSI=" + sharedPrefs.getString(Constants.XMPP_USERNAME, ""));

		// -------------------------------------------------
		return sharedPrefs.contains(Constants.XMPP_USERNAME)
				&& sharedPrefs.contains(Constants.XMPP_PASSWORD)
				&& !sharedPrefs.getString(Constants.XMPP_USERNAME, "").equals(Imsi)
		// -------------------------------------------------
		// && Imsi.equals(Username)
		// -------------------------------------------------
		;
	}

	private void submitConnectTask() {
		Log.d(LOGTAG, "submitConnectTask()...");
		// -------------------------------------------------
		System.out.println("提交连接任务");
		// -------------------------------------------------
		addTask(new ConnectTask());
	}

	private void submitRegisterTask() {
		Log.d(LOGTAG, "submitRegisterTask()...");
		// -------------------------------------------------
		System.out.println("提交注册任务");
		// -------------------------------------------------
		submitConnectTask();
		addTask(new RegisterTask());
	}

	private void submitLoginTask() {
		Log.d(LOGTAG, "submitLoginTask()...");
		// -------------------------------------------------
		System.out.println("提交登录任务");
		// -------------------------------------------------
		submitRegisterTask();
		// zjg�Լ�ע��
		addTask(new LoginTask());
	}

	private void addTask(Runnable runnable) {
		Log.d(LOGTAG, "addTask(runnable)...");
		taskTracker.increase();
		synchronized (taskList) {
			if (taskList.isEmpty() && !running) {
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			} else {

				runTask();
				taskList.add(runnable);
			}
		}
		Log.d(LOGTAG, "addTask(runnable)... done");
	}

	/**
	 * 去除任务列表中的任务
	 * 
	 * @param dropCount
	 */
	public void dropTask(int dropCount) {
		synchronized (taskList) {
			if (taskList.size() >= dropCount) {
				for (int i = 0; i < dropCount; i++) {
					taskList.remove(0);
					taskTracker.decrease();
				}
			}
		}
	}

	private void removeAccount() {
		Editor editor = sharedPrefs.edit();
		editor.remove(Constants.XMPP_USERNAME);
		editor.remove(Constants.XMPP_PASSWORD);
		editor.commit();
	}

	/**
	 * A runnable task to connect the server.
	 */
	private class ConnectTask implements Runnable {

		final XmppManager xmppManager;

		private ConnectTask() {
			this.xmppManager = XmppManager.this;
			// this.xmppManager = new XmppManager(notificationService);
		}

		public void run() {
			Log.i(LOGTAG, "ConnectTask.run()...");
			// -------------------------------------------------
			System.out.println("运行连接任务");
			// -------------------------------------------------

			if (!xmppManager.isConnected()) {
				// Create the configuration for this new connection

				SharedPreferences pnSP = context.getSharedPreferences(ConfigSP.SP_reseach,
						Context.MODE_PRIVATE);
				xmppHost = pnSP.getString(ConfigSP.SP_reseach_ip, "");

				ConnectionConfiguration connConfig = new ConnectionConfiguration(xmppHost, xmppPort);
				// connConfig.setSecurityMode(SecurityMode.disabled);
				connConfig.setSecurityMode(SecurityMode.required);
				connConfig.setSASLAuthenticationEnabled(false);
				connConfig.setCompressionEnabled(false);

				XMPPConnection connection = new XMPPConnection(connConfig);
				xmppManager.setConnection(connection);

				try {
					// Connect to the server,连接服务器
					connection.connect();
					Log.i(LOGTAG, "XMPP connected successfully");
					// -------------------------------------------------
					// editor.putString(ConfigSP.SP_reseach_PNstatus, "已连接")
					// .commit();
					// System.out.println("XMPP连接成功");
					// -------------------------------------------------
					// packet provider
					ProviderManager.getInstance().addIQProvider("notification",
							"androidpn:iq:notification", new NotificationIQProvider());
					editor.putString(ConfigSP.SP_reseach_PNstatus, "网络连接成功").commit();
					// 连接成功，直接运行下一个登录任务
					xmppManager.runTask();

				} catch (XMPPException e) {
					Log.e(LOGTAG, "XMPP connection failed", e);
					// 连接不成功，先去掉后面的注册和登录
					xmppManager.dropTask(2);
					xmppManager.runTask();
					// 启动重连
					xmppManager.startReconnectionThread();
				}

			} else {
				Log.i(LOGTAG, "XMPP connected already");
				xmppManager.runTask();
			}
		}
	}

	/**
	 * A runnable task to register a new user onto the server.
	 */
	private class RegisterTask implements Runnable {

		final XmppManager xmppManager;

		boolean isRegisterSuccessed;

		boolean hasDropTask;

		private RegisterTask() {
			xmppManager = XmppManager.this;
		}

		public void run() {
			Log.i(LOGTAG, "RegisterTask.run()...");

			if (!xmppManager.isRegistered()) {
				isRegisterSuccessed = false;
				hasDropTask = false;
				// final String newUsername = newRandomUUID();

				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				Imsi = tm.getSubscriberId();// 手机卡唯一标识

				// final String newUsername = ConfigPN.pnRegisterMap
				// .get("userName");
				// String newUsername = Imsi;
				// final String newPassword = newRandomUUID();
				final String newPassword = "zjg";

				Registration registration = new Registration();

				PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
						registration.getPacketID()), new PacketTypeFilter(IQ.class));

				// 等待服务器返回，回调函数
				PacketListener packetListener = new PacketListener() {

					public void processPacket(Packet packet) {
						synchronized (xmppManager) {
							Log.d("RegisterTask.PacketListener", "processPacket().....");
							Log.d("RegisterTask.PacketListener", "packet=" + packet.toXML());

							if (packet instanceof IQ) {
								IQ response = (IQ) packet;
								// Log.e("", response.getChildElementXML());
								// response.getChildElementXML();
								if (response.getType() == IQ.Type.ERROR) {
									if (!response.getError().toString().contains("409")) {

										// -------------------------------------------------
										System.out.println("注册XMPP用户时未知错误!");
										editor.putString(ConfigSP.SP_reseach_PNstatus,
												"注册XMPP用户时未知错误!").commit();
										// -------------------------------------------------
										Log.e(LOGTAG,
												"Unknown error while registering XMPP account! "
														+ response.getError().getCondition());
									}
								} else if (response.getType() == IQ.Type.RESULT
										|| !Imsi.equals(Username)) {
									xmppManager.setUsername(Imsi);
									xmppManager.setPassword(newPassword);
									Log.d(LOGTAG, "username=" + Imsi);
									Log.d(LOGTAG, "password=" + newPassword);

									Editor editor = sharedPrefs.edit();

									// 存入到共享参数中，更换用户时，会使用旧用户进行登录
									editor.putString(Constants.XMPP_USERNAME, Imsi);
									editor.putString(Constants.XMPP_PASSWORD, newPassword);
									editor.commit();
									Log.i(LOGTAG, "Account registered successfully");
									isRegisterSuccessed = true;

									// -------------------------------------------------
									System.out.println("用户注册成功");
									editor.putString(ConfigSP.SP_reseach_PNstatus, "注册完成").commit();
									editor.putString(ConfigSP.SP_reseach_PNstatus, "注册成功").commit();
									// -------------------------------------------------
									if (hasDropTask) {
										xmppManager.runTask();
									}
								}
							}
						}
					}
				};

				connection.addPacketListener(packetListener, packetFilter);

				registration.setType(IQ.Type.SET);

				// registration.setTo(xmppHost);
				// Map<String, String> attributes = new HashMap<String,
				// String>();
				// attributes.put("username", rUsername);
				// attributes.put("password", rPassword);
				// registration.setAttributes(attributes);

				// registration.addAttribute("username", newUsername);
				// registration.addAttribute("password", newPassword);

				// Map<String, String> pnRegisterMap =
				// registration.getAttributes();
				// for (String key : ConfigPN.pnRegisterMap.keySet()) {
				// String value = ConfigPN.pnRegisterMap.get(key);
				// registration.getAttributes().put(key, value);
				// // registration.addAttribute(key, value);
				// }

				connection.sendPacket(registration);
				try {
					Thread.sleep(3 * 1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (xmppManager) {
					if (!isRegisterSuccessed) {
						xmppManager.dropTask(1);
						xmppManager.runTask();
						xmppManager.startReconnectionThread();
						hasDropTask = true;
					}
				}
				// 注册成功
			} else {
				// 注册成功后记录，下次登录时就不用再次注册
				SharedPreferences sp = context.getSharedPreferences(ConfigPN.SP_PN,
						Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putBoolean(ConfigPN.SP_PN_ISREGISTERED, true);
				editor.commit();
				Log.i(LOGTAG, "Account registered already");
				editor.putString(ConfigSP.SP_reseach_PNstatus, "注册成功").commit();
				// -------------------------------------------------
				System.out.println("用户已经注册");
				// -------------------------------------------------
				xmppManager.runTask();
			}
		}
	}

	/**
	 * A runnable task to log into the server.
	 */
	private class LoginTask implements Runnable {

		final XmppManager xmppManager;

		private LoginTask() {
			this.xmppManager = XmppManager.this;
		}

		public void run() {
			Log.i(LOGTAG, "LoginTask.run()...");
			// -------------------------------------------------
			System.out.println("运行登录任务");
			// -------------------------------------------------

			if (!xmppManager.isAuthenticated()) {
				Log.d(LOGTAG, "username=" + username);
				Log.d(LOGTAG, "password=" + password);
				System.out.println("!isAuthenticated_username=" + xmppManager.getUsername());
				try {
					xmppManager.getConnection().login(xmppManager.getUsername(),
							xmppManager.getPassword(), XMPP_RESOURCE_NAME);
					Log.e(LOGTAG, "Logged in successfully");
					// -------------------------------------------------
					System.out.println("登录成功");
					// -------------------------------------------------
					// -------------------------------------------------
					System.out.println("username=" + xmppManager.getUsername());
					editor.putString(ConfigSP.SP_reseach_PNstatus, "登录成功").commit();
					// -------------------------------------------------

					// connection listener
					if (xmppManager.getConnectionListener() != null) {
						xmppManager.getConnection().addConnectionListener(
								xmppManager.getConnectionListener());
					}

					// packet filter
					PacketFilter packetFilter = new PacketTypeFilter(NotificationIQ.class);
					// packet listener
					PacketListener packetListener = xmppManager.getNotificationPacketListener();
					connection.addPacketListener(packetListener, packetFilter);

					getConnection().startKeepAliveThread(xmppManager);

					// AlarmManagerUtils.registerAlarmManager(context, 10,
					// HeartActionBroadcastReceiver.class,
					// HeartActionBroadcastReceiver.ACTION);

					// ((Activity) context).runOnUiThread(new Runnable() {
					// @Override
					// public void run() {
					//
					// }
					// });

				} catch (XMPPException e) {
					Log.e(LOGTAG, "LoginTask.run()... xmpp error");
					// -------------------------------------------------
					System.out.println("登录任务运行:xmpp错误!");

					// -------------------------------------------------
					Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: " + e.getMessage());
					String INVALID_CREDENTIALS_ERROR_CODE = "401";
					String errorMessage = e.getMessage();
					if (errorMessage != null
							&& errorMessage.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
						xmppManager.reregisterAccount();
						return;
					}
					xmppManager.startReconnectionThread();

				} catch (Exception e) {
					Log.e(LOGTAG, "LoginTask.run()... other error");
					// -------------------------------------------------
					System.out.println("登录任务运行:其他错误!");
					editor.putString(ConfigSP.SP_reseach_PNstatus, "登录任务运行:其他错误!").commit();
					// -------------------------------------------------
					Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: " + e.getMessage());
					xmppManager.startReconnectionThread();
				} finally {
					xmppManager.runTask();
				}

			} else {
				Log.i(LOGTAG, "Logged in already");
				editor.putString(ConfigSP.SP_reseach_PNstatus, "已连接").commit();

				// 设置标识位 已经连接成功！
				setIsConnect(true);
				System.out.println("连接成功----" + getIsConnect());
				// -------------------------------------------------
				System.out.println("登录成功");
				// -------------------------------------------------
				xmppManager.runTask();
				// if (IsFirstRegister == true) {
				// IsFirstRegister = false;
				// ServiceManager serviceManager = new ServiceManager(context);
				// Log.i(LOGTAG, "停止服务");
				// // -------------------------------------------------
				// System.out.println("停止服务");
				// // -------------------------------------------------
				// serviceManager.stopService();
				// try {
				// Thread.sleep(10000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				// serviceManager.startService();
				// Log.i(LOGTAG, "再次start");
				// // -------------------------------------------------
				// System.out.println("再次start");
				// // -------------------------------------------------
				// }
			}

		}
	}

}
