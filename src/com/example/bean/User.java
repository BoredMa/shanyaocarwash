package com.example.bean;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.SyncStateContract.Constants;

import com.example.addmycar.Const;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class User implements Serializable{
	
	int card_price;
	String phone;
	String name;
	String age;
	String sex;
	int washed_num;
    int remaining_num;
    int remainings;
    String carid;
    String logid;
    Car[] cars;
    
    String[] Ids;
    String[] carIds;
    String[] imageUrls;
	String[] brands;
	String[] plateNums;
	String[] colors;
	
	ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
	
	public User(int card_price, String phone, String name, String age,
			String sex, int washed_num, int remaining_num, int remainings,
			String carid, String logid, Car[] cars) {
		super();
		this.card_price = card_price;
		this.phone = phone;
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.washed_num = washed_num;
		this.remaining_num = remaining_num;
		this.remainings = remainings;
		this.carid = carid;
		this.logid = logid;
		this.cars = cars;
	}


	public User() {
	}


	public int getCard_price() {
		return card_price;
	}

	public String getPhone() {
		return phone;
	}

	public String getName() {
		return name;
	}

	public String getAge() {
		return age;
	}

	public String getSex() {
		return sex;
	}

	public int getWashed_num() {
		return washed_num;
	}

	public int getRemaining_num() {
		return remaining_num;
	}

	public int getRemainings() {
		return remainings;
	}

	public String getCarid() {
		return carid;
	}

	public String getLogid() {
		return logid;
	}
	public Car[] getCars() {
		return cars;
	}
    
	public User geUser(JSONObject jsonObj) {
		User user = null;
		try {
			int card_price = Integer.parseInt(jsonObj.getString("card_price"));
			String phone = jsonObj.getString("phone");
			String name = jsonObj.getString("name");
			String age = jsonObj.getString("age");
			String sex = jsonObj.getString("sex");
			int washed_num = Integer.parseInt(jsonObj.getString("washed_num"));
			int remaining_num = Integer.parseInt(jsonObj
					.getString("remaining_num"));
			int remainings = Integer.parseInt(jsonObj.getString("remainings"));
			String carid = jsonObj.getString("carid");
			String logid = jsonObj.getString("logid");
			
			//获取用户的车辆信息
			String cars = jsonObj.getString("cars");
			getCars(new JSONObject(cars));
			Car[] mycars = geCars();

			user = new User(card_price, phone, name, age, sex, washed_num,
					remaining_num, remainings, carid, logid,mycars);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}
	private Car[] geCars() {
		//如果没车辆的话，返回null
		if(carIds == null){
			return null;
		}
		int len = plateNums.length;
		Car[] cars = new Car[len];
		for(int i=0;i<len;i++){
			cars[i] = new Car(Ids[i],carIds[i],imageUrls[i],brands[i],plateNums[i],colors[i]);
		}
		return cars;
	}


	private void getCars(JSONObject jsonObj) {
		try {
			int len = jsonObj.length();
			// 如果该用户没有车，应该把相应信息设为空
			if (len == 0) {
				Ids = null;
				carIds = null;
				imageUrls = null;
				brands = null;
				plateNums = null;
				colors = null;
			} else {
				Ids = new String[len];
				carIds = new String[len];
				imageUrls = new String[len];
				brands = new String[len];
				plateNums = new String[len];
				colors = new String[len];
				for (int i = 0; i < len; i++) {
					JSONObject car = new JSONObject(
							jsonObj.getString("car" + i));
					Ids[i] = car.getString("id");
					carIds[i] = car.getString("carid");
					imageUrls[i] = car.getString("imageurl");
					brands[i] = car.getString("brand");
					plateNums[i] = car.getString("platenum");
					colors[i] = car.getString("color");
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
