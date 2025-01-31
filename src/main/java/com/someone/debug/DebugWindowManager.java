package com.someone.debug;

/*
 * @Author Someone
 * @Date 2024/09/04 20:29
 */

import static com.someone.util.WindowManagerUtil.LayoutParams;
import static com.someone.util.WindowManagerUtil.addView;
import static com.someone.util.WindowManagerUtil.createLayoutParams;
import static com.someone.util.WindowManagerUtil.getWindowManager;
import static com.someone.util.WindowManagerUtil.hideView;
import static com.someone.util.WindowManagerUtil.removeView;
import static com.someone.util.WindowManagerUtil.showView;
import static com.someone.util.WindowUtil.getScreenHeight;
import static com.someone.util.WindowUtil.getScreenWidth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.Selection;
import android.text.Spannable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.someone.util.ClipBoardUtil;
import com.someone.util.GlobalUtilSetting;

public class DebugWindowManager {
    @SuppressLint("StaticFieldLeak")
    private static final Context context = GlobalUtilSetting.getContext();
    @SuppressLint("StaticFieldLeak")
    private static final TextView logText = new TextView(context);
    @SuppressLint("StaticFieldLeak")
    private static final ScrollView displayScroll = new ScrollView(context);
    private static LayoutParams params;
    @SuppressLint("StaticFieldLeak")
    private static View floatButton;
    @SuppressLint("StaticFieldLeak")
    private static View floatWindow;
    @SuppressLint("StaticFieldLeak")
    private static ViewGroup debugWindow;

    static {
        initDebugWindow();
    }

