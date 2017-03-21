package com.example.login;

import java.util.HashMap;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.addmycar.Const;
import com.example.bean.User;
import com.example.shanyaocarwash.MainActivity;
import com.example.shanyaocarwash.R;
import com.example.web.WebServicePost;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

public class LoginFilter extends Activity{

	private SharedPreferences pref;
	private SharedPreferences.Editor editor;

	private String phoneNum;
	private String getInfoPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/GetInfo";
	private String RequestPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/ExecuteEvents";
	
	protected static final int GET_INFO = 2;
	
	Map<String, String> params = new HashMap<String, String>();
	private JSONObject jsonObj;
	
	private User user;
	
	private Context that;
	
	public static boolean connected = false;
	public static boolean lastConnected = false;
	public static final int GET_CONNECTED = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loginfilter);
		that = LoginFilter.this;
		isConnectedToInternet();		
	}

	private void Judge() {
		pref = getSharedPreferences("shanyaodata", MODE_PRIVATE);
		editor = getSharedPreferences("shanyaodata", MODE_PRIVATE).edit();
		boolean logined = pref.getBoolean("logined", false);
		phoneNum = pref.getString("phone", "");
		if(logined){
			sendGetInfoReq();
			finish();
		}else{
			startActivity(new Intent(this,Login.class));
			finish();
		}
	}

	private void isConnectedToInternet() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					StringBuilder url = new StringBuilder();
					url.append(Const.NEWS_URL);
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(url.toString());
					httpGet.addHeader("Accept_Language","zh-CN");
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if(httpResponse.getStatusLine().getStatusCode() == 200){
						connected = true;
					}else{
						connected = false;
					}
					
				}catch(Exception e){
					e.printStackTrace();
					connected = false;
				}finally{
					Message msg = new Message();
					msg.what = GET_CONNECTED;
					msg.obj = connected;
					handler.sendMessage(msg);
				}
			  }
			}).start();
	}
	
	
	private void sendGetInfoReq() {
		new Thread(new Runnable() {
			@Override
			public void run() {	
				String result = "";
				try {
					params.put("type", "getinfo");
					params.put("phone", phoneNum);
					result = WebServicePost.sendPOSTRequest(RequestPath, params, "utf-8");
					Message msg = new Message();
					msg.what = GET_INFO;
					msg.obj = result;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();	
	}

	private Handler handler = new Handler() { 
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_INFO:
				String info = (String) msg.obj;
				user = new User();
				try {
					jsonObj = new JSONObject(info);
					user = user.geUser(jsonObj);
					Intent intent = new Intent(LoginFilter.this, MainActivity.class);
					intent.putExtra("user", new Gson().toJson(user));
					startActivity(intent);
					finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case GET_CONNECTED:
				if((boolean)msg.obj){					
						Judge();
				}else{
					MainActivity.showNoInternetDialog(that);
				}
				break;
			}
		}

	};
	
}
