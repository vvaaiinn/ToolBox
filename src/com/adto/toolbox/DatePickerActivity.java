package com.adto.toolbox;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.adto.util.SysApplication;

public class DatePickerActivity extends Activity {
	public DatePicker dp1 = null;
	public DatePicker dp2 = null;
	public NumberPicker np = null;
	ApplicationData app;
	Calendar c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_date_picker);
		app = (ApplicationData) getApplication();
		dp1 = (DatePicker) findViewById(R.id.date1);
		dp2 = (DatePicker) findViewById(R.id.date2);
		np = (NumberPicker) findViewById(R.id.np);
		np.setMaxValue(23);
		np.setMinValue(0);

		c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, -2);
		SharedPreferences settings = getSharedPreferences("setting", 0);

		if (app.isInit()) {
			dp1.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH));
			dp2.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH));
			np.setValue(c.get(Calendar.HOUR_OF_DAY));
		} else {
			dp1.updateDate(settings.getInt("year1", c.get(Calendar.YEAR)),
					settings.getInt("month1", c.get(Calendar.MONTH)),
					settings.getInt("day1", c.get(Calendar.DAY_OF_MONTH)));
			dp2.updateDate(settings.getInt("year2", c.get(Calendar.YEAR)),
					settings.getInt("month2", c.get(Calendar.MONTH)),
					settings.getInt("day2", c.get(Calendar.DAY_OF_MONTH)));
			np.setValue(settings.getInt("hour", c.get(Calendar.HOUR_OF_DAY)));
		}
		Button btn = (Button) findViewById(R.id.okBtn);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				app.setInit(false);
				Intent intent;
				SharedPreferences settings = getSharedPreferences("setting", 0);
				SharedPreferences.Editor editor = settings.edit();
				String text = "该时间数据未产生";
				if (dp1.getYear() > c.get(Calendar.YEAR)) {
					Toast.makeText(DatePickerActivity.this, text + "\t年份错误",
							Toast.LENGTH_LONG).show();
				} else if ((dp1.getYear() == c.get(Calendar.YEAR))
						&& (dp1.getMonth() > c.get(Calendar.MONTH))) {
					Toast.makeText(DatePickerActivity.this, text + "\t月份错误",
							Toast.LENGTH_LONG).show();
				} else if ((dp1.getMonth() == c.get(Calendar.MONTH))
						&& (dp1.getYear() == c.get(Calendar.YEAR))
						&& (dp1.getDayOfMonth() > c.get(Calendar.DAY_OF_MONTH))) {
					Toast.makeText(DatePickerActivity.this, text + "\t天数错误",
							Toast.LENGTH_LONG).show();
				} else if ((dp1.getMonth() == c.get(Calendar.MONTH)
						&& (dp1.getYear() == c.get(Calendar.YEAR))
						&& (dp1.getDayOfMonth() == c.get(Calendar.DAY_OF_MONTH)) && (np
						.getValue() > c.get(Calendar.HOUR_OF_DAY)))) {
					// System.out.println("heheheheheh"
					// + c.get(Calendar.HOUR_OF_DAY));
					Toast.makeText(DatePickerActivity.this, text + "\t时间错误",
							Toast.LENGTH_LONG).show();
				} else {
					intent = new Intent(DatePickerActivity.this,
							TabHostActivity.class);
					editor.putInt("year1", dp1.getYear());
					editor.putInt("month1", dp1.getMonth());
					editor.putInt("day1", dp1.getDayOfMonth());
					editor.putInt("year2", dp2.getYear());
					editor.putInt("month2", dp2.getMonth());
					editor.putInt("day2", dp2.getDayOfMonth());

					editor.putInt("hour", np.getValue());
					editor.commit();
					intent.putExtra("load_date", "yes");
					startActivity(intent);
					finish();
				}
				// DatePickerActivity.this.setResult(0,intent);
				// finish();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.date_picker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
