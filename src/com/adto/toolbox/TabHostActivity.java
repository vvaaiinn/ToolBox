package com.adto.toolbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.adto.entity.Constants;
import com.adto.entity.UpdateInfo;

import com.adto.util.NetUtil;
import com.adto.util.SysApplication;
import com.adto.util.UserUtil;
import com.adto.util.VersionUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

public class TabHostActivity extends TabActivity {
	private RadioGroup group;
	private TabHost tabHost;
	NetUtil netUtil;
	boolean isconnected = true;

	long m_newVerCode; // 最新版的版本号
	String m_newVerName; // 最新版的版本名
	String m_appNameStr; // 下载到本地要给这个APP命的名字
	Handler m_mainHandler;
	ProgressDialog m_progressDlg;
	UpdateInfo upInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		netUtil = new NetUtil(getApplicationContext());
		if (!netUtil.isNetworkConnected()) {
			isconnected = false;
			Toast.makeText(getApplicationContext(), "网络异常，请检查网络",
					Toast.LENGTH_SHORT).show();
		}

		if (isconnected) {
			initVariable();
			new checkNewestVersionAsyncTask().execute();
		}

		SysApplication.getInstance().addActivity(this);
		if (!UserUtil.isLogin(getApplicationContext())) {
			Intent intent = new Intent(TabHostActivity.this,
					LoginActivity.class);
			startActivity(intent);
			this.finish();
		} else {

			setContentView(R.layout.activity_tab);
			tabHost = getTabHost();
			Intent intent1 = new Intent(TabHostActivity.this,
					MainActivity.class);
			Intent intent2 = new Intent(TabHostActivity.this,
					HourActivity.class);
			Intent intent3 = new Intent(TabHostActivity.this,
					MyinfoActivity.class);

			tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("tab1")
					.setContent(intent1));
			tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("tab2")
					.setContent(intent2));
			tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("tab3")
					.setContent(intent3));

			group = (RadioGroup) findViewById(R.id.main_radio);
			group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch (checkedId) {
					case R.id.accumulation:
						tabHost.setCurrentTabByTag("tab1");
						break;

					case R.id.hour:
						tabHost.setCurrentTabByTag("tab2");
						break;
					case R.id.myinfo:
						tabHost.setCurrentTabByTag("tab3");
						break;
					default:
						break;
					}
				}
			});
		}
	}

	private void initVariable() {
		m_mainHandler = new Handler();
		m_progressDlg = new ProgressDialog(this);
		m_progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
		m_progressDlg.setIndeterminate(false);
		m_appNameStr = "iMonitor.apk";
	}

	class checkNewestVersionAsyncTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (getUpdateInfo()) {
				int vercode = VersionUtil.getVerCode(getApplicationContext()); // 用到前面第一节写的方法
				if (Integer.valueOf(upInfo.getDate()) > vercode) {
					return true;

				} else {
					return false;
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if (result) {// 如果有最新版本
				doNewVersionUpdate(); // 更新新版本
			} else {
				// notNewVersionDlgShow(); // 提示当前为最新版本
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
	}

	/**
	 * 从服务器获取当前最新版本号，如果成功返回TURE，如果失败，返回FALSE
	 * 
	 * @return
	 */
	private Boolean getUpdateInfo() {
		JSONObject jsonArray = null;
		try {
			jsonArray = new JSONObject(VersionUtil.getVersionFromServer());
			if (jsonArray != null) {
				JSONObject obj = jsonArray.getJSONObject("1");
				upInfo = new UpdateInfo(obj.getString("version"),
						obj.getString("url"), obj.getString("date"));
				return true;
			}
			return false;

		} catch (Exception e) {
			Log.e("msg", e.getMessage());
			return false;
		}
	}

	/**
	 * 提示更新新版本
	 */
	private void doNewVersionUpdate() {
		int verCode = VersionUtil.getVerCode(getApplicationContext());
		String verName = VersionUtil.getVerName(getApplicationContext());

		String str = "当前版本：" + verName + "\n发现新版本：" + upInfo.getVersion()
				+ "\n是否更新？";
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle("软件更新")
				.setMessage(str)
				// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								m_progressDlg.setTitle("正在下载");
								m_progressDlg.setMessage("请稍候...");

								downFile(upInfo.getUrl()); // 开始下载

							}
						})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 点击"取消"按钮之后退出程序
								SysApplication.getInstance().exit();
								//20150518 感觉此处有问题，有待测试。
								finish();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	/**
	 * 提示当前为最新版本
	 */
//	private void notNewVersionDlgShow() {
//		int verCode = VersionUtil.getVerCode(this);
//		String verName = VersionUtil.getVerName(this);
//		String str = "当前版本:" + verName + "\n已是最新版,无需更新!";
//		Dialog dialog = new AlertDialog.Builder(this).setTitle("软件更新")
//				.setMessage(str)// 设置内容
//				.setPositiveButton("确定",// 设置确定按钮
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								finish();
//							}
//						}).create();// 创建
//		// 显示对话框
//		dialog.show();
//	}

	private void downFile(final String url) {
		m_progressDlg.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {

					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();

					m_progressDlg.setMax((int) length);// 设置进度条的最大值

					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					Log.e("msg", "" + upInfo.getUrl());
					if (is != null) {
						File file = new File(
								Environment.getExternalStorageDirectory(),
								m_appNameStr);
						fileOutputStream = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;

						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							if (length > 0) {
								m_progressDlg.setProgress(count);
							}
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void down() {
		m_mainHandler.post(new Runnable() {
			public void run() {
				m_progressDlg.cancel();
				update();
			}
		});
	}

	void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);

		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), m_appNameStr)),
				"application/vnd.android.package-archive");
		Log.e("msg", Environment.getExternalStorageDirectory() + ""
				+ m_appNameStr);
		startActivity(intent);
	}

//	private String getVersionName() throws Exception {
//		// 获取packagemanager的实例
//		PackageManager packageManager = getPackageManager();
//		// getPackageName()是你当前类的包名，0代表是获取版本信息
//		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
//				0);
//		return packInfo.versionName;
//	}

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
