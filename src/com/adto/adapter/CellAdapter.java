package com.adto.adapter;


import java.util.HashMap;
import java.util.List;




import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.adto.toolbox.ApplicationData;
import com.adto.toolbox.R;

public class CellAdapter extends BaseAdapter {

    private List<Map<String, String>> list;
    private String tempString;
    private LayoutInflater layoutInflater;

    public CellAdapter(Context context,
            List<Map<String, String>> list) {

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

 /*   @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        if (layoutInflater != null) {

            view = layoutInflater.inflate(R.layout.cell, null);
            TextView textView = (TextView) view.findViewById(R.id.content);
            //获取自定义的类实例
            //tempString = list.get(position).get("title");
 
            textView.setText(list.get(position).get("opt"));

        }
        return view;
    }*/
    
    
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View row=convertView;
         
        if(convertView == null) {
            row=layoutInflater.inflate(R.layout.cell, null);
            holder = new ViewHolder();          
      
            holder.title=(TextView)row.findViewById(R.id.content);
            //holder.rightLineView=(View)row.findViewById(R.id.item_rightline);
            row.setTag(holder);
        } else {
            holder=(ViewHolder)row.getTag();
        }
        String t=list.get(position).get("opt");
        holder.title.setText(t);
    
         
        //单数就隐藏右边线，索引从0开始（双数开始）
        if(position%2!=0) {
            holder.rightLineView.setVisibility(View.GONE);
        }
         
        return row;
    }
    
    
    class ViewHolder {

        TextView title;
        View rightLineView;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public class GridViewItem {
        public String title;// 题标

        // 待扩展

        public GridViewItem() {
        }

        public GridViewItem( String title) {
            super();
            this.title = title;
        }

    }

}

