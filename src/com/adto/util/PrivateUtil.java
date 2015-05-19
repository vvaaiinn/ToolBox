package com.adto.util;

import org.json.JSONObject;

public class PrivateUtil {
	//public String json="";
	
	public static String getName(String id,String json) {
		String name = "";
		try {
			JSONObject obj = new JSONObject(json).getJSONObject(id);
			name = obj.getString("name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	
	public static String getPath(int id,String json) {
		String father = "";
		String child=getName(String.valueOf(id),json);
		int tmp=Integer.valueOf(id)/10;
		if(tmp==0){
			father=child;
		}else{
			father=getPath(tmp,json)+"/"+child;
		}
		return father;
	}
}
