package com.someone.debug;

/*
 * @Author Someone
 * @Date 2024/09/04 20:34
 */

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.someone.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public final class LogReceiver {
    @SuppressLint("StaticFieldLeak")
    private static final TextView logTextView;
    @SuppressLint("StaticFieldLeak")
    private static final ScrollView logScrollView;

    static {
        logTextView = DebugWindowManager.getLogText();
        logScrollView = DebugWindowManager.getDisplayScroll();
    }

    @Nullable
    private static View findViewWithTag(ViewGroup viewGroup, Object tag) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (tag.equals(child.getTag())) {
                return child;
            }
            if (child instanceof ViewGroup) {
                View result = findViewWithTag((ViewGroup) child, tag);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @NonNull
    private static SpannableString setTextColor(String text, int color) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @NonNull
    private static SpannableString setTextBold(String text) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;

    }


    private static void addLog(final String type, final Object... messages) {

        if (isMainThread()) {
            try {
                StringBuilder messageStr = new StringBuilder();
                for (Object message : messages) {
                    messageStr.append(" ").append(JsonSerializer.toJson(message));
                }
                String logInfo = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    logInfo += getCurrentTime() + " " + type + " " + getCurrentClassName() + "." + getCurrentMethodName() + "() " + messageStr + System.lineSeparator();
                }
                logTextView.append(setTextColor(logInfo, getTextColor(type)));
                logScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        logScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            } catch (IllegalAccessException | JSONException e) {
                ToastUtil.makeLongToast(e);
            }
        } else {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        StringBuilder messageStr = new StringBuilder();
                        for (Object message : messages) {
                            messageStr.append(" ").append(JsonSerializer.toJson(message));
                        }
                        String logInfo = "";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            logInfo += getCurrentTime() + " " + type + " " + messageStr + System.lineSeparator();
                        }
                        logTextView.append(setTextColor(logInfo, getTextColor(type)));
                        logScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                logScrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    } catch (IllegalAccessException | JSONException e) {
                        ToastUtil.makeLongToast(e);
                    }
                }
            });
        }

    }

    private static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private static int getTextColor(@NonNull String type) {
        switch (type) {
            case "Info":
                return Color.parseColor("#FF8BC34A");
            case "Error":
                return Color.parseColor("#FFF56162");
            case "Warn":
                return Color.parseColor("#FFFFD740");
            case "Verbose":
                return Color.parseColor("#FFFFFFFF");
            case "Debug":
                return Color.parseColor("#FF23BBFF");
        }
        return Color.WHITE;
    }

    public static void e(Object... message) {
        addLog("Error", message);
    }

    public static void w(Object... message) {
        addLog("Warn", message);
    }

    public static void i(Object... message) {
        addLog("Info", message);
    }

    public static void v(Object... message) {
        addLog("Verbose", message);
    }

    public static void d(Object... message) {
        addLog("Debug", message);
    }

    @NonNull
    private static String getCurrentTime() {
        Date currentDate = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentDate);
    }

    private static String getCurrentClassName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length >= 5) {
            StackTraceElement element = stackTraceElements[5];
            return element.getClassName();
        } else {
            return "Unknown className";
        }
    }

    private static String getCurrentMethodName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length >= 5) {
            StackTraceElement element = stackTraceElements[5];
            return element.getMethodName();
        } else {
            return "Unknown methodName";
        }
    }

    @NonNull
    private static String getStack() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StringBuilder stack = new StringBuilder();
        for (StackTraceElement element : stackTraceElements) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                stack.append(element.toString()).append(System.lineSeparator());
            }
        }
        return stack.toString();
    }

    public static class JsonSerializer {

        @NonNull
        public static String toJson(Object obj) throws JSONException, IllegalAccessException {
            if (obj == null) return "null";

            // 处理基本类型、字符串、日期
            if (isPrimitiveOrWrapper(obj) || obj instanceof String || obj instanceof Date) {
                return parsePrimitiveValue(obj).toString();
            }

            // 处理数组或集合
            if (obj.getClass().isArray() || obj instanceof Collection) {
                return parseArray(obj).toString();
            }

            if (obj instanceof Enum) {
                return ((Enum) obj).name();
            }

            // 处理自定义对象
            return parseObject(obj).toString();
        }

        private static boolean isPrimitiveOrWrapper(Object obj) {
            return obj instanceof Number || obj instanceof Boolean || obj instanceof Character;
        }

        private static Object parsePrimitiveValue(Object value) {
            if (value instanceof Date) {
                return ((Date) value).getTime();
            }
            return value;
        }

        @NonNull
        private static JSONArray parseArray(@NonNull Object obj) throws JSONException, IllegalAccessException {
            JSONArray jsonArray = new JSONArray();
            if (obj.getClass().isArray()) {
                int length = Array.getLength(obj);
                for (int i = 0; i < length; i++) {
                    jsonArray.put(parseValue(Array.get(obj, i)));
                }
            } else if (obj instanceof Collection) {
                for (Object item : (Collection<?>) obj) {
                    jsonArray.put(parseValue(item));
                }
            }
            return jsonArray;
        }

        @NonNull
        private static JSONObject parseObject(Object obj) throws JSONException, IllegalAccessException {
            JSONObject jsonObject = new JSONObject();
            Class<?> clazz = obj.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (java.lang.reflect.Modifier.isTransient(field.getModifiers()) ||
                        java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Object value = field.get(obj);
                jsonObject.put(field.getName(), parseValue(value));
            }
            return jsonObject;
        }

        private static Object parseValue(Object value) throws JSONException, IllegalAccessException {
            if (value == null) return JSONObject.NULL;

            if (value instanceof Enum) {
                return ((Enum) value).name();
            }

            if (isPrimitiveOrWrapper(value) || value instanceof String || value instanceof Date) {
                return parsePrimitiveValue(value);
            }

            if (value.getClass().isArray() || value instanceof Collection) {
                return parseArray(value);
            }

            return parseObject(value);
        }
    }
}
