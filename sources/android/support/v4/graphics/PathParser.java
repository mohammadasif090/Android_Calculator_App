package android.support.v4.graphics;

import android.graphics.Path;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.util.ArrayList;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class PathParser {
    private static final String LOGTAG = "PathParser";

    static float[] copyOfRange(float[] original, int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException();
        }
        int originalLength = original.length;
        if (start < 0 || start > originalLength) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int resultLength = end - start;
        int copyLength = Math.min(resultLength, originalLength - start);
        float[] result = new float[resultLength];
        System.arraycopy(original, start, result, 0, copyLength);
        return result;
    }

    public static Path createPathFromPathData(String pathData) {
        Path path = new Path();
        PathDataNode[] nodes = createNodesFromPathData(pathData);
        if (nodes != null) {
            try {
                PathDataNode.nodesToPath(nodes, path);
                return path;
            } catch (RuntimeException e) {
                throw new RuntimeException("Error in parsing " + pathData, e);
            }
        }
        return null;
    }

    public static PathDataNode[] createNodesFromPathData(String pathData) {
        if (pathData == null) {
            return null;
        }
        int start = 0;
        int end = 1;
        ArrayList<PathDataNode> list = new ArrayList<>();
        while (end < pathData.length()) {
            int end2 = nextStart(pathData, end);
            String s = pathData.substring(start, end2).trim();
            if (s.length() > 0) {
                float[] val = getFloats(s);
                addNode(list, s.charAt(0), val);
            }
            start = end2;
            end = end2 + 1;
        }
        if (end - start == 1 && start < pathData.length()) {
            addNode(list, pathData.charAt(start), new float[0]);
        }
        return (PathDataNode[]) list.toArray(new PathDataNode[list.size()]);
    }

    public static PathDataNode[] deepCopyNodes(PathDataNode[] source) {
        if (source == null) {
            return null;
        }
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

    public static void updateNodes(PathDataNode[] target, PathDataNode[] source) {
        for (int i = 0; i < source.length; i++) {
            target[i].mType = source[i].mType;
            for (int j = 0; j < source[i].mParams.length; j++) {
                target[i].mParams[j] = source[i].mParams[j];
            }
        }
    }

    private static int nextStart(String s, int end) {
        while (end < s.length()) {
            char c = s.charAt(end);
            if (((c - 'A') * (c - 'Z') <= 0 || (c - 'a') * (c - 'z') <= 0) && c != 'e' && c != 'E') {
                return end;
            }
            end++;
        }
        return end;
    }

    private static void addNode(ArrayList<PathDataNode> list, char cmd, float[] val) {
        list.add(new PathDataNode(cmd, val));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ExtractFloatResult {
        int mEndPosition;
        boolean mEndWithNegOrDot;

        ExtractFloatResult() {
        }
    }

    private static float[] getFloats(String s) {
        if (s.charAt(0) == 'z' || s.charAt(0) == 'Z') {
            return new float[0];
        }
        try {
            float[] results = new float[s.length()];
            int count = 0;
            int startPosition = 1;
            ExtractFloatResult result = new ExtractFloatResult();
            int totalLength = s.length();
            while (startPosition < totalLength) {
                extract(s, startPosition, result);
                int endPosition = result.mEndPosition;
                if (startPosition < endPosition) {
                    results[count] = Float.parseFloat(s.substring(startPosition, endPosition));
                    count++;
                }
                if (result.mEndWithNegOrDot) {
                    startPosition = endPosition;
                } else {
                    startPosition = endPosition + 1;
                }
            }
            return copyOfRange(results, 0, count);
        } catch (NumberFormatException e) {
            throw new RuntimeException("error in parsing \"" + s + "\"", e);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x003b A[LOOP:0: B:3:0x0007->B:24:0x003b, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x003e A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static void extract(java.lang.String r7, int r8, android.support.v4.graphics.PathParser.ExtractFloatResult r9) {
        /*
            r0 = r8
            r1 = 0
            r2 = 0
            r9.mEndWithNegOrDot = r2
            r3 = 0
        L7:
            int r4 = r7.length()
            if (r0 >= r4) goto L3e
            r4 = r2
            r2 = 0
            char r5 = r7.charAt(r0)
            r6 = 32
            if (r5 == r6) goto L36
            r6 = 69
            if (r5 == r6) goto L34
            r6 = 101(0x65, float:1.42E-43)
            if (r5 == r6) goto L34
            r6 = 1
            switch(r5) {
                case 44: goto L36;
                case 45: goto L2c;
                case 46: goto L24;
                default: goto L23;
            }
        L23:
            goto L38
        L24:
            if (r3 != 0) goto L28
            r3 = 1
            goto L38
        L28:
            r1 = 1
            r9.mEndWithNegOrDot = r6
            goto L38
        L2c:
            if (r0 == r8) goto L38
            if (r4 != 0) goto L38
            r1 = 1
            r9.mEndWithNegOrDot = r6
            goto L38
        L34:
            r2 = 1
            goto L38
        L36:
            r1 = 1
        L38:
            if (r1 == 0) goto L3b
            goto L3e
        L3b:
            int r0 = r0 + 1
            goto L7
        L3e:
            r9.mEndPosition = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.PathParser.extract(java.lang.String, int, android.support.v4.graphics.PathParser$ExtractFloatResult):void");
    }

    /* loaded from: classes.dex */
    public static class PathDataNode {
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public float[] mParams;
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public char mType;

        PathDataNode(char type, float[] params) {
            this.mType = type;
            this.mParams = params;
        }

        PathDataNode(PathDataNode n) {
            this.mType = n.mType;
            this.mParams = PathParser.copyOfRange(n.mParams, 0, n.mParams.length);
        }

        public static void nodesToPath(PathDataNode[] node, Path path) {
            float[] current = new float[6];
            char previousCommand = 'm';
            for (int i = 0; i < node.length; i++) {
                addCommand(path, current, previousCommand, node[i].mType, node[i].mParams);
                previousCommand = node[i].mType;
            }
        }

        public void interpolatePathDataNode(PathDataNode nodeFrom, PathDataNode nodeTo, float fraction) {
            for (int i = 0; i < nodeFrom.mParams.length; i++) {
                this.mParams[i] = (nodeFrom.mParams[i] * (1.0f - fraction)) + (nodeTo.mParams[i] * fraction);
            }
        }

        private static void addCommand(Path path, float[] current, char previousCmd, char cmd, float[] val) {
            int k;
            float ctrlPointX;
            float ctrlPointY;
            float reflectiveCtrlPointX;
            float reflectiveCtrlPointY;
            float ctrlPointX2;
            float ctrlPointY2;
            float reflectiveCtrlPointX2;
            float reflectiveCtrlPointY2;
            int incr = 2;
            float currentX = current[0];
            float currentY = current[1];
            float ctrlPointX3 = current[2];
            float ctrlPointY3 = current[3];
            float currentSegmentStartX = current[4];
            float currentSegmentStartY = current[5];
            switch (cmd) {
                case 'A':
                case 'a':
                    incr = 7;
                    break;
                case 'C':
                case 'c':
                    incr = 6;
                    break;
                case 'H':
                case 'V':
                case 'h':
                case 'v':
                    incr = 1;
                    break;
                case 'L':
                case 'M':
                case 'T':
                case 'l':
                case 'm':
                case 't':
                    incr = 2;
                    break;
                case 'Q':
                case 'S':
                case 'q':
                case 's':
                    incr = 4;
                    break;
                case 'Z':
                case 'z':
                    path.close();
                    currentX = currentSegmentStartX;
                    currentY = currentSegmentStartY;
                    ctrlPointX3 = currentSegmentStartX;
                    ctrlPointY3 = currentSegmentStartY;
                    path.moveTo(currentX, currentY);
                    break;
            }
            int incr2 = incr;
            char previousCmd2 = previousCmd;
            float currentX2 = currentX;
            float currentY2 = currentY;
            float ctrlPointX4 = ctrlPointX3;
            float ctrlPointY4 = ctrlPointY3;
            float currentSegmentStartX2 = currentSegmentStartX;
            float currentSegmentStartY2 = currentSegmentStartY;
            int k2 = 0;
            while (true) {
                int k3 = k2;
                int k4 = val.length;
                if (k3 >= k4) {
                    current[0] = currentX2;
                    current[1] = currentY2;
                    current[2] = ctrlPointX4;
                    current[3] = ctrlPointY4;
                    current[4] = currentSegmentStartX2;
                    current[5] = currentSegmentStartY2;
                    return;
                }
                switch (cmd) {
                    case 'A':
                        k = k3;
                        drawArc(path, currentX2, currentY2, val[k + 5], val[k + 6], val[k + 0], val[k + 1], val[k + 2], val[k + 3] != 0.0f, val[k + 4] != 0.0f);
                        currentX2 = val[k + 5];
                        currentY2 = val[k + 6];
                        ctrlPointX = currentX2;
                        ctrlPointY = currentY2;
                        ctrlPointX4 = ctrlPointX;
                        ctrlPointY4 = ctrlPointY;
                        break;
                    case 'C':
                        k = k3;
                        path.cubicTo(val[k + 0], val[k + 1], val[k + 2], val[k + 3], val[k + 4], val[k + 5]);
                        currentX2 = val[k + 4];
                        currentY2 = val[k + 5];
                        ctrlPointX = val[k + 2];
                        ctrlPointY = val[k + 3];
                        ctrlPointX4 = ctrlPointX;
                        ctrlPointY4 = ctrlPointY;
                        break;
                    case 'H':
                        k = k3;
                        path.lineTo(val[k + 0], currentY2);
                        currentX2 = val[k + 0];
                        break;
                    case 'L':
                        k = k3;
                        path.lineTo(val[k + 0], val[k + 1]);
                        currentX2 = val[k + 0];
                        currentY2 = val[k + 1];
                        break;
                    case 'M':
                        k = k3;
                        currentX2 = val[k + 0];
                        currentY2 = val[k + 1];
                        if (k <= 0) {
                            path.moveTo(val[k + 0], val[k + 1]);
                            currentSegmentStartX2 = currentX2;
                            currentSegmentStartY2 = currentY2;
                            break;
                        } else {
                            path.lineTo(val[k + 0], val[k + 1]);
                            break;
                        }
                    case 'Q':
                        k = k3;
                        path.quadTo(val[k + 0], val[k + 1], val[k + 2], val[k + 3]);
                        ctrlPointX = val[k + 0];
                        ctrlPointY = val[k + 1];
                        currentX2 = val[k + 2];
                        currentY2 = val[k + 3];
                        ctrlPointX4 = ctrlPointX;
                        ctrlPointY4 = ctrlPointY;
                        break;
                    case 'S':
                        k = k3;
                        char previousCmd3 = previousCmd2;
                        float currentY3 = currentY2;
                        float currentX3 = currentX2;
                        if (previousCmd3 == 'c' || previousCmd3 == 's' || previousCmd3 == 'C' || previousCmd3 == 'S') {
                            reflectiveCtrlPointX = (currentX3 * 2.0f) - ctrlPointX4;
                            reflectiveCtrlPointY = (currentY3 * 2.0f) - ctrlPointY4;
                        } else {
                            reflectiveCtrlPointX = currentX3;
                            reflectiveCtrlPointY = currentY3;
                        }
                        path.cubicTo(reflectiveCtrlPointX, reflectiveCtrlPointY, val[k + 0], val[k + 1], val[k + 2], val[k + 3]);
                        ctrlPointX4 = val[k + 0];
                        ctrlPointY4 = val[k + 1];
                        currentX2 = val[k + 2];
                        currentY2 = val[k + 3];
                        break;
                    case 'T':
                        k = k3;
                        char previousCmd4 = previousCmd2;
                        float currentY4 = currentY2;
                        float currentX4 = currentX2;
                        float reflectiveCtrlPointX3 = currentX4;
                        float reflectiveCtrlPointY3 = currentY4;
                        if (previousCmd4 == 'q' || previousCmd4 == 't' || previousCmd4 == 'Q' || previousCmd4 == 'T') {
                            reflectiveCtrlPointX3 = (currentX4 * 2.0f) - ctrlPointX4;
                            reflectiveCtrlPointY3 = (currentY4 * 2.0f) - ctrlPointY4;
                        }
                        path.quadTo(reflectiveCtrlPointX3, reflectiveCtrlPointY3, val[k + 0], val[k + 1]);
                        currentX2 = val[k + 0];
                        currentY2 = val[k + 1];
                        ctrlPointX4 = reflectiveCtrlPointX3;
                        ctrlPointY4 = reflectiveCtrlPointY3;
                        break;
                    case 'V':
                        k = k3;
                        path.lineTo(currentX2, val[k + 0]);
                        currentY2 = val[k + 0];
                        break;
                    case 'a':
                        k = k3;
                        drawArc(path, currentX2, currentY2, val[k + 5] + currentX2, val[k + 6] + currentY2, val[k + 0], val[k + 1], val[k + 2], val[k + 3] != 0.0f, val[k + 4] != 0.0f);
                        currentX2 += val[k + 5];
                        currentY2 += val[k + 6];
                        ctrlPointX = currentX2;
                        ctrlPointY = currentY2;
                        ctrlPointX4 = ctrlPointX;
                        ctrlPointY4 = ctrlPointY;
                        break;
                    case 'c':
                        k = k3;
                        path.rCubicTo(val[k + 0], val[k + 1], val[k + 2], val[k + 3], val[k + 4], val[k + 5]);
                        ctrlPointX2 = val[k + 2] + currentX2;
                        ctrlPointY2 = val[k + 3] + currentY2;
                        currentX2 += val[k + 4];
                        currentY2 += val[k + 5];
                        ctrlPointX4 = ctrlPointX2;
                        ctrlPointY4 = ctrlPointY2;
                        break;
                    case 'h':
                        k = k3;
                        path.rLineTo(val[k + 0], 0.0f);
                        currentX2 += val[k + 0];
                        break;
                    case 'l':
                        k = k3;
                        path.rLineTo(val[k + 0], val[k + 1]);
                        currentX2 += val[k + 0];
                        currentY2 += val[k + 1];
                        break;
                    case 'm':
                        k = k3;
                        currentX2 += val[k + 0];
                        currentY2 += val[k + 1];
                        if (k > 0) {
                            path.rLineTo(val[k + 0], val[k + 1]);
                        } else {
                            path.rMoveTo(val[k + 0], val[k + 1]);
                            currentSegmentStartX2 = currentX2;
                            currentSegmentStartY2 = currentY2;
                        }
                        break;
                    case 'q':
                        k = k3;
                        path.rQuadTo(val[k + 0], val[k + 1], val[k + 2], val[k + 3]);
                        ctrlPointX2 = val[k + 0] + currentX2;
                        ctrlPointY2 = val[k + 1] + currentY2;
                        currentX2 += val[k + 2];
                        currentY2 += val[k + 3];
                        ctrlPointX4 = ctrlPointX2;
                        ctrlPointY4 = ctrlPointY2;
                        break;
                    case 's':
                        if (previousCmd2 == 'c' || previousCmd2 == 's' || previousCmd2 == 'C' || previousCmd2 == 'S') {
                            float reflectiveCtrlPointX4 = currentX2 - ctrlPointX4;
                            reflectiveCtrlPointX2 = reflectiveCtrlPointX4;
                            reflectiveCtrlPointY2 = currentY2 - ctrlPointY4;
                        } else {
                            reflectiveCtrlPointX2 = 0.0f;
                            reflectiveCtrlPointY2 = 0.0f;
                        }
                        k = k3;
                        path.rCubicTo(reflectiveCtrlPointX2, reflectiveCtrlPointY2, val[k3 + 0], val[k3 + 1], val[k3 + 2], val[k3 + 3]);
                        ctrlPointX2 = val[k + 0] + currentX2;
                        ctrlPointY2 = val[k + 1] + currentY2;
                        currentX2 += val[k + 2];
                        currentY2 += val[k + 3];
                        ctrlPointX4 = ctrlPointX2;
                        ctrlPointY4 = ctrlPointY2;
                        break;
                    case 't':
                        float reflectiveCtrlPointX5 = 0.0f;
                        float reflectiveCtrlPointY4 = 0.0f;
                        if (previousCmd2 == 'q' || previousCmd2 == 't' || previousCmd2 == 'Q' || previousCmd2 == 'T') {
                            reflectiveCtrlPointX5 = currentX2 - ctrlPointX4;
                            reflectiveCtrlPointY4 = currentY2 - ctrlPointY4;
                        }
                        path.rQuadTo(reflectiveCtrlPointX5, reflectiveCtrlPointY4, val[k3 + 0], val[k3 + 1]);
                        float ctrlPointX5 = currentX2 + reflectiveCtrlPointX5;
                        float ctrlPointY5 = currentY2 + reflectiveCtrlPointY4;
                        currentX2 += val[k3 + 0];
                        currentY2 += val[k3 + 1];
                        ctrlPointX4 = ctrlPointX5;
                        ctrlPointY4 = ctrlPointY5;
                        k = k3;
                        break;
                    case 'v':
                        path.rLineTo(0.0f, val[k3 + 0]);
                        currentY2 += val[k3 + 0];
                        k = k3;
                        break;
                    default:
                        k = k3;
                        break;
                }
                previousCmd2 = cmd;
                k2 = k + incr2;
            }
        }

        private static void drawArc(Path p, float x0, float y0, float x1, float y1, float a, float b, float theta, boolean isMoreThanHalf, boolean isPositiveArc) {
            double cx;
            double cy;
            double thetaD = Math.toRadians(theta);
            double cosTheta = Math.cos(thetaD);
            double sinTheta = Math.sin(thetaD);
            double d = x0;
            Double.isNaN(d);
            double d2 = y0;
            Double.isNaN(d2);
            double d3 = (d * cosTheta) + (d2 * sinTheta);
            double d4 = a;
            Double.isNaN(d4);
            double x0p = d3 / d4;
            double d5 = -x0;
            Double.isNaN(d5);
            double d6 = y0;
            Double.isNaN(d6);
            double d7 = (d5 * sinTheta) + (d6 * cosTheta);
            double d8 = b;
            Double.isNaN(d8);
            double y0p = d7 / d8;
            double d9 = x1;
            Double.isNaN(d9);
            double d10 = y1;
            Double.isNaN(d10);
            double d11 = (d9 * cosTheta) + (d10 * sinTheta);
            double d12 = a;
            Double.isNaN(d12);
            double x1p = d11 / d12;
            double d13 = -x1;
            Double.isNaN(d13);
            double d14 = y1;
            Double.isNaN(d14);
            double d15 = (d13 * sinTheta) + (d14 * cosTheta);
            double d16 = b;
            Double.isNaN(d16);
            double y1p = d15 / d16;
            double dx = x0p - x1p;
            double dy = y0p - y1p;
            double xm = (x0p + x1p) / 2.0d;
            double ym = (y0p + y1p) / 2.0d;
            double dsq = (dx * dx) + (dy * dy);
            if (dsq == 0.0d) {
                Log.w(PathParser.LOGTAG, " Points are coincident");
                return;
            }
            double disc = (1.0d / dsq) - 0.25d;
            if (disc < 0.0d) {
                Log.w(PathParser.LOGTAG, "Points are too far apart " + dsq);
                float adjust = (float) (Math.sqrt(dsq) / 1.99999d);
                drawArc(p, x0, y0, x1, y1, a * adjust, b * adjust, theta, isMoreThanHalf, isPositiveArc);
                return;
            }
            double s = Math.sqrt(disc);
            double sdx = s * dx;
            double sdy = s * dy;
            if (isMoreThanHalf == isPositiveArc) {
                cx = xm - sdy;
                cy = ym + sdx;
            } else {
                cx = xm + sdy;
                cy = ym - sdx;
            }
            double eta0 = Math.atan2(y0p - cy, x0p - cx);
            double sweep = Math.atan2(y1p - cy, x1p - cx) - eta0;
            if (isPositiveArc != (sweep >= 0.0d)) {
                if (sweep <= 0.0d) {
                    sweep += 6.283185307179586d;
                } else {
                    sweep -= 6.283185307179586d;
                }
            }
            double eta1 = a;
            Double.isNaN(eta1);
            double cx2 = cx * eta1;
            double d17 = b;
            Double.isNaN(d17);
            double cy2 = d17 * cy;
            double cx3 = (cx2 * cosTheta) - (cy2 * sinTheta);
            double cy3 = (cx2 * sinTheta) + (cy2 * cosTheta);
            double cy4 = a;
            arcToBezier(p, cx3, cy3, cy4, b, x0, y0, thetaD, eta0, sweep);
        }

        private static void arcToBezier(Path p, double cx, double cy, double a, double b, double e1x, double e1y, double theta, double start, double sweep) {
            double eta1 = a;
            int numSegments = (int) Math.ceil(Math.abs((sweep * 4.0d) / 3.141592653589793d));
            double eta12 = start;
            double cosTheta = Math.cos(theta);
            double sinTheta = Math.sin(theta);
            double cosEta1 = Math.cos(eta12);
            double sinEta1 = Math.sin(eta12);
            double ep1x = (((-eta1) * cosTheta) * sinEta1) - ((b * sinTheta) * cosEta1);
            double ep1x2 = -eta1;
            double ep1y = (ep1x2 * sinTheta * sinEta1) + (b * cosTheta * cosEta1);
            double ep1y2 = numSegments;
            Double.isNaN(ep1y2);
            double anglePerSegment = sweep / ep1y2;
            int i = 0;
            double ep1y3 = ep1y;
            double e1y2 = e1y;
            double ep1x3 = ep1x;
            double e1x2 = e1x;
            while (true) {
                int i2 = i;
                if (i2 >= numSegments) {
                    return;
                }
                double eta2 = eta12 + anglePerSegment;
                double sinEta2 = Math.sin(eta2);
                double cosEta2 = Math.cos(eta2);
                double e2x = (cx + ((eta1 * cosTheta) * cosEta2)) - ((b * sinTheta) * sinEta2);
                double e2y = cy + (eta1 * sinTheta * cosEta2) + (b * cosTheta * sinEta2);
                double ep2x = (((-eta1) * cosTheta) * sinEta2) - ((b * sinTheta) * cosEta2);
                double ep2y = ((-eta1) * sinTheta * sinEta2) + (b * cosTheta * cosEta2);
                double tanDiff2 = Math.tan((eta2 - eta12) / 2.0d);
                double alpha = (Math.sin(eta2 - eta12) * (Math.sqrt(((tanDiff2 * 3.0d) * tanDiff2) + 4.0d) - 1.0d)) / 3.0d;
                double q1x = e1x2 + (alpha * ep1x3);
                double q1y = e1y2 + (alpha * ep1y3);
                p.rLineTo(0.0f, 0.0f);
                p.cubicTo((float) q1x, (float) q1y, (float) (e2x - (alpha * ep2x)), (float) (e2y - (alpha * ep2y)), (float) e2x, (float) e2y);
                e1x2 = e2x;
                e1y2 = e2y;
                ep1x3 = ep2x;
                ep1y3 = ep2y;
                i = i2 + 1;
                eta12 = eta2;
                anglePerSegment = anglePerSegment;
                numSegments = numSegments;
                cosTheta = cosTheta;
                sinTheta = sinTheta;
                eta1 = a;
            }
        }
    }
}
