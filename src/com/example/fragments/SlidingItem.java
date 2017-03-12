package com.example.fragments;

public class SlidingItem {

	private String name;
	
	private int imageId;

	public SlidingItem(String name, int imageId) {
		super();
		this.name = name;
		this.imageId = imageId;
	}

	public String getName() {
		return name;
	}

	public int getImageId() {
		return imageId;
	}
	
}