    @SuppressLint("RtlHardcoded")
    public static void initDebugWindow() {
        debugWindow = new LinearLayout(context);
        floatButton = createDebugButton();
        floatWindow = createDebugWindow();
        debugWindow.addView(floatButton);
        params = createLayoutParams().setParamsType(LayoutParams.TYPE_APPLICATION).setParamsSize(-2, -2).setParamsFormat(PixelFormat.TRANSLUCENT).setParamsFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL).setParamsGravity(Gravity.TOP | Gravity.LEFT).setParamsLocation(getScreenWidth() / 2 - debugWindow.getChildAt(0).getLayoutParams().width / 2, getScreenHeight() / 2 - debugWindow.getChildAt(0).getLayoutParams().height / 2).getLayoutParams();
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        debugWindow.setLayoutParams(params);
    }

    public static void addDebugWindow() {
        if (params != null) {
            addView("debugWindow", debugWindow, params);
        }
    }

    public static void removeDebugWindow() {
        removeView("debugWindow");
    }

    public static void showDebugWindow() {
        showView("debugWindow");
    }

    public static void hideDebugWindow() {
        hideView("debugWindow");
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    public static View createDebugButton() {
        final FrameLayout rootLayout = getFrameLayout();
        ImageView floatBtnImg = new ImageView(context);
        floatBtnImg.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
        ShapeDrawable circleDrawable = new ShapeDrawable(new OvalShape());
        circleDrawable.getPaint().setColor(Color.parseColor("#CC4F4F4F"));
        floatBtnImg.setBackground(circleDrawable);
        TextView floatBtnText = new TextView(context);
        floatBtnText.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
        floatBtnText.setGravity(Gravity.CENTER);
        floatBtnText.setText("Debug");
        floatBtnText.setTextSize(11);
        floatBtnText.setTextColor(Color.WHITE);
        rootLayout.addView(floatBtnImg);
        rootLayout.addView(floatBtnText);
        return rootLayout;
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    private static FrameLayout getFrameLayout() {
        final FrameLayout rootLayout = new FrameLayout(context);
        rootLayout.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
        rootLayout.setOnTouchListener(new onMoveAndClickListener() {
            @Override
            public void setonClickEvent() {
                removeDebugWindow();
                debugWindow.removeAllViews();
                params.x -= floatWindow.getLayoutParams().width / 2 - floatButton.getLayoutParams().width / 2;
                debugWindow.addView(floatWindow);
                addDebugWindow();
            }
        });
        return rootLayout;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "RtlHardcoded"})
    public static View createDebugWindow() {
        final LinearLayout rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setLayoutParams(new ViewGroup.LayoutParams(800, 800));
        {
            GradientDrawable squareDrawable = new GradientDrawable();
            squareDrawable.setCornerRadius(35);
            squareDrawable.setColor(Color.parseColor("#D0505050"));
            squareDrawable.setShape(GradientDrawable.RECTANGLE);
            rootLayout.setBackground(squareDrawable);
        }
        RelativeLayout topBar = new RelativeLayout(context);
        topBar.setLayoutParams(new ViewGroup.LayoutParams(-1, 125));
        {
            GradientDrawable squareDrawable = new GradientDrawable();
            float[] radii = new float[]{35f, 35f, 35f, 35f, 0f, 0f, 0f, 0f};
            squareDrawable.setCornerRadii(radii);
            squareDrawable.setColor(Color.parseColor("#CC4F4F4F"));
            squareDrawable.setShape(GradientDrawable.RECTANGLE);
            topBar.setBackground(squareDrawable);
        }
        topBar.setOnTouchListener(new onMoveAndClickListener() {
            @Override
            public void setonClickEvent() {
                removeDebugWindow();
                debugWindow.removeAllViews();
                params.x += floatWindow.getLayoutParams().width / 2 - floatButton.getLayoutParams().width / 2;
                debugWindow.addView(floatButton);
                addDebugWindow();
            }
        });
        RelativeLayout.LayoutParams titleLayoutParams = new RelativeLayout.LayoutParams(-2, -1);
        titleLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        TextView titleText = new TextView(context);
        titleText.setLayoutParams(titleLayoutParams);
        titleText.setGravity(Gravity.CENTER);
        titleText.setTag("WindowTitle");
        titleText.setText("LogCatch");
        titleText.setTextColor(Color.WHITE);
        titleText.setTextSize(18);
        topBar.addView(titleText);
        displayScroll.setLayoutParams(new ViewGroup.LayoutParams(-1, 550));
        displayScroll.setTag("LogScroll");
        displayScroll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                displayScroll.post(new Runnable() {
                    @Override
                    public void run() {
                        displayScroll.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
        LinearLayout displayLayout = new LinearLayout(context);
        displayLayout.setOrientation(LinearLayout.VERTICAL);
        displayLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        displayLayout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        logText.setLayoutParams(new ViewGroup.LayoutParams(800, -2));
        logText.setGravity(Gravity.LEFT | Gravity.TOP);
        logText.setTag("LogInfo");
        logText.setText("");
        logText.setTextColor(Color.WHITE);
        logText.setTextSize(13);
        logText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Selection.setSelection((Spannable) ((TextView) v).getText(), 0, ((TextView) v).getText().length());
                ClipBoardUtil.clip(((TextView) v).getText().toString());
                return true;
            }
        });
        displayLayout.addView(logText);
        displayScroll.addView(displayLayout);
        RelativeLayout bottomBar = new RelativeLayout(context);
        bottomBar.setLayoutParams(new ViewGroup.LayoutParams(800, 125));
        {
            GradientDrawable squareDrawable = new GradientDrawable();
            float[] radii = new float[]{0f, 0f, 0f, 0f, 35f, 35f, 35f, 35f};
            squareDrawable.setCornerRadii(radii);
            squareDrawable.setColor(Color.parseColor("#CC4F4F4F"));
            squareDrawable.setShape(GradientDrawable.RECTANGLE);
            bottomBar.setBackground(squareDrawable);
        }
        rootLayout.addView(topBar);
        rootLayout.addView(displayScroll);
        rootLayout.addView(bottomBar);
        return rootLayout;
    }

    public static TextView getLogText() {
        return logText;
    }

    public static ScrollView getDisplayScroll() {
        return displayScroll;
    }

    public static View getDebugFloatButton() {
        return floatButton;
    }

    public static View getDebugFloatWindow() {
        return floatWindow;
    }

    public static abstract class onMoveAndClickListener implements View.OnTouchListener {
        private long startTime;
        private boolean isDown;
        private boolean isMove;
        private int LastX;
        private int LastY;
        private int StartX;
        private int StartY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (!isDown) {
                        startTime = System.currentTimeMillis();
                        isDown = true;
                    }
                    isMove = false;
                    LastX = (int) event.getRawX();
                    LastY = (int) event.getRawY();
                    StartX = (int) event.getX();
                    StartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int MoveX = (int) event.getX();
                    int MoveY = (int) event.getY();
                    float dx = MoveX - StartX;
                    float dy = MoveY - StartY;
                    isMove = !(Math.sqrt(dx + dy) < 2);
                    if (isMove) {
                        // 更新悬浮窗位置
                        params.x = params.x + (int) (event.getRawX() - LastX);
                        params.y = params.y + (int) (event.getRawY() - LastY);
                        // 限制悬浮窗位置
                        params.x = Math.max(0, Math.min(params.x, getScreenWidth() - debugWindow.getChildAt(0).getLayoutParams().width));
                        params.y = Math.max(0, Math.min(params.y, getScreenHeight() - debugWindow.getChildAt(0).getLayoutParams().height));
                        if (debugWindow.isAttachedToWindow()) {
                            getWindowManager().updateViewLayout(debugWindow, params);
                        }
                        LastX = (int) event.getRawX();
                        LastY = (int) event.getRawY();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isDown = false;
                    if (!isMove) {
                        long endTime = System.currentTimeMillis();
                        if (endTime - startTime < 500) {
                            setonClickEvent();
                        }
                    }
                    break;
            }
            return true;
        }

        public abstract void setonClickEvent();
    }
}
    

