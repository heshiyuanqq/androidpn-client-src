package com.lte.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.lte.config.ConfigSP;

import android.content.Context;
import android.util.Log;

public class PowerResponce {
	private static PowerResponce pre;

	public static PowerResponce getInstance() {
		if (pre == null) {
			pre = new PowerResponce();
		}
		return pre;
	}
	
	public String responce(Context context, String strPowerType){
		String strImsi = context.getSharedPreferences(ConfigSP.SP_reseach,
				Context.MODE_PRIVATE).getString(ConfigSP.SP_reseach_Imsi, "");// 手机卡唯一标识
		// 从存储文件内读取ip
		String ip = context.getApplicationContext()
				.getSharedPreferences(ConfigSP.SP_reseach,// 表名
						Context.MODE_PRIVATE).getString(ConfigSP.SP_reseach_ip,// key名
						"");
		String serverIp = "http://" + ip;
		String url = serverIp + ":8080/ResearchProject/terminal/requestPower"
				+ "?method=GET&strImsi=" + strImsi + "&strPowerType="
				+ strPowerType;// url地址

		String result = "";// 存储返回值

		Log.i("url", url);

		/* 建立HTTP Get对象 */
		HttpGet httpRequest = new HttpGet(url);

		try {
			/* 发送请求并等待响应 */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String response = EntityUtils
						.toString(httpResponse.getEntity());
				if ("SUCCESS".equals(response)) {
					System.out.println("电量检测请求成功");
					result = "请求成功";
				} else if ("FAILED".equals(response)) {
					System.out.println("电量检测请求失败");
					result = "请求失败";
				}
				// else {
				// System.out.println("电量检测请求意外故障" + response);
				// result = "意外请求故障";
				// }
			}

			// } catch (Exception e) {
			// System.out.println("Http请求失败");
			// result = "Http请求失败";
		} catch (ClientProtocolException e) {
			result = "Http请求失败"
//		+ "1ClientProtocolException:" + e
					;
		} catch (IOException e) {
			result = "Http请求失败"
//		+ "2IOException:" + e
					;
		} catch (Exception e) {
			result = "Http请求失败"
//		+ "3Exception:" + e
					;
		}

		return result;
	}
}
