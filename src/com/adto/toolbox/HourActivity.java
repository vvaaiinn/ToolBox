package com.adto.toolbox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adto.entity.Constants;
import com.adto.util.GetPostUtil;
import com.adto.util.NetUtil;
import com.adto.util.PrivateUtil;
import com.adto.util.SysApplication;
import com.adto.util.VersionUtil;

public class HourActivity extends Activity {

	// 包裹滑动图片LinearLayout

	// 包裹小圆点的LinearLayout

	int year1, year2, month1, month2, day1, day2, hour = 12;
	private TextView set, dateText;
	ImageButton add, update, calendarBtn;
	String date1, date2;
	ArrayList<HashMap<String, String>> data;
	LinearLayout loading;
	TextView compareText, consumeText;
	ListView listview;
	ListItemAdapter la;
	public static final int QUEST = 1;
	public static final int SUCCESS = 2;
	public static final int REFRESH = 3;
	public static final int RATE = 4;
	public static final int PARSE = 5;
	public static final int RATE1 = 6;
	boolean dataType = true;
	String res = "";
	String catalog = "";
	String url = "";
	String[] ids;
	String phone, validkey;
	boolean isconnected = true;
	boolean day = true;

	MainHandler myhandler;

	private ApplicationData result;

	NetUtil netUtil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		result = (ApplicationData) getApplication();

