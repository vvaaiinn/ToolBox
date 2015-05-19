//package com.adto.adapter;
//
//
//import java.util.HashMap;
//import java.util.List;
//
//
//
//
//import java.util.Map;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.View.OnClickListener;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.adto.toolbox.ApplicationData;
//import com.adto.toolbox.MainActivity;
//import com.adto.toolbox.R;
//
//
//public class ListItemAdapter extends BaseAdapter {
//
//	private List<HashMap<String, String>> list;
//
//	private LayoutInflater layoutInflater;
//	private Context context;
//
//	public ListItemAdapter(Context context,
//			List<HashMap<String, String>> list) {
//
//		this.list = list;
//		this.context=context;
//		layoutInflater = LayoutInflater.from(context);
//
//	}
//
//	/**
//	 * 数据总数
//	 */
//	@Override
//	public int getCount() {
//
//		return list.size();
//	}
//
//	/**
//	 * 获取当前数据
//	 */
//	@Override
//	public Object getItem(int position) {
//
//		return list.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//
//		return position;
//	}
//
//	public View getView(int position, View convertView, ViewGroup parent) {
//		ViewHolder holder = null;
//		View row = convertView;
//
//		if (convertView == null) {
//			row = layoutInflater.inflate(R.layout.list_item, null);
//			holder = new ViewHolder();
//			holder.t1 = (TextView) row.findViewById(R.id.t1);
//			holder.t2 = (TextView) row.findViewById(R.id.t2);
//			holder.t3 = (TextView) row.findViewById(R.id.t3);
//			holder.path = (TextView) row.findViewById(R.id.path);
//			row.setTag(holder);
//		} else {
//			holder = (ViewHolder) row.getTag();
//		}
//		String item = list.get(position).get("item");
//		String consume = list.get(position).get("consume");
//		String rate = list.get(position).get("rate");
//
//		String path = list.get(position).get("path");
//
//		holder.t1.setText(item);
//		holder.t2.setText(consume);
//		holder.t3.setText(rate);
//		holder.path.setText(path);
//
//		//holder.t3.setOnClickListener(context.get);
//
//
//		if (rate.indexOf('-') != -1) {
//
//			holder.t3.setBackgroundColor(context.getResources().getColor(R.color.green));
//		} else if (rate.equals("0.00%") || rate.equals("N/A")) {
//
//			holder.t3.setBackgroundColor(context.getResources().getColor(R.color.grey));
//		} else {
//			holder.t3.setBackgroundColor(context.getResources().getColor(R.color.red));
//		}
//
//		return row;
//	}
//	
//	class ViewHolder {
//		TextView t1;
//		TextView t2;
//		TextView t3;
//		TextView path;
//	}
//
//}
//
