package com.example.loglist;

public class Log {
	
	private String logDate;
	
	private String logContent;
	
	public Log(String logDate, String logContent) {
		super();
		this.logDate = logDate;
		this.logContent = logContent;
	}

	public String getLogDate() {
		return logDate;
	}

	public String getLogContent() {
		return logContent;
	}
	
}
