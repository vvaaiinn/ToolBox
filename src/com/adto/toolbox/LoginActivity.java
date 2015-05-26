package com.adto.toolbox;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.adto.entity.Constants;
import com.adto.util.EncryptUtil;
import com.adto.util.GetPostUtil;
import com.adto.util.NetUtil;
import com.adto.util.SysApplication;
import com.adto.util.VersionUtil;

public class LoginActivity extends Activity {

	TextView register;
	Button loginBtn, sendBtn;
	String phone, captcha, udid, timestamp, validkey;
	EditText phoneEdit, captchaEdit;
	String res = "";
	Timer timer;
	NetUtil netUtil;

	MainHandler myHandler;

	public static final int SENDCODE = 1;
	public static final int SUBMITCODE = 2;
	public static final int ERROR = 3;
	public static final int SUCCESS = 4;
	public static final int TIMER = 5;
	public static final int RESET = 6;

	public boolean checked = false;
	EventHandler loginEH;
	EventHandler smsEH;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_login);
		phoneEdit = (EditText) findViewById(R.id.phone);
		captchaEdit = (EditText) findViewById(R.id.captcha);
		register = (TextView) findViewById(R.id.registerText);
		loginBtn = (Button) findViewById(R.id.loginBtn);
		sendBtn = (Button) findViewById(R.id.sendcode);
		initView();
	}

	public void initView() {
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
						// 提交验证码成功
						Log.e("login", "logining");
						checked = true;
						myHandler.sendEmptyMessage(SUBMITCODE);
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						// 获取验证码成功
						myHandler.sendEmptyMessage(SENDCODE);
					} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
						// 返回支持发送验证码的国家列表
					} else {
						Log.e("unkonwn", "unkonwn err");
					}
				} else {
					((Throwable) data).printStackTrace();
					Bundle b = new Bundle();
					b.putString("err", getErrorMsg(data.toString()));
					Message msg = new Message();
					msg.what = ERROR;
					msg.setData(b);

					myHandler.sendMessage(msg);
					Log.e("unkown err", data.toString());
				}

			}
		};
		SMSSDK.registerEventHandler(smsEH); // 注册短信回调

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		netUtil = new NetUtil(getApplicationContext());

		register.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});

		loginBtn.setOnClickListener(listener_login);

		sendBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.e("send", "send code");
				if (!phoneEdit.getText().toString().trim().equals("")) {
					if (phoneEdit.getText().toString().length() == 11) {
						phoneEdit.setBackground(getResources().getDrawable(
								R.drawable.edit_box));
						sendBtn.setClickable(false);
						sendBtn.setBackground(getResources().getDrawable(
								R.drawable.btn_unable));
					}
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
	}

	private OnClickListener listener_login = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (isvalid()) {
				Log.e("checked", checked + "");
				if (!netUtil.isNetworkConnected()) {
					Toast.makeText(getApplicationContext(), "网络异常",
							Toast.LENGTH_LONG).show();
				} else {
					SMSSDK.submitVerificationCode("86", phoneEdit.getText()
							.toString(), captchaEdit.getText().toString());

					Log.e("login", "login here");

				}
			} else {
				if (phoneEdit.getText().toString().length() > 11) {
					phoneEdit.setBackground(getResources().getDrawable(
							R.drawable.err_box));
					Toast.makeText(getApplicationContext(), "非法手机号",
							Toast.LENGTH_LONG).show();
				} else {
					if (phoneEdit.getText().toString().length() < 11) {
						phoneEdit.setBackground(getResources().getDrawable(
								R.drawable.err_box));
						phoneEdit.requestFocus();
					} else
						captchaEdit.requestFocus();
					Toast.makeText(getApplicationContext(), "请填写全信息",
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	public boolean isvalid() {

		boolean valid = true;
		phone = phoneEdit.getText().toString();
		captcha = captchaEdit.getText().toString();

		phoneEdit
				.setBackground(getResources().getDrawable(R.drawable.edit_box));
		captchaEdit.setBackground(getResources().getDrawable(
				R.drawable.edit_box));

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
		}
		return valid;
	}

	public void afterSend() {
		Toast.makeText(getApplicationContext(), "验证码已发送，请查收", Toast.LENGTH_LONG)
				.show();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			int seconds = 60;

			public void run() {
				if (seconds > 0) {
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putInt("data", seconds--);
					msg.setData(b);
					msg.what = TIMER;
					myHandler.sendMessage(msg);
				} else {
					myHandler.sendEmptyMessage(RESET);
					this.cancel();
				}
			}
		}, 0, 1000);
	}

	public void loginAction() {

		String url = Constants.URL + "login.php";
		StringBuilder params = new StringBuilder();
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		String mac = "macnull";
		if (wm != null) {
			mac = wm.getConnectionInfo().getMacAddress();
		}
		BluetoothAdapter m_BluetoothAdapter = null;
		m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		String bluemac = "bluenull";
		if (m_BluetoothAdapter != null) {
			bluemac = m_BluetoothAdapter.getAddress();
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
		params.append("&validkey=" + validkey);
		params.append("&ver=" + VersionUtil.getVerName(getApplicationContext()));
		res = GetPostUtil.sendPost(url, params.toString());
		Log.e("", res);
		try {
			JSONObject obj = new JSONObject(res);
			Log.e("", params.toString());
			if (obj.get("status").toString().equals("0")) {
				// myHandler.sendEmptyMessage(SUCCESS);
				SharedPreferences settings = getSharedPreferences("setting", 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("phone", phone);
				editor.putString("validkey", validkey);
				editor.putString("isLogin", "login");
				editor.commit();
				Intent intent = new Intent(LoginActivity.this,
						TabHostActivity.class);
				startActivity(intent);
				SMSSDK.unregisterAllEventHandler();
				Log.e("msg", "success");
			} else {
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("err", obj.get("description").toString());
				Log.e("err", obj.get("description").toString() + "__" + res);
				msg.what = 3;
				msg.setData(b);
				myHandler.sendMessage(msg);
				Log.e("msg", "failure");
			}

		} catch (Exception e) {
			Log.e("err", e.getMessage());
		}

	}

	public class MainHandler extends Handler {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case SENDCODE:
				afterSend();
				break;
			case SUBMITCODE:

				Toast.makeText(getApplicationContext(), "正在登录...",
						Toast.LENGTH_SHORT).show();

				new Thread(new Runnable() {
					public void run() {
						loginAction();
					}
				}).start();
				break;
			case ERROR:
				// System.out.println("hahaha" +
				// msg.getData().getString("err"));
				if (msg.getData().getString("err").equals("验证码错误")) {
					captchaEdit.setBackground(getResources().getDrawable(
							R.drawable.err_box));
				} else
					phoneEdit.setBackground(getResources().getDrawable(
							R.drawable.err_box));

				Toast.makeText(getApplicationContext(),
						"" + msg.getData().getString("err"), Toast.LENGTH_SHORT)
						.show();
				Log.e("err", msg.getData().getString("err"));
				break;
			case SUCCESS:
				Toast.makeText(getApplicationContext(), "登录成功",
						Toast.LENGTH_SHORT).show();
				break;
			case TIMER:
				sendBtn.setText(getString(R.string.send) + "("
						+ msg.getData().getInt("data") + ")");
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

	public static String getErrorMsg(String err) {
		err = err.substring(err.indexOf('{'), err.indexOf('}') + 1);
		String res = "";
		try {
			res = new JSONObject(err).getString("description");
		} catch (Exception e) {
			res = "解析异常";
		}
		return res;
	}

	/* 再次返回后退出 */
	private long exitTime = 0;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				SysApplication.getInstance().exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
