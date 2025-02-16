package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/09/07 20:44
 */

import android.os.Handler;
import android.os.Looper;

public class ThreadUtils {

    public static void makeThread(final Runnable runnable) {
        new Thread(new java.lang.Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }).start();
    }

    public static void runOnUiThread(final Runnable runnable) {
        GlobalContextUtil.getActivity().runOnUiThread(new java.lang.Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    public static void makeMainHandler(final Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(new java.lang.Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    public interface Runnable {
        public abstract void run();
    }
}
