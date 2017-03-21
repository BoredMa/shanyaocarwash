package com.coolweather.app.activity;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.example.shanyaocarwash.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements android.view.View.OnClickListener{

	private LinearLayout weatherInfoLayout;
	/**
	 * 用于显示城市名
	 */
	public static TextView cityNameText;
	/**
	 * 用于显示加载中
	 */
	public static TextView loadingText;
	/**
	 * 用于显示天气类型 
	 */
	public static TextView type;
	/**
	 * 用于显示天气描述信息
	 */
	public static TextView weatherDespText;
	/**
	 * 用于显示气温1
	 */
	public static TextView temp1Text;
	/**
	 * 用于显示气温连接符
	 */
	public static TextView prefix;
	/**
	 * 用于显示气温2
	 */
	public static TextView temp2Text;
	/**
	 * 用于显示风向
	 */
	public static TextView windDir;
	/**
	 * 用于显示风力
	 */
	public static TextView windStren;
	/**
	 * 用于显示当前日期
	 */
	public static TextView currentDateText;
	
	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;
	
	/**
	 * 判断是否从WeatherActivity中来
	 */
	public static boolean isFromWeatherActivity = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//初始化各控件
		cityNameText = (TextView) findViewById(R.id.city);
		loadingText = (TextView) findViewById(R.id.loading_text);
		type = (TextView) findViewById(R.id.type);
		weatherDespText = (TextView) findViewById(R.id.weather_descrip);
		temp2Text = (TextView) findViewById(R.id.temp2);
		prefix = (TextView) findViewById(R.id.prefix);
		temp1Text = (TextView) findViewById(R.id.temp1);
		windDir = (TextView) findViewById(R.id.wind_dir);
		windStren = (TextView) findViewById(R.id.wind_stren);
		currentDateText = (TextView) findViewById(R.id.date);
		
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		
		String cityName = getIntent().getStringExtra("city_name");
		if(!TextUtils.isEmpty(cityName)){
			//有城市名就去查询
			loadingText.setText("同步中...");
			prefix.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			isFromWeatherActivity = true;
			queryWeatherInfo(cityName);
		}else{
			//没有城市名
			showWeather();
		}
	}

	private void showWeather() {
		SharedPreferences prefs = getSharedPreferences("weather_info", MODE_PRIVATE);
		cityNameText.setText(prefs.getString("city_name", ""));
		windStren.setText(prefs.getString("windStren",""));
		windDir.setText(prefs.getString("windDir",""));
		type.setText(prefs.getString("type",""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		currentDateText.setText(prefs.getString("current_date", ""));
		cityNameText.setVisibility(View.VISIBLE);
		prefix.setVisibility(View.VISIBLE);
		loadingText.setVisibility(View.INVISIBLE);
		startService(new Intent(this,AutoUpdateService.class));
	}



	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 * @param address
	 * @param string
	 */
	private void queryFromServer(final String address) {
		HttpUtil.sendHttpRequest(address);
	}

	protected void queryWeatherInfo(String cityName) {
		String address = "http://wthrcdn.etouch.cn/weather_mini?city=";
		try {
			address += URLEncoder.encode(cityName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		queryFromServer(address);
	}



	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			loadingText.setVisibility(View.VISIBLE);
			loadingText.setText("同步中...");
			SharedPreferences prefs = getSharedPreferences("weather_info", MODE_PRIVATE);
			String cityName = prefs.getString("city_name", "");
			if(!TextUtils.isEmpty(cityName)){
				queryWeatherInfo(cityName);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isFromWeatherActivity = false;
	}

	
}
