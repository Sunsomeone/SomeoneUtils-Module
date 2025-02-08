package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/09/11 15:30
 */

import android.graphics.Color;

import androidx.annotation.NonNull;

public class ColorUtil {

    public static int alphaColor(int alpha, int color) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int darkenColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        r = Math.round(r * factor);
        g = Math.round(g * factor);
        b = Math.round(b * factor);
        return Color.argb(a, Math.max(r, 0), Math.max(g, 0), Math.max(b, 0));
    }

    @NonNull
    public static String colorToHex(int color) {
        String alpha = Integer.toHexString((color >> 24) & 0xFF);
        String red = Integer.toHexString((color >> 16) & 0xFF);
        String green = Integer.toHexString((color >> 8) & 0xFF);
        String blue = Integer.toHexString(color & 0xFF);
        alpha = alpha.length() == 1 ? "0" + alpha : alpha;
        red = red.length() == 1 ? "0" + red : red;
        green = green.length() == 1 ? "0" + green : green;
        blue = blue.length() == 1 ? "0" + blue : blue;
        return "#" + alpha + red + green + blue;
    }

}
