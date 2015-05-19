package com.adto.toolbox;

import android.app.Application;

public class ApplicationData extends Application{
	private String res="";
	private String catalog="";
	private boolean init=true;

	public String getRes() {
		return res;
	}

	public void setRes(String res) {
		this.res = res;
	}
	
	public void onCreate(){
		super.onCreate();
		this.res="";
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}

	
}
