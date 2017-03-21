package com.example.newslist;

public class News {
	
	private String title;
	
	private String url;

	public News(String title, String url) {
		super();
		this.title = title;
		this.url = url;
	}

	public News() {
		// TODO Auto-generated constructor stub
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
