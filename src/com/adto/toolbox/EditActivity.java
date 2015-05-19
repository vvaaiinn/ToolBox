package com.adto.toolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.adto.entity.Constants;
import com.adto.util.GetPostUtil;
import com.adto.util.PrivateUtil;
import com.adto.util.SysApplication;
import com.adto.view.DragListView;


public class EditActivity extends Activity {

	TextView back;
	TextView deleteText,selectAllText;
	CheckBox selectAll;
	ListView listView;
	String url = Constants.URL+"getBizlist.php";
	String res = "";
	String[] ids;
	String idStr;
	LinearLayout loading;

	HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();
	ArrayList<HashMap<String, String>> list;
	ImageButton add;
	private ApplicationData appData;
	String deleteIds = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		Log.e("debug","right here");
		SysApplication.getInstance().addActivity(this);
		appData = (ApplicationData) getApplication();
		Log.e("debug","right here2");
		selectAll = (CheckBox) findViewById(R.id.selectall);
		selectAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox c = (CheckBox) v;
				boolean isChecked = c.isChecked();
				if (isChecked) {
					parseJson(true);
					state.clear();
					for(int i=0;i<ids.length;i++){
						state.put(i, true);
					}
					deleteText.setText("É¾³ý("+ids.length+")");
					setTextGrey(false);
				} else {
					parseJson(false);
					state.clear();
					deleteIds="";
					deleteText.setText("É¾³ý");
					setTextGrey(true);
				}
				c.setChecked(isChecked);

			}
		});

		selectAllText = (TextView) findViewById(R.id.selectAllText);
		deleteText = (TextView) findViewById(R.id.deleteText);
		deleteText.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (deleteIds != "") {
					deleteItems();
					state.clear();
					parseJson(false);
					Toast.makeText(EditActivity.this, "É¾³ý³É¹¦",
							Toast.LENGTH_SHORT).show();
					deleteText.setText("É¾³ý");
					selectAll.setChecked(false);
				} else {
					Toast.makeText(EditActivity.this, "ÇëÑ¡ÔñÒªÉ¾³ýµÄÑ¡Ïî", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		loading = (LinearLayout) findViewById(R.id.loading);
		back = (TextView) findViewById(R.id.edit);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(EditActivity.this,
						TabHostActivity.class);
				intent.putExtra("load_date", "yes");
				startActivity(intent);
				overridePendingTransition(R.anim.show, R.anim.gone);
			}
		});

		add = (ImageButton) findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(EditActivity.this,
						ListActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

		res = appData.getRes();

		parseJson(false);

		/*DragListView dragListView = (DragListView)findViewById(R.id.drag_list);
		EditItemAdapter adapter = new EditItemAdapter(this, list);
        dragListView.setAdapter(adapter);*/

		 /*listView = (ListView) findViewById(R.id.data);
		 listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				final int index = arg2;
				final String deleteItem = getName(String.valueOf(ids[index]));
				// TextView text = (TextView) findViewById(R.id.warning);
				TextView text = new TextView(EditActivity.this);

				text.setTextSize(22);
				text.setPadding(15, 15, 0, 0);
				text.setText("ÄúÈ·¶¨É¾³ý´ËÒµÎñÂð£¿");

				new AlertDialog.Builder(EditActivity.this)
						.setTitle("É¾³ý")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setMessage("ÄúÈ·¶¨É¾³ý  " + deleteItem + " Âð£¿")
						// .setView(text)
						.setPositiveButton("È·¶¨",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										try {
											deleteItem(index);
											Toast.makeText(EditActivity.this,
													"É¾³ý " + deleteItem,
													Toast.LENGTH_SHORT).show();
											parseJson(false);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}

								}).setNegativeButton("È¡Ïû", null).show();

				return false;
			}
		});*/

	}

	public void parseJson(boolean select) {
		try {
			
			loading.setVisibility(View.GONE);
			SharedPreferences settings = getSharedPreferences("setting", 0);
			idStr = settings.getString("selected", "0,1,2,3,4,");
			ids = idStr.split(",");
			Log.e("ids", idStr + "_" + ids.length);
			JSONObject jsonArray = new JSONObject(res).getJSONObject("hour")
					.getJSONObject("0");
			list = new ArrayList<HashMap<String, String>>();
			if (!idStr.equals("")) {
				for (int i = 0; i < ids.length; i++) {
				
					JSONObject obj = jsonArray.getJSONObject(ids[i]);
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("id", ids[i]);
					map.put("name", obj.getString("name"));
					map.put("path",PrivateUtil.getPath(Integer.valueOf(ids[i]), appData.getCatalog()));
					list.add(map);
				}
			}
			EditItemAdapter la = null;
			if (select) {
				la = new EditItemAdapter(EditActivity.this, list, select);
			} else {
				la = new EditItemAdapter(EditActivity.this, list);
			}

			ListView data = (ListView) findViewById(R.id.drag_list);
			loading.setVisibility(View.GONE);
			data.setAdapter(la);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public class myDialogInterfaceOnClickListener implements
			DialogInterface.OnClickListener {
		int id;

		public myDialogInterfaceOnClickListener(int i) {
			id = i;
		}

		public void onClick(DialogInterface dialog, int which) {
			try {
				// deleteItem(id);
				ListView data = (ListView) findViewById(R.id.drag_list);
				data.removeViewAt(id);
				Toast.makeText(EditActivity.this, "É¾³ý " + id,
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteItem(int id) {
		String newids = "";
		for (int i = 0; i < ids.length; i++) {
			if (i != id) {
				newids += ids[i] + ",";
			}
		}

		SharedPreferences settings = getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("selected", newids);
		editor.commit();

	}

	public void deleteItems() {
		String newIds = "";
		for (int i = 0; i < ids.length; i++) {
			if (deleteIds.indexOf(ids[i]) == -1) {
				newIds += ids[i] + ",";
			}
		}
		SharedPreferences settings = getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("selected", newIds);
		editor.commit();

	}

	public String getName(String id) {
		String name = "";
		try {
			JSONObject obj = new JSONObject(res).getJSONObject("hour")
					.getJSONObject("0").getJSONObject(id);
			name = obj.getString("name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			Intent intent = new Intent(EditActivity.this, TabHostActivity.class);
			intent.putExtra("load_date", "yes");
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public class EditItemAdapter extends BaseAdapter {

		private List<HashMap<String, String>> list;
		private LayoutInflater layoutInflater;
		
		boolean select;

		public EditItemAdapter(Context context,
				List<HashMap<String, String>> list) {
			this.list = list;
			layoutInflater = LayoutInflater.from(context);
		}

		public EditItemAdapter(Context context,
				List<HashMap<String, String>> list, boolean select) {
			this.list = list;
			layoutInflater = LayoutInflater.from(context);
			this.select = select;
		}
		
		public void swapItem(int from,int to){
			HashMap<String, String> item=list.get(from);
			list.remove(from);
			list.add(to, item);
			boolean mid;
			HashMap<Integer, Boolean> newState=new HashMap<Integer, Boolean>();
			for (HashMap.Entry<Integer, Boolean> it : state.entrySet()) {
				int index=Integer.valueOf(it.getKey());
				if(Mid(index,from,to)){
					//state.remove(index);
					if(index==from){newState.put(to, true);continue;}
					if(from<to){
						newState.put(index-1, true);
					}else{
						newState.put(index+1, true);
					}
				}else{
					newState.put(index, true);
				}				
			}
			state.clear();
			state=newState;
			Log.e("delete id",deleteIds);
			this.notifyDataSetChanged();
			ids= getSharedPreferences("setting", 0).getString("selected", "0,1,2,3,4,").split(",");
			parseJson(false);
		}
		
		public boolean Mid(int m,int x,int y){
			if(x<=y&&m>=x&&m<=y){return true;}
			if(x>y&&m>=y&&m<=x){return true;}
			return false;
		}

		public int getSelectedSize() {
		
			deleteIds = "";
			if(state.entrySet().size()==0){return 0;}
		
			
			for (HashMap.Entry<Integer, Boolean> item : state.entrySet()) {
				Log.e("deleteid",item.getKey().toString());
				deleteIds += ids[item.getKey()] + ",";
			}
			
			return state.entrySet().size();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			View row = convertView;
			if (convertView == null) {
				row = layoutInflater.inflate(R.layout.edit_item, null);
				holder = new ViewHolder();
				holder.check = (CheckBox) row.findViewById(R.id.checkBox);
				holder.name = (TextView) row.findViewById(R.id.itemname);
				holder.img = (ImageView) row.findViewById(R.id.drag);
				holder.path = (TextView) row.findViewById(R.id.path);
				row.setTag(holder);
			} else {
				holder = (ViewHolder) row.getTag();
			}
			String name = list.get(position).get("name");
			String id = list.get(position).get("id");
			String path = list.get(position).get("path");
			holder.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {

							View view = layoutInflater.inflate(
									R.layout.edit_item, null);
							
							// TODO Auto-generated method stub
							if (isChecked) {
								
								state.put(position, isChecked);
								if(state.size()==list.size()){selectAll.setChecked(true);selectAllText.setTextColor(getResources().getColor(R.color.white));}
								Log.e("checked",position+"");
							} else {
								state.remove(position);
								selectAll.setChecked(false);
								selectAllText.setTextColor(getResources().getColor(R.color.grey));
							}
							int dSize = getSelectedSize();
							if (dSize != 0) {
								deleteText.setText("É¾³ý(" + dSize + ")");
								setTextGrey(false);
							} else {
								deleteText.setText("É¾³ý");
								setTextGrey(true);
							}
						}

					});
			holder.check.setTag(id);
			holder.check.setChecked(state.get(position) == null ? false : true);

			holder.name.setText(name); 
			holder.path.setText(path); 
			holder.img.setImageResource(R.drawable.drag);

			return row;
		}

		class ViewHolder {
			CheckBox check;
			TextView name,path;
			ImageView img;
		}

	}
	
	public void setTextGrey(boolean grey){
		if(grey){
			deleteText.setTextColor(getResources().getColor(R.color.grey));
			selectAllText.setTextColor(getResources().getColor(R.color.grey));
		}else{
			deleteText.setTextColor(getResources().getColor(R.color.white));
			selectAllText.setTextColor(getResources().getColor(R.color.white));
		}
	}
	


}
