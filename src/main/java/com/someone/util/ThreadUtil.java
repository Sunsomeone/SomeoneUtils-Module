package com.someone.util;

/*
  @Author Someone
 * @Date 2024/09/07 20:44
 */
import android.os.Handler;
import android.os.Looper;

public abstract class ThreadUtil {
    
    public static void makeThread(final ThreadUtil threadUtil) {
        new Thread(new Runnable() {
                @Override
                public void run() {
                    threadUtil.run();
                }
            }
        ).start();
    }
    
    public static void runOnUiThread(final ThreadUtil threadUtil) {
        GlobalUtilSetting.getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    threadUtil.run();
                }
            }
        );
    }

    public static void makeMainHandler(final ThreadUtil threadUtil) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    threadUtil.run();
                }});
    }
    
    public abstract void run();
}
