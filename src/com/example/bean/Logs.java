package com.example.bean;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import android.util.Log;

import com.example.addmycar.Const;

public class Logs {

	private String date;
	
	private String detail;
	
	private static String path;
	
	
	private static Map<String, String> logMap = new HashMap<String, String>();

	public void setDate(String date) {
		this.date = date;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getDate() {
		return date;
	}

	public String getDetail() {
		return detail;
	}
	
	public static void writeLogs(final Logs logs,final String filePath) {
		
		path = Const.WRITE_LOG_URL;
		
		new Thread(){  
			@Override  
            public void run() {  

				String info = "<logs><date>" + logs.getDate()
						+ "</date><detail>" + logs.getDetail()
						+ "</detail></logs>\n";

				HttpURLConnection conn = null;
				InputStream is = null;
				URL realurl = null;
				try {
					realurl = new URL(path);
					conn = (HttpURLConnection)realurl.openConnection();
					conn.setRequestMethod("POST");
					conn.setDoOutput(true);	
					// 设置编码格式
	                conn.setRequestProperty("Charset", "UTF-8");
					OutputStream out = conn.getOutputStream();
					PrintWriter pw = new PrintWriter(out);
					logMap.put("filepath", filePath);
					String logParams = map2Url(logMap);
					pw.write(logParams+"&info="+info);
					pw.flush();
					pw.close();

					is = conn.getInputStream();
					int code = conn.getResponseCode();
					if(code == 200){
						Log.e("test", "chenggong");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}
	
	
	protected static String map2Url(Map<String, String> paramToMap) {
		if (null == paramToMap || paramToMap.isEmpty()) {
			return null;
		}
		StringBuffer url = new StringBuffer();
		boolean isfist = true;
		for (Map.Entry<String, String> entry : paramToMap.entrySet()) {
			if (isfist) {
				isfist = false;
			} else {
				url.append("&");
			}
			url.append(entry.getKey()).append("=");
			String value = entry.getValue();
			if (!TextUtils.isEmpty(value)) {
				try {
					url.append(URLEncoder.encode(value, "utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return url.toString();
	}
	
}
