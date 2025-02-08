package com.someone.util;

import android.graphics.Path;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public final class PathParser {

    @NonNull
    static float[] copyOfRange(float[] original, int start, int end) {
        if (start > end || start < 0 || start > original.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int resultLength = end - start;
        float[] result = new float[resultLength];
        System.arraycopy(original, start, result, 0, Math.min(resultLength, original.length - start));
        return result;
    }

    @NonNull
    public static Path createPathFromPathData(String pathData) {
        Path path = new Path();
        PathDataNode[] nodes = createNodesFromPathData(pathData);
        try {
            nodesToPath(nodes, path);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error in parsing " + pathData, e);
        }
        return path;
    }

    @NonNull
    public static PathDataNode[] createNodesFromPathData(@NonNull String pathData) {
        ArrayList<PathDataNode> list = new ArrayList<>();
        int start = 0, end = 1;
        while (end < pathData.length()) {
            end = nextStart(pathData, end);
            String s = pathData.substring(start, end).trim();
            if (!s.isEmpty()) {
                float[] val = getFloats(s);
                addNode(list, s.charAt(0), val);
            }
            start = end++;
        }
        if ((end - start) == 1 && start < pathData.length()) {
            addNode(list, pathData.charAt(start), new float[0]);
        }
        return list.toArray(new PathDataNode[0]);
    }

    @NonNull
    public static PathDataNode[] deepCopyNodes(@NonNull PathDataNode[] source) {
        PathDataNode[] copy = new PathDataNode[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = new PathDataNode(source[i]);
        }
        return copy;
    }

    public static boolean canMorph(PathDataNode[] nodesFrom, PathDataNode[] nodesTo) {
        if (nodesFrom == null || nodesTo == null || nodesFrom.length != nodesTo.length) {
            return false;
        }
        for (int i = 0; i < nodesFrom.length; i++) {
            if (nodesFrom[i].mType != nodesTo[i].mType || nodesFrom[i].mParams.length != nodesTo[i].mParams.length) {
                return false;
            }
        }
        return true;
    }

    public static void updateNodes(PathDataNode[] target, @NonNull PathDataNode[] source) {
        for (int i = 0; i < source.length; i++) {
            target[i].mType = source[i].mType;
            System.arraycopy(source[i].mParams, 0, target[i].mParams, 0, source[i].mParams.length);
        }
    }

    private static int nextStart(@NonNull String s, int end) {
        while (end < s.length()) {
            char c = s.charAt(end);
            if (((c - 'A') * (c - 'Z') <= 0 || (c - 'a') * (c - 'z') <= 0) && c != 'e' && c != 'E') {
                return end;
            }
            end++;
        }
        return end;
    }

    private static void addNode(@NonNull ArrayList<PathDataNode> list, char cmd, float[] val) {
        list.add(new PathDataNode(cmd, val));
    }

    @NonNull
    private static float[] getFloats(@NonNull String s) {
        if (s.charAt(0) == 'z' || s.charAt(0) == 'Z') {
            return new float[0];
        }
        try {
            float[] results = new float[s.length()];
            int count = 0;
            int startPosition = 1;
            ExtractFloatResult result = new ExtractFloatResult(); // 重用 ExtractFloatResult 对象
            while (startPosition < s.length()) {
                extract(s, startPosition, result);
                if (startPosition < result.mEndPosition) {
                    results[count++] = Float.parseFloat(s.substring(startPosition, result.mEndPosition));
                }
                startPosition = result.mEndWithNegOrDot ? result.mEndPosition : result.mEndPosition + 1;
            }
            return copyOfRange(results, 0, count);
        } catch (NumberFormatException e) {
            throw new RuntimeException("error in parsing \"" + s + "\"", e);
        }
    }

    private static void extract(@NonNull String s, int start, @NonNull ExtractFloatResult result) {
        int currentIndex = start;
        boolean foundSeparator = false;
        result.mEndWithNegOrDot = false;
        boolean secondDot = false;
        boolean isExponential = false;
        for (; currentIndex < s.length(); currentIndex++) {
            char currentChar = s.charAt(currentIndex);
            switch (currentChar) {
                case ' ':
                case ',':
                    foundSeparator = true;
                    break;
                case '-':
                    if (currentIndex != start && !isExponential) {
                        foundSeparator = true;
                        result.mEndWithNegOrDot = true;
                    }
                    break;
                case '.':
                    if (!secondDot) {
                        secondDot = true;
                    } else {
                        foundSeparator = true;
                        result.mEndWithNegOrDot = true;
                    }
                    break;
                case 'e':
                case 'E':
                    isExponential = true;
                    break;
            }
            if (foundSeparator) {
                break;
            }
        }
        result.mEndPosition = currentIndex;
    }

    public static void interpolatePathDataNodes(PathDataNode[] target, float fraction, PathDataNode[] from, PathDataNode[] to) {
        if (!canMorph(from, to)) {
            throw new IllegalArgumentException("Can't interpolate between two incompatible pathData");
        }
        for (int i = 0; i < target.length; i++) {
            target[i].interpolatePathDataNode(from[i], to[i], fraction);
        }
    }

    public static void nodesToPath(@NonNull PathDataNode[] nodes, Path path) {
        float[] current = new float[6];
        char previousCommand = 'm';
        for (PathDataNode node : nodes) {
            node.addCommand(path, current, previousCommand, node.mType, node.mParams);
            previousCommand = node.mType;
        }
    }

    public static class PathDataNode {
        private final float[] mParams;
        private char mType;

        PathDataNode(char type, float[] params) {
            this.mType = type;
            this.mParams = params;
        }

        PathDataNode(@NonNull PathDataNode n) {
            mType = n.mType;
            mParams = copyOfRange(n.mParams, 0, n.mParams.length);
        }

        private static void drawArc(Path p, float x0, float y0, float x1, float y1, float a, float b, float theta, boolean isMoreThanHalf, boolean isPositiveArc) {
            double thetaD = Math.toRadians(theta);
            double cosTheta = Math.cos(thetaD);
            double sinTheta = Math.sin(thetaD);
            double x0p = (x0 * cosTheta + y0 * sinTheta) / a;
            double y0p = (-x0 * sinTheta + y0 * cosTheta) / b;
            double x1p = (x1 * cosTheta + y1 * sinTheta) / a;
            double y1p = (-x1 * sinTheta + y1 * cosTheta) / b;
            double dx = x0p - x1p;
            double dy = y0p - y1p;
            double xm = (x0p + x1p) / 2;
            double ym = (y0p + y1p) / 2;
            double dsq = dx * dx + dy * dy;
            if (dsq == 0.0) {
                return;
            }
            double disc = 1.0 / dsq - 1.0 / 4.0;
            if (disc < 0.0) {
                float adjust = (float) (Math.sqrt(dsq) / 1.99999);
                drawArc(p, x0, y0, x1, y1, a * adjust, b * adjust, theta, isMoreThanHalf, isPositiveArc);
                return;
            }
            double s = Math.sqrt(disc);
            double sdx = s * dx;
            double sdy = s * dy;
            double cx;
            double cy;
            if (isMoreThanHalf == isPositiveArc) {
                cx = xm - sdy;
                cy = ym + sdx;
            } else {
                cx = xm + sdy;
                cy = ym - sdx;
            }
            double eta0 = Math.atan2((y0p - cy), (x0p - cx));
            double eta1 = Math.atan2((y1p - cy), (x1p - cx));
            double sweep = (eta1 - eta0);
            if (isPositiveArc != (sweep >= 0)) {
                if (sweep > 0) {
                    sweep -= 2 * Math.PI;
                } else {
                    sweep += 2 * Math.PI;
                }
            }
            cx *= a;
            cy *= b;
            double tcx = cx;
            cx = cx * cosTheta - cy * sinTheta;
            cy = tcx * sinTheta + cy * cosTheta;
            arcToBezier(p, cx, cy, a, b, x0, y0, thetaD, eta0, sweep);
        }

        private static void arcToBezier(Path p, double cx, double cy, double a, double b, double e1x, double e1y, double theta, double start, double sweep) {
            int numSegments = (int) Math.ceil(Math.abs(sweep * 4 / Math.PI));
            double eta1 = start;
            double cosTheta = Math.cos(theta);
            double sinTheta = Math.sin(theta);
            double cosEta1 = Math.cos(eta1);
            double sinEta1 = Math.sin(eta1);
            double ep1x = (-a * cosTheta * sinEta1) - (b * sinTheta * cosEta1);
            double ep1y = (-a * sinTheta * sinEta1) + (b * cosTheta * cosEta1);
            double anglePerSegment = sweep / numSegments;
            for (int i = 0; i < numSegments; i++) {
                double eta2 = eta1 + anglePerSegment;
                double sinEta2 = Math.sin(eta2);
                double cosEta2 = Math.cos(eta2);
                double e2x = cx + (a * cosTheta * cosEta2) - (b * sinTheta * sinEta2);
                double e2y = cy + (a * sinTheta * cosEta2) + (b * cosTheta * sinEta2);
                double ep2x = -a * cosTheta * sinEta2 - b * sinTheta * cosEta2;
                double ep2y = -a * sinTheta * sinEta2 + b * cosTheta * cosEta2;
                double tanDiff2 = Math.tan((eta2 - eta1) / 2);
                double alpha = Math.sin(eta2 - eta1) * (Math.sqrt(4 + (3 * tanDiff2 * tanDiff2)) - 1) / 3;
                double q1x = e1x + alpha * ep1x;
                double q1y = e1y + alpha * ep1y;
                double q2x = e2x - alpha * ep2x;
                double q2y = e2y - alpha * ep2y;
                p.cubicTo((float) q1x, (float) q1y, (float) q2x, (float) q2y, (float) e2x, (float) e2y);
                eta1 = eta2;
                e1x = e2x;
                e1y = e2y;
                ep1x = ep2x;
                ep1y = ep2y;
            }
        }

        public char getType() {
            return mType;
        }

        public float[] getParams() {
            return mParams;
        }

        public void interpolatePathDataNode(@NonNull PathDataNode nodeFrom, PathDataNode nodeTo, float fraction) {
            mType = nodeFrom.mType;
            for (int i = 0; i < nodeFrom.mParams.length; i++) {
                mParams[i] = nodeFrom.mParams[i] * (1 - fraction) + nodeTo.mParams[i] * fraction;
            }
        }

        private void addCommand(Path path, @NonNull float[] current, char previousCmd, char cmd, float[] val) {
            int incr = 2;
            float currentX = current[0], currentY = current[1], ctrlPointX = current[2], ctrlPointY = current[3];
            float currentSegmentStartX = current[4], currentSegmentStartY = current[5];
            float reflectiveCtrlPointX, reflectiveCtrlPointY;

            switch (cmd) {
                case 'z':
                case 'Z':
                    path.close();
                    currentX = currentSegmentStartX;
                    currentY = currentSegmentStartY;
                    ctrlPointX = currentSegmentStartX;
                    ctrlPointY = currentSegmentStartY;
                    path.moveTo(currentX, currentY);
                    break;
                case 'm':
                case 'M':
                case 'l':
                case 'L':
                case 't':
                case 'T':
                    break;
                case 'h':
                case 'H':
                case 'v':
                case 'V':
                    incr = 1;
                    break;
                case 'c':
                case 'C':
                    incr = 6;
                    break;
                case 's':
                case 'S':
                case 'q':
                case 'Q':
                    incr = 4;
                    break;
                case 'a':
                case 'A':
                    incr = 7;
                    break;
            }

            for (int k = 0; k < val.length; k += incr) {
                switch (cmd) {
                    case 'm':
                        currentX += val[k];
                        currentY += val[k + 1];
                        if (k > 0) {
                            path.rLineTo(val[k], val[k + 1]);
                        } else {
                            path.rMoveTo(val[k], val[k + 1]);
                            currentSegmentStartX = currentX;
                            currentSegmentStartY = currentY;
                        }
                        break;
                    case 'M':
                        currentX = val[k];
                        currentY = val[k + 1];
                        if (k > 0) {
                            path.lineTo(val[k], val[k + 1]);
                        } else {
                            path.moveTo(val[k], val[k + 1]);
                            currentSegmentStartX = currentX;
                            currentSegmentStartY = currentY;
                        }
                        break;
                    case 'l':
                        path.rLineTo(val[k], val[k + 1]);
                        currentX += val[k];
                        currentY += val[k + 1];
                        break;
                    case 'L':
                        path.lineTo(val[k], val[k + 1]);
                        currentX = val[k];
                        currentY = val[k + 1];
                        break;
                    case 'h':
                        path.rLineTo(val[k], 0);
                        currentX += val[k];
                        break;
                    case 'H':
                        path.lineTo(val[k], currentY);
                        currentX = val[k];
                        break;
                    case 'v':
                        path.rLineTo(0, val[k]);
                        currentY += val[k];
                        break;
                    case 'V':
                        path.lineTo(currentX, val[k]);
                        currentY = val[k];
                        break;
                    case 'c':
                        path.rCubicTo(val[k], val[k + 1], val[k + 2], val[k + 3], val[k + 4], val[k + 5]);
                        ctrlPointX = currentX + val[k + 2];
                        ctrlPointY = currentY + val[k + 3];
                        currentX += val[k + 4];
                        currentY += val[k + 5];
                        break;
                    case 'C':
                        path.cubicTo(val[k], val[k + 1], val[k + 2], val[k + 3], val[k + 4], val[k + 5]);
                        currentX = val[k + 4];
                        currentY = val[k + 5];
                        ctrlPointX = val[k + 2];
                        ctrlPointY = val[k + 3];
                        break;
                    case 's':
                        reflectiveCtrlPointX = 0;
                        reflectiveCtrlPointY = 0;
                        if (previousCmd == 'c' || previousCmd == 's' || previousCmd == 'C' || previousCmd == 'S') {
                            reflectiveCtrlPointX = currentX - ctrlPointX;
                            reflectiveCtrlPointY = currentY - ctrlPointY;
                        }
                        path.rCubicTo(reflectiveCtrlPointX, reflectiveCtrlPointY, val[k], val[k + 1], val[k + 2], val[k + 3]);
                        ctrlPointX = currentX + val[k];
                        ctrlPointY = currentY + val[k + 1];
                        currentX += val[k + 2];
                        currentY += val[k + 3];
                        break;
                    case 'S':
                        reflectiveCtrlPointX = currentX;
                        reflectiveCtrlPointY = currentY;
                        if (previousCmd == 'c' || previousCmd == 's' || previousCmd == 'C' || previousCmd == 'S') {
                            reflectiveCtrlPointX = 2 * currentX - ctrlPointX;
                            reflectiveCtrlPointY = 2 * currentY - ctrlPointY;
                        }
                        path.cubicTo(reflectiveCtrlPointX, reflectiveCtrlPointY, val[k], val[k + 1], val[k + 2], val[k + 3]);
                        ctrlPointX = val[k];
                        ctrlPointY = val[k + 1];
                        currentX = val[k + 2];
                        currentY = val[k + 3];
                        break;
                    case 'q':
                        path.rQuadTo(val[k], val[k + 1], val[k + 2], val[k + 3]);
                        ctrlPointX = currentX + val[k];
                        ctrlPointY = currentY + val[k + 1];
                        currentX += val[k + 2];
                        currentY += val[k + 3];
                        break;
                    case 'Q':
                        path.quadTo(val[k], val[k + 1], val[k + 2], val[k + 3]);
                        ctrlPointX = val[k];
                        ctrlPointY = val[k + 1];
                        currentX = val[k + 2];
                        currentY = val[k + 3];
                        break;
                    case 't':
                        reflectiveCtrlPointX = 0;
                        reflectiveCtrlPointY = 0;
                        if (previousCmd == 'q' || previousCmd == 't' || previousCmd == 'Q' || previousCmd == 'T') {
                            reflectiveCtrlPointX = currentX - ctrlPointX;
                            reflectiveCtrlPointY = currentY - ctrlPointY;
                        }
                        path.rQuadTo(reflectiveCtrlPointX, reflectiveCtrlPointY, val[k], val[k + 1]);
                        ctrlPointX = currentX + reflectiveCtrlPointX;
                        ctrlPointY = currentY + reflectiveCtrlPointY;
                        currentX += val[k];
                        currentY += val[k + 1];
                        break;
                    case 'T':
                        reflectiveCtrlPointX = currentX;
                        reflectiveCtrlPointY = currentY;
                        if (previousCmd == 'q' || previousCmd == 't' || previousCmd == 'Q' || previousCmd == 'T') {
                            reflectiveCtrlPointX = 2 * currentX - ctrlPointX;
                            reflectiveCtrlPointY = 2 * currentY - ctrlPointY;
                        }
                        path.quadTo(reflectiveCtrlPointX, reflectiveCtrlPointY, val[k], val[k + 1]);
                        ctrlPointX = reflectiveCtrlPointX;
                        ctrlPointY = reflectiveCtrlPointY;
                        currentX = val[k];
                        currentY = val[k + 1];
                        break;
                    case 'a':
                        drawArc(path, currentX, currentY, val[k + 5] + currentX, val[k + 6] + currentY, val[k], val[k + 1], val[k + 2], val[k + 3] != 0, val[k + 4] != 0);
                        currentX += val[k + 5];
                        currentY += val[k + 6];
                        ctrlPointX = currentX;
                        ctrlPointY = currentY;
                        break;
                    case 'A':
                        drawArc(path, currentX, currentY, val[k + 5], val[k + 6], val[k], val[k + 1], val[k + 2], val[k + 3] != 0, val[k + 4] != 0);
                        currentX = val[k + 5];
                        currentY = val[k + 6];
                        ctrlPointX = currentX;
                        ctrlPointY = currentY;
                        break;
                }
                previousCmd = cmd;
            }
            current[0] = currentX;
            current[1] = currentY;
            current[2] = ctrlPointX;
            current[3] = ctrlPointY;
            current[4] = currentSegmentStartX;
            current[5] = currentSegmentStartY;
        }
    }

    private static class ExtractFloatResult {
        int mEndPosition;
        boolean mEndWithNegOrDot;
    }
}