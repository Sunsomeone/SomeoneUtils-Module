package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/11/12 16:36
 */

import android.app.Activity;
import android.content.Context;
import java.lang.ref.WeakReference;

public class GlobalContextUtil {
    private static WeakReference<Context> contextRef;
    private static WeakReference<Activity> activityRef;
    
    public static Context getContext(){
        return contextRef.get();
    }
    
    public static Activity getActivity(){
        return activityRef.get();
    }
    
    public static void setActivity(Activity activity){
        GlobalContextUtil.activityRef = new WeakReference<>(activity);
        GlobalContextUtil.contextRef = new WeakReference<Context>(activity);
    }
}
