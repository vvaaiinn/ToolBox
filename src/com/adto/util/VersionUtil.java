package com.adto.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import com.adto.entity.Constants;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class VersionUtil {  
    //public static final String SERVER_IP="http://portal.ad.sogou.com/consume/download.php";  
    public static final String SERVER_ADDRESS=Constants.URL+"download.php";//软件更新包地址  

    
    public static String getVersionFromServer() {  
        
        try {  
            
            return GetPostUtil.sendGet(SERVER_ADDRESS, null);  
  
        } catch (Exception e) {  
            // TODO: handle exception  
            Log.e("msg",e.getMessage());  
            return "";  
        } finally {  
            try {  
                
                // 这两种释放连接的方法都可以  
            } catch (Exception e) {  
                // TODO Auto-generated catch block  
                Log.e("msg",e.getMessage());  
            }  
        }  
    }  
      
    /** 
     * 获取软件版本号 
     * @param context 
     * @return 
     */  
    public static int getVerCode(Context context) {  
        int verCode = -1;  
        try {  
            //注意："com.example.try_downloadfile_progress"对应AndroidManifest.xml里的package="……"部分  
            verCode = context.getPackageManager().getPackageInfo(  
                    "com.adto.toolbox", 0).versionCode;  
        } catch (NameNotFoundException e) {  
            Log.e("msg",e.getMessage());  
        }  
        return verCode;  
    }  
   /** 
    * 获取版本名称 
    * @param context 
    * @return 
    */  
    public static String getVerName(Context context) {  
        String verName = "";  
        try {  
            verName = context.getPackageManager().getPackageInfo(  
                    "com.adto.toolbox", 0).versionName;  
        } catch (NameNotFoundException e) {  
            Log.e("msg",e.getMessage());  
        }  
        return verName;     
}     
      
}  