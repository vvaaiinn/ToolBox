package com.adto.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {
	private  Context context;
	public NetUtil(Context c){
		this.context=c;
	}	
	public  boolean isNetworkConnected() {  
        ConnectivityManager cm =   
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo network = cm.getActiveNetworkInfo();  
        if (network != null) {  
            return network.isAvailable();  
        }  
        return false;  
    }
}
