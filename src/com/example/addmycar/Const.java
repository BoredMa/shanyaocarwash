package com.example.addmycar;

import com.example.web.WebServicePost;

public class Const {
	
	public static final String URL_PREFIX="http://192.168.191.1:8080/WirelessOrder";
	
	/**
	 * 图片上传地址
	 */
	public static final String UPLOAD_URL=URL_PREFIX+"/UploadServlet";
	
	/**
	 * 图片下载地址
	 */
	public static final String DOWNLOAD_URL=URL_PREFIX+"/upload/";

	public static final String WRITE_LOG_URL=URL_PREFIX+"/WriteLogs";
	
	
	public static final String LOG_URL=URL_PREFIX+"/log/";
	
	public static final String REQUEST_URL = URL_PREFIX+"/ExecuteEvents";
	
}
