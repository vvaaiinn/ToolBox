package com.adto.toolbox;

import com.adto.util.SysApplication;
import com.adto.util.UserUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MyinfoActivity extends Activity {

	TextView phone,logout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		SharedPreferences login=getSharedPreferences("setting",0);
		String islogin=login.getString("isLogin", "");
		Log.e("",islogin+"--");
		if(islogin.equals("login")){
			setContentView(R.layout.activity_myinfo);
			phone=(TextView)findViewById(R.id.phone);
			phone.setText(login.getString("phone", ""));
			logout=(TextView)findViewById(R.id.logoutText);
			logout.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					Log.e("","dialog");
					new AlertDialog.Builder(MyinfoActivity.this)
					.setTitle("退出提示")
					.setMessage("您确定退出吗？")
					// 设置内容
					.setPositiveButton("确定",// 设置确定按钮
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									UserUtil.logout(getApplicationContext());
									Intent intent=new Intent(MyinfoActivity.this,LoginActivity.class);
									startActivity(intent);

								}
							})
					.setNegativeButton("取消",null
							).show();
					
				}
			});
			
		}else{
			//setContentView(R.layout.activity_login);
			Intent intent=new Intent(MyinfoActivity.this,LoginActivity.class);
			startActivity(intent);
		}
	}
}
