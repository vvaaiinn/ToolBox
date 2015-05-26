package com.adto.toolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adto.entity.Constants;
import com.adto.util.PrivateUtil;
import com.adto.util.SysApplication;

public class EditActivity extends Activity {

	TextView back;
	TextView deleteText, selectAllText;
	CheckBox selectAll;
	ListView listView;
	String url = Constants.URL + "getBizlist.php";
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
		Log.e("debug", "right here");
		SysApplication.getInstance().addActivity(this);
		appData = (ApplicationData) getApplication();
		Log.e("debug", "right here2");
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
					for (int i = 0; i < ids.length; i++) {
						state.put(i, true);
					}
					deleteText.setText("删除(" + ids.length + ")");
					setTextGrey(false);
				} else {
					parseJson(false);
					state.clear();
					deleteIds = "";
					deleteText.setText("删除");
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
					Toast.makeText(EditActivity.this, "删除成功",
							Toast.LENGTH_SHORT).show();
					deleteText.setText("删除");
					selectAll.setChecked(false);
				} else {
					Toast.makeText(EditActivity.this, "请选择要删除的选项",
							Toast.LENGTH_SHORT).show();
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

		/*
		 * DragListView dragListView =
		 * (DragListView)findViewById(R.id.drag_list); EditItemAdapter adapter =
		 * new EditItemAdapter(this, list); dragListView.setAdapter(adapter);
		 */

		/*
		 * listView = (ListView) findViewById(R.id.data);
		 * listView.setOnItemLongClickListener(new OnItemLongClickListener() {
		 * 
		 * @Override public boolean onItemLongClick(AdapterView<?> arg0, View
		 * arg1, int arg2, long arg3) {
		 * 
		 * final int index = arg2; final String deleteItem =
		 * getName(String.valueOf(ids[index])); // TextView text = (TextView)
		 * findViewById(R.id.warning); TextView text = new
		 * TextView(EditActivity.this);
		 * 
		 * text.setTextSize(22); text.setPadding(15, 15, 0, 0);
		 * text.setText("您确定删除此业务吗？");
		 * 
		 * new AlertDialog.Builder(EditActivity.this) .setTitle("删除")
		 * .setIcon(android.R.drawable.ic_dialog_info) .setMessage("您确定删除  " +
		 * deleteItem + " 吗？") // .setView(text) .setPositiveButton("确定", new
		 * DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int which) { try { deleteItem(index);
		 * Toast.makeText(EditActivity.this, "删除 " + deleteItem,
		 * Toast.LENGTH_SHORT).show(); parseJson(false); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 * 
		 * }).setNegativeButton("取消", null).show();
		 * 
		 * return false; } });
		 */

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
					map.put("path", PrivateUtil.getPath(
							Integer.valueOf(ids[i]), appData.getCatalog()));
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
				Toast.makeText(EditActivity.this, "删除 " + id,
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
		String[] tt = deleteIds.split(",");
		int flag = 0;
		for (int i = 0; i < ids.length; i++) {
			for (int j = 0; j < tt.length; j++) {
				if (tt[j].equals(ids[i])) {
					flag = 1;
					break;
				}
			}
			if (flag == 0)
				newIds += ids[i] + ",";
			flag = 0;

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

		public void swapItem(int from, int to) {
			HashMap<String, String> item = list.get(from);
			list.remove(from);
			list.add(to, item);
			boolean mid;
			HashMap<Integer, Boolean> newState = new HashMap<Integer, Boolean>();
			for (HashMap.Entry<Integer, Boolean> it : state.entrySet()) {
				int index = Integer.valueOf(it.getKey());
				if (Mid(index, from, to)) {
					// state.remove(index);
					if (index == from) {
						newState.put(to, true);
						continue;
					}
					if (from < to) {
						newState.put(index - 1, true);
					} else {
						newState.put(index + 1, true);
					}
				} else {
					newState.put(index, true);
				}
			}
			state.clear();
			state = newState;
			Log.e("delete id", deleteIds);
			this.notifyDataSetChanged();
			ids = getSharedPreferences("setting", 0).getString("selected",
					"0,1,2,3,4,").split(",");
			parseJson(false);
		}

		public boolean Mid(int m, int x, int y) {
			if (x <= y && m >= x && m <= y) {
				return true;
			}
			if (x > y && m >= y && m <= x) {
				return true;
			}
			return false;
		}

		public int getSelectedSize() {

			deleteIds = "";
			if (state.entrySet().size() == 0) {
				return 0;
			}

			for (HashMap.Entry<Integer, Boolean> item : state.entrySet()) {
				Log.e("deleteid", item.getKey().toString());
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
			holder.check.setTag(id);
			holder.name.setText(name);
			holder.path.setText(path);
			final ViewHolder hh = holder;
			holder.name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 自动生成的方法存根

					if (state.get(position) == null) {
						state.put(position, true);
						if (state.size() == list.size()) {
							selectAll.setChecked(true);
							selectAllText.setTextColor(getResources().getColor(
									R.color.white));
						}
						Log.e("checked", position + "");
						Log.e("checked", state.get(position).toString());
						// Log.e("checked", tt + "haha");

					} else {
						state.remove(position);
						selectAll.setChecked(false);
						selectAllText.setTextColor(getResources().getColor(
								R.color.grey));
					}
					hh.check.setChecked(state.get(position) == null ? false
							: true);
					Log.e("checked", position + "position");
					int dSize = getSelectedSize();
					if (dSize != 0) {
						deleteText.setText("删除(" + dSize + ")");
						setTextGrey(false);
					} else {
						deleteText.setText("删除");
						setTextGrey(true);
					}
				}

			});
			holder = hh;
			holder.path.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 自动生成的方法存根

					if (state.get(position) == null) {
						state.put(position, true);
						if (state.size() == list.size()) {
							selectAll.setChecked(true);
							selectAllText.setTextColor(getResources().getColor(
									R.color.white));
						}
						Log.e("checked", position + "");
						Log.e("checked", state.get(position).toString());
						// Log.e("checked", tt + "haha");

					} else {
						state.remove(position);
						selectAll.setChecked(false);
						selectAllText.setTextColor(getResources().getColor(
								R.color.grey));
					}
					hh.check.setChecked(state.get(position) == null ? false
							: true);
					Log.e("checked", position + "position");
					int dSize = getSelectedSize();
					if (dSize != 0) {
						deleteText.setText("删除(" + dSize + ")");
						setTextGrey(false);
					} else {
						deleteText.setText("删除");
						setTextGrey(true);
					}
				}

			});
			holder = hh;
			holder.check
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {

							// View view = layoutInflater.inflate(
							// R.layout.edit_item, null);

							// TODO Auto-generated method stub
							if (isChecked) {

								state.put(position, isChecked);
								if (state.size() == list.size()) {
									selectAll.setChecked(true);
									selectAllText.setTextColor(getResources()
											.getColor(R.color.white));
								}
								Log.e("checked", position + "");
							} else {
								state.remove(position);
								selectAll.setChecked(false);
								selectAllText.setTextColor(getResources()
										.getColor(R.color.grey));
							}
							int dSize = getSelectedSize();
							if (dSize != 0) {
								deleteText.setText("删除(" + dSize + ")");
								setTextGrey(false);
							} else {
								deleteText.setText("删除");
								setTextGrey(true);
							}
						}

					});

			holder.check.setChecked(state.get(position) == null ? false : true);

			holder.img.setImageResource(R.drawable.drag);

			return row;
		}

		class ViewHolder {
			CheckBox check;
			TextView name, path;
			ImageView img;
		}

	}

	public void setTextGrey(boolean grey) {
		if (grey) {
			deleteText.setTextColor(getResources().getColor(R.color.grey));
			selectAllText.setTextColor(getResources().getColor(R.color.grey));
		} else {
			deleteText.setTextColor(getResources().getColor(R.color.white));
			selectAllText.setTextColor(getResources().getColor(R.color.white));
		}
	}

}
