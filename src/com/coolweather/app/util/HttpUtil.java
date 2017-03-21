package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.service.AutoUpdateService;
import com.example.bean.User;
import com.example.fragments.MineFragment;
import com.example.shanyaocarwash.MainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

public class HttpUtil {

	protected static final int GET_WEATHER = 0;
	
	public static void sendHttpRequest(final String address){
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			
			HttpURLConnection connection = null;
			try{
				URL url = new URL(address);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(8000);
				connection.setReadTimeout(8000);
				// 设置请求的头  
				connection.setRequestProperty("Connection", "keep-alive");  
				connection  
	                    .setRequestProperty("User-Agent",  
	                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder response = new StringBuilder();
				String line;
				while((line = reader.readLine()) != null){
					response.append(line);
				}
				Message msg = new Message();
				msg.what = GET_WEATHER;
				msg.obj = response.toString();
				AutoUpdateService.handler.sendMessage(msg);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(connection != null){
					connection.disconnect();
				}
			}
			}
		}).start();
		
	}
	
	public static void sendHttpAreaRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			
			HttpURLConnection connection = null;
			try{
				URL url = new URL(address);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(8000);
				connection.setReadTimeout(8000);
				// 设置请求的头  
				connection.setRequestProperty("Connection", "keep-alive");  
				connection  
	                    .setRequestProperty("User-Agent",  
	                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder response = new StringBuilder();
				String line;
				while((line = reader.readLine()) != null){
					response.append(line);
				}
				if(listener != null){
					listener.onFinish(response.toString());
				}
			}catch(Exception e){
				if(listener != null)
					listener.onError(e);
			}finally{
				if(connection != null){
					connection.disconnect();
				}
			}
			}
		}).start();
	}
}
