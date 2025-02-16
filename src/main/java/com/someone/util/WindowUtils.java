package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/09/23 07:45
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WindowUtils {
    private static final DisplayMetrics displayMetrics = new DisplayMetrics();
    private static int statusBarHeight;
    private static int navigationBarHeight;

    static {
        init();
    }

    public static void init() {
        // 优先级1：通过 WindowInsets 动态获取
        final View decorView = GlobalContextUtil.getActivity().getWindow().getDecorView();
        ViewCompat.setOnApplyWindowInsetsListener(decorView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                // 现代API获取方式（API 20+）
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    WindowInsetsCompat compatInsets = ViewCompat.getRootWindowInsets(view);
                    if (compatInsets != null) {
                        statusBarHeight = compatInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                        navigationBarHeight = compatInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
                    }
                }*/
                // 兼容旧API获取方式（API 14+）
                if (statusBarHeight <= 0) {
                    statusBarHeight = insets.getSystemWindowInsetTop();
                }
                if (navigationBarHeight <= 0) {
                    navigationBarHeight = insets.getSystemWindowInsetBottom();
                }
                return insets;
            }
        });
        // 强制请求布局更新（触发inset回调）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            decorView.post(new Runnable() {
                @Override
                public void run() {

                    decorView.requestApplyInsets();
                    decorView.requestLayout();
                }
            });
        }
        // 标记已收到窗口插入数据
        if (statusBarHeight <= 0 || navigationBarHeight <= 0) {
            // 优先级2：通过反射获取系统资源（兼容旧设备）
            // 状态栏高度反射获取
            if (statusBarHeight <= 0) {
                @SuppressLint({"InternalInsetResource", "DiscouragedApi"}) int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    statusBarHeight = Resources.getSystem().getDimensionPixelSize(resourceId);
                }
            }
            // 导航栏高度反射获取
            if (navigationBarHeight <= 0) {
                @SuppressLint({"InternalInsetResource", "DiscouragedApi"}) int resourceId = Resources.getSystem().getIdentifier("navigation_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    navigationBarHeight = Resources.getSystem().getDimensionPixelSize(resourceId);
                }
            }
        }
        final int MIN_STATUS_BAR_DP = 24;
        final int MIN_NAV_BAR_DP = 48;
        if (statusBarHeight <= 0) {
            statusBarHeight = ViewUtils.toPx(MIN_STATUS_BAR_DP);
        }
        if (navigationBarHeight <= 0) {
            navigationBarHeight = ViewUtils.toPx(MIN_NAV_BAR_DP);
        }
    }

    public static DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    public static void getMetrics() {
        ((WindowManager) GlobalContextUtil.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
    }

    public static void getRealMetrics() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            ((WindowManager) GlobalContextUtil.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(displayMetrics);
        else getMetrics();
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
        return GlobalContextUtil.getContext().getResources().getConfiguration().orientation;
    }

    public static int getStatusBarHeight() {
        return statusBarHeight;
    }

    public static int getNavigationBarHeight() {
        return navigationBarHeight;
    }
}
