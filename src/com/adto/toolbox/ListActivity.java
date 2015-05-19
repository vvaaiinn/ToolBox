package com.adto.toolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.adto.adapter.CellAdapter;
import com.adto.entity.Constants;
import com.adto.util.GetPostUtil;
import com.adto.util.SysApplication;
import com.adto.view.MyGridView;

public class ListActivity extends Activity {

	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	TextView msgText;
	Button btn;
	String jsonString;
	String res = "";
	LinearLayout linearLayout;
	ImageButton back;
	GridView gv;
	ViewGroup viewGroup;
	LinearLayout loading;

	int[] ids = { 0, 1, 2, 3, 4, 11, 12, 13, 14, 15, 16, 17, 18, 19, 22, 23,
			24, 25, 26, 31, 32, 33, 41, 42, 43, 44, 45, 46, 47, 48, 431, 432,
			434, 436, 441, 442, 451, 452, 481, 482, 5 };
	String selectedIds = "";
	
	
	boolean[] visit = new boolean[ids.length];
	public static final int QUEST = 1;
	public static final int PARSE = 2;

	private ViewPager viewPager;
	private ArrayList<View> pageViews;
	private ImageView imageView;
	private ImageView[] imageViews;
	// 包裹滑动图片LinearLayout
	private ViewGroup main;
	// 包裹小圆点的LinearLayout
	private ViewGroup group;
	@SuppressWarnings("unused")
	private TextView content;

	ApplicationData appData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		SysApplication.getInstance().addActivity(this);
		
		appData = (ApplicationData) getApplication();
		res=appData.getCatalog();
		SharedPreferences settings = getSharedPreferences("setting", 0);
		selectedIds = settings.getString("selected", "0,1,2,3,4,");
		
		msgText = (TextView) findViewById(R.id.msgText);
		linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
		loading = (LinearLayout) findViewById(R.id.loading);	

		back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ListActivity.this,
						TabHostActivity.class);
				intent.putExtra("load_date", "yes");
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});

		viewGroup = (ViewGroup) findViewById(R.id.listLayout);
		parseJson();
	}

	public void parseJson() {
		try {
			// Log.e("",res);
			loading.setVisibility(View.GONE);
			JSONObject jsonArray = new JSONObject(res);
			JSONObject obj ;
			TextView text;

			for (int i = 0; i < ids.length; i++) {

				if (!visit[i]) {
					visit[i] = true;
					obj = jsonArray.getJSONObject(String.valueOf(ids[i]));
					text = new TextView(ListActivity.this);
					text.setText(obj.getString("name"));
					text.setTag(ids[i]);
					text.setPadding(0, 20, 0, 20);
					text.setGravity(Gravity.CENTER);
					// text.setBackground(getResources().getDrawable(R.drawable.option_bg));
					text.setBackground(getResources().getDrawable(R.drawable.option_selected_bg));
					/*if(selectedIds.indexOf(ids[i]+",")!=-1){
						text.setBackground(getResources().getDrawable(R.drawable.option_selected_bg));
					}*/
					text.setTextColor(Color.parseColor("#ffffff"));
					text.setOnClickListener(new myTextOnClickListener());
					viewGroup.addView(text);
					if(ids[i]==0){
						text= new TextView(ListActivity.this);
						text.setHeight(10);
						viewGroup.addView(text);
					}
					int[] childs = getChild(ids[i]);

					List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();
					for (int j = 0; j < childs.length; j++) {
						obj = jsonArray
								.getJSONObject(String.valueOf(childs[j]));
						Map<String, String> item = new HashMap<String, String>();
						item.put("opt", obj.getString("name"));
						listItems.add(item);
					}

					while (listItems.size() % 3 != 0) {
						Map<String, String> item = new HashMap<String, String>();
						item.put("opt", "");
						listItems.add(item);
					}

					SimpleAdapter sa = new SimpleAdapter(this, listItems,
							R.layout.cell, new String[] { "opt" },
							new int[] { R.id.content });

					CellAdapter ca = new CellAdapter(ListActivity.this,
							listItems);

					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);

					gv = new MyGridView(ListActivity.this);
					gv.setNumColumns(3);
					// gv.setVerticalSpacing(-2);
					// gv.setBackground(getResources().getDrawable(R.drawable.option_bg));
					//gv.setBackgroundColor(Color.parseColor("#ffffff"));
					gv.setAdapter(sa);
					// gv.setc

					gv.setOnItemClickListener(new myGirdViewOnItemClickListener(
							ids[i]));

					viewGroup.addView(gv, params);
				}
			}
		} catch (Exception e) {
			msgText.setText("解析出错！");
			Log.e("err", e.getMessage());
		}
	}

	

	public class myGirdViewOnItemClickListener implements OnItemClickListener

	{
		int category = 1;

		public myGirdViewOnItemClickListener() {
			category = 1;
		}

		public myGirdViewOnItemClickListener(int c) {
			category = c;
		}

		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			int[] childs = getChild(category);
			if (position < childs.length) {
				//Toast.makeText(getApplicationContext(), getPath(childs[position]),Toast.LENGTH_SHORT).show();
				if(addItem(childs[position])){
					//v.setBackground(getResources().getDrawable(R.drawable.option_selected_bg));
					v.setBackgroundColor(Color.parseColor("#ff0000"));
					Log.e("","add background");
				}
			}
		}

	}

	public class myTextOnClickListener implements OnClickListener {

		public void onClick(View v) {
			addItem(Integer.valueOf(v.getTag().toString()));

		}

	}

	public int[] getChild(int parent) {
		
		int[] childs = {};
		if(parent==0) return childs;
		for (int i = 0; i < ids.length; i++) {
			if (ids[i] == 43 || ids[i] == 44 || ids[i] == 45 || ids[i] == 48)
				continue;
			if (ids[i] / 10 == parent) {
				visit[i] = true;
				childs = ArrayUtils.add(childs, ids[i]);
			}
		}
		return childs;
	}

	public boolean addItem(int id) {
		boolean add=true;
		String ids = "";
		String sid = String.valueOf(id);
		SharedPreferences settings = getSharedPreferences("setting", 0);
		ids = settings.getString("selected", "0,1,2,3,4,");
		Log.e("msg", settings.getString("selected", ""));
		if (ids.indexOf(id + ",") == -1) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("selected", ids + id + ",");
			editor.commit();
			Toast.makeText(ListActivity.this, "添加 " + getName(sid) + " 成功",Toast.LENGTH_SHORT).show();
			Log.e("msg", settings.getString("selected", ""));
		} else {
			Toast.makeText(ListActivity.this, getName(sid) + "  已经添加", Toast.LENGTH_SHORT).show();
			add=false;
		}
		return add;
	}

	public String getName(String id) {
		String name = "";
		try {
			JSONObject obj = new JSONObject(res).getJSONObject(id);
			name = obj.getString("name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			Intent intent = new Intent(ListActivity.this, TabHostActivity.class);
			intent.putExtra("load_date", "yes");
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public String getPath(int id) {
		String father = "";
		String child=getName(String.valueOf(id));
		int tmp=Integer.valueOf(id)/10;
		if(tmp==0){
			father=child;
		}else{
			father=getPath(tmp)+"/"+getName(String.valueOf(id));
		}
		return father;
	}

}
