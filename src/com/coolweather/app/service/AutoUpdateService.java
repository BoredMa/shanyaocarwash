package com.coolweather.app.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.coolweather.app.activity.WeatherActivity;
import com.coolweather.app.receiver.AutoUpdateReciever;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.example.fragments.MineFragment;
import com.example.shanyaocarwash.MainActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

public class AutoUpdateService extends Service {

	protected static final int GET_WEATHER = 0;

	private static final Context mContext = MainActivity.that;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateWeather();				
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour =  4 * 60 * 60 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent i = new Intent(this,AutoUpdateReciever.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	protected void updateWeather() {
		SharedPreferences prefs = getSharedPreferences("weather_info", MODE_PRIVATE);
		String cityName = prefs.getString("city_name", "");
		String address = "http://wthrcdn.etouch.cn/weather_mini?city=";
		try {
			address += URLEncoder.encode(cityName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpUtil.sendHttpRequest(address);

	}

	
	public static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_WEATHER:
				String weatherInfo = (String)msg.obj;
				//处理天气信息，并保持到sharedpreference
				Utility.handleWeatherResponse(mContext, weatherInfo);
				//获取prefs
				SharedPreferences prefs = mContext.getSharedPreferences("weather_info", mContext.MODE_PRIVATE);
				//如果MineFragment加载出来了，更新MineFragment中的布局信息
				if(MineFragment.isFromMineFragment){
					MineFragment.weatherTemp.setText(prefs.getString("temp", "")+"°C");
					MineFragment.weatherType.setText(prefs.getString("type",""));
					MineFragment.weatherReminder.setText(prefs.getString("weatherDesp", ""));
					MineFragment.mLocation.setText(prefs.getString("city_name",""));
				}
				
				
				//如果是从WeatherActivity中发来的请求，更新WeatherActivity中的布局信息
				if(WeatherActivity.isFromWeatherActivity){
					WeatherActivity.cityNameText.setText(prefs.getString("city_name", ""));
					WeatherActivity.windStren.setText(prefs.getString("windStren",""));
					WeatherActivity.windDir.setText(prefs.getString("windDir",""));
					WeatherActivity.type.setText(prefs.getString("type",""));
					WeatherActivity.temp1Text.setText(prefs.getString("temp1", ""));
					WeatherActivity.temp2Text.setText(prefs.getString("temp2", ""));
					WeatherActivity.weatherDespText.setText(prefs.getString("weather_desp", ""));
					WeatherActivity.currentDateText.setText(prefs.getString("current_date", ""));
					WeatherActivity.cityNameText.setVisibility(View.VISIBLE);
					WeatherActivity.prefix.setVisibility(View.VISIBLE);
					WeatherActivity.loadingText.setVisibility(View.INVISIBLE);
				}
				
				break;
			}
		}

	};
}
