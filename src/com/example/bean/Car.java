package com.example.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class Car {
	private String id;
	
	private String carId;
	
	private String imageUrl;
	
	private String brand;
	
	private String plateNum;
	
	private String color;

	public Car(String id,String carId, String imageUrl, String brand, String plateNum, String color) {
		super();
		this.id = id;
		this.carId = carId;
		this.imageUrl = imageUrl;
		this.brand = brand;
		this.plateNum = plateNum;
		this.color = color;
	}
	
	public Car(){}

	public String getId(){
		return id;
	}
	
	public String getImageUrl(){
		return imageUrl;
	}

//	public Car(String plateNum, String color) {
//	super();
//	this.plateNum = plateNum;
//	this.color = color;
//}

	public String getCarId() {
		return carId;
	}
	
	public String getBrand() {
		return brand;
	}

	public String getPlateNum() {
		return plateNum;
	}

	public String getColor() {
		return color;
	}


	
}
