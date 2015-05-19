package com.adto.adapter;


import java.util.HashMap;
import java.util.List;




import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.adto.toolbox.ApplicationData;
import com.adto.toolbox.R;

public class EditItemAdapter extends BaseAdapter {

    private List<HashMap<String, String>> list;
    private LayoutInflater layoutInflater;
    HashMap<Integer, Boolean>  state = new HashMap<Integer,Boolean>();
    boolean select;
    public EditItemAdapter(Context context,
            List<HashMap<String, String>> list) {
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }
    
    public EditItemAdapter(Context context,
            List<HashMap<String, String>> list,boolean select) {
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
        this.select=select;
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View row=convertView;
        if(convertView == null) {
            row=layoutInflater.inflate(R.layout.edit_item, null);
            holder = new ViewHolder();   
            holder.check=(CheckBox)row.findViewById(R.id.checkBox);
            holder.name=(TextView)row.findViewById(R.id.name);
            holder.img=(ImageView)row.findViewById(R.id.drag);
            row.setTag(holder);
        } else {
            holder=(ViewHolder)row.getTag();
        }
        String name=list.get(position).get("name");
        String id=list.get(position).get("id");

    	holder.check.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				 
				View view = layoutInflater.inflate(R.layout.edit_item, null); 
				TextView t=(TextView)view.findViewById(R.id.deleteText);
				// TODO Auto-generated method stub
				if(isChecked)
                {  
                       state.put(position, isChecked);
                        //System.out.println("复选框以选中，选中的行数为：" + temp_position);
                       
                }else{
                     state.remove(position);
                }
				//t.setText("删除"+position);
				
			}
    		
    	});
    	holder.check.setTag(id);
    	holder.check.setChecked(state.get(position)==null? false : true);
    	/*holder.check.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Log.e("click",v.getTag().toString());
    		}
    	});*/
    	
        holder.name.setText(name);
        holder.img.setImageResource(R.drawable.drag);
        
        return row;
    }

    class ViewHolder {
    	CheckBox check;
    	TextView name;        
    	ImageView img;
    }

}

