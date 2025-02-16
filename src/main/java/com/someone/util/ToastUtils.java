package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/09/07 20:13
 */

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class ToastUtils {

    public static void makeToast(final Object text, final int duration) {
        if (isMainThread()) {
            Toast.makeText(GlobalContextUtil.getContext(), "" + text, duration).show();
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GlobalContextUtil.getContext(), text.toString(), duration).show();
                }
            });
        }
    }

    public static void makeShortToast(@NonNull final Object... text) {
        StringBuilder mText = new StringBuilder();
        for (Object nText : text) {
            mText.append(nText);
        }
        makeToast(mText.toString(), Toast.LENGTH_SHORT);
    }

    public static void makeLongToast(@NonNull final Object... text) {
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
