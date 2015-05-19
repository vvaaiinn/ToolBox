package com.adto.toolbox;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.xclcharts.chart.PointD;

import com.adto.entity.Constants;
import com.adto.util.GetPostUtil;
import com.adto.view.SplineChartView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RelativeLayout;

public class ChartActivity extends Activity {

	public String res = "";
	public List<String> labels;
	public List<String> vlabels;
	public List<Double> values;
	public static final int QUEST = 1;
	public static final int INIT = 2;
	String date="";
	String bizid="";
	String name="";
	int day=5;
	double ymax=0;
	List<PointD> linePoint;
	MyHandler myHandler;
	
	String[] keys={"20141228","20141229","20141230","20141231","20150101"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(1);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart);

		labels=new ArrayList<String>();
		vlabels=new ArrayList<String>();
		
		bizid=getIntent().getStringExtra("bizid");
		name=getIntent().getStringExtra("name");
		
		myHandler=new MyHandler();
		myHandler.sendEmptyMessage(QUEST);

		 linePoint=new ArrayList<PointD>();
		

	}

	public void parseJson(String json) {
		try {
			
			
			JSONObject jsonObject = new JSONObject(json);
			String status = jsonObject.getString("status");
			
			if (status.equals("0")) {
				for(int i=0;i<keys.length;i++){
					JSONObject obj=jsonObject.getJSONObject(keys[i]);
					labels.add(obj.getString("date"));
					vlabels.add(obj.getString("consume"));
					 int tmp=Integer.parseInt(obj.getString("consume"));
					 if(tmp>ymax)ymax=tmp;
					PointD p=new PointD(i,tmp);
					linePoint.add(p);
				}
				
			}
		} catch (Exception e) {
			Log.e("parseErr",e.getMessage());
		}
	}
	
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case QUEST:
				new Thread(new Runnable() {
					public void run() {
						res = GetPostUtil.sendGet(Constants.URL
								+ "getDayTrend.php?date=20150101&day=5&bizid="+bizid, null);
						parseJson(res);
						
						myHandler.sendEmptyMessage(INIT);
					}
				}).start();
				break;
			case INIT:
				
				DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
				RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(
						(int) (0.9D * localDisplayMetrics.widthPixels),
						(int) (0.9D * localDisplayMetrics.heightPixels));
				localLayoutParams.addRule(13);

				RelativeLayout localRelativeLayout = (RelativeLayout) findViewById(R.id.linearLayout);
				
				SplineChartView chartview = new SplineChartView(getApplicationContext());
				chartview.setChartLabels(labels);
				chartview.setyMax(dataFormat(ymax*1.2));
				chartview.setyStep(dataFormat(ymax/3));
				chartview.setxMax(keys.length-1);
				chartview.setTitle(name);
				chartview.setChartData(linePoint);
				Log.e("view_length",linePoint.size()+"");
				localRelativeLayout.addView(chartview, localLayoutParams);
				Log.e("right",linePoint.size()+"");
				break;
		}
		
		}
	}
	
	public double dataFormat(double data){
		double tmp= Math.floor(data);
		String s=String.valueOf(tmp);
		Log.e("",s+"___");
		int t=Integer.valueOf(s.substring(0,1));
		if(t<5){
			t=5;
		}else 
			t=10;
		
		return t*Math.pow(10, s.length()-3);
		//return tmp;
	}
	
	
}
