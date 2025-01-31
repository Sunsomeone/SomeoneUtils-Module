package com.someone.util;

/*
  @Author Someone
 * @Date 2024/11/05 12:10
 */

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class WindowManagerUtil {
    private static final WindowManager windowManager = (WindowManager) GlobalUtilSetting.getContext().getSystemService(Context.WINDOW_SERVICE);
    private static final HashMap<String,View> viewMap = new HashMap<>();
    private static final HashMap<String,List<String>> tagMap = new HashMap<>();
    private static WindowManagerListener windowManagerListener;

    public static final String TAG_IS_SHOWING = "isShowing";
    public static final String TAG_IS_VISIBILITY = "isVisibility";
    public static final String TAG_IS_HIDING = "isHiding";

    public static WindowManager.LayoutParams getLayoutParams(View view) {
        return (WindowManager.LayoutParams)view.getLayoutParams();
    }

    public static int getViewCount() {
        return viewMap.size();
    }

    public static View getView(String viewName) {
        return viewMap.get(viewName);
    }

    public static LayoutParams createLayoutParams() {
        return new WindowManagerUtil.LayoutParams();
    }

    public static WindowManager getWindowManager() {
        return windowManager;
    }

    public static void updateViewLayout(View view, WindowManager.LayoutParams params) {
        if (windowManagerListener != null) {
            windowManagerListener.onUpdateViewLayout(view, params);
        }
        windowManager.updateViewLayout(view, params);
    }

    public static void addView(String tag, View view, WindowManager.LayoutParams params) {
        if (windowManagerListener != null) {
            windowManagerListener.onAddView(tag, view, params);
        }
        if (viewMap.containsKey(tag)) {
            removeView(tag);
        }
        viewMap.put(tag, view);
        tagMap.put(tag, new ArrayList<>());
        windowManager.addView(view, params);
    }

    public static void removeView(String tag) {
        if (windowManagerListener != null) {
            windowManagerListener.onRemoveView(tag);
        }
        if (viewMap.containsKey(tag)) {
            windowManager.removeView(viewMap.get(tag));
            viewMap.remove(tag);
            tagMap.remove(tag);
        }
    }

    public static String getViewStateTag(String tag) {
        if (viewMap.containsKey(tag)) {
            return Objects.requireNonNull(tagMap.get(tag)).get(0);
        }
        return null;
    }

    public static void showView(String tag) {
        if (viewMap.containsKey(tag)) {
            Objects.requireNonNull(viewMap.get(tag)).setVisibility(View.VISIBLE);
            Objects.requireNonNull(tagMap.get(tag)).add(0, TAG_IS_SHOWING);
        }
    }

    public static void invisibleView(String tag) {
        if (viewMap.containsKey(tag)) {
            Objects.requireNonNull(viewMap.get(tag)).setVisibility(View.INVISIBLE);
            Objects.requireNonNull(tagMap.get(tag)).add(0, TAG_IS_VISIBILITY);
        }
    }

    public static void hideView(String tag) {
        if (viewMap.containsKey(tag)) {
            Objects.requireNonNull(viewMap.get(tag)).setVisibility(View.GONE);
            Objects.requireNonNull(tagMap.get(tag)).add(0, TAG_IS_HIDING);
        }
    }

    public static void addWindowManagerListener(WindowManagerListener windowManagerListener) {
        WindowManagerUtil.windowManagerListener = windowManagerListener;
    }

    public static void removeWindowManagerListener() {
        WindowManagerUtil.windowManagerListener = null;
    }


    public static abstract interface WindowManagerListener {

        public abstract void onAddView(String tag, View view, WindowManager.LayoutParams params);

        public abstract void onUpdateViewLayout(View view, WindowManager.LayoutParams params);

        public abstract void onRemoveView(String tag);

    }

    public static class LayoutParams extends WindowManager.LayoutParams {

        public LayoutParams() {
            setDefaultParams();
        }

        public LayoutParams getLayoutParams() {
            return this;
        }

        public void setDefaultParams() {
            width = WindowManager.LayoutParams.WRAP_CONTENT;
            height = WindowManager.LayoutParams.WRAP_CONTENT;
            type = WindowManager.LayoutParams.TYPE_APPLICATION;
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            format = PixelFormat.TRANSLUCENT;
        }

        public LayoutParams setParamsSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public LayoutParams setParamsFormat(int format) {
            this.format = format;
            return this;
        }

        public LayoutParams setParamsType(int type) {
            this.type = type;
            return this;
        }

        public LayoutParams setParamsFlags(int flags) {
            this.flags = flags;
            return this;
        }

        public LayoutParams setParamsGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public LayoutParams setParamsLocation(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public abstract class CustomRealize {
            public abstract void setCustomRealize();
        }
    }

}
