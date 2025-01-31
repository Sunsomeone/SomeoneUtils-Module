package com.someone.util;

/*
  @Author Someone
 * @Date 2024/09/23 07:45
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class WindowUtil {
    private static final DisplayMetrics displayMetrics = new DisplayMetrics();

    public static DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    public static void getMetrics() {
        ((WindowManager) GlobalUtilSetting.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
    }

    public static void getRealMetrics() {
        ((WindowManager) GlobalUtilSetting.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(displayMetrics);
    }

    public static int getRealScreenWidth() {
        getRealMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getRealScreenHeight() {
        getRealMetrics();
        return displayMetrics.heightPixels;
    }

    public static int getScreenWidth() {
        getMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        getMetrics();
        return displayMetrics.heightPixels;
    }

    public static int getWindowOrientation() {
        return GlobalUtilSetting.getContext().getResources().getConfiguration().orientation;
    }

    public static int getStatusBarHeight() {
        int result = 0;
        @SuppressLint({"InternalInsetResource", "DiscouragedApi"}) int resourceId = GlobalUtilSetting.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = GlobalUtilSetting.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavigationBarHeight() {
        Resources resources = GlobalUtilSetting.getContext().getResources();
        @SuppressLint({"InternalInsetResource", "DiscouragedApi"}) int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

}
