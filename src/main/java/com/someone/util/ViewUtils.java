package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/09/08 22:45
 */

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class ViewUtils {

    public static int toPx(int dp) {
        return (int) (dp * GlobalContextUtil.getContext().getResources().getDisplayMetrics().density);
    }

    public static int toDp(int px) {
        return (int) (px / GlobalContextUtil.getContext().getResources().getDisplayMetrics().density);
    }

    public static int toRoundPx(int dp) {
        return Math.round(dp * GlobalContextUtil.getContext().getResources().getDisplayMetrics().density);
    }

    public static int toRoundDp(int px) {
        return Math.round(px / GlobalContextUtil.getContext().getResources().getDisplayMetrics().density);
    }

    public static class TextFit {
        public static void fitText(@NonNull final TextView view) {
            view.setIncludeFontPadding(false);
            ViewTreeObserver observer = view.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int height = view.getHeight();
                    if (height > 0) {
                        adjustSize(view, height);
                    } else {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                int height = view.getHeight();
                                if (height > 0) {
                                    adjustSize(view, height);
                                }
                            }
                        });
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }

        private static void adjustSize(@NonNull TextView view, int height) {
            String text = view.getText().toString();
            if (text.isEmpty()) return;
            Paint paint = view.getPaint();
            float minSize = 1;
            float maxSize = 100;
            float bestSize = minSize;
            Paint.FontMetricsInt metrics;
            while (maxSize - minSize > 0.5f) {
                float mid = (minSize + maxSize) / 2;
                paint.setTextSize(mid);
                metrics = paint.getFontMetricsInt();
                int ascent = metrics.ascent;
                int descent = metrics.descent;
                int totalHeight = Math.abs(ascent) + Math.abs(descent);
                if (totalHeight < height) {
                    bestSize = mid;
                    minSize = mid;
                } else {
                    maxSize = mid;
                }
            }
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, bestSize);
        }
    }

    @Nullable
    public static View findViewWithTag(@NonNull ViewGroup viewGroup, Object tag) {
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

    public static int getMeasuredWidth(@NonNull View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int widthMeasureSpec;
        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        } else {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
        }
        int heightMeasureSpec;
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        } else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
        }
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredWidth();
    }

    public static int getMeasuredHeight(@NonNull View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int widthMeasureSpec;
        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        } else {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
        }
        int heightMeasureSpec;
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        } else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
        }
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();
    }

    public static void setLayoutParams(@NonNull View view, int width, int height) {
        if (view.getLayoutParams() != null) {
            view.getLayoutParams().width = width;
            view.getLayoutParams().height = height;
            view.requestLayout();
        } else {
            view.setLayoutParams(new ViewGroup.MarginLayoutParams(width, height));
        }
    }

    public static View createCustomView(int width, int height) {
        return new View(GlobalContextUtil.getContext());
    }

    @NonNull
    public static LinearLayout createBaseTopBar() {
        LinearLayout toolbar = new LinearLayout(GlobalContextUtil.getContext());
        toolbar.setLayoutParams(new ViewGroup.LayoutParams(-1, toPx(56)));
        return toolbar;
    }

    @NonNull
    public static ViewGroup createBaseContent() {
        return createLinearLayout(-1, -1, new int[]{}, Gravity.CENTER, LinearLayout.VERTICAL);
    }

    @NonNull
    public static TableRow createTableRow(int width, int height) {
        return createTableRow(width, height, new int[]{0}, Gravity.NO_GRAVITY);
    }

    @NonNull
    public static TableRow createTableRow(int width, int height, int[] margins) {
        return createTableRow(width, height, margins, Gravity.NO_GRAVITY);
    }

    @NonNull
    public static TableRow createTableRow(int width, int height, int[] margins, int gravity) {
        TableRow tableLayout = new TableRow(GlobalContextUtil.getContext());
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
        setMargins(params, margins);
        tableLayout.setLayoutParams(params);
        tableLayout.setGravity(gravity);
        return tableLayout;
    }

    @NonNull
    public static TableLayout createTableLayout(int width, int height) {
        return createTableLayout(width, height, new int[]{0}, Gravity.NO_GRAVITY);
    }

    @NonNull
    public static TableLayout createTableLayout(int width, int height, int[] margins) {
        return createTableLayout(width, height, margins, Gravity.NO_GRAVITY);
    }

    @NonNull
    public static TableLayout createTableLayout(int width, int height, int[] margins, int gravity) {
        TableLayout tableLayout = new TableLayout(GlobalContextUtil.getContext());
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
        setMargins(params, margins);
        tableLayout.setLayoutParams(params);
        tableLayout.setGravity(gravity);
        return tableLayout;
    }

    @NonNull
    public static ScrollView createScrollView(int width, int height) {
        return createScrollView(width, height, new int[]{});
    }

    @NonNull
    public static ScrollView createScrollView(int width, int height, int[] margins) {
        ScrollView scroll = new ScrollView(GlobalContextUtil.getContext());
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
        setMargins(params, margins);
        scroll.setLayoutParams(params);
        return scroll;
    }

    @NonNull
    public static RelativeLayout createRelativeLayout(int width, int height) {
        return createRelativeLayout(width, height, new int[]{}, Gravity.NO_GRAVITY);
    }

    @NonNull
    public static RelativeLayout createRelativeLayout(int width, int height, int[] margins) {
        return createRelativeLayout(width, height, margins, Gravity.NO_GRAVITY);
    }

    @NonNull
    public static RelativeLayout createRelativeLayout(int width, int height, int[] margins, int gravity) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
        setMargins(params, margins);
        RelativeLayout layout = new RelativeLayout(GlobalContextUtil.getContext());
        layout.setLayoutParams(params);
        layout.setGravity(gravity);
        return layout;
    }

    @NonNull
    public static LinearLayout createLinearLayout(int width, int height) {
        return createLinearLayout(width, height, new int[]{}, Gravity.NO_GRAVITY, LinearLayout.VERTICAL);
    }

    @NonNull
    public static LinearLayout createLinearLayout(int width, int height, int[] margins) {
        return createLinearLayout(width, height, margins, Gravity.NO_GRAVITY, LinearLayout.VERTICAL);
    }

    @NonNull
    public static LinearLayout createLinearLayout(int width, int height, int[] margins, int orientation) {
        return createLinearLayout(width, height, margins, Gravity.NO_GRAVITY, orientation);
    }

    @NonNull
    public static LinearLayout createLinearLayout(int width, int height, int[] margins, int gravity, int orientation) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
        setMargins(params, margins);
        LinearLayout layout = new LinearLayout(GlobalContextUtil.getContext());
        layout.setLayoutParams(params);
        layout.setGravity(gravity);
        layout.setOrientation(orientation);
        return layout;
    }

    @NonNull
    public static FrameLayout createFrameLayout(int width, int height) {
        return createFrameLayout(width, height, new int[]{0});
    }

    @NonNull
    public static FrameLayout createFrameLayout(int width, int height, int[] margins) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
        setMargins(params, margins);
        FrameLayout layout = new FrameLayout(GlobalContextUtil.getContext());
        layout.setLayoutParams(params);
        return layout;
    }

    @NonNull
    public static CardView createCardView(int width, int height) {
        return createCardView(width, height, new int[]{}, Gravity.NO_GRAVITY, 0, 0, Color.GRAY);
    }

    @NonNull
    public static CardView createCardView(int width, int height, int[] margins) {
        return createCardView(width, height, margins, Gravity.NO_GRAVITY, 0, 0, Color.GRAY);
    }

    @NonNull
    public static CardView createCardView(int width, int height, int[] margins, int gravity) {
        return createCardView(width, height, margins, gravity, 0, 0, Color.GRAY);
    }

    @NonNull
    public static CardView createCardView(int width, int height, int[] margins, int gravity, float radius, int elevation, int backgroundColor) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
        setMargins(params, margins);
        CardView view = new CardView(GlobalContextUtil.getContext());
        view.setLayoutParams(params);
        view.setRadius(radius);
        view.setCardElevation(elevation);
        view.setCardBackgroundColor(backgroundColor);
        view.setClipToPadding(false);
        view.setClipChildren(false);
        return view;
    }

    /*public static LinearLayout createSeekBar(int width , int height, int[] margins, int max, int min,int progress) {
     ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
     setMargins(params, margins);
     LinearLayout layout = new LinearLayout(GlobalUtilSetting.getContext());
     layout.setLayoutParams(params);
     layout.setGravity(gravity);
     layout.setOrientation(orientation);
     return layout;
     }*/

    @NonNull
    public static View createView(int width, int height, int[] margins) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
        setMargins(params, margins);
        View view = new View(GlobalContextUtil.getContext());
        view.setLayoutParams(params);
        return view;
    }

    @NonNull
    public static TextView createTextView(int width, int height) {
        return createTextView(width, height, new int[]{}, Gravity.CENTER, "", 16, Color.BLACK);
    }

    @NonNull
    public static TextView createTextView(int width, int height, String text) {
        return createTextView(width, height, new int[]{}, Gravity.CENTER, text, 16, Color.BLACK);
    }

    @NonNull
    public static TextView createTextView(int width, int height, int gravity, String text) {
        return createTextView(width, height, new int[]{}, gravity, text, 16, Color.BLACK);
    }

    @NonNull
    public static TextView createTextView(int width, int height, int gravity, String text, float textSize, int textColor) {
        return createTextView(width, height, new int[]{}, gravity, text, textSize, textColor);
    }

    @NonNull
    public static TextView createTextView(int width, int height, int[] margins, int gravity, String text, float textSize, int textColor) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
        setMargins(params, margins);
        TextView view = new TextView(GlobalContextUtil.getContext());
        view.setLayoutParams(params);
        view.setGravity(gravity);
        view.setText(text);
        view.setTextSize(textSize);
        view.setTextColor(textColor);
        return view;
    }

    @NonNull
    public static ImageView createImageView(int width, int height) {
        return createImageView(width, height, new int[]{}, null);
    }

    @NonNull
    public static ImageView createImageView(int width, int height, Drawable drawable) {
        return createImageView(width, height, new int[]{}, drawable);
    }

    @NonNull
    public static ImageView createImageView(int width, int height, int[] margins, Drawable drawable) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
        setMargins(params, margins);
        ImageView view = new ImageView(GlobalContextUtil.getContext());
        view.setLayoutParams(params);
        view.setImageDrawable(drawable);
        return view;
    }

    @NonNull
    public static ImageButton createImageButton(int width, int height) {
        return createImageButton(width, height, new int[]{}, null);
    }

    @NonNull
    public static ImageButton createImageButton(int width, int height, Drawable drawable) {
        return createImageButton(width, height, new int[]{}, drawable);
    }

    @NonNull
    public static ImageButton createImageButton(int width, int height, int[] margins, Drawable drawable) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
        setMargins(params, margins);
        ImageButton view = new ImageButton(GlobalContextUtil.getContext());
        view.setLayoutParams(params);
        view.setImageDrawable(drawable);
        return view;
    }

    private static void setMargins(ViewGroup.MarginLayoutParams params, @NonNull int[] margins) {
        int marginLeft = 0, marginTop = 0, marginRight = 0, marginBottom = 0;
        if (margins.length == 1) {
            marginLeft = margins[0];
            marginTop = margins[0];
            marginRight = margins[0];
            marginBottom = margins[0];
        }
        if (margins.length == 4) {
            marginBottom = margins[3];
        }
        if (margins.length >= 3) {
            marginRight = margins[2];
        }
        if (margins.length >= 2) {
            marginLeft = margins[0];
            marginTop = margins[1];
        }
        params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
    }

    public static class ViewSet {
        private final View view;

        public ViewSet(View view) {
            this.view = view;
        }

        public View getView() {
            return view;
        }

        public ViewSet setLayoutParams(int width, int height, float weight) {
            if (view.getLayoutParams() != null && view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                view.getLayoutParams().width = width;
                view.getLayoutParams().height = height;
                ((LinearLayout.LayoutParams) view.getLayoutParams()).weight = weight;
                view.requestLayout();
            } else {
                view.setLayoutParams(new LinearLayout.LayoutParams(width, height, weight));
            }
            return this;
        }

        public ViewSet setLayoutParams(int width, int height) {
            if (view.getLayoutParams() != null) {
                view.getLayoutParams().width = width;
                view.getLayoutParams().height = height;
                view.requestLayout();
            } else {
                view.setLayoutParams(new ViewGroup.MarginLayoutParams(width, height));
            }
            return this;
        }

        public ViewSet setMargins(int margin) {
            return setMargins(margin, margin, margin, margin);
        }

        public ViewSet setMargins(int marginLeft, int marginTop, int marginRight, int marginBottom) {
            if (marginLeft < 0 || marginTop < 0 || marginRight < 0 || marginBottom < 0) {
                return this;
            }
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    marginLayoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
                }
            } else {
                ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                marginLayoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
                view.setLayoutParams(marginLayoutParams);
            }
            view.requestLayout();
            return this;
        }

        public ViewSet setBackground(Drawable drawable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
            return this;
        }

        public ViewSet setBackgroundColor(int color) {
            return setBackground(new ColorDrawable(color));
        }
    }
}
