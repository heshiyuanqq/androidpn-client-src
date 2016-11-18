package com.lte.function;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPConnector;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.connectors.DirectConnector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FTPDown {

	private String host; // 主机名或IP
	private int port; // ftp端口
	private String userName; // ftp用户名
	private String passWord; // ftp密码
	private String filePath; // 下载存储文件夹
	private String fileName; // 文件名
	private int downTime; // 下载耗时（毫秒）
	private int linkTime;// 链接时间（秒）
	private int processNum; // 线程数
	private int maxNum = 1; // 最大连接次数

	public FTPDown(String host, int port, String userName, String passWord,
			String filePath, String fileName, int downTime, int linkTime,
			int processNum, int maxNum) {
		super();
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.passWord = passWord;
		this.filePath = filePath;
		this.fileName = fileName;
		this.downTime = downTime;
		this.linkTime = linkTime;
		this.processNum = processNum;
		this.maxNum = maxNum;
	}

	public FTPDown(String host, int port, String filePath, String fileName,
			int downTime, int linkTime, int processNum, int maxNum) {
		super();
		this.host = host;
		this.port = port;
		this.userName = "admin";
		this.passWord = "123456";
		this.filePath = filePath;
		this.fileName = fileName;
		this.downTime = downTime;
		this.linkTime = linkTime;
		this.processNum = processNum;
		this.maxNum = maxNum;
	}

	/**
	 * 创建ftp连接
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

	private void conn(FTPClient client) {
		// TODO 连接服务器

		// 登陆服务器
		try {

			client.connect(host, port);
			client.login(userName, passWord);
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

		finally {

			if (client.isConnected()) {
				System.out.println("连接成功");
			} else {
				System.out.println("连接失败,重新连接");
				maxNum--;
				if (maxNum > 0) {
					conn(client);
				} else {
					System.out.println("超过链接次数");
				}
			}
		}

	}

	/**
	 * FTP下载文件到本地
	 * 
	 * @param client
	 *            ftp客户端
	 * @param filePath
	 *            下载存储文件夹
	 * @param fileName
	 *            文件名
	 * @return
	 */

	public void getDownloadFile(FTPClient client, String filePath,
			String fileName) {

		File downLoadDirFile = new File(filePath); // 下载地址File

		if (downLoadDirFile.exists() == false) {
			downLoadDirFile.mkdirs();
		}

		// 下载
		File ftpDownFileFile = new File(downLoadDirFile + File.separator
				+ fileName); // 下载文件File
		try {

			client.download(fileName, ftpDownFileFile);// 进行下载并保存文件

		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
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
		} catch (FTPDataTransferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 多线程下载文件 到本地
	 * 
	 */
	public void getDownloadFileList() {
		long beginTime = System.currentTimeMillis();
		final List<FTPClient> clients = new ArrayList<FTPClient>();

		System.out.println("开始下载");
		for (int i = 0; i < processNum; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					FTPClient client = makeFtpConnection();

					if (client.isConnected()) {
						client.setCharset("utf-8");// 设置编码格式
						clients.add(client);
						client.setType(FTPClient.TYPE_AUTO);
						getDownloadFile(client, filePath, fileName);

					} else {
						System.out.println("子线程连接失败");
					}
				}
			}).start();
		}
		while (System.currentTimeMillis() - beginTime < downTime) {
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

		System.out.println("超时断线");
		for (FTPClient client : clients) {

			try {
				client.abortCurrentDataTransfer(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPIllegalReplyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// closeConnection(client);
		}
	}

	/**
	 * 关闭FTP连接，关闭时候像服务器发送一条关闭命令
	 * 
	 * @param client
	 *            ftp客户端
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

	// /**
	// * 所有FTPClient的集合
	// */
	// private ArrayList<FTPClient> ftpClientList = new ArrayList<FTPClient>();

	// public static void main(String[] args) {
	// ftpDown("192.168.1.4", "ftpdown", "work.rar", "10", "1");
	// }

	// /**
	// * ftp下载
	// *
	// * @param destIP
	// * 服务器地址
	// * @param filePath
	// * 下载存储文件夹
	// * @param fileName
	// * 文件名
	// * @param timeout
	// * 时限
	// * @param ProcessNum
	// * 并行线程数量
	// */
	// public void ftpDown(final String destIP, final String filePath,
	// final String fileName, String timeout, String ProcessNum) {
	// final ArrayList<FTPClient> ftpClientList = new ArrayList<FTPClient>();
	// for (int i = 0; i < Integer.parseInt(ProcessNum); i++) {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// try {
	// FTPClient ftpClient = new FTPClient();
	// ftpClient.setPassive(false);
	// /*
	// * ftp4j.activeDataTransfer.acceptTimeout A value in
	// * milliseconds that will be picked as the connection
	// * timeout. If the server does not connect the client
	// * within the given timeout, the transfer is interrupted
	// * throwing a FTPDataTransferException. A value equal to
	// * 0 means that no timeout will be applied. Default
	// * value is 30000 (30 seconds).
	// */
	// System.setProperty(
	// "ftp4j.activeDataTransfer.acceptTimeout",
	// "60000");
	// FTPConnector ftpConnector = new DirectConnector();
	// ftpConnector.setConnectionTimeout(20);
	// // 将ftpClient加入ftpClientList，供停止时调用
	// ftpClientList.add(ftpClient);
	// System.out.println(destIP);
	// ftpClient.setConnector(ftpConnector);
	// // ftpClient.setConnector(new
	// // FTPProxyConnector("192.168.1.4", 1080));
	// // ftpClient.getConnector().setConnectionTimeout(0);
	// // ftpClient.getConnector().setReadTimeout(0);
	// // ftpClient.setSSLSocketFactory(getSocketFactory());
	// // also tried SECURITY_FTPS
	// // ftpClient.setSecurity(FTPClient.SECURITY_FTPES);
	// ftpClient.connect(destIP, 21); // 连接FTP
	// ftpClient.login("123", "123"); // 登陆
	// ftpClient.setCharset("utf-8"); // 设置编码
	// // 若FTP连接成功，则开始下载
	// if (ftpClient.isConnected() == true) {
	// // String appRootDir =
	// // Environment.getExternalStorageDirectory()+File.separator+
	// // context.getPackageName(); //SDCard中应用根目录
	// File downLoadDirFile = new File(filePath); // 下载地址File
	// // System.out.println("appRootDir："+
	// // downLoadDirFile);
	// // 若下载文件夹不存在，则创建
	// if (downLoadDirFile.exists() == false) {
	// downLoadDirFile.mkdirs();
	// }
	// // 下载
	// File ftpDownFileFile = new File(downLoadDirFile
	// + File.separator + fileName); // 下载文件File
	// ftpClient.download(fileName, ftpDownFileFile);
	// }
	// } catch (IllegalStateException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (FTPIllegalReplyException e) {
	// e.printStackTrace();
	// } catch (FTPException e) {
	// e.printStackTrace();
	// } catch (FTPDataTransferException e) {
	// e.printStackTrace();
	// } catch (FTPAbortedException e) {
	// e.printStackTrace();
	// }
	// }
	// }).start();
	// }
	// }

}
