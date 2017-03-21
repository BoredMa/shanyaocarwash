package com.example.weather;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;

import com.coolweather.app.util.HttpUtil;

public class Weather {

	public static void queryWeatherInfo(String currentPosition) {
		String address = "http://wthrcdn.etouch.cn/weather_mini?city=";
		try {
			address += URLEncoder.encode(currentPosition, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		queryFromServer(address);
	}

	private static void queryFromServer(String address) {
		HttpUtil.sendHttpRequest(address);		
	}

}
