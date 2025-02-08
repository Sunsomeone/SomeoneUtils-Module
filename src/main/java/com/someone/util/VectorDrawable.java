package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/09/11 07:17
 */

import static com.someone.util.PathParser.PathDataNode;
import static com.someone.util.PathParser.createNodesFromPathData;
import static com.someone.util.PathParser.nodesToPath;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class VectorDrawable extends Drawable {
    private final String[] pathDatas;
    private final int[] fillColors;
    private final float[] fillAlphas;
    private final Path.FillType[] fillTypes;
    private final int[] strokeColors;
    private final float[] strokeWidths;
    private final float[] strokeAlphas;
    private final Paint.Style[] styles;
    private final int width;
    private final int height;
    private final boolean keepAspectRatio;

    private Path[] originalPaths;
    private Path[] scaledPaths;
    private Paint[] paint;
    private Paint[] strokePaint;
    private float translateX;
    private float translateY;

    private VectorDrawable(@NonNull Builder builder) {
        this.pathDatas = builder.pathDatas;
        this.fillColors = builder.fillColors;
        this.fillAlphas = builder.fillAlphas;
        this.fillTypes = builder.fillTypes;
        this.strokeColors = builder.strokeColors;
        this.strokeWidths = builder.strokeWidths;
        this.strokeAlphas = builder.strokeAlphas;
        this.styles = builder.styles;
        this.width = builder.width;
        this.height = builder.height;
        this.keepAspectRatio = builder.keepAspectRatio;
        init();
    }

    private void init() {
        validateInputs();
        parseOriginalPaths();
        initPaints();
    }

    private void validateInputs() {
        if (pathDatas == null || pathDatas.length == 0) {
            throw new IllegalArgumentException("Paths must not be null or empty");
        }
        if (fillColors != null && fillColors.length != 1 && fillColors.length != pathDatas.length) {
            throw new IllegalArgumentException("Fill colors length mismatch");
        }
        if (styles != null && styles.length != 1 && styles.length != pathDatas.length) {
            throw new IllegalArgumentException("Styles length mismatch");
        }
    }

    private void parseOriginalPaths() {
        originalPaths = new Path[pathDatas.length];
        for (int i = 0; i < pathDatas.length; i++) {
            originalPaths[i] = new Path();
            if (fillTypes != null && fillTypes.length > 0) {
                Path.FillType safeArrayElement = getSafeArrayElement(fillTypes, i, Path.FillType.WINDING);
                if (safeArrayElement != null) {
                    originalPaths[i].setFillType(safeArrayElement);
                }
            }
            PathDataNode[] nodes = createNodesFromPathData(pathDatas[i]);
            nodesToPath(nodes, originalPaths[i]);
        }
    }

    private void initPaints() {
        paint = new Paint[pathDatas.length];
        boolean initStroke = strokeColors != null && strokeWidths != null
                && strokeColors.length > 0 && strokeWidths.length > 0;

        if (initStroke) {
            strokePaint = new Paint[pathDatas.length];
        }

        for (int i = 0; i < pathDatas.length; i++) {
            initFillPaint(i);
            if (initStroke) {
                initStrokePaint(i);
            }
        }
    }

    private void initFillPaint(int index) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(getSafeArrayElement(fillColors, index, Color.TRANSPARENT));
        p.setStyle(getSafeArrayElement(styles, index, Paint.Style.FILL));
        p.setAlpha((int) (getSafeArrayElement(fillAlphas, index, 1.0f) * 255)); // 应用 fillAlphas
        paint[index] = p;
    }

    private void initStrokePaint(int index) {
        Paint sp = new Paint(Paint.ANTI_ALIAS_FLAG);
        sp.setStyle(Paint.Style.STROKE);
        sp.setColor(getSafeArrayElement(strokeColors, index, Color.TRANSPARENT));
        sp.setStrokeWidth(getSafeArrayElement(strokeWidths, index, 0f));
        sp.setAlpha((int) (getSafeArrayElement(strokeAlphas, index, 1.0f) * 255)); // 应用 strokeAlphas
        strokePaint[index] = sp;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        updateScaledPaths();
    }

    private void updateScaledPaths() {
        int viewWidth = getBounds().width();
        int viewHeight = getBounds().height();
        if (viewWidth <= 0 || viewHeight <= 0) return;
        RectF originalBounds = calculateOriginalBounds();
        Matrix transformMatrix = createTransformMatrix(viewWidth, viewHeight, originalBounds);
        applyTransformToPaths(transformMatrix);
        calculateTranslation(viewWidth, viewHeight);
    }

    @NonNull
    private RectF calculateOriginalBounds() {
        RectF bounds = new RectF();
        for (Path path : originalPaths) {
            RectF pathBounds = new RectF();
            path.computeBounds(pathBounds, true);
            bounds.union(pathBounds);
        }
        // 增加对描边宽度的考虑
        if (strokePaint != null) {
            for (Paint sp : strokePaint) {
                bounds.inset(-sp.getStrokeWidth() / 2, -sp.getStrokeWidth() / 2);
            }
        }
        return bounds;
    }

    @NonNull
    private Matrix createTransformMatrix(int viewWidth, int viewHeight, @NonNull RectF originalBounds) {
        float targetWidth = resolveTargetDimension(width, viewWidth, originalBounds.width());
        float targetHeight = resolveTargetDimension(height, viewHeight, originalBounds.height());
        float scaleX = targetWidth / originalBounds.width();
        float scaleY = targetHeight / originalBounds.height();
        float scale = keepAspectRatio ? Math.min(scaleX, scaleY) : 1f;
        Matrix matrix = new Matrix();
        matrix.setScale(
                keepAspectRatio ? scale : scaleX,
                keepAspectRatio ? scale : scaleY
        );
        return matrix;
    }

    private float resolveTargetDimension(int sizeSpec, int viewSize, float originalSize) {
        if (sizeSpec == -2) return originalSize;
        if (sizeSpec > 0) return sizeSpec;
        return viewSize;
    }

    private void applyTransformToPaths(Matrix matrix) {
        scaledPaths = new Path[originalPaths.length];
        for (int i = 0; i < originalPaths.length; i++) {
            scaledPaths[i] = new Path(originalPaths[i]);
            scaledPaths[i].transform(matrix);
        }
    }

    private void calculateTranslation(int viewWidth, int viewHeight) {
        RectF scaledBounds = new RectF();
        for (Path path : scaledPaths) {
            RectF pathBounds = new RectF();
            path.computeBounds(pathBounds, true);
            scaledBounds.union(pathBounds);
        }
        translateX = (viewWidth - scaledBounds.width()) / 2 - scaledBounds.left;
        translateY = (viewHeight - scaledBounds.height()) / 2 - scaledBounds.top;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (scaledPaths == null) return;
        canvas.save();
        canvas.translate(translateX, translateY);
        for (int i = 0; i < scaledPaths.length; i++) {
            canvas.drawPath(scaledPaths[i], paint[i]);
            if (strokePaint != null && strokePaint[i] != null) {
                canvas.drawPath(scaledPaths[i], strokePaint[i]);
            }
        }
        canvas.restore();
    }


    private int getSafeArrayElement(int[] array, int index, int defaultValue) {
        if (array == null || array.length == 0) return defaultValue;
        return index < array.length ? array[index] : array[0];
    }

    private float getSafeArrayElement(float[] array, int index, float defaultValue) {
        if (array == null || array.length == 0) return defaultValue;
        return index < array.length ? array[index] : array[0];
    }

    private Paint.Style getSafeArrayElement(Paint.Style[] array, int index, Paint.Style defaultValue) {
        if (array == null || array.length == 0) return defaultValue;
        return index < array.length ? array[index] : array[0];
    }

    private Path.FillType getSafeArrayElement(Path.FillType[] array, int index, Path.FillType defaultValue) {
        if (array == null || array.length == 0) return defaultValue;
        return index < array.length ? array[index] : array[0];
    }

    public static class Builder {
        private String[] pathDatas;
        private int[] fillColors;
        private float[] fillAlphas;
        private Path.FillType[] fillTypes;
        private int[] strokeColors;
        private float[] strokeWidths;
        private float[] strokeAlphas;
        private Paint.Style[] styles;
        private int width = -2;
        private int height = -2;
        private boolean keepAspectRatio = false;

        public Builder() {
        }

        public Builder(@NonNull VectorPath.Path paths) {
            this.fillColors = paths.getFillColors();
            this.pathDatas = paths.getPathDatas();
            this.fillTypes = paths.getFillTypes();
            this.fillAlphas = paths.getFillAlphas();
            this.strokeAlphas = paths.getStrokeAlphas();
        }

        public Builder setPathDatas(String... paths) {
            this.pathDatas = paths;
            return this;
        }

        public Builder setFillColors(int... fillColors) {
            this.fillColors = fillColors;
            return this;
        }

        public Builder setFillAlphas(float... fillAlphas) {
            this.fillAlphas = fillAlphas;
            return this;
        }

        public Builder setFillTypes(Path.FillType... fillTypes) {
            this.fillTypes = fillTypes;
            return this;
        }

        public Builder setStrokeColors(int... strokeColors) {
            this.strokeColors = strokeColors;
            return this;
        }

        public Builder setStrokeWidths(float... strokeWidths) {
            this.strokeWidths = strokeWidths;
            return this;
        }

        public Builder setStrokeAlphas(float... strokeAlphas) {
            this.strokeAlphas = strokeAlphas;
            return this;
        }

        public Builder setStyles(Paint.Style... styles) {
            this.styles = styles;
            return this;
        }

        public Builder setSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder setKeepAspectRatio(boolean keepAspectRatio) {
            this.keepAspectRatio = keepAspectRatio;
            return this;
        }

        public VectorDrawable build() {
            return new VectorDrawable(this);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        for (Paint value : paint) {
            value.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        for (Paint value : paint) {
            value.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
