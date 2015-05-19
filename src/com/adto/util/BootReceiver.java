package com.adto.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {      
    
    @Override     
    public void onReceive(Context context, Intent intent) {      
    /*    //���չ㲥��ϵͳ������ɺ����г���      
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {      
             Intent newIntent = new Intent(context, WatchInstall.class);      
             newIntent.setAction("android.intent.action.MAIN");         
             newIntent.addCategory("android.intent.category.LAUNCHER");       
             newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);         
             context.startActivity(newIntent);      
        }    */  
        //���չ㲥���豸���°�װ��һ��Ӧ�ó�������Զ������°�װӦ�ó���      
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {      
            String packageName = intent.getDataString();      
            System.out.println("---------------" + packageName);      
            Intent newIntent = new Intent();      
            newIntent.setClassName(packageName,packageName+".MainActivity");      
            newIntent.setAction("android.intent.action.MAIN");               
            newIntent.addCategory("android.intent.category.LAUNCHER");               
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);      
            context.startActivity(newIntent);      
        }      
        //���չ㲥���豸��ɾ����һ��Ӧ�ó������      
      /*  if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {      
            System.out.println("********************************");      
            DatabaseHelper dbhelper = new DatabaseHelper();      
            dbhelper.executeSql("delete from users");      
        }  */    
    }
    }    
