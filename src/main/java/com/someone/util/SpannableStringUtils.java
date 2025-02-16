package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/11/10 09:38
 */

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class SpannableStringUtils {
    public final static int TYPEFACE_NORMAL = 0;
    public final static int TYPEFACE_BOLD = 1;
    public final static int TYPEFACE_ITALIC = 2;
    public final static int TYPEFACE_BOLD_ITALIC = 3;
    private final SpannableString spannableString;
    private int flags = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;
    private int start = 0;
    private int end = -1;
    private SpanStyle style;

    public SpannableStringUtils(String str) {
        this.spannableString = new SpannableString(str);
        this.style = new SpanStyle();
    }

    public SpannableStringUtils setApplyRange(int start, int end) {
        this.start = start;
        this.end = end;
        return this;
    }

    public SpannableStringUtils setTypeface(int typeface) {
        this.style.setTypeface(typeface);
        return this;
    }

    public SpannableStringUtils setForegroundColor(int color) {
        this.style.setForegroundColor(color);
        return this;
    }

    public SpannableStringUtils setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public SpannableStringUtils removeTypeface() {
        Object removeTypeface = null;
        for (Object span : spannableString.getSpans(start, end, Object.class)) {
            if (span instanceof StyleSpan) {
                removeTypeface = span;
                break;
            }
        }
        if (removeTypeface != null) {
            spannableString.removeSpan(removeTypeface);
        }
        return this;
    }

    public SpannableStringUtils removeForegroundColor() {
        Object removeForegroundColor = null;
        for (Object span : spannableString.getSpans(start, end, Object.class)) {
            if (span instanceof ForegroundColorSpan) {
                removeForegroundColor = span;
                break;
            }
        }
        if (removeForegroundColor != null) {
            spannableString.removeSpan(removeForegroundColor);
        }
        return this;
    }

    public SpannableStringUtils apply() {
        int end = this.end;
        int flags = this.flags;
        int start = this.start;
        if (end == -1) {
            end = spannableString.length();
        }
        spannableString.setSpan(style, start, end, flags);
        style = new SpanStyle();
        return this;
    }

    public SpannableString getSpannableString() {
        return spannableString;
    }

    public static class SpanStyle extends CharacterStyle {
        private StyleSpan styleSpan = new StyleSpan(0);
        private ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLACK);

        public void setTypeface(int textStyle) {
            this.styleSpan = new StyleSpan(textStyle);
        }

        public void setForegroundColor(int textColor) {
            this.colorSpan = new ForegroundColorSpan(textColor);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            styleSpan.updateDrawState(tp);
            colorSpan.updateDrawState(tp);
        }
    }
}
