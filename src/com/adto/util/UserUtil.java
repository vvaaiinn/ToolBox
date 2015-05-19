package com.adto.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

import com.adto.entity.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;


public class UserUtil {
	static SharedPreferences isLogin;	//存储用户登陆状态的
	static final String SETTING="setting";

		public static boolean isLogin(Context thisContext){
			boolean loginState=false;
			isLogin=thisContext.getSharedPreferences(SETTING,0);
			String loginOrNot=isLogin.getString("isLogin", "none_login");
			if(loginOrNot.equals("login"))
			{
				loginState=true;
			}
			return loginState;
		}

		public static String getPhone(Context thisContext){
			String phone;
			isLogin=thisContext.getSharedPreferences(SETTING, 0);
			phone=isLogin.getString("phone", "");
			return phone;
		}

		/**
		 * 用户登陆时在Preference里记录下登陆的状态
		 */
		public static void login(Context thisContext,String phone,UserInfo user){
			SharedPreferences.Editor editor=thisContext.getSharedPreferences(SETTING,0).edit();
			editor.putString("isLogin", "login");
			editor.putString("phone", phone);
			editor.putString("validkey", user.getValidkey());
			editor.commit();
		}
		
		/**
		 * 用户退出时在Preference里记录下未登录的状态
		 */
		public static void logout(Context thisContext){
			SharedPreferences.Editor editor=thisContext.getSharedPreferences(SETTING, 0).edit();
			editor.putString("phone", "");
			editor.putString("isLogin", "none_login");
			editor.putString("validkey", "");
			//editor.clear();
			editor.commit();
		}
		
		
	}
