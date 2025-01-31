package com.someone.util;

/**
 * @Author Someone
 * @Date 2024/09/07 20:13
 */
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtil {

    public static void makeToast(final Object text, final int duration) {
        if (isMainThread()) {
            Toast.makeText(GlobalUtilSetting.getContext(), "" + text, duration).show();
        } else {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GlobalUtilSetting.getContext(), "" + text, duration).show();
                    }});
        }
    }

    public static void makeShortToast(final Object... text) {
        StringBuilder mText = new StringBuilder();
        for (Object nText : text) {
            mText.append(nText);
        }
        makeToast(mText.toString(), Toast.LENGTH_SHORT);
    }

    public static void makeLongToast(final Object... text) {
        StringBuilder mText = new StringBuilder();
        for (Object nText : text) {
            mText.append(nText);
        }
        makeToast(mText.toString(), Toast.LENGTH_LONG);
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
