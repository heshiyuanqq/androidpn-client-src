package com.lte.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

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
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class NetUtil {

	private static NetUtil netUtil;

	public static NetUtil getInstance() {
		if (netUtil == null) {
			netUtil = new NetUtil();
		}
		return netUtil;
	}

	/**
	 * 上传文件/数据
	 * 
	 * @param url
	 * @return 文件路径/数据 NameValuePair
	 */
	// private static HttpPost post;
	// private static String result = "";
	public String httpUpload(Context context, String url, List<NameValuePair> params) {
		String result = "";
		if (ConnectivityManagerUtil.getInstance(context).isConnectivity()) {
			HttpPost post = null;
			try {
				// 将客户端数据封装到实体中//将实体添加到请求中
				HttpClient client = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				// 请求超时
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
				client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
						Charset.forName("UTF-8"));// 關鍵的一句,讓API識別到charset
				post = new HttpPost(url);
				post.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
						Charset.forName("UTF-8"));// 關鍵的一句,讓API識別到charset
				// post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				// "UTF-8");
				// post.addRequestHeader("Content-Type","text/html;charset=UTF-8");
				// post.setRequestHeader("Content-Type",
				// "text/html;charset=UTF-8");
				MultipartEntity multipartEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				for (NameValuePair nameValuePair : params) {
					if (nameValuePair.getName().contains("file")) {// zipfile
						if (nameValuePair.getValue() != null) {
							// file:///mnt/sdcard/MCache/14093.png
							String allpath = nameValuePair.getValue();
							String filename = allpath.substring(allpath.lastIndexOf("/") + 1);

							if (allpath.contains("file://")) {
								allpath = allpath.replace("file://", "");
							}
							File file = new File(allpath);
							InputStream in = null;
							try {
								in = new FileInputStream(file);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
								// System.out.println(e);
							}
							// InputStreamBody inputStreamBody = new
							// InputStreamBody(in, filename);
							filename = URLEncoder.encode(filename, "UTF-8");
							Log.e("文件name", filename);
							InputStreamBody inputStreamBody = new InputStreamBody(in, filename);
							multipartEntity.addPart(nameValuePair.getName(), inputStreamBody);
						}
					} else {
						StringBody st = new StringBody(nameValuePair.getValue(),
								Charset.forName(HTTP.UTF_8));
						multipartEntity.addPart(nameValuePair.getName(), st);// 添加普通数据参数
					}
				}
				post.setEntity(multipartEntity);
				// 添加客户端提交数据
				HttpResponse res = client.execute(post, localContext);
				if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = res.getEntity();
					// 返回页面信息
					if (entity != null) {
						result = EntityUtils.toString(entity, "UTF-8");
					}
				} else {
					result = "服务器连接失败！";
				}
			} catch (Exception e) {
				e.printStackTrace();
				// System.out.println(e);
				result = "服务器连接失败！！";
			} finally {
				post.abort();
			}
		} else {
			result = "请检查网络连接！";
		}
		return result;
	}

}
