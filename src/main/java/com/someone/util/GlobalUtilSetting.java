package com.someone.util;

/*
  @Author Someone
 * @Date 2024/11/12 16:36
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

public class GlobalUtilSetting {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static Activity activity;
    
    public static Context getContext(){
        return context;
    }
    
    public static Activity getActivity(){
        return activity;
    }
    
    public static void setActivity(Activity activity){
        GlobalUtilSetting.activity = activity;
        GlobalUtilSetting.context = activity;
    }


}
