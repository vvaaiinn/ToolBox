package com.adto.entity;

public class UpdateInfo {
	String version;
	String url;
	String date;
	String description;
	
	public UpdateInfo() {
	}
	public UpdateInfo(String version, String url, String date) {
		super();
		this.version = version;
		this.url = url;
		this.date = date;
	}
	public UpdateInfo(String version, String url,String date, String description) {
		this.version = version;
		this.url = url;
		this.description = description;
		this.date = date;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
