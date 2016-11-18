package com.lte.function;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPConnector;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.connectors.DirectConnector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FTPUp {

	private String host; // 主机名或IP
	private int port; // ftp端口
	private String username; // ftp用户名
	private String password; // ftp密码
	private String localfilepath; // 本地文件
	private String remoteFolderPath; // 上传目录
	private int upTime; // 上传时限（毫秒）
	private int linkTime;// 链接时间（秒）
	private int processNum; // 线程数
	private int maxNum = 1; // 最大连接次数

	public FTPUp(String host, int port, String localfilepath,
			String remoteFolderPath, int upTime, int linkTime, int processNum,
			int maxNum) {
		this.host = host;
		this.port = port;
		this.username = "admin";
		this.password = "123456";
		this.localfilepath = localfilepath;
		this.remoteFolderPath = remoteFolderPath;
		this.upTime = upTime;
		this.linkTime = linkTime;
		this.processNum = processNum;
		this.maxNum = maxNum;
	}

	public FTPUp(String host, int port, String username, String password,
			String localfilepath, String remoteFolderPath, int upTime,
			int linkTime, int processNum, int maxNum) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.localfilepath = localfilepath;
		this.remoteFolderPath = remoteFolderPath;
		this.upTime = upTime;
		this.linkTime = linkTime;
		this.processNum = processNum;
		this.maxNum = maxNum;
	}

	/**
	 * 创建FTP连接
	 * 
	 */
	public FTPClient makeFtpConnection() {
		FTPConnector ftpConnector = new DirectConnector();
		ftpConnector.setConnectionTimeout(linkTime);
		// ftpConnector.setReadTimeout(20);
		FTPClient client = new FTPClient();
		// 设置连接参数
		client.setConnector(ftpConnector);
		if (maxNum > 0) {
			conn(client);
		}
		return client;
	}

	public void conn(FTPClient client) {

		// 连接服务器

		try {
			client.connect(host, port);

			// 登陆服务器
			client.login(username, password);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPIllegalReplyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// finally {
		//
		// if (client.isConnected()) {
		// System.out.println("连接成功");
		// } else {
		// System.out.println("连接失败,重新连接");
		// maxNum--;
		// if (maxNum > 0) {
		// conn(client);
		// } else {
		// System.out.println("超过链接次数");
		// }
		// }
		// }

	}

	/**
	 * FTP上传本地文件到FTP的一个目录下
	 * 
	 * @param client
	 *            FTP客户端
	 * @param localfile
	 *            本地文件
	 * @param remoteFolderPath
	 *            FTP上传目录
	 */
	private File getUploadFile(FTPClient client, File localfile,
			String remoteFolderPath) {
		//
		// // 创建上传监听对象
		// MyFtpListener listener = MyFtpListener.instance(FTPOptType.UP);
		try {
			client.changeDirectory(remoteFolderPath);
			// 如果文件不存在
			if (!localfile.exists())
				System.out.println("文件不存在");
			// 如果不是文件
			if (!localfile.isFile())
				System.out.println("非文件类型");
			// if (listener != null)
			// client.upload(localfile, listener);
			// else
			System.out.println(localfile);
			client.upload(localfile);
			// 返回到根目录
			client.changeDirectory("/");
		} catch (Exception e) {
			// e.printStackTrace();
			// System.out.println("上传失败");
		}
		return localfile;
	}

	/**
	 * FTP上传本地文件到FTP的一个目录下
	 * 
	 * @param client
	 *            FTP客户端
	 * 
	 */
	public void getUploadFile(FTPClient client) {
		File localfile = new File(localfilepath);
		getUploadFile(client, localfile, remoteFolderPath);
	}

	/**
	 * 多线程FTP上传本地文件到FTP的一个目录下
	 * 
	 */
	public void getUploadFileList() {

		long beginTime = System.currentTimeMillis();
		final List<FTPClient> clients = new ArrayList<FTPClient>();

		System.out.println("开始上传");
		for (int i = 0; i < processNum; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					FTPClient client = makeFtpConnection();

					if (client.isConnected()) {
						client.setCharset("utf-8");// 设置编码格式
						clients.add(client);
						client.setType(FTPClient.TYPE_AUTO);
						getUploadFile(client);
					} else {
						System.out.println("子线程连接失败");
					}
				}
			}).start();
		}

		while (System.currentTimeMillis() - beginTime < upTime) {
			try {
				Thread.sleep(1000);
				System.out
						.println("上传时间"
								+ (System.currentTimeMillis() - beginTime)
								/ 1000 + "秒");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		// 取消数据传输
		try {
			System.out.println("超时断线");
			for (FTPClient client : clients) {
				client.abortCurrentDataTransfer(true);
				// closeConnection(client);
			}
		} catch (IOException e) {
			System.out.println("ftp下载异常IOException");
			e.printStackTrace();
		} catch (FTPIllegalReplyException e) {
			System.out.println("ftp下载异常FTPIllegalReplyException");
			e.printStackTrace();
		} catch (IllegalStateException e) {
			System.out.println("ftp下载异常IllegalStateException");
			e.printStackTrace();
		}

	}

	/**
	 * 关闭FTP连接，关闭时候像服务器发送一条关闭命令
	 * 
	 * @param client
	 *            FTP客户端
	 * @return 关闭成功，或者链接已断开，或者链接为null时候返回true，通过两次关闭都失败时候返回false
	 */

	public boolean closeConnection(FTPClient client) {
		if (client == null)
			return true;
		if (client.isConnected()) {
			try {
				// 安全退出
				client.disconnect(true);
				return true;
			} catch (Exception e) {
				try {
					// 强制退出
					client.disconnect(false);
				} catch (Exception e1) {
					e1.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
}
