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
	static SharedPreferences isLogin;	//�洢�û���½״̬��
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
		 * �û���½ʱ��Preference���¼�µ�½��״̬
		 */
		public static void login(Context thisContext,String phone,UserInfo user){
			SharedPreferences.Editor editor=thisContext.getSharedPreferences(SETTING,0).edit();
			editor.putString("isLogin", "login");
			editor.putString("phone", phone);
			editor.putString("validkey", user.getValidkey());
			editor.commit();
		}
		
		/**
		 * �û��˳�ʱ��Preference���¼��δ��¼��״̬
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
