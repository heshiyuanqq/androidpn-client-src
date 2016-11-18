package com.lte.util;

import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class XutilsUploader {

	/**
	 * 返回值超时时间
	 */
	static int readTimeOut = 20 * 1000;

	/**
	 * HTTP请求超时时间
	 */
	static int connetTimeOut = 20 * 1000;

	/**
	 * 同步POST请求
	 * 
	 * @param params
	 *            参数列表
	 * @param uri
	 *            请求路径
	 */
	public static String uploadSynMethod(final RequestParams params, final String uri) {
		String reStr = "FAILED";
		HttpUtils http = new HttpUtils();
		http.configSoTimeout(readTimeOut);
		http.configTimeout(connetTimeOut);
		try {
			ResponseStream res = http.sendSync(HttpRequest.HttpMethod.POST, uri, params);
			if (res.getStatusCode() == 200) {
				Log.e("上传文件。。。。。。。", "成功！");
				reStr = "SUCCESS";
			} else {
				Log.e("上传文件。。。。。。。", "失败！");
			}
		} catch (HttpException e) {
			e.printStackTrace();
			Log.e("上传文件。。。。。。。", "出错！" + e.toString());
		}
		return reStr;
	}

	/**
	 * 异步POST请求
	 * 
	 * @param params
	 *            参数列表
	 * @param uri
	 *            请求路径
	 */
	public static void uploadAsynMethod(final RequestParams params, final String uri) {
		HttpUtils http = new HttpUtils();
		http.configSoTimeout(readTimeOut);
		http.configTimeout(connetTimeOut);
		http.send(HttpRequest.HttpMethod.POST, uri, params, new RequestCallBack<String>() {
			@Override
			public void onStart() {
				// msgTextview.setText("conn...");
				Log.e("上传文件。。。。。。。", "开始上传！！");
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				if (isUploading) {
					// msgTextview.setText("upload: " + current + "/"+
					// total);
				} else {
					// msgTextview.setText("reply: " + current + "/"+
					// total);
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// msgTextview.setText("reply: " + responseInfo.result);
				Log.e("上传文件。。。。。。。", "成功！！");
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Log.e("上传文件。。。。。。。", "失败！！" + msg + ":" + error.getExceptionCode());
				// msgTextview.setText(error.getExceptionCode() + ":" +
				// msg);
			}
		});
	}
}
