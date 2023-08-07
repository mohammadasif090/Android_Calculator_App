package android.support.v7.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.Xml;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
final class AppCompatColorStateListInflater {
    private static final int DEFAULT_COLOR = -65536;

    private AppCompatColorStateListInflater() {
    }

    @NonNull
    public static ColorStateList createFromXml(@NonNull Resources r, @NonNull XmlPullParser parser, @Nullable Resources.Theme theme) throws XmlPullParserException, IOException {
        int type;
        AttributeSet attrs = Xml.asAttributeSet(parser);
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        return createFromXmlInner(r, parser, attrs, theme);
    }

    @NonNull
    private static ColorStateList createFromXmlInner(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs, @Nullable Resources.Theme theme) throws XmlPullParserException, IOException {
        String name = parser.getName();
        if (!name.equals("selector")) {
            throw new XmlPullParserException(parser.getPositionDescription() + ": invalid color state list tag " + name);
        }
        return inflate(r, parser, attrs, theme);
    }

    private static ColorStateList inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs, @Nullable Resources.Theme theme) throws XmlPullParserException, IOException {
        int depth;
        int innerDepth;
        int defaultColor;
        int i = 1;
        int innerDepth2 = parser.getDepth() + 1;
        int[][] stateSpecList = new int[20];
        int[] colorList = new int[stateSpecList.length];
        int defaultColor2 = -65536;
        int listSize = 0;
        while (true) {
            int type = parser.next();
            if (type != i && ((depth = parser.getDepth()) >= innerDepth2 || type != 3)) {
                if (type != 2 || depth > innerDepth2) {
                    innerDepth = innerDepth2;
                    defaultColor = defaultColor2;
                } else if (parser.getName().equals("item")) {
                    TypedArray a = obtainAttributes(r, theme, attrs, R.styleable.ColorStateListItem);
                    int baseColor = a.getColor(R.styleable.ColorStateListItem_android_color, -65281);
                    float alphaMod = 1.0f;
                    if (a.hasValue(R.styleable.ColorStateListItem_android_alpha)) {
                        alphaMod = a.getFloat(R.styleable.ColorStateListItem_android_alpha, 1.0f);
                    } else if (a.hasValue(R.styleable.ColorStateListItem_alpha)) {
                        alphaMod = a.getFloat(R.styleable.ColorStateListItem_alpha, 1.0f);
                    }
                    a.recycle();
                    int numAttrs = attrs.getAttributeCount();
                    int[] stateSpec = new int[numAttrs];
                    int innerDepth3 = innerDepth2;
                    int j = 0;
                    int j2 = 0;
                    while (j2 < numAttrs) {
                        int numAttrs2 = numAttrs;
                        int stateResId = attrs.getAttributeNameResource(j2);
                        int defaultColor3 = defaultColor2;
                        if (stateResId != 16843173 && stateResId != 16843551 && stateResId != R.attr.alpha) {
                            int j3 = j + 1;
                            stateSpec[j] = attrs.getAttributeBooleanValue(j2, false) ? stateResId : -stateResId;
                            j = j3;
                        }
                        j2++;
                        numAttrs = numAttrs2;
                        defaultColor2 = defaultColor3;
                    }
                    int defaultColor4 = defaultColor2;
                    int[] stateSpec2 = StateSet.trimStateSet(stateSpec, j);
                    int color = modulateColorAlpha(baseColor, alphaMod);
                    if (listSize == 0 || stateSpec2.length == 0) {
                        defaultColor2 = color;
                    } else {
                        defaultColor2 = defaultColor4;
                    }
                    colorList = GrowingArrayUtils.append(colorList, listSize, color);
                    stateSpecList = (int[][]) GrowingArrayUtils.append(stateSpecList, listSize, stateSpec2);
                    listSize++;
                    innerDepth2 = innerDepth3;
                    i = 1;
                } else {
                    innerDepth = innerDepth2;
                    defaultColor = defaultColor2;
                }
                innerDepth2 = innerDepth;
                defaultColor2 = defaultColor;
                i = 1;
            }
        }
        int[] colors = new int[listSize];
        int[][] stateSpecs = new int[listSize];
        System.arraycopy(colorList, 0, colors, 0, listSize);
        System.arraycopy(stateSpecList, 0, stateSpecs, 0, listSize);
        return new ColorStateList(stateSpecs, colors);
    }

    private static TypedArray obtainAttributes(Resources res, Resources.Theme theme, AttributeSet set, int[] attrs) {
        return theme == null ? res.obtainAttributes(set, attrs) : theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    private static int modulateColorAlpha(int color, float alphaMod) {
        return ColorUtils.setAlphaComponent(color, Math.round(Color.alpha(color) * alphaMod));
    }
}
