package com.lte.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class FnUtil {

	private static FnUtil fnUtil;
	
	public static FnUtil getInstance() {
		if (fnUtil == null) {
			fnUtil = new FnUtil();
		}
		return fnUtil;
	}
	
/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
 */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale;
    }

/**
 * 将数据流写入文件
 * @param inputStream
 * @param frpUpFile 目标文件
 */
	public void writeStreamToFile(InputStream inputStream, File frpUpFile) {
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
	 * @param str	
	 * @param pathString
	 */
	public void writeStringToFile(String str, String dirString, String pathString) {
		//文件不存在则创建
		File dirFile = new File(dirString);
		if (dirFile.exists() == false) {
			dirFile.mkdirs();
		}
		InputStream inputStream = new ByteArrayInputStream(str.getBytes());
		FileOutputStream fileOS = null;
		try {
			fileOS = new FileOutputStream(pathString, true);				//true表示在文件末尾追加
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
 * 子线程中发出Toast
 * @param context
 * @param text
 */
	public void showToastOnUIThread(final Context context, final String text) {
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
/**
 * 从Assets中读取图片Bitmap
 */
	public Bitmap getImageBitmapFromAssets(Context context, String fileName) {
		Bitmap imageBitmap = null;
		AssetManager am = context.getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			imageBitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageBitmap;
	}
	
	/**
	 * 正则表达式查找<br>
	 * 结果不完整<br>
	 * 若/12/23/34/匹配/(.*?)/，结果为{"12", "34"}
	 * @param str	原始字符串
	 * @param regex 正则
	 * @return 匹配结果List
	 */
	public ArrayList<String> getRegexList(String str, String regex) {
		// String str =
		// "rrwerqq84461376qqasfdasdfrrwerqq84461377qqasfdasdaa654645aafrrwerqq84461378qqasfdaa654646aaasdfrrwerqq84461379qqasfdasdfrrwerqq84461376qqasfdasdf";
		// Pattern p = Pattern.compile("/(.*?)/");
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		ArrayList<String> regexList = new ArrayList<String>();
		while (matcher.find()) {
			regexList.add(matcher.group(1));
		}
		for (String s : regexList) {
//			System.out.println(s);
		}
		return regexList;
	}
	
	/**
	 * 获取文件MIME类型
	 */
	public String getMIMEType(String fName) {
		String type = "*/*";
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	/**
	 * 后缀名与MIME类型对应关系
	 * 可以自己随意添加
	 */
	private String[][] MIME_MapTable = {
			// {后缀名，MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" },
			{ ".rc", "text/plain" },
			{ ".rmvb", "audio/x-pn-realaudio" },
			{ ".rtf", "application/rtf" },
			{ ".sh", "text/plain" },
			{ ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" },
			{ ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" },
			{ ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" },
			{ ".xml", "text/plain" },
			{ ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" },
			{ "", "*/*" }
	};
	
	/**
	 * 判断网络连接是否可用
	 * @param context
	 * @return
	 */
	public boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
		} else {
			// 如果仅仅是用来判断网络连接
			// 则可以使用 cm.getActiveNetworkInfo().isAvailable();
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断WIFI是否打开
	 * @param context
	 * @return
	 */
	public boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || 
				mgrTel.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}
	
	/**
	 * 判断是否是移动网络
	 * @param context
	 * @return
	 */
	public boolean isMobileEnabled(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
		if (networkINfo != null && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是WiFi还是移动网络
	 * @param context
	 * @return
	 */
	public boolean isWifi(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
		if (networkINfo != null && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断GPS是否打开
	 * @param context
	 * @return
	 */
	public boolean isGpsEnabled(Context context) {
		LocationManager lm = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = lm.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}
	
	/**
	 * 字符串中提取数字（包含小数点）
	 * @return
	 */
	public String getNumFromString(String string) {
		String regEx = "[^(0-9\\.)]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(string);
		return m.replaceAll("").trim();
	}
	
	/**
	 * 将int型networkType转换为String类型
	 * @param networkType
	 * @return
	 */
	public String convertNetworkType(int networkType) {
		String networkTypeString =null;
		switch (networkType) {
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				networkTypeString = "UNKNOWN";
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS:
				networkTypeString = "GPRS";
				break;
			case TelephonyManager.NETWORK_TYPE_EDGE:
				networkTypeString = "EDGE";
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:
				networkTypeString = "UMTS";
				break;
			case TelephonyManager.NETWORK_TYPE_CDMA:
				networkTypeString = "CDMA";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				networkTypeString = "EVDO_0";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				networkTypeString = "EVDO_A";
				break;
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				networkTypeString = "1xRTT";
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				networkTypeString = "HSDPA";
				break;
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				networkTypeString = "HSUPA";
				break;
			case TelephonyManager.NETWORK_TYPE_HSPA:
				networkTypeString = "HSPA";
				break;
			case TelephonyManager.NETWORK_TYPE_IDEN:
				networkTypeString = "IDEN";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
				networkTypeString = "EVDO_B";
				break;
			case TelephonyManager.NETWORK_TYPE_LTE:
				networkTypeString = "LTE";
				break;
			case TelephonyManager.NETWORK_TYPE_EHRPD:
				networkTypeString = "EHRPD";
				break;
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				networkTypeString = "HSPAP";
				break;
			default:
				networkTypeString = "Android未知网络类型，可能为TD-SCDMA";
				break;
		}
		return networkTypeString;
	}
	
	/**
	 * 将int型connectionState（连接状态）转换为String类型
	 * @param connectionState
	 * @return
	 */
	public String convertConnectionState(int connectionState) {
		String connectionStateString =null;
		switch (connectionState) {
			case TelephonyManager.DATA_DISCONNECTED:
				connectionStateString = "DISCONNECTED";
				break;
			case TelephonyManager.DATA_CONNECTING:
				connectionStateString = "CONNECTING";
				break;
			case TelephonyManager.DATA_CONNECTED:
				connectionStateString = "CONNECTED";
				break;
			case TelephonyManager.DATA_SUSPENDED:
				connectionStateString = "SUSPENDED";
				break;
			default:
				connectionStateString = "UNKNOWN";
				break;
		}
		return connectionStateString;
	}
	
//	/**
//	 * 上传文件/数据
//	 * @param url
//	 * @param client
//	 * @return 文件路径/数据 NameValuePair
//	 */
////	private static HttpPost post;
////	private static String result = "";
//	public String httpUpload(Context context, String url, List<NameValuePair> params) {
//		String result = "";
//		if (ConnectivityManagerUtils.getInstance(context).isConnectivity()) {
//			HttpPost post = null;
//			try {
//				// 将客户端数据封装到实体中//将实体添加到请求中
//				HttpClient client = new DefaultHttpClient();
//				HttpContext localContext = new BasicHttpContext();
//				// 请求超时
//				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
//				post = new HttpPost(url);
//				MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//				for (NameValuePair nameValuePair : params) {
//					if (nameValuePair.getName().contains("file")) {
//						if (nameValuePair.getValue() != null) {
//							// file:///mnt/sdcard/MCache/14093.png
//							String allpath = nameValuePair.getValue();
//							String filename = allpath.substring(allpath.lastIndexOf("/") + 1);
//							if (allpath.contains("file://")) {
//								allpath = allpath.replace("file://", "");
//							}
//							File file = new File(allpath);
//							InputStream in = null;
//							try {
//								in = new FileInputStream(file);
//							} catch (FileNotFoundException e) {
//								e.printStackTrace();
//							}
//							InputStreamBody inputStreamBody = new InputStreamBody(in, filename);
//							multipartEntity.addPart(nameValuePair.getName(), inputStreamBody);
//						}
//					} else {
//						StringBody st = new StringBody(nameValuePair.getValue(), Charset.forName(HTTP.UTF_8));
//						multipartEntity.addPart(nameValuePair.getName(), st);// 添加普通数据参数
//					}
//				}
//				post.setEntity(multipartEntity);
//				// 添加客户端提交数据
//				HttpResponse res = client.execute(post, localContext);
//				if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//					HttpEntity entity = res.getEntity();
//					// 返回页面信息
//					if (entity != null) {
//						result = EntityUtils.toString(entity, "UTF-8");
//					}
//				} else {
//					result = "服务器连接失败！";
//				}
//			} catch (Exception e) {
//				result = "服务器连接失败！";
//			} finally {
//				post.abort();
//			}
//		} else {
//			result = "请检查网络连接！";
//		}
//		return result;
//	}
	
	/**
	 * 拨打电话
	 * @param context Context
	 * @param phoneNumber 被叫电话号码
	 */
	public void callPhone(Context context, String phoneNumber) {
		Uri callUri = Uri.parse("tel:"+ phoneNumber);
		Intent callIntent = new Intent(Intent.ACTION_CALL, callUri);
		callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(callIntent);
	}
	
//	/**
//	 * 挂断电话
//	 * @param context Context
//	 */
//	public void hangUp(Context context) {
//		ITelephony itelephony = this.getITelephony(context);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		try {
//			itelephony.endCall();
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}
	
//	/**
//	 * 用反射方法获取ITelephony对象<br>
//	 * 用于终端自动接听和挂断
//	 * @param context Context
//	 * @return ITelephony
//	 */
//	private ITelephony getITelephony(Context context) {
//		TelephonyManager mTelephonyManager = (TelephonyManager) context	.getSystemService(Context.TELEPHONY_SERVICE);
//		Class<TelephonyManager> c = TelephonyManager.class;
//		Method getITelephonyMethod = null;
//		ITelephony iTelephony = null;
//		try {
//			getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);									// 获取声明的方法
//			getITelephonyMethod.setAccessible(true);
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		}
//		try {
//			iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelephonyManager, (Object[]) null);	// 获取实例
//			return iTelephony;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return iTelephony;
//	}
	
}
