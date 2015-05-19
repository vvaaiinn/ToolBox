package com.adto.toolbox;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.adto.entity.Constants;
import com.adto.toolbox.LoginActivity.MainHandler;
import com.adto.util.EncryptUtil;
import com.adto.util.GetPostUtil;
import com.adto.util.IsConnectity;
import com.adto.util.NetUtil;
import com.adto.util.SysApplication;
import com.adto.util.VersionUtil;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	Button registerBtn, sendBtn;
	String phone, name, number, leader, dept, validkey, timestamp, udid,
			captcha;
	EditText phoneEdit, captchaEdit, nameEdit, numberEdit, leaderEdit,
			deptEdit;
	TextView login;
	NetUtil netUtil;
	String res = "";
	Timer timer;
	EventHandler smsEH;
	MainHandler myHandler;

	public static final int SENDCODE = 1;
	public static final int SUBMITCODE = 2;
	public static final int ERROR = 3;
	public static final int SUCCESS = 4;
	public static final int TIMER = 5;
	public static final int RESET = 6;
	public boolean checked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_register);

		myHandler = new MainHandler();
		SMSSDK.initSDK(getApplicationContext(), Constants.APPKEY,
				Constants.APPSECRET);
		SMSSDK.unregisterAllEventHandler();
		smsEH = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {

				if (result == SMSSDK.RESULT_COMPLETE) {
					// 回调完成
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						Log.e("register", "registering");
						checked = true;
						myHandler.sendEmptyMessage(SUBMITCODE);
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						myHandler.sendEmptyMessage(SENDCODE);
					}
				} else {
					((Throwable) data).printStackTrace();
					Bundle b = new Bundle();
					b.putString("err",
							LoginActivity.getErrorMsg(data.toString()));
					Message msg = new Message();
					msg.what = ERROR;
					msg.setData(b);
					myHandler.sendMessage(msg);
					Log.e("unkown err", data.toString());
				}

			}
		};
		SMSSDK.registerEventHandler(smsEH); // 注册短信回调

		netUtil = new NetUtil(getApplicationContext());
		phoneEdit = (EditText) findViewById(R.id.phone);
		captchaEdit = (EditText) findViewById(R.id.captcha);
		nameEdit = (EditText) findViewById(R.id.name);
		numberEdit = (EditText) findViewById(R.id.number);
		leaderEdit = (EditText) findViewById(R.id.leader);
		deptEdit = (EditText) findViewById(R.id.dept);
		registerBtn = (Button) findViewById(R.id.registerBtn);
		sendBtn = (Button) findViewById(R.id.sendcode);
		sendBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!phoneEdit.getText().toString().trim().equals("")) {
					sendBtn.setClickable(false);
					sendBtn.setBackground(getResources().getDrawable(
							R.drawable.btn_unable));
					SMSSDK.getVerificationCode("86", phoneEdit.getText()
							.toString());
				} else {
					phoneEdit.setBackground(getResources().getDrawable(
							R.drawable.err_box));
					Toast.makeText(getApplicationContext(), "号码不能为空",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		login = (TextView) findViewById(R.id.loginText);
		login.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this,
						LoginActivity.class);
				startActivity(intent);
			}
		});

		registerBtn.setOnClickListener(listener_register);

	}

	public boolean isvalid() {
		boolean valid = true;
		phone = phoneEdit.getText().toString();
		name = nameEdit.getText().toString();
		captcha = captchaEdit.getText().toString();
		number = numberEdit.getText().toString();
		leader = leaderEdit.getText().toString();
		dept = deptEdit.getText().toString();

		phoneEdit
				.setBackground(getResources().getDrawable(R.drawable.edit_box));
		captchaEdit.setBackground(getResources().getDrawable(
				R.drawable.edit_box));
		nameEdit.setBackground(getResources().getDrawable(R.drawable.edit_box));
		numberEdit.setBackground(getResources()
				.getDrawable(R.drawable.edit_box));
		leaderEdit.setBackground(getResources()
				.getDrawable(R.drawable.edit_box));
		deptEdit.setBackground(getResources().getDrawable(R.drawable.edit_box));

		if (phone.equals("")) {
			phoneEdit.setBackground(getResources().getDrawable(
					R.drawable.err_box));
			phoneEdit.requestFocus();
			valid = false;
		} else if (captcha.equals("")) {
			captchaEdit.setBackground(getResources().getDrawable(
					R.drawable.err_box));
			captchaEdit.requestFocus();
			valid = false;
		} else if (name.equals("")) {
			nameEdit.setBackground(getResources().getDrawable(
					R.drawable.err_box));
			nameEdit.requestFocus();
			valid = false;
		} else if (number.equals("")) {
			numberEdit.setBackground(getResources().getDrawable(
					R.drawable.err_box));
			numberEdit.requestFocus();
			valid = false;
		} else if (leader.equals("")) {
			leaderEdit.setBackground(getResources().getDrawable(
					R.drawable.err_box));
			leaderEdit.requestFocus();
			valid = false;
		} else if (dept.equals("")) {
			deptEdit.setBackground(getResources().getDrawable(
					R.drawable.err_box));
			deptEdit.requestFocus();
			valid = false;
		}
		return valid;
	}

	private OnClickListener listener_register = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (isvalid()) {

				if (!netUtil.isNetworkConnected()) {
					Toast.makeText(getApplicationContext(), "网络异常",
							Toast.LENGTH_SHORT).show();
				} else {
					SMSSDK.submitVerificationCode("86", phoneEdit.getText()
							.toString(), captchaEdit.getText().toString());
				}

			}
		}
	};

	public void registerAction() {
		String url = Constants.URL + "reg.php";
		StringBuilder params = new StringBuilder();
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		String mac = "macnull";
		if (wm != null) {
			mac=wm.getConnectionInfo().getMacAddress();
		}
		BluetoothAdapter m_BluetoothAdapter = null;
		m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		String bluemac = "bluenull";
		if (m_BluetoothAdapter != null) {
			bluemac=m_BluetoothAdapter.getAddress();
		}
		udid = mac + bluemac;
		timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		Log.e("", phone + udid + "__" + captcha);
		validkey = EncryptUtil.md5(phone + udid).substring(0, 16)
				+ EncryptUtil.md5(captcha).substring(16, 32);

		params.append("phone=" + phone);
		params.append("&captcha=" + captcha);
		params.append("&udid=" + udid);
		params.append("&timestamp=" + timestamp);
		params.append("&name=" + name);
		params.append("&department=" + dept);
		params.append("&wid=" + number);
		params.append("&mentor=" + leader);
		params.append("&validkey=" + validkey);
		params.append("&ver=" + VersionUtil.getVerName(getApplicationContext()));
		res = GetPostUtil.sendPost(url, params.toString());
		Log.e("", res);
		try {
			JSONObject obj = new JSONObject(res);
			Log.e("", params.toString());
			if (obj.get("status").toString().equals("0")) {
				myHandler.sendEmptyMessage(SUCCESS);
				/*
				 * SharedPreferences settings = getSharedPreferences("setting",
				 * 0); SharedPreferences.Editor editor = settings.edit();
				 * editor.putString("udid", udid); editor.putString("validkey",
				 * validkey); editor.commit();
				 */
				Intent intent = new Intent(RegisterActivity.this,
						LoginActivity.class);
				startActivity(intent);
				SMSSDK.unregisterAllEventHandler();
				Log.e("msg", "success");
			} else {
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("err", obj.get("description").toString());
				
				msg.what = ERROR;
				msg.setData(b);
				myHandler.sendMessage(msg);
				Log.e("msg", "failure");
			}

		} catch (Exception e) {
			Log.e("err", e.getMessage());
		}
	}

	public void afterSend() {
		Toast.makeText(getApplicationContext(), "验证码已发送，请查收", Toast.LENGTH_LONG)
				.show();

		timer = new Timer();
		timer.schedule(new TimerTask(){
			int seconds=60;
			Message msg=new Message();
			Bundle b=new Bundle();
			public void run(){
				if(seconds>0){
					Message msg=new Message();
					Bundle b=new Bundle();
					b.putInt("data",seconds--);
					msg.setData(b);
					msg.what=TIMER;
					myHandler.sendMessage(msg);
				}else{
					myHandler.sendEmptyMessage(RESET);
					this.cancel();
				}
			}
		}, 0,1000);

	}

	public class MainHandler extends Handler {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case SENDCODE:
				afterSend();
				break;
			case SUBMITCODE:
				Toast.makeText(getApplicationContext(), "正在注册...",
						Toast.LENGTH_SHORT).show();
				new Thread(new Runnable() {
					public void run() {
						registerAction();
					}
				}).start();
				break;
			case ERROR:
				Toast.makeText(getApplicationContext(),
						"注册失败：" + msg.getData().getString("err"),
						Toast.LENGTH_LONG).show();
				break;
			case SUCCESS:
				Toast.makeText(RegisterActivity.this, "注册成功，请登录",
						Toast.LENGTH_LONG).show();
				break;
			case TIMER:
				sendBtn.setText(getString(R.string.send)+"("+msg.getData().getInt("data")+")");
				break;
			case RESET:
				sendBtn.setText(getString(R.string.send));
				sendBtn.setClickable(true);
				sendBtn.setBackground(getResources().getDrawable(
						R.drawable.btn_blue));
				break;
				
			}
		}
	}

}
