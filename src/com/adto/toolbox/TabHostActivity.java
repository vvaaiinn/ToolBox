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

	long m_newVerCode; // ���°�İ汾��
	String m_newVerName; // ���°�İ汾��
	String m_appNameStr; // ���ص�����Ҫ�����APP��������
	Handler m_mainHandler;
	ProgressDialog m_progressDlg;
	UpdateInfo upInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		netUtil = new NetUtil(getApplicationContext());
		if (!netUtil.isNetworkConnected()) {
			isconnected = false;
			Toast.makeText(getApplicationContext(), "�����쳣����������",
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
		// ����ProgressDialog �Ľ������Ƿ���ȷ false ���ǲ�����Ϊ����ȷ
		m_progressDlg.setIndeterminate(false);
		m_appNameStr = "iMonitor.apk";
	}

	class checkNewestVersionAsyncTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (getUpdateInfo()) {
				int vercode = VersionUtil.getVerCode(getApplicationContext()); // �õ�ǰ���һ��д�ķ���
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
			if (result) {// ��������°汾
				doNewVersionUpdate(); // �����°汾
			} else {
				// notNewVersionDlgShow(); // ��ʾ��ǰΪ���°汾
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
	 * �ӷ�������ȡ��ǰ���°汾�ţ�����ɹ�����TURE�����ʧ�ܣ�����FALSE
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
	 * ��ʾ�����°汾
	 */
	private void doNewVersionUpdate() {
		int verCode = VersionUtil.getVerCode(getApplicationContext());
		String verName = VersionUtil.getVerName(getApplicationContext());

		String str = "��ǰ�汾��" + verName + "\n�����°汾��" + upInfo.getVersion()
				+ "\n�Ƿ���£�";
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle("�������")
				.setMessage(str)
				// ��������
				.setPositiveButton("����",// ����ȷ����ť
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								m_progressDlg.setTitle("��������");
								m_progressDlg.setMessage("���Ժ�...");

								downFile(upInfo.getUrl()); // ��ʼ����

							}
						})
				.setNegativeButton("�ݲ�����",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// ���"ȡ��"��ť֮���˳�����
								SysApplication.getInstance().exit();
								//20150518 �о��˴������⣬�д����ԡ�
								finish();
							}
						}).create();// ����
		// ��ʾ�Ի���
		dialog.show();
	}

	/**
	 * ��ʾ��ǰΪ���°汾
	 */
//	private void notNewVersionDlgShow() {
//		int verCode = VersionUtil.getVerCode(this);
//		String verName = VersionUtil.getVerName(this);
//		String str = "��ǰ�汾:" + verName + "\n�������°�,�������!";
//		Dialog dialog = new AlertDialog.Builder(this).setTitle("�������")
//				.setMessage(str)// ��������
//				.setPositiveButton("ȷ��",// ����ȷ����ť
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								finish();
//							}
//						}).create();// ����
//		// ��ʾ�Ի���
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

					m_progressDlg.setMax((int) length);// ���ý����������ֵ

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
//		// ��ȡpackagemanager��ʵ��
//		PackageManager packageManager = getPackageManager();
//		// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
//		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
//				0);
//		return packInfo.versionName;
//	}

	/* �ٴη��غ��˳� */
	private long exitTime = 0;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
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
