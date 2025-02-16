package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/09/11 15:30
 */

import android.graphics.Color;

import androidx.annotation.NonNull;

public class ColorUtils {

    public static int setColorAlpha(int originalColor, int alpha) {
        return Color.argb(Math.max(0, Math.min(255, alpha)), Color.red(originalColor), Color.green(originalColor), Color.blue(originalColor));
    }

    public static int setColorDarken(int color, float factor) {
        return Color.argb(Color.alpha(color), Math.max(Math.round(Color.red(color) * factor), 0), Math.max(Math.round(Color.green(color) * factor), 0), Math.max(Math.round(Color.blue(color) * factor), 0));
    }

    @NonNull
    public static String colorToHex(int color) {
        String alpha = Integer.toHexString((color >> 24) & 0xFF).length() == 1 ? "0" + Integer.toHexString((color >> 24) & 0xFF) : Integer.toHexString((color >> 24) & 0xFF);
        String red = Integer.toHexString((color >> 16) & 0xFF).length() == 1 ? "0" + Integer.toHexString((color >> 16) & 0xFF) : Integer.toHexString((color >> 16) & 0xFF);
        String green = Integer.toHexString((color >> 8) & 0xFF).length() == 1 ? "0" + Integer.toHexString((color >> 8) & 0xFF) : Integer.toHexString((color >> 8) & 0xFF);
        String blue = Integer.toHexString(color & 0xFF).length() == 1 ? "0" + Integer.toHexString(color & 0xFF) : Integer.toHexString(color & 0xFF);
        return "#" + alpha + red + green + blue;
    }

}