		setContentView(R.layout.activity_main);
		data = new ArrayList<HashMap<String, String>>();
		loading = (LinearLayout) findViewById(R.id.loading);
		dateText = (TextView) findViewById(R.id.dateText);
		compareText = (TextView) findViewById(R.id.compare2);
		consumeText = (TextView) findViewById(R.id.compare1);
		consumeText.setText("小时consume(元)");
		myhandler = new MainHandler();
		netUtil = new NetUtil(getApplicationContext());
		if (!netUtil.isNetworkConnected()) {
			isconnected = false;
			Toast.makeText(getApplicationContext(), "网络异常，请检查网络",
					Toast.LENGTH_SHORT).show();
			loading.setVisibility(View.GONE);
		} else {

			myhandler.sendEmptyMessage(QUEST);
		}
		bindListener();
		compareText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				day = !day;
				myhandler.sendEmptyMessage(RATE);
			}
		});
		consumeText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				dataType = !dataType;
				myhandler.sendEmptyMessage(RATE1);

			}
		});

	}

	public boolean initData() {
		boolean success = true;
		try {
			data.clear();
			SharedPreferences settings = getSharedPreferences("setting", 0);
			ids = settings.getString("selected", "0,1,2,3,4,").split(",");

			JSONObject result = new JSONObject(res);
			String status = result.getString("status");
			if (status.equals("0")) {

				JSONObject hourArray = result.getJSONObject("hour");
				JSONObject today = hourArray.getJSONObject("0");
				JSONObject yesterday = hourArray.getJSONObject("1");
				JSONObject lastweek = hourArray.getJSONObject("7");
				date1 = hourArray.getString("date1");
				date2 = hourArray.getString("date7");
				for (int i = 0; i < ids.length; i++) {
					String id = ids[i];
					JSONObject obj = today.getJSONObject(id);
					JSONObject obj2 = yesterday.getJSONObject(id);
					JSONObject obj3 = lastweek.getJSONObject(id);
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("item", obj.getString("name"));
					map.put("consume", obj.getString("consume"));
					map.put("rate",
							rateFormat(obj.getString("consume"),
									obj2.getString("consume")));
					if (!day) {
						map.put("rate",
								rateFormat(obj.getString("consume"),
										obj3.getString("consume")));
					}
					map.put("path",
							PrivateUtil.getPath(Integer.valueOf(id), catalog));
					data.add(map);
				}
			} else {
				Toast.makeText(getApplicationContext(),
						result.getString("description"), Toast.LENGTH_LONG)
						.show();
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(intent);
				finish();
				success = false;
			}
		} catch (Exception e) {
			success = false;
			dateText.setText("err:" + e.getMessage());
			e.printStackTrace();
		}
		return success;
	}

	public boolean initData2() {
		boolean success = true;
		try {
			data.clear();
			SharedPreferences settings = getSharedPreferences("setting", 0);
			ids = settings.getString("selected", "0,1,2,3,4,").split(",");

			JSONObject result = new JSONObject(res);
			String status = result.getString("status");
			if (status.equals("0")) {
				JSONObject hourArray = result.getJSONObject("hour");
				JSONObject today = hourArray.getJSONObject("0");
				JSONObject yesterday = hourArray.getJSONObject("1");
				JSONObject lastweek = hourArray.getJSONObject("7");
				date1 = hourArray.getString("date1");
				date2 = hourArray.getString("date7");
				for (int i = 0; i < ids.length; i++) {
					String id = ids[i];
					JSONObject obj = today.getJSONObject(id);
					JSONObject obj2 = yesterday.getJSONObject(id);
					JSONObject obj3 = lastweek.getJSONObject(id);
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("item", obj.getString("name"));

					// String d1 = obj.getString("consume");
					// String d2 = obj.getString("filter_pv");
					// String d3 = obj.getString("pv");
					// map.put("consume", CalcEcpm(d1, d2, d3));

					if (obj.getString("ecpm").equals("N/A"))
						map.put("consume", "N/A");
					else
						map.put("consume", String.valueOf(new BigDecimal(Float
								.valueOf(obj.getString("ecpm"))).setScale(2,
								BigDecimal.ROUND_HALF_UP).floatValue()));

					// String t1 = obj2.getString("consume");
					// String t2 = obj2.getString("filter_pv");
					// String t3 = obj2.getString("pv");
					// map.put("rate",
					// rateFormat(CalcEcpm(d1, d2, d3),
					// CalcEcpm(t1, t2, t3)));
					map.put("rate",
							rateFormat(obj.getString("ecpm"),
									obj2.getString("ecpm")));
					// t1 = obj3.getString("consume");
					// t2 = obj3.getString("filter_pv");
					// t3 = obj3.getString("pv");
					if (!day) {
						map.put("rate",
								rateFormat(obj.getString("ecpm"),
										obj3.getString("ecpm")));
						// map.put("rate",
						// rateFormat(CalcEcpm(d1, d2, d3),
						// CalcEcpm(t1, t2, t3)));
					}
					if (obj.getString("name").equals("总计")
							|| obj.getString("name").equals("其它搜索")) {
						map.put("consume", "N/A");
						map.put("rate", "N/A");
					}
					map.put("path",
							PrivateUtil.getPath(Integer.valueOf(id), catalog));
					data.add(map);
				}
			} else {
				Toast.makeText(getApplicationContext(),
						result.getString("description"), Toast.LENGTH_LONG)
						.show();
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(intent);
				finish();
				success = false;
			}
		} catch (Exception e) {
			success = false;
			dateText.setText("err:" + e.getMessage());
			e.printStackTrace();
		}
		return success;
	}

	public void setAdapter() {
		listview = (ListView) findViewById(R.id.listview);

		la = new ListItemAdapter(getApplicationContext(), data);

		if (data != null && data.size() > 0) {
			listview.setAdapter(la);
		}
	}

	public void bindListener() {

		add = (ImageButton) findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ListActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

		update = (ImageButton) findViewById(R.id.update);
		update.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!netUtil.isNetworkConnected()) {
					isconnected = false;
					Toast.makeText(getApplicationContext(), "网络异常，请检查网络",
							Toast.LENGTH_SHORT).show();
				} else {
					loading.setVisibility(View.VISIBLE);
					myhandler.sendEmptyMessage(QUEST);

				}
			}
		});

		loading = (LinearLayout) findViewById(R.id.loading);
		set = (TextView) findViewById(R.id.edit);
		set.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						EditActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.show, R.anim.gone);
			}
		});

		calendarBtn = (ImageButton) findViewById(R.id.calendarBtn);
		calendarBtn.setOnClickListener(new dateClickListener());
		// dateText = (TextView) findViewById(R.id.dateText);
		// dateText.setOnClickListener(new dateClickListener());
	}

	public void setDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, -2);

		Intent i = getIntent();
		String load = i.getStringExtra("load_date");
		if (result.isInit()) {
			year1 = c.get(Calendar.YEAR);
			month1 = c.get(Calendar.MONTH) + 1;
			day1 = c.get(Calendar.DAY_OF_MONTH);
			hour = c.get(Calendar.HOUR_OF_DAY);

		} else {
			SharedPreferences settings = getSharedPreferences("setting", 0);
			year1 = settings.getInt("year1", c.get(Calendar.YEAR));
			month1 = settings.getInt("month1", c.get(Calendar.MONTH)) + 1;
			day1 = settings.getInt("day1", c.get(Calendar.DAY_OF_MONTH));
			hour = settings.getInt("hour", c.get(Calendar.HOUR_OF_DAY));
		}
	}

	public void setDateText() {
		setDate();
		if (res.equals("") || date1 == null || date2 == null) {
			dateText.setText(dateFormat(year1, month1, day1) + " 请求数据失败 ");
		} else {
			dateText.setText(dateFormat(year1, month1, day1) + " VS " + date1
					+ "  " + hour + "时数据对比");
		}
	}

	public class MainHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case QUEST:
				new Thread() {
					public void run() {
						try {
							res = "";

							SharedPreferences settings = getSharedPreferences(
									"setting", 0);
							StringBuilder params = new StringBuilder();
							params.append("phone="
									+ settings.getString("phone", ""));
							params.append("&validkey="
									+ settings.getString("validkey", ""));
							params.append("&ver="
									+ VersionUtil
											.getVerName(getApplicationContext()));

							res = GetPostUtil.sendGet(Constants.URL
									+ "getContent.php",
									getDatePram() + params.toString());
							catalog = GetPostUtil.sendGet(Constants.URL
									+ "getBizlist.php", params.toString());

							myhandler.sendEmptyMessage(PARSE);
							result.setRes(res);
							result.setCatalog(catalog);

						} catch (Exception e) {
							Log.e("msg", "err:" + e.getMessage());
						}
					}
				}.start();

				break;
			case SUCCESS:
				loading.setVisibility(View.GONE);
				break;
			case REFRESH:

				//
				// result.setRes(res);
				// initData();

				break;
			case RATE:
				if (dataType)
					initData();
				else
					initData2();
				la.notifyDataSetChanged();
				loading.setVisibility(View.GONE);
				if (day) {
					compareText.setText(getString(R.string.day_on_day));
					Toast.makeText(getApplicationContext(),
							getString(R.string.day_on_day), Toast.LENGTH_SHORT)
							.show();
				} else {
					compareText.setText(getString(R.string.week_on_week));
					Toast.makeText(getApplicationContext(),
							getString(R.string.week_on_week),
							Toast.LENGTH_SHORT).show();
				}
				break;
			case RATE1:
				if (dataType) {
					consumeText.setText("小时consume(元)");
					Toast.makeText(getApplicationContext(), "小时consume(元)",
							Toast.LENGTH_SHORT).show();
				} else {
					consumeText.setText("小时ECPM(元)");
					Toast.makeText(getApplicationContext(), "小时ecpm(元)",
							Toast.LENGTH_SHORT).show();
				}
				if (dataType)
					initData();
				else
					initData2();
				la.notifyDataSetChanged();
				loading.setVisibility(View.GONE);
				break;
			case PARSE:
				// Boolean flag = false;
				if (dataType)
					initData();
				else
					initData2();
				if (true) {
					setAdapter();
					bindListener();
					setDateText();
					compareText.setText(getString(R.string.day_on_day));
					loading.setVisibility(View.GONE);
					Toast.makeText(getApplicationContext(),
							"\t\t\t\t\t\t\t\t\t\t刷新成功\n" + dateText.getText(),
							Toast.LENGTH_LONG).show();
				}
				break;

			}

		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	class dateClickListener implements OnClickListener {
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(),
					DatePickerActivity.class);
			// startActivityForResult(intent, 0);
			startActivity(intent);
		}
	}

	public String rateFormat(String str1, String str2) {
		if (str2.equals("") || str2.equals("0"))
			return "N/A";
		if (str2.equals(str1))
			return "0.00%";
		BigDecimal b = null;
		boolean fail = false;
		try {
			float data1 = Float.valueOf(str1);
			float data2 = Float.valueOf(str2);
			float rate = (data1 - data2) * 100 / data2;
			b = new BigDecimal(rate);
		} catch (Exception e) {
			fail = true;
		}
		if (fail)
			return "N/A";
		return b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + "%";
	}

	public String getDatePram() {
		String urlPram = "";
		setDate();
		String date1 = dateFormat(year1, month1, day1);
		String date2 = dateFormat(year2, month2, day2);
		urlPram = "date1=" + date1 + "&hour=" + hour + "&";
		return urlPram;
	}

	public String dateFormat(int year, int month, int day) {
		return year + intFormat(month) + intFormat(day);
	}

	public String intFormat(int i) {
		return i > 9 ? String.valueOf(i) : ("0" + String.valueOf(i));
	}

	public class ListItemAdapter extends BaseAdapter {
		private List<HashMap<String, String>> list;
		private LayoutInflater layoutInflater;

		public ListItemAdapter(Context context,
				List<HashMap<String, String>> list) {
			this.list = list;
			layoutInflater = LayoutInflater.from(context);
		}

		/**
		 * 数据总数
		 */
		@Override
		public int getCount() {

			return list.size();
		}

		/**
		 * 获取当前数据
		 */
		@Override
		public Object getItem(int position) {

			return list.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			View row = convertView;
			if (convertView == null) {
				row = layoutInflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.t1 = (TextView) row.findViewById(R.id.t1);
				holder.t2 = (TextView) row.findViewById(R.id.t2);
				holder.t3 = (TextView) row.findViewById(R.id.t3);
				holder.path = (TextView) row.findViewById(R.id.path);
				row.setTag(holder);
			} else {
				holder = (ViewHolder) row.getTag();
			}
			String item = list.get(position).get("item");
			String consume = list.get(position).get("consume");
			String rate = list.get(position).get("rate");
			String path = list.get(position).get("path");
			holder.t1.setText(item);
			holder.t2.setText(consume);
			holder.t3.setText(rate);
			holder.path.setText(path);
			holder.t3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 自动生成的方法存根
					day = !day;
					myhandler.sendEmptyMessage(RATE);
				}
			});
			holder.t2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO 自动生成的方法存根
					dataType = !dataType;
					myhandler.sendEmptyMessage(RATE1);
				}
			});

			if (rate.indexOf('-') != -1) {

				holder.t3.setBackgroundColor(getResources().getColor(
						R.color.green));
			} else if (rate.equals("0.00%") || rate.equals("N/A")) {

				holder.t3.setBackgroundColor(getResources().getColor(
						R.color.grey));
			} else {
				holder.t3.setBackgroundColor(getResources().getColor(
						R.color.red));
			}
			return row;
		}

		class ViewHolder {
			TextView t1;
			TextView t2;
			TextView t3;
			TextView path;
		}

	}

	public String CalcEcpm(String str1, String str2, String str3) {
		float ecpm = 0;
		BigDecimal b;
		if (Integer.parseInt(str2) == 0) {
			if (Integer.parseInt(str3) == 0)
				return "N/A";
			float data1 = Float.valueOf(str1);
			float data2 = Float.valueOf(str3);
			ecpm = data1 * 1000 / data2;
		} else {
			try {
				float data1 = Float.valueOf(str1);
				float data2 = Float.valueOf(str2);
				ecpm = data1 * 1000 / data2;
			} catch (Exception e) {
				return "N/A";
			}
		}
		b = new BigDecimal(ecpm);
		return String.valueOf(b.setScale(2, BigDecimal.ROUND_HALF_UP)
				.floatValue());
	}

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
