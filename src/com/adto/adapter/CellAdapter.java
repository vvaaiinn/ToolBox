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
     * ��������
     */
    @Override
    public int getCount() {

        return list.size();
    }

    /**
     * ��ȡ��ǰ����
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
            //��ȡ�Զ������ʵ��
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
    
         
        //�����������ұ��ߣ�������0��ʼ��˫����ʼ��
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
        public String title;// ���

        // ����չ

        public GridViewItem() {
        }

        public GridViewItem( String title) {
            super();
            this.title = title;
        }

    }

}

