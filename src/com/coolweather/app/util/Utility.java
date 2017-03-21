package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvinceResponce(CoolWeatherDB coolWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length>0){
				for(String p : allProvinces){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//将解析出来的数据存储在Province表
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public synchronized static boolean handleCityResponce(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities != null && allCities.length>0){
				for(String c : allCities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//将解析出来的数据存储在City表
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public synchronized static boolean handleCountyResponce(CoolWeatherDB coolWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties != null && allCounties.length>0){
				for(String c : allCounties){
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//将解析出来的数据存储在County表
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(Context context,String response){
		try{
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("data");
			String cityName = weatherInfo.getString("city");
			String weatherDesp = weatherInfo.getString("ganmao");
			String temp = weatherInfo.getString("wendu");
			JSONObject todayWeather = weatherInfo.getJSONArray("forecast").getJSONObject(0);
			String windDir = todayWeather.getString("fengxiang");
			String windStren = todayWeather.getString("fengli");
			String temp1 = todayWeather.getString("low");
			String temp2 = todayWeather.getString("high");
			String type = todayWeather.getString("type");
			saveWeatherInfo(context,cityName,weatherDesp,windDir,windStren,temp1,temp2,type,temp);
		}catch(JSONException e){
			e.printStackTrace();
		}
	}


	private static void saveWeatherInfo(Context context, String cityName ,String weatherDesp, String windDir, String windStren, String temp1,String temp2,String type,String temp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor = context.getSharedPreferences("weather_info", context.MODE_PRIVATE).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weatherDesp", weatherDesp);
		editor.putString("windDir", windDir);
		editor.putString("windStren", windStren);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("temp", temp);
		editor.putString("type", type);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
