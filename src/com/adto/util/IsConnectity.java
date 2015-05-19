package com.adto.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.adto.entity.Constants;


public class IsConnectity {
	public boolean isConnByHttp() {

		boolean isConn = false;
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(Constants.URL + "getContent.php");
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(1000 * 2);
			if (conn.getResponseCode() == 200) {
				isConn = true;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			conn.disconnect();
		}
		return isConn;
	}
}