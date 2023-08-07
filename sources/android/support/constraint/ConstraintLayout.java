package android.support.constraint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.constraint.solver.Metrics;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.constraint.solver.widgets.ConstraintWidgetContainer;
import android.support.v4.internal.view.SupportMenu;
import android.support.v7.widget.ActivityChooserView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes.dex */
public class ConstraintLayout extends ViewGroup {
    static final boolean ALLOWS_EMBEDDED = false;
    private static final boolean DEBUG = false;
    public static final int DESIGN_INFO_ID = 0;
    private static final String TAG = "ConstraintLayout";
    private static final boolean USE_CONSTRAINTS_HELPER = true;
    public static final String VERSION = "ConstraintLayout-1.1.0";
    SparseArray<View> mChildrenByIds;
    private ArrayList<ConstraintHelper> mConstraintHelpers;
    private ConstraintSet mConstraintSet;
    private int mConstraintSetId;
    private HashMap<String, Integer> mDesignIds;
    private boolean mDirtyHierarchy;
    private int mLastMeasureHeight;
    int mLastMeasureHeightMode;
    int mLastMeasureHeightSize;
    private int mLastMeasureWidth;
    int mLastMeasureWidthMode;
    int mLastMeasureWidthSize;
    ConstraintWidgetContainer mLayoutWidget;
    private int mMaxHeight;
    private int mMaxWidth;
    private Metrics mMetrics;
    private int mMinHeight;
    private int mMinWidth;
    private int mOptimizationLevel;
    private final ArrayList<ConstraintWidget> mVariableDimensionsWidgets;

    public void setDesignInformation(int type, Object value1, Object value2) {
        if (type == 0 && (value1 instanceof String) && (value2 instanceof Integer)) {
            if (this.mDesignIds == null) {
                this.mDesignIds = new HashMap<>();
            }
            String name = (String) value1;
            int index = name.indexOf("/");
            if (index != -1) {
                name = name.substring(index + 1);
            }
            int id = ((Integer) value2).intValue();
            this.mDesignIds.put(name, Integer.valueOf(id));
        }
    }

    public Object getDesignInformation(int type, Object value) {
        if (type == 0 && (value instanceof String)) {
            String name = (String) value;
            if (this.mDesignIds != null && this.mDesignIds.containsKey(name)) {
                return this.mDesignIds.get(name);
            }
            return null;
        }
        return null;
    }

    public ConstraintLayout(Context context) {
        super(context);
        this.mChildrenByIds = new SparseArray<>();
        this.mConstraintHelpers = new ArrayList<>(4);
        this.mVariableDimensionsWidgets = new ArrayList<>(100);
        this.mLayoutWidget = new ConstraintWidgetContainer();
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mMaxWidth = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mMaxHeight = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
        this.mOptimizationLevel = 3;
        this.mConstraintSet = null;
        this.mConstraintSetId = -1;
        this.mDesignIds = new HashMap<>();
        this.mLastMeasureWidth = -1;
        this.mLastMeasureHeight = -1;
        this.mLastMeasureWidthSize = -1;
        this.mLastMeasureHeightSize = -1;
        this.mLastMeasureWidthMode = 0;
        this.mLastMeasureHeightMode = 0;
        init(null);
    }

    public ConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mChildrenByIds = new SparseArray<>();
        this.mConstraintHelpers = new ArrayList<>(4);
        this.mVariableDimensionsWidgets = new ArrayList<>(100);
        this.mLayoutWidget = new ConstraintWidgetContainer();
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mMaxWidth = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mMaxHeight = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
        this.mOptimizationLevel = 3;
        this.mConstraintSet = null;
        this.mConstraintSetId = -1;
        this.mDesignIds = new HashMap<>();
        this.mLastMeasureWidth = -1;
        this.mLastMeasureHeight = -1;
        this.mLastMeasureWidthSize = -1;
        this.mLastMeasureHeightSize = -1;
        this.mLastMeasureWidthMode = 0;
        this.mLastMeasureHeightMode = 0;
        init(attrs);
    }

    public ConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mChildrenByIds = new SparseArray<>();
        this.mConstraintHelpers = new ArrayList<>(4);
        this.mVariableDimensionsWidgets = new ArrayList<>(100);
        this.mLayoutWidget = new ConstraintWidgetContainer();
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mMaxWidth = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mMaxHeight = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
        this.mOptimizationLevel = 3;
        this.mConstraintSet = null;
        this.mConstraintSetId = -1;
        this.mDesignIds = new HashMap<>();
        this.mLastMeasureWidth = -1;
        this.mLastMeasureHeight = -1;
        this.mLastMeasureWidthSize = -1;
        this.mLastMeasureHeightSize = -1;
        this.mLastMeasureWidthMode = 0;
        this.mLastMeasureHeightMode = 0;
        init(attrs);
    }

    @Override // android.view.View
    public void setId(int id) {
        this.mChildrenByIds.remove(getId());
        super.setId(id);
        this.mChildrenByIds.put(getId(), this);
    }

    private void init(AttributeSet attrs) {
        this.mLayoutWidget.setCompanionWidget(this);
        this.mChildrenByIds.put(getId(), this);
        this.mConstraintSet = null;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.ConstraintLayout_Layout_android_minWidth) {
                    this.mMinWidth = a.getDimensionPixelOffset(attr, this.mMinWidth);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_minHeight) {
                    this.mMinHeight = a.getDimensionPixelOffset(attr, this.mMinHeight);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_maxWidth) {
                    this.mMaxWidth = a.getDimensionPixelOffset(attr, this.mMaxWidth);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_maxHeight) {
                    this.mMaxHeight = a.getDimensionPixelOffset(attr, this.mMaxHeight);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_optimizationLevel) {
                    this.mOptimizationLevel = a.getInt(attr, this.mOptimizationLevel);
                } else if (attr == R.styleable.ConstraintLayout_Layout_constraintSet) {
                    int id = a.getResourceId(attr, 0);
                    try {
                        this.mConstraintSet = new ConstraintSet();
                        this.mConstraintSet.load(getContext(), id);
                    } catch (Resources.NotFoundException e) {
                        this.mConstraintSet = null;
                    }
                    this.mConstraintSetId = id;
                }
            }
            a.recycle();
        }
        this.mLayoutWidget.setOptimizationLevel(this.mOptimizationLevel);
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (Build.VERSION.SDK_INT < 14) {
            onViewAdded(child);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(View view) {
        super.removeView(view);
        if (Build.VERSION.SDK_INT < 14) {
            onViewRemoved(view);
        }
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View view) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onViewAdded(view);
        }
        ConstraintWidget widget = getViewWidget(view);
        if ((view instanceof Guideline) && !(widget instanceof android.support.constraint.solver.widgets.Guideline)) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.widget = new android.support.constraint.solver.widgets.Guideline();
            layoutParams.isGuideline = USE_CONSTRAINTS_HELPER;
            ((android.support.constraint.solver.widgets.Guideline) layoutParams.widget).setOrientation(layoutParams.orientation);
        }
        if (view instanceof ConstraintHelper) {
            ConstraintHelper helper = (ConstraintHelper) view;
            helper.validateParams();
            ((LayoutParams) view.getLayoutParams()).isHelper = USE_CONSTRAINTS_HELPER;
            if (!this.mConstraintHelpers.contains(helper)) {
                this.mConstraintHelpers.add(helper);
            }
        }
        this.mChildrenByIds.put(view.getId(), view);
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
    }

    @Override // android.view.ViewGroup
    public void onViewRemoved(View view) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onViewRemoved(view);
        }
        this.mChildrenByIds.remove(view.getId());
        ConstraintWidget widget = getViewWidget(view);
        this.mLayoutWidget.remove(widget);
        this.mConstraintHelpers.remove(view);
        this.mVariableDimensionsWidgets.remove(widget);
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
    }

    public void setMinWidth(int value) {
        if (value == this.mMinWidth) {
            return;
        }
        this.mMinWidth = value;
        requestLayout();
    }

    public void setMinHeight(int value) {
        if (value == this.mMinHeight) {
            return;
        }
        this.mMinHeight = value;
        requestLayout();
    }

    public int getMinWidth() {
        return this.mMinWidth;
    }

    public int getMinHeight() {
        return this.mMinHeight;
    }

    public void setMaxWidth(int value) {
        if (value == this.mMaxWidth) {
            return;
        }
        this.mMaxWidth = value;
        requestLayout();
    }

    public void setMaxHeight(int value) {
        if (value == this.mMaxHeight) {
            return;
        }
        this.mMaxHeight = value;
        requestLayout();
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    private void updateHierarchy() {
        int count = getChildCount();
        boolean recompute = false;
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            }
            View child = getChildAt(i);
            if (!child.isLayoutRequested()) {
                i++;
            } else {
                recompute = USE_CONSTRAINTS_HELPER;
                break;
            }
        }
        if (recompute) {
            this.mVariableDimensionsWidgets.clear();
            setChildrenConstraints();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r4v1, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r4v7 */
    private void setChildrenConstraints() {
        int i;
        int resolvedLeftToLeft;
        int resolveGoneRightMargin;
        int resolvedLeftToRight;
        float resolvedHorizontalBias;
        int resolveGoneLeftMargin;
        int resolvedRightToLeft;
        int count;
        int helperCount;
        float resolvedHorizontalBias2;
        int resolvedRightToRight;
        int helperCount2;
        LayoutParams layoutParams;
        ConstraintWidget target;
        ConstraintWidget target2;
        ConstraintWidget target3;
        ConstraintWidget target4;
        int resolvedLeftToLeft2;
        int resolvedLeftToLeft3;
        int resolvedLeftToLeft4;
        boolean isInEditMode = isInEditMode();
        int count2 = getChildCount();
        ?? r4 = 0;
        int i2 = -1;
        if (isInEditMode) {
            int i3 = 0;
            while (true) {
                int i4 = i3;
                if (i4 >= count2) {
                    break;
                }
                View view = getChildAt(i4);
                try {
                    String IdAsString = getResources().getResourceName(view.getId());
                    setDesignInformation(0, IdAsString, Integer.valueOf(view.getId()));
                    int slashIndex = IdAsString.indexOf(47);
                    if (slashIndex != -1) {
                        IdAsString = IdAsString.substring(slashIndex + 1);
                    }
                    getTargetWidget(view.getId()).setDebugName(IdAsString);
                } catch (Resources.NotFoundException e) {
                }
                i3 = i4 + 1;
            }
        }
        for (int i5 = 0; i5 < count2; i5++) {
            ConstraintWidget widget = getViewWidget(getChildAt(i5));
            if (widget != null) {
                widget.reset();
            }
        }
        int i6 = this.mConstraintSetId;
        if (i6 != -1) {
            for (int i7 = 0; i7 < count2; i7++) {
                View child = getChildAt(i7);
                if (child.getId() == this.mConstraintSetId && (child instanceof Constraints)) {
                    this.mConstraintSet = ((Constraints) child).getConstraintSet();
                }
            }
        }
        if (this.mConstraintSet != null) {
            this.mConstraintSet.applyToInternal(this);
        }
        this.mLayoutWidget.removeAllChildren();
        int helperCount3 = this.mConstraintHelpers.size();
        if (helperCount3 > 0) {
            for (int i8 = 0; i8 < helperCount3; i8++) {
                ConstraintHelper helper = this.mConstraintHelpers.get(i8);
                helper.updatePreLayout(this);
            }
        }
        for (int i9 = 0; i9 < count2; i9++) {
            View child2 = getChildAt(i9);
            if (child2 instanceof Placeholder) {
                ((Placeholder) child2).updatePreLayout(this);
            }
        }
        int i10 = 0;
        while (true) {
            int i11 = i10;
            if (i11 >= count2) {
                return;
            }
            View child3 = getChildAt(i11);
            ConstraintWidget widget2 = getViewWidget(child3);
            if (widget2 != null) {
                LayoutParams layoutParams2 = (LayoutParams) child3.getLayoutParams();
                layoutParams2.validate();
                if (layoutParams2.helped) {
                    layoutParams2.helped = r4;
                } else if (isInEditMode) {
                    try {
                        String IdAsString2 = getResources().getResourceName(child3.getId());
                        setDesignInformation(r4, IdAsString2, Integer.valueOf(child3.getId()));
                        getTargetWidget(child3.getId()).setDebugName(IdAsString2.substring(IdAsString2.indexOf("id/") + 3));
                    } catch (Resources.NotFoundException e2) {
                    }
                }
                widget2.setVisibility(child3.getVisibility());
                if (layoutParams2.isInPlaceholder) {
                    widget2.setVisibility(8);
                }
                widget2.setCompanionWidget(child3);
                this.mLayoutWidget.add(widget2);
                if (!layoutParams2.verticalDimensionFixed || !layoutParams2.horizontalDimensionFixed) {
                    this.mVariableDimensionsWidgets.add(widget2);
                }
                if (layoutParams2.isGuideline) {
                    android.support.constraint.solver.widgets.Guideline guideline = (android.support.constraint.solver.widgets.Guideline) widget2;
                    int resolvedGuideBegin = layoutParams2.resolvedGuideBegin;
                    int resolvedGuideEnd = layoutParams2.resolvedGuideEnd;
                    float resolvedGuidePercent = layoutParams2.resolvedGuidePercent;
                    if (Build.VERSION.SDK_INT < 17) {
                        resolvedGuideBegin = layoutParams2.guideBegin;
                        resolvedGuideEnd = layoutParams2.guideEnd;
                        resolvedGuidePercent = layoutParams2.guidePercent;
                    }
                    if (resolvedGuidePercent != -1.0f) {
                        guideline.setGuidePercent(resolvedGuidePercent);
                    } else if (resolvedGuideBegin != i2) {
                        guideline.setGuideBegin(resolvedGuideBegin);
                    } else if (resolvedGuideEnd != i2) {
                        guideline.setGuideEnd(resolvedGuideEnd);
                    }
                } else if (layoutParams2.leftToLeft != i2 || layoutParams2.leftToRight != i2 || layoutParams2.rightToLeft != i2 || layoutParams2.rightToRight != i2 || layoutParams2.startToStart != i2 || layoutParams2.startToEnd != i2 || layoutParams2.endToStart != i2 || layoutParams2.endToEnd != i2 || layoutParams2.topToTop != i2 || layoutParams2.topToBottom != i2 || layoutParams2.bottomToTop != i2 || layoutParams2.bottomToBottom != i2 || layoutParams2.baselineToBaseline != i2 || layoutParams2.editorAbsoluteX != i2 || layoutParams2.editorAbsoluteY != i2 || layoutParams2.circleConstraint != i2 || layoutParams2.width == i2 || layoutParams2.height == i2) {
                    int resolvedLeftToLeft5 = layoutParams2.resolvedLeftToLeft;
                    int resolvedLeftToRight2 = layoutParams2.resolvedLeftToRight;
                    int resolvedRightToLeft2 = layoutParams2.resolvedRightToLeft;
                    int resolvedRightToRight2 = layoutParams2.resolvedRightToRight;
                    int resolveGoneLeftMargin2 = layoutParams2.resolveGoneLeftMargin;
                    int resolveGoneRightMargin2 = layoutParams2.resolveGoneRightMargin;
                    float resolvedHorizontalBias3 = layoutParams2.resolvedHorizontalBias;
                    int resolvedLeftToLeft6 = Build.VERSION.SDK_INT;
                    if (resolvedLeftToLeft6 >= 17) {
                        i = -1;
                        resolvedLeftToLeft = resolvedLeftToLeft5;
                        resolveGoneRightMargin = resolveGoneRightMargin2;
                        resolvedLeftToRight = resolvedLeftToRight2;
                        resolvedHorizontalBias = resolvedHorizontalBias3;
                        resolveGoneLeftMargin = resolveGoneLeftMargin2;
                        resolvedRightToLeft = resolvedRightToLeft2;
                    } else {
                        int resolvedLeftToLeft7 = layoutParams2.leftToLeft;
                        int resolvedLeftToRight3 = layoutParams2.leftToRight;
                        int resolvedRightToLeft3 = layoutParams2.rightToLeft;
                        resolvedRightToRight2 = layoutParams2.rightToRight;
                        int i12 = layoutParams2.goneLeftMargin;
                        int resolveGoneRightMargin3 = layoutParams2.goneRightMargin;
                        float resolvedHorizontalBias4 = layoutParams2.horizontalBias;
                        if (resolvedLeftToLeft7 == -1 && resolvedLeftToRight3 == -1) {
                            resolvedLeftToLeft2 = resolvedLeftToLeft7;
                            int resolvedLeftToLeft8 = layoutParams2.startToStart;
                            if (resolvedLeftToLeft8 == -1) {
                                if (layoutParams2.startToEnd != -1) {
                                    resolvedLeftToRight3 = layoutParams2.startToEnd;
                                }
                            } else {
                                resolvedLeftToLeft3 = layoutParams2.startToStart;
                                if (resolvedRightToLeft3 != -1 && resolvedRightToRight2 == -1) {
                                    resolvedLeftToLeft4 = resolvedLeftToLeft3;
                                    int resolvedLeftToLeft9 = layoutParams2.endToStart;
                                    if (resolvedLeftToLeft9 == -1) {
                                        if (layoutParams2.endToEnd != -1) {
                                            resolvedRightToRight2 = layoutParams2.endToEnd;
                                        }
                                    } else {
                                        resolvedRightToLeft3 = layoutParams2.endToStart;
                                    }
                                } else {
                                    resolvedLeftToLeft4 = resolvedLeftToLeft3;
                                }
                                resolveGoneRightMargin = resolveGoneRightMargin3;
                                resolvedLeftToRight = resolvedLeftToRight3;
                                resolvedRightToLeft = resolvedRightToLeft3;
                                resolvedLeftToLeft = resolvedLeftToLeft4;
                                i = -1;
                                resolvedHorizontalBias = resolvedHorizontalBias4;
                                resolveGoneLeftMargin = i12;
                            }
                        } else {
                            resolvedLeftToLeft2 = resolvedLeftToLeft7;
                        }
                        resolvedLeftToLeft3 = resolvedLeftToLeft2;
                        if (resolvedRightToLeft3 != -1) {
                        }
                        resolvedLeftToLeft4 = resolvedLeftToLeft3;
                        resolveGoneRightMargin = resolveGoneRightMargin3;
                        resolvedLeftToRight = resolvedLeftToRight3;
                        resolvedRightToLeft = resolvedRightToLeft3;
                        resolvedLeftToLeft = resolvedLeftToLeft4;
                        i = -1;
                        resolvedHorizontalBias = resolvedHorizontalBias4;
                        resolveGoneLeftMargin = i12;
                    }
                    int resolvedLeftToRight4 = layoutParams2.circleConstraint;
                    if (resolvedLeftToRight4 != i) {
                        ConstraintWidget target5 = getTargetWidget(layoutParams2.circleConstraint);
                        if (target5 != null) {
                            count = count2;
                            widget2.connectCircularConstraint(target5, layoutParams2.circleAngle, layoutParams2.circleRadius);
                        } else {
                            count = count2;
                        }
                        helperCount = helperCount3;
                        layoutParams = layoutParams2;
                    } else {
                        count = count2;
                        if (resolvedLeftToLeft != -1) {
                            ConstraintWidget target6 = getTargetWidget(resolvedLeftToLeft);
                            if (target6 != null) {
                                resolvedHorizontalBias2 = resolvedHorizontalBias;
                                resolvedRightToRight = resolvedRightToRight2;
                                helperCount = helperCount3;
                                helperCount2 = resolvedRightToLeft;
                                layoutParams = layoutParams2;
                                widget2.immediateConnect(ConstraintAnchor.Type.LEFT, target6, ConstraintAnchor.Type.LEFT, layoutParams2.leftMargin, resolveGoneLeftMargin);
                            } else {
                                helperCount = helperCount3;
                                resolvedHorizontalBias2 = resolvedHorizontalBias;
                                resolvedRightToRight = resolvedRightToRight2;
                                helperCount2 = resolvedRightToLeft;
                                layoutParams = layoutParams2;
                            }
                        } else {
                            helperCount = helperCount3;
                            resolvedHorizontalBias2 = resolvedHorizontalBias;
                            resolvedRightToRight = resolvedRightToRight2;
                            helperCount2 = resolvedRightToLeft;
                            layoutParams = layoutParams2;
                            if (resolvedLeftToRight != -1 && (target = getTargetWidget(resolvedLeftToRight)) != null) {
                                widget2.immediateConnect(ConstraintAnchor.Type.LEFT, target, ConstraintAnchor.Type.RIGHT, layoutParams.leftMargin, resolveGoneLeftMargin);
                            }
                        }
                        if (helperCount2 != -1) {
                            ConstraintWidget target7 = getTargetWidget(helperCount2);
                            if (target7 != null) {
                                widget2.immediateConnect(ConstraintAnchor.Type.RIGHT, target7, ConstraintAnchor.Type.LEFT, layoutParams.rightMargin, resolveGoneRightMargin);
                            }
                        } else if (resolvedRightToRight != -1 && (target2 = getTargetWidget(resolvedRightToRight)) != null) {
                            widget2.immediateConnect(ConstraintAnchor.Type.RIGHT, target2, ConstraintAnchor.Type.RIGHT, layoutParams.rightMargin, resolveGoneRightMargin);
                        }
                        if (layoutParams.topToTop != -1) {
                            ConstraintWidget target8 = getTargetWidget(layoutParams.topToTop);
                            if (target8 != null) {
                                widget2.immediateConnect(ConstraintAnchor.Type.TOP, target8, ConstraintAnchor.Type.TOP, layoutParams.topMargin, layoutParams.goneTopMargin);
                            }
                        } else if (layoutParams.topToBottom != -1 && (target3 = getTargetWidget(layoutParams.topToBottom)) != null) {
                            widget2.immediateConnect(ConstraintAnchor.Type.TOP, target3, ConstraintAnchor.Type.BOTTOM, layoutParams.topMargin, layoutParams.goneTopMargin);
                        }
                        if (layoutParams.bottomToTop != -1) {
                            ConstraintWidget target9 = getTargetWidget(layoutParams.bottomToTop);
                            if (target9 != null) {
                                widget2.immediateConnect(ConstraintAnchor.Type.BOTTOM, target9, ConstraintAnchor.Type.TOP, layoutParams.bottomMargin, layoutParams.goneBottomMargin);
                            }
                        } else if (layoutParams.bottomToBottom != -1 && (target4 = getTargetWidget(layoutParams.bottomToBottom)) != null) {
                            widget2.immediateConnect(ConstraintAnchor.Type.BOTTOM, target4, ConstraintAnchor.Type.BOTTOM, layoutParams.bottomMargin, layoutParams.goneBottomMargin);
                        }
                        if (layoutParams.baselineToBaseline != -1) {
                            View view2 = this.mChildrenByIds.get(layoutParams.baselineToBaseline);
                            ConstraintWidget target10 = getTargetWidget(layoutParams.baselineToBaseline);
                            if (target10 != null && view2 != null && (view2.getLayoutParams() instanceof LayoutParams)) {
                                LayoutParams targetParams = (LayoutParams) view2.getLayoutParams();
                                layoutParams.needsBaseline = USE_CONSTRAINTS_HELPER;
                                targetParams.needsBaseline = USE_CONSTRAINTS_HELPER;
                                ConstraintAnchor baseline = widget2.getAnchor(ConstraintAnchor.Type.BASELINE);
                                ConstraintAnchor targetBaseline = target10.getAnchor(ConstraintAnchor.Type.BASELINE);
                                baseline.connect(targetBaseline, 0, -1, ConstraintAnchor.Strength.STRONG, 0, USE_CONSTRAINTS_HELPER);
                                widget2.getAnchor(ConstraintAnchor.Type.TOP).reset();
                                widget2.getAnchor(ConstraintAnchor.Type.BOTTOM).reset();
                            }
                        }
                        if (resolvedHorizontalBias2 >= 0.0f && resolvedHorizontalBias2 != 0.5f) {
                            widget2.setHorizontalBiasPercent(resolvedHorizontalBias2);
                        }
                        if (layoutParams.verticalBias >= 0.0f && layoutParams.verticalBias != 0.5f) {
                            widget2.setVerticalBiasPercent(layoutParams.verticalBias);
                        }
                    }
                    if (isInEditMode && (layoutParams.editorAbsoluteX != -1 || layoutParams.editorAbsoluteY != -1)) {
                        widget2.setOrigin(layoutParams.editorAbsoluteX, layoutParams.editorAbsoluteY);
                    }
                    if (!layoutParams.horizontalDimensionFixed) {
                        if (layoutParams.width == -1) {
                            widget2.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
                            widget2.getAnchor(ConstraintAnchor.Type.LEFT).mMargin = layoutParams.leftMargin;
                            widget2.getAnchor(ConstraintAnchor.Type.RIGHT).mMargin = layoutParams.rightMargin;
                        } else {
                            widget2.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                            widget2.setWidth(0);
                        }
                    } else {
                        widget2.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                        widget2.setWidth(layoutParams.width);
                    }
                    if (!layoutParams.verticalDimensionFixed) {
                        if (layoutParams.height == -1) {
                            widget2.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
                            widget2.getAnchor(ConstraintAnchor.Type.TOP).mMargin = layoutParams.topMargin;
                            widget2.getAnchor(ConstraintAnchor.Type.BOTTOM).mMargin = layoutParams.bottomMargin;
                        } else {
                            widget2.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                            widget2.setHeight(0);
                        }
                    } else {
                        widget2.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                        widget2.setHeight(layoutParams.height);
                    }
                    if (layoutParams.dimensionRatio != null) {
                        widget2.setDimensionRatio(layoutParams.dimensionRatio);
                    }
                    widget2.setHorizontalWeight(layoutParams.horizontalWeight);
                    widget2.setVerticalWeight(layoutParams.verticalWeight);
                    widget2.setHorizontalChainStyle(layoutParams.horizontalChainStyle);
                    widget2.setVerticalChainStyle(layoutParams.verticalChainStyle);
                    widget2.setHorizontalMatchStyle(layoutParams.matchConstraintDefaultWidth, layoutParams.matchConstraintMinWidth, layoutParams.matchConstraintMaxWidth, layoutParams.matchConstraintPercentWidth);
                    widget2.setVerticalMatchStyle(layoutParams.matchConstraintDefaultHeight, layoutParams.matchConstraintMinHeight, layoutParams.matchConstraintMaxHeight, layoutParams.matchConstraintPercentHeight);
                    i10 = i11 + 1;
                    count2 = count;
                    helperCount3 = helperCount;
                    r4 = 0;
                    i2 = -1;
                }
            }
            count = count2;
            helperCount = helperCount3;
            i10 = i11 + 1;
            count2 = count;
            helperCount3 = helperCount;
            r4 = 0;
            i2 = -1;
        }
    }

    private final ConstraintWidget getTargetWidget(int id) {
        if (id == 0) {
            return this.mLayoutWidget;
        }
        View view = this.mChildrenByIds.get(id);
        if (view == this) {
            return this.mLayoutWidget;
        }
        if (view == null) {
            return null;
        }
        return ((LayoutParams) view.getLayoutParams()).widget;
    }

    public final ConstraintWidget getViewWidget(View view) {
        if (view == this) {
            return this.mLayoutWidget;
        }
        if (view == null) {
            return null;
        }
        return ((LayoutParams) view.getLayoutParams()).widget;
    }

    private void internalMeasureChildren(int parentWidthSpec, int parentHeightSpec) {
        int baseline;
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        ConstraintLayout constraintLayout = this;
        int i = parentWidthSpec;
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int widgetsCount = getChildCount();
        int i2 = 0;
        while (i2 < widgetsCount) {
            View child = constraintLayout.getChildAt(i2);
            if (child.getVisibility() != 8) {
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                ConstraintWidget widget = params.widget;
                if (!params.isGuideline && !params.isHelper) {
                    widget.setVisibility(child.getVisibility());
                    int width = params.width;
                    int height = params.height;
                    boolean doMeasure = (params.horizontalDimensionFixed || params.verticalDimensionFixed || (!params.horizontalDimensionFixed && params.matchConstraintDefaultWidth == 1) || params.width == -1 || (!params.verticalDimensionFixed && (params.matchConstraintDefaultHeight == 1 || params.height == -1))) ? USE_CONSTRAINTS_HELPER : false;
                    boolean didWrapMeasureWidth = false;
                    boolean didWrapMeasureHeight = false;
                    if (doMeasure) {
                        if (width == 0) {
                            childWidthMeasureSpec = getChildMeasureSpec(i, widthPadding, -2);
                            didWrapMeasureWidth = USE_CONSTRAINTS_HELPER;
                        } else if (width == -1) {
                            childWidthMeasureSpec = getChildMeasureSpec(i, widthPadding, -1);
                        } else {
                            if (width == -2) {
                                didWrapMeasureWidth = USE_CONSTRAINTS_HELPER;
                            }
                            childWidthMeasureSpec = getChildMeasureSpec(i, widthPadding, width);
                        }
                        int childWidthMeasureSpec2 = childWidthMeasureSpec;
                        if (height == 0) {
                            childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding, -2);
                            didWrapMeasureHeight = USE_CONSTRAINTS_HELPER;
                        } else if (height == -1) {
                            childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding, -1);
                        } else {
                            if (height == -2) {
                                didWrapMeasureHeight = USE_CONSTRAINTS_HELPER;
                            }
                            childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding, height);
                        }
                        child.measure(childWidthMeasureSpec2, childHeightMeasureSpec);
                        if (constraintLayout.mMetrics != null) {
                            constraintLayout.mMetrics.measures++;
                        }
                        widget.setWidthWrapContent(width == -2 ? USE_CONSTRAINTS_HELPER : false);
                        widget.setHeightWrapContent(height == -2 ? USE_CONSTRAINTS_HELPER : false);
                        width = child.getMeasuredWidth();
                        height = child.getMeasuredHeight();
                    }
                    widget.setWidth(width);
                    widget.setHeight(height);
                    if (didWrapMeasureWidth) {
                        widget.setWrapWidth(width);
                    }
                    if (didWrapMeasureHeight) {
                        widget.setWrapHeight(height);
                    }
                    if (params.needsBaseline && (baseline = child.getBaseline()) != -1) {
                        widget.setBaselineDistance(baseline);
                    }
                }
            }
            i2++;
            constraintLayout = this;
            i = parentWidthSpec;
        }
    }

    private void updatePostMeasures() {
        int widgetsCount = getChildCount();
        for (int i = 0; i < widgetsCount; i++) {
            View child = getChildAt(i);
            if (child instanceof Placeholder) {
                ((Placeholder) child).updatePostMeasure(this);
            }
        }
        int helperCount = this.mConstraintHelpers.size();
        if (helperCount > 0) {
            for (int i2 = 0; i2 < helperCount; i2++) {
                ConstraintHelper helper = this.mConstraintHelpers.get(i2);
                helper.updatePostMeasure(this);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:117:0x0216  */
    /* JADX WARN: Removed duplicated region for block: B:128:0x0254  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x0275  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x0284  */
    /* JADX WARN: Removed duplicated region for block: B:141:0x028d  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x028f  */
    /* JADX WARN: Removed duplicated region for block: B:145:0x0295  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x0297  */
    /* JADX WARN: Removed duplicated region for block: B:149:0x02ab  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x02b0  */
    /* JADX WARN: Removed duplicated region for block: B:153:0x02b5  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x02bd  */
    /* JADX WARN: Removed duplicated region for block: B:156:0x02c6  */
    /* JADX WARN: Removed duplicated region for block: B:157:0x02ce  */
    /* JADX WARN: Removed duplicated region for block: B:160:0x02db  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x02e6 A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void internalMeasureDimensions(int r31, int r32) {
        /*
            Method dump skipped, instructions count: 778
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.ConstraintLayout.internalMeasureDimensions(int, int):void");
    }

    public void fillMetrics(Metrics metrics) {
        this.mMetrics = metrics;
        this.mLayoutWidget.fillMetrics(metrics);
    }

    /* JADX WARN: Removed duplicated region for block: B:190:0x039c  */
    /* JADX WARN: Removed duplicated region for block: B:193:0x03c8  */
    /* JADX WARN: Removed duplicated region for block: B:200:0x0403  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00b3  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x00c5  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x00db  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x00e2  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x00ec  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x00ee  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x00f1  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x00ff  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x010b  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x012d  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void onMeasure(int r53, int r54) {
        /*
            Method dump skipped, instructions count: 1039
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.ConstraintLayout.onMeasure(int, int):void");
    }

    private void setSelfDimensionBehaviour(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int widthPadding = getPaddingLeft() + getPaddingRight();
        ConstraintWidget.DimensionBehaviour widthBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
        ConstraintWidget.DimensionBehaviour heightBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
        int desiredWidth = 0;
        int desiredHeight = 0;
        getLayoutParams();
        if (widthMode == Integer.MIN_VALUE) {
            widthBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
            desiredWidth = widthSize;
        } else if (widthMode != 0) {
            if (widthMode == 1073741824) {
                desiredWidth = Math.min(this.mMaxWidth, widthSize) - widthPadding;
            }
        } else {
            widthBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        }
        if (heightMode == Integer.MIN_VALUE) {
            heightBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
            desiredHeight = heightSize;
        } else if (heightMode != 0) {
            if (heightMode == 1073741824) {
                desiredHeight = Math.min(this.mMaxHeight, heightSize) - heightPadding;
            }
        } else {
            heightBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        }
        this.mLayoutWidget.setMinWidth(0);
        this.mLayoutWidget.setMinHeight(0);
        this.mLayoutWidget.setHorizontalDimensionBehaviour(widthBehaviour);
        this.mLayoutWidget.setWidth(desiredWidth);
        this.mLayoutWidget.setVerticalDimensionBehaviour(heightBehaviour);
        this.mLayoutWidget.setHeight(desiredHeight);
        this.mLayoutWidget.setMinWidth((this.mMinWidth - getPaddingLeft()) - getPaddingRight());
        this.mLayoutWidget.setMinHeight((this.mMinHeight - getPaddingTop()) - getPaddingBottom());
    }

    protected void solveLinearSystem(String reason) {
        this.mLayoutWidget.layout();
        if (this.mMetrics != null) {
            this.mMetrics.resolutions++;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int widgetsCount = getChildCount();
        boolean isInEditMode = isInEditMode();
        for (int i = 0; i < widgetsCount; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            ConstraintWidget widget = params.widget;
            if ((child.getVisibility() != 8 || params.isGuideline || params.isHelper || isInEditMode) && !params.isInPlaceholder) {
                int l = widget.getDrawX();
                int t = widget.getDrawY();
                int r = widget.getWidth() + l;
                int b = widget.getHeight() + t;
                child.layout(l, t, r, b);
                if (child instanceof Placeholder) {
                    Placeholder holder = (Placeholder) child;
                    View content = holder.getContent();
                    if (content != null) {
                        content.setVisibility(0);
                        content.layout(l, t, r, b);
                    }
                }
            }
        }
        int helperCount = this.mConstraintHelpers.size();
        if (helperCount > 0) {
            for (int i2 = 0; i2 < helperCount; i2++) {
                ConstraintHelper helper = this.mConstraintHelpers.get(i2);
                helper.updatePostLayout(this);
            }
        }
    }

    public void setOptimizationLevel(int level) {
        this.mLayoutWidget.setOptimizationLevel(level);
    }

    public int getOptimizationLevel() {
        return this.mLayoutWidget.getOptimizationLevel();
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void setConstraintSet(ConstraintSet set) {
        this.mConstraintSet = set;
    }

    public View getViewById(int id) {
        return this.mChildrenByIds.get(id);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        int count;
        float cw;
        float ch;
        float ow;
        super.dispatchDraw(canvas);
        if (isInEditMode()) {
            int count2 = getChildCount();
            float cw2 = getWidth();
            float ch2 = getHeight();
            float ow2 = 1080.0f;
            char c = 0;
            int i = 0;
            while (i < count2) {
                View child = getChildAt(i);
                if (child.getVisibility() == 8) {
                    count = count2;
                    cw = cw2;
                    ch = ch2;
                    ow = ow2;
                } else {
                    Object tag = child.getTag();
                    if (tag != null && (tag instanceof String)) {
                        String coordinates = (String) tag;
                        String[] split = coordinates.split(",");
                        if (split.length == 4) {
                            int x = Integer.parseInt(split[c]);
                            int x2 = (int) ((x / ow2) * cw2);
                            int y = (int) ((Integer.parseInt(split[1]) / 1920.0f) * ch2);
                            int w = (int) ((Integer.parseInt(split[2]) / ow2) * cw2);
                            int h = (int) ((Integer.parseInt(split[3]) / 1920.0f) * ch2);
                            Paint paint = new Paint();
                            count = count2;
                            paint.setColor(SupportMenu.CATEGORY_MASK);
                            cw = cw2;
                            float cw3 = y;
                            ch = ch2;
                            ow = ow2;
                            float ow3 = y;
                            canvas.drawLine(x2, cw3, x2 + w, ow3, paint);
                            canvas.drawLine(x2 + w, y, x2 + w, y + h, paint);
                            canvas.drawLine(x2 + w, y + h, x2, y + h, paint);
                            canvas.drawLine(x2, y + h, x2, y, paint);
                            paint.setColor(-16711936);
                            canvas.drawLine(x2, y, x2 + w, y + h, paint);
                            canvas.drawLine(x2, y + h, x2 + w, y, paint);
                        }
                    }
                    count = count2;
                    cw = cw2;
                    ch = ch2;
                    ow = ow2;
                }
                i++;
                count2 = count;
                cw2 = cw;
                ch2 = ch;
                ow2 = ow;
                c = 0;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public static final int BASELINE = 5;
        public static final int BOTTOM = 4;
        public static final int CHAIN_PACKED = 2;
        public static final int CHAIN_SPREAD = 0;
        public static final int CHAIN_SPREAD_INSIDE = 1;
        public static final int END = 7;
        public static final int HORIZONTAL = 0;
        public static final int LEFT = 1;
        public static final int MATCH_CONSTRAINT = 0;
        public static final int MATCH_CONSTRAINT_PERCENT = 2;
        public static final int MATCH_CONSTRAINT_SPREAD = 0;
        public static final int MATCH_CONSTRAINT_WRAP = 1;
        public static final int PARENT_ID = 0;
        public static final int RIGHT = 2;
        public static final int START = 6;
        public static final int TOP = 3;
        public static final int UNSET = -1;
        public static final int VERTICAL = 1;
        public int baselineToBaseline;
        public int bottomToBottom;
        public int bottomToTop;
        public float circleAngle;
        public int circleConstraint;
        public int circleRadius;
        public boolean constrainedHeight;
        public boolean constrainedWidth;
        public String dimensionRatio;
        int dimensionRatioSide;
        float dimensionRatioValue;
        public int editorAbsoluteX;
        public int editorAbsoluteY;
        public int endToEnd;
        public int endToStart;
        public int goneBottomMargin;
        public int goneEndMargin;
        public int goneLeftMargin;
        public int goneRightMargin;
        public int goneStartMargin;
        public int goneTopMargin;
        public int guideBegin;
        public int guideEnd;
        public float guidePercent;
        public boolean helped;
        public float horizontalBias;
        public int horizontalChainStyle;
        boolean horizontalDimensionFixed;
        public float horizontalWeight;
        boolean isGuideline;
        boolean isHelper;
        boolean isInPlaceholder;
        public int leftToLeft;
        public int leftToRight;
        public int matchConstraintDefaultHeight;
        public int matchConstraintDefaultWidth;
        public int matchConstraintMaxHeight;
        public int matchConstraintMaxWidth;
        public int matchConstraintMinHeight;
        public int matchConstraintMinWidth;
        public float matchConstraintPercentHeight;
        public float matchConstraintPercentWidth;
        boolean needsBaseline;
        public int orientation;
        int resolveGoneLeftMargin;
        int resolveGoneRightMargin;
        int resolvedGuideBegin;
        int resolvedGuideEnd;
        float resolvedGuidePercent;
        float resolvedHorizontalBias;
        int resolvedLeftToLeft;
        int resolvedLeftToRight;
        int resolvedRightToLeft;
        int resolvedRightToRight;
        public int rightToLeft;
        public int rightToRight;
        public int startToEnd;
        public int startToStart;
        public int topToBottom;
        public int topToTop;
        public float verticalBias;
        public int verticalChainStyle;
        boolean verticalDimensionFixed;
        public float verticalWeight;
        ConstraintWidget widget;

        public void reset() {
            if (this.widget != null) {
                this.widget.reset();
            }
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.MarginLayoutParams) source);
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.circleConstraint = -1;
            this.circleRadius = 0;
            this.circleAngle = 0.0f;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioValue = 0.0f;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = 0.0f;
            this.verticalWeight = 0.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.constrainedWidth = false;
            this.constrainedHeight = false;
            this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
            this.guideBegin = source.guideBegin;
            this.guideEnd = source.guideEnd;
            this.guidePercent = source.guidePercent;
            this.leftToLeft = source.leftToLeft;
            this.leftToRight = source.leftToRight;
            this.rightToLeft = source.rightToLeft;
            this.rightToRight = source.rightToRight;
            this.topToTop = source.topToTop;
            this.topToBottom = source.topToBottom;
            this.bottomToTop = source.bottomToTop;
            this.bottomToBottom = source.bottomToBottom;
            this.baselineToBaseline = source.baselineToBaseline;
            this.circleConstraint = source.circleConstraint;
            this.circleRadius = source.circleRadius;
            this.circleAngle = source.circleAngle;
            this.startToEnd = source.startToEnd;
            this.startToStart = source.startToStart;
            this.endToStart = source.endToStart;
            this.endToEnd = source.endToEnd;
            this.goneLeftMargin = source.goneLeftMargin;
            this.goneTopMargin = source.goneTopMargin;
            this.goneRightMargin = source.goneRightMargin;
            this.goneBottomMargin = source.goneBottomMargin;
            this.goneStartMargin = source.goneStartMargin;
            this.goneEndMargin = source.goneEndMargin;
            this.horizontalBias = source.horizontalBias;
            this.verticalBias = source.verticalBias;
            this.dimensionRatio = source.dimensionRatio;
            this.dimensionRatioValue = source.dimensionRatioValue;
            this.dimensionRatioSide = source.dimensionRatioSide;
            this.horizontalWeight = source.horizontalWeight;
            this.verticalWeight = source.verticalWeight;
            this.horizontalChainStyle = source.horizontalChainStyle;
            this.verticalChainStyle = source.verticalChainStyle;
            this.constrainedWidth = source.constrainedWidth;
            this.constrainedHeight = source.constrainedHeight;
            this.matchConstraintDefaultWidth = source.matchConstraintDefaultWidth;
            this.matchConstraintDefaultHeight = source.matchConstraintDefaultHeight;
            this.matchConstraintMinWidth = source.matchConstraintMinWidth;
            this.matchConstraintMaxWidth = source.matchConstraintMaxWidth;
            this.matchConstraintMinHeight = source.matchConstraintMinHeight;
            this.matchConstraintMaxHeight = source.matchConstraintMaxHeight;
            this.matchConstraintPercentWidth = source.matchConstraintPercentWidth;
            this.matchConstraintPercentHeight = source.matchConstraintPercentHeight;
            this.editorAbsoluteX = source.editorAbsoluteX;
            this.editorAbsoluteY = source.editorAbsoluteY;
            this.orientation = source.orientation;
            this.horizontalDimensionFixed = source.horizontalDimensionFixed;
            this.verticalDimensionFixed = source.verticalDimensionFixed;
            this.needsBaseline = source.needsBaseline;
            this.isGuideline = source.isGuideline;
            this.resolvedLeftToLeft = source.resolvedLeftToLeft;
            this.resolvedLeftToRight = source.resolvedLeftToRight;
            this.resolvedRightToLeft = source.resolvedRightToLeft;
            this.resolvedRightToRight = source.resolvedRightToRight;
            this.resolveGoneLeftMargin = source.resolveGoneLeftMargin;
            this.resolveGoneRightMargin = source.resolveGoneRightMargin;
            this.resolvedHorizontalBias = source.resolvedHorizontalBias;
            this.widget = source.widget;
        }

        /* loaded from: classes.dex */
        private static class Table {
            public static final int ANDROID_ORIENTATION = 1;
            public static final int LAYOUT_CONSTRAINED_HEIGHT = 28;
            public static final int LAYOUT_CONSTRAINED_WIDTH = 27;
            public static final int LAYOUT_CONSTRAINT_BASELINE_CREATOR = 43;
            public static final int LAYOUT_CONSTRAINT_BASELINE_TO_BASELINE_OF = 16;
            public static final int LAYOUT_CONSTRAINT_BOTTOM_CREATOR = 42;
            public static final int LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF = 15;
            public static final int LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF = 14;
            public static final int LAYOUT_CONSTRAINT_CIRCLE = 2;
            public static final int LAYOUT_CONSTRAINT_CIRCLE_ANGLE = 4;
            public static final int LAYOUT_CONSTRAINT_CIRCLE_RADIUS = 3;
            public static final int LAYOUT_CONSTRAINT_DIMENSION_RATIO = 44;
            public static final int LAYOUT_CONSTRAINT_END_TO_END_OF = 20;
            public static final int LAYOUT_CONSTRAINT_END_TO_START_OF = 19;
            public static final int LAYOUT_CONSTRAINT_GUIDE_BEGIN = 5;
            public static final int LAYOUT_CONSTRAINT_GUIDE_END = 6;
            public static final int LAYOUT_CONSTRAINT_GUIDE_PERCENT = 7;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_DEFAULT = 32;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_MAX = 37;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_MIN = 36;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_PERCENT = 38;
            public static final int LAYOUT_CONSTRAINT_HORIZONTAL_BIAS = 29;
            public static final int LAYOUT_CONSTRAINT_HORIZONTAL_CHAINSTYLE = 47;
            public static final int LAYOUT_CONSTRAINT_HORIZONTAL_WEIGHT = 45;
            public static final int LAYOUT_CONSTRAINT_LEFT_CREATOR = 39;
            public static final int LAYOUT_CONSTRAINT_LEFT_TO_LEFT_OF = 8;
            public static final int LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF = 9;
            public static final int LAYOUT_CONSTRAINT_RIGHT_CREATOR = 41;
            public static final int LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF = 10;
            public static final int LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF = 11;
            public static final int LAYOUT_CONSTRAINT_START_TO_END_OF = 17;
            public static final int LAYOUT_CONSTRAINT_START_TO_START_OF = 18;
            public static final int LAYOUT_CONSTRAINT_TOP_CREATOR = 40;
            public static final int LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF = 13;
            public static final int LAYOUT_CONSTRAINT_TOP_TO_TOP_OF = 12;
            public static final int LAYOUT_CONSTRAINT_VERTICAL_BIAS = 30;
            public static final int LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE = 48;
            public static final int LAYOUT_CONSTRAINT_VERTICAL_WEIGHT = 46;
            public static final int LAYOUT_CONSTRAINT_WIDTH_DEFAULT = 31;
            public static final int LAYOUT_CONSTRAINT_WIDTH_MAX = 34;
            public static final int LAYOUT_CONSTRAINT_WIDTH_MIN = 33;
            public static final int LAYOUT_CONSTRAINT_WIDTH_PERCENT = 35;
            public static final int LAYOUT_EDITOR_ABSOLUTEX = 49;
            public static final int LAYOUT_EDITOR_ABSOLUTEY = 50;
            public static final int LAYOUT_GONE_MARGIN_BOTTOM = 24;
            public static final int LAYOUT_GONE_MARGIN_END = 26;
            public static final int LAYOUT_GONE_MARGIN_LEFT = 21;
            public static final int LAYOUT_GONE_MARGIN_RIGHT = 23;
            public static final int LAYOUT_GONE_MARGIN_START = 25;
            public static final int LAYOUT_GONE_MARGIN_TOP = 22;
            public static final int UNUSED = 0;
            public static final SparseIntArray map = new SparseIntArray();

            private Table() {
            }

            static {
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toLeftOf, 8);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toRightOf, 9);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_toLeftOf, 10);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_toRightOf, 11);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_toTopOf, 12);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_toBottomOf, 13);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toTopOf, 14);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toBottomOf, 15);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_toBaselineOf, 16);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircle, 2);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircleRadius, 3);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircleAngle, 4);
                map.append(R.styleable.ConstraintLayout_Layout_layout_editor_absoluteX, 49);
                map.append(R.styleable.ConstraintLayout_Layout_layout_editor_absoluteY, 50);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_begin, 5);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_end, 6);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_percent, 7);
                map.append(R.styleable.ConstraintLayout_Layout_android_orientation, 1);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintStart_toEndOf, 17);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintStart_toStartOf, 18);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toStartOf, 19);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toEndOf, 20);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginLeft, 21);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginTop, 22);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginRight, 23);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginBottom, 24);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginStart, 25);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginEnd, 26);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_bias, 29);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_bias, 30);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintDimensionRatio, 44);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_weight, 45);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_weight, 46);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_chainStyle, 47);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_chainStyle, 48);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constrainedWidth, 27);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constrainedHeight, 28);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_default, 31);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_default, 32);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_min, 33);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_max, 34);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_percent, 35);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_min, 36);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_max, 37);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_percent, 38);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_creator, 39);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_creator, 40);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_creator, 41);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_creator, 42);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_creator, 43);
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            int commaIndex;
            int i = -1;
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.circleConstraint = -1;
            int i2 = 0;
            this.circleRadius = 0;
            this.circleAngle = 0.0f;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioValue = 0.0f;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = 0.0f;
            this.verticalWeight = 0.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.constrainedWidth = false;
            this.constrainedHeight = false;
            this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            int N = a.getIndexCount();
            int i3 = 0;
            while (true) {
                int i4 = i3;
                if (i4 < N) {
                    int attr = a.getIndex(i4);
                    int look = Table.map.get(attr);
                    switch (look) {
                        case 0:
                            continue;
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 1:
                            this.orientation = a.getInt(attr, this.orientation);
                            continue;
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 2:
                            this.circleConstraint = a.getResourceId(attr, this.circleConstraint);
                            if (this.circleConstraint == -1) {
                                this.circleConstraint = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 3:
                            this.circleRadius = a.getDimensionPixelSize(attr, this.circleRadius);
                            break;
                        case 4:
                            this.circleAngle = a.getFloat(attr, this.circleAngle) % 360.0f;
                            if (this.circleAngle < 0.0f) {
                                this.circleAngle = (360.0f - this.circleAngle) % 360.0f;
                                break;
                            }
                            break;
                        case 5:
                            this.guideBegin = a.getDimensionPixelOffset(attr, this.guideBegin);
                            break;
                        case 6:
                            this.guideEnd = a.getDimensionPixelOffset(attr, this.guideEnd);
                            break;
                        case 7:
                            this.guidePercent = a.getFloat(attr, this.guidePercent);
                            break;
                        case 8:
                            this.leftToLeft = a.getResourceId(attr, this.leftToLeft);
                            if (this.leftToLeft == -1) {
                                this.leftToLeft = a.getInt(attr, -1);
                                break;
                            } else {
                                continue;
                                i3 = i4 + 1;
                                i = -1;
                                i2 = 0;
                            }
                        case 9:
                            this.leftToRight = a.getResourceId(attr, this.leftToRight);
                            if (this.leftToRight == -1) {
                                this.leftToRight = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 10:
                            this.rightToLeft = a.getResourceId(attr, this.rightToLeft);
                            if (this.rightToLeft == -1) {
                                this.rightToLeft = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 11:
                            this.rightToRight = a.getResourceId(attr, this.rightToRight);
                            if (this.rightToRight == -1) {
                                this.rightToRight = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 12:
                            this.topToTop = a.getResourceId(attr, this.topToTop);
                            if (this.topToTop == -1) {
                                this.topToTop = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 13:
                            this.topToBottom = a.getResourceId(attr, this.topToBottom);
                            if (this.topToBottom == -1) {
                                this.topToBottom = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 14:
                            this.bottomToTop = a.getResourceId(attr, this.bottomToTop);
                            if (this.bottomToTop == -1) {
                                this.bottomToTop = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 15:
                            this.bottomToBottom = a.getResourceId(attr, this.bottomToBottom);
                            if (this.bottomToBottom == -1) {
                                this.bottomToBottom = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 16:
                            this.baselineToBaseline = a.getResourceId(attr, this.baselineToBaseline);
                            if (this.baselineToBaseline == -1) {
                                this.baselineToBaseline = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 17:
                            this.startToEnd = a.getResourceId(attr, this.startToEnd);
                            if (this.startToEnd == -1) {
                                this.startToEnd = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 18:
                            this.startToStart = a.getResourceId(attr, this.startToStart);
                            if (this.startToStart == -1) {
                                this.startToStart = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 19:
                            this.endToStart = a.getResourceId(attr, this.endToStart);
                            if (this.endToStart == -1) {
                                this.endToStart = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 20:
                            this.endToEnd = a.getResourceId(attr, this.endToEnd);
                            if (this.endToEnd == -1) {
                                this.endToEnd = a.getInt(attr, -1);
                            } else {
                                continue;
                            }
                            i3 = i4 + 1;
                            i = -1;
                            i2 = 0;
                        case 21:
                            this.goneLeftMargin = a.getDimensionPixelSize(attr, this.goneLeftMargin);
                            break;
                        case 22:
                            this.goneTopMargin = a.getDimensionPixelSize(attr, this.goneTopMargin);
                            break;
                        case 23:
                            this.goneRightMargin = a.getDimensionPixelSize(attr, this.goneRightMargin);
                            break;
                        case 24:
                            this.goneBottomMargin = a.getDimensionPixelSize(attr, this.goneBottomMargin);
                            break;
                        case 25:
                            this.goneStartMargin = a.getDimensionPixelSize(attr, this.goneStartMargin);
                            break;
                        case 26:
                            this.goneEndMargin = a.getDimensionPixelSize(attr, this.goneEndMargin);
                            break;
                        case 27:
                            this.constrainedWidth = a.getBoolean(attr, this.constrainedWidth);
                            break;
                        case 28:
                            this.constrainedHeight = a.getBoolean(attr, this.constrainedHeight);
                            break;
                        case 29:
                            this.horizontalBias = a.getFloat(attr, this.horizontalBias);
                            break;
                        case 30:
                            this.verticalBias = a.getFloat(attr, this.verticalBias);
                            break;
                        case 31:
                            this.matchConstraintDefaultWidth = a.getInt(attr, 0);
                            if (this.matchConstraintDefaultWidth == 1) {
                                Log.e(ConstraintLayout.TAG, "layout_constraintWidth_default=\"wrap\" is deprecated.\nUse layout_width=\"WRAP_CONTENT\" and layout_constrainedWidth=\"true\" instead.");
                                break;
                            }
                            break;
                        case 32:
                            this.matchConstraintDefaultHeight = a.getInt(attr, 0);
                            if (this.matchConstraintDefaultHeight == 1) {
                                Log.e(ConstraintLayout.TAG, "layout_constraintHeight_default=\"wrap\" is deprecated.\nUse layout_height=\"WRAP_CONTENT\" and layout_constrainedHeight=\"true\" instead.");
                            }
                            break;
                        case 33:
                            try {
                                this.matchConstraintMinWidth = a.getDimensionPixelSize(attr, this.matchConstraintMinWidth);
                            } catch (Exception e) {
                                int value = a.getInt(attr, this.matchConstraintMinWidth);
                                if (value == -2) {
                                    this.matchConstraintMinWidth = -2;
                                }
                            }
                            break;
                        case 34:
                            try {
                                this.matchConstraintMaxWidth = a.getDimensionPixelSize(attr, this.matchConstraintMaxWidth);
                            } catch (Exception e2) {
                                int value2 = a.getInt(attr, this.matchConstraintMaxWidth);
                                if (value2 == -2) {
                                    this.matchConstraintMaxWidth = -2;
                                }
                            }
                            break;
                        case 35:
                            this.matchConstraintPercentWidth = Math.max(0.0f, a.getFloat(attr, this.matchConstraintPercentWidth));
                            break;
                        case 36:
                            try {
                                this.matchConstraintMinHeight = a.getDimensionPixelSize(attr, this.matchConstraintMinHeight);
                            } catch (Exception e3) {
                                int value3 = a.getInt(attr, this.matchConstraintMinHeight);
                                if (value3 == -2) {
                                    this.matchConstraintMinHeight = -2;
                                }
                            }
                            break;
                        case 37:
                            try {
                                this.matchConstraintMaxHeight = a.getDimensionPixelSize(attr, this.matchConstraintMaxHeight);
                            } catch (Exception e4) {
                                int value4 = a.getInt(attr, this.matchConstraintMaxHeight);
                                if (value4 == -2) {
                                    this.matchConstraintMaxHeight = -2;
                                }
                            }
                            break;
                        case 38:
                            this.matchConstraintPercentHeight = Math.max(0.0f, a.getFloat(attr, this.matchConstraintPercentHeight));
                            break;
                        case 44:
                            this.dimensionRatio = a.getString(attr);
                            this.dimensionRatioValue = Float.NaN;
                            this.dimensionRatioSide = i;
                            if (this.dimensionRatio != null) {
                                int len = this.dimensionRatio.length();
                                int commaIndex2 = this.dimensionRatio.indexOf(44);
                                if (commaIndex2 > 0 && commaIndex2 < len - 1) {
                                    String dimension = this.dimensionRatio.substring(i2, commaIndex2);
                                    if (dimension.equalsIgnoreCase("W")) {
                                        this.dimensionRatioSide = i2;
                                    } else if (dimension.equalsIgnoreCase("H")) {
                                        this.dimensionRatioSide = 1;
                                    }
                                    commaIndex = commaIndex2 + 1;
                                } else {
                                    commaIndex = 0;
                                }
                                int commaIndex3 = commaIndex;
                                int colonIndex = this.dimensionRatio.indexOf(58);
                                if (colonIndex >= 0 && colonIndex < len - 1) {
                                    String nominator = this.dimensionRatio.substring(commaIndex3, colonIndex);
                                    String denominator = this.dimensionRatio.substring(colonIndex + 1);
                                    if (nominator.length() > 0 && denominator.length() > 0) {
                                        try {
                                            float nominatorValue = Float.parseFloat(nominator);
                                            float denominatorValue = Float.parseFloat(denominator);
                                            if (nominatorValue > 0.0f && denominatorValue > 0.0f) {
                                                if (this.dimensionRatioSide == 1) {
                                                    this.dimensionRatioValue = Math.abs(denominatorValue / nominatorValue);
                                                } else {
                                                    this.dimensionRatioValue = Math.abs(nominatorValue / denominatorValue);
                                                }
                                            }
                                        } catch (NumberFormatException e5) {
                                        }
                                    }
                                } else {
                                    String r = this.dimensionRatio.substring(commaIndex3);
                                    if (r.length() > 0) {
                                        try {
                                            this.dimensionRatioValue = Float.parseFloat(r);
                                        } catch (NumberFormatException e6) {
                                        }
                                    }
                                }
                            }
                            break;
                        case 45:
                            this.horizontalWeight = a.getFloat(attr, 0.0f);
                            break;
                        case 46:
                            this.verticalWeight = a.getFloat(attr, 0.0f);
                            break;
                        case 47:
                            this.horizontalChainStyle = a.getInt(attr, i2);
                            break;
                        case 48:
                            this.verticalChainStyle = a.getInt(attr, i2);
                            break;
                        case 49:
                            this.editorAbsoluteX = a.getDimensionPixelOffset(attr, this.editorAbsoluteX);
                            break;
                        case 50:
                            this.editorAbsoluteY = a.getDimensionPixelOffset(attr, this.editorAbsoluteY);
                            break;
                    }
                    i3 = i4 + 1;
                    i = -1;
                    i2 = 0;
                } else {
                    a.recycle();
                    validate();
                    return;
                }
            }
        }

        public void validate() {
            this.isGuideline = false;
            this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            if (this.width == -2 && this.constrainedWidth) {
                this.horizontalDimensionFixed = false;
                this.matchConstraintDefaultWidth = 1;
            }
            if (this.height == -2 && this.constrainedHeight) {
                this.verticalDimensionFixed = false;
                this.matchConstraintDefaultHeight = 1;
            }
            if (this.width == 0 || this.width == -1) {
                this.horizontalDimensionFixed = false;
                if (this.width == 0 && this.matchConstraintDefaultWidth == 1) {
                    this.width = -2;
                    this.constrainedWidth = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
            }
            if (this.height == 0 || this.height == -1) {
                this.verticalDimensionFixed = false;
                if (this.height == 0 && this.matchConstraintDefaultHeight == 1) {
                    this.height = -2;
                    this.constrainedHeight = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
            }
            if (this.guidePercent != -1.0f || this.guideBegin != -1 || this.guideEnd != -1) {
                this.isGuideline = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                if (!(this.widget instanceof android.support.constraint.solver.widgets.Guideline)) {
                    this.widget = new android.support.constraint.solver.widgets.Guideline();
                }
                ((android.support.constraint.solver.widgets.Guideline) this.widget).setOrientation(this.orientation);
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.circleConstraint = -1;
            this.circleRadius = 0;
            this.circleAngle = 0.0f;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioValue = 0.0f;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = 0.0f;
            this.verticalWeight = 0.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.constrainedWidth = false;
            this.constrainedHeight = false;
            this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.circleConstraint = -1;
            this.circleRadius = 0;
            this.circleAngle = 0.0f;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioValue = 0.0f;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = 0.0f;
            this.verticalWeight = 0.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.constrainedWidth = false;
            this.constrainedHeight = false;
            this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
        }

        @Override // android.view.ViewGroup.MarginLayoutParams, android.view.ViewGroup.LayoutParams
        @TargetApi(17)
        public void resolveLayoutDirection(int layoutDirection) {
            int preLeftMargin = this.leftMargin;
            int preRightMargin = this.rightMargin;
            super.resolveLayoutDirection(layoutDirection);
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolveGoneLeftMargin = this.goneLeftMargin;
            this.resolveGoneRightMargin = this.goneRightMargin;
            this.resolvedHorizontalBias = this.horizontalBias;
            this.resolvedGuideBegin = this.guideBegin;
            this.resolvedGuideEnd = this.guideEnd;
            this.resolvedGuidePercent = this.guidePercent;
            boolean isRtl = 1 == getLayoutDirection() ? ConstraintLayout.USE_CONSTRAINTS_HELPER : false;
            if (!isRtl) {
                if (this.startToEnd != -1) {
                    this.resolvedLeftToRight = this.startToEnd;
                }
                if (this.startToStart != -1) {
                    this.resolvedLeftToLeft = this.startToStart;
                }
                if (this.endToStart != -1) {
                    this.resolvedRightToLeft = this.endToStart;
                }
                if (this.endToEnd != -1) {
                    this.resolvedRightToRight = this.endToEnd;
                }
                if (this.goneStartMargin != -1) {
                    this.resolveGoneLeftMargin = this.goneStartMargin;
                }
                if (this.goneEndMargin != -1) {
                    this.resolveGoneRightMargin = this.goneEndMargin;
                }
            } else {
                boolean startEndDefined = false;
                if (this.startToEnd == -1) {
                    if (this.startToStart != -1) {
                        this.resolvedRightToRight = this.startToStart;
                        startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                    }
                } else {
                    this.resolvedRightToLeft = this.startToEnd;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
                if (this.endToStart != -1) {
                    this.resolvedLeftToRight = this.endToStart;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
                if (this.endToEnd != -1) {
                    this.resolvedLeftToLeft = this.endToEnd;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
                if (this.goneStartMargin != -1) {
                    this.resolveGoneRightMargin = this.goneStartMargin;
                }
                if (this.goneEndMargin != -1) {
                    this.resolveGoneLeftMargin = this.goneEndMargin;
                }
                if (startEndDefined) {
                    this.resolvedHorizontalBias = 1.0f - this.horizontalBias;
                }
                if (this.isGuideline && this.orientation == 1) {
                    if (this.guidePercent == -1.0f) {
                        if (this.guideBegin == -1) {
                            if (this.guideEnd != -1) {
                                this.resolvedGuideBegin = this.guideEnd;
                                this.resolvedGuideEnd = -1;
                                this.resolvedGuidePercent = -1.0f;
                            }
                        } else {
                            this.resolvedGuideEnd = this.guideBegin;
                            this.resolvedGuideBegin = -1;
                            this.resolvedGuidePercent = -1.0f;
                        }
                    } else {
                        this.resolvedGuidePercent = 1.0f - this.guidePercent;
                        this.resolvedGuideBegin = -1;
                        this.resolvedGuideEnd = -1;
                    }
                }
            }
            if (this.endToStart == -1 && this.endToEnd == -1 && this.startToStart == -1 && this.startToEnd == -1) {
                if (this.rightToLeft == -1) {
                    if (this.rightToRight != -1) {
                        this.resolvedRightToRight = this.rightToRight;
                        if (this.rightMargin <= 0 && preRightMargin > 0) {
                            this.rightMargin = preRightMargin;
                        }
                    }
                } else {
                    this.resolvedRightToLeft = this.rightToLeft;
                    if (this.rightMargin <= 0 && preRightMargin > 0) {
                        this.rightMargin = preRightMargin;
                    }
                }
                if (this.leftToLeft == -1) {
                    if (this.leftToRight != -1) {
                        this.resolvedLeftToRight = this.leftToRight;
                        if (this.leftMargin <= 0 && preLeftMargin > 0) {
                            this.leftMargin = preLeftMargin;
                            return;
                        }
                        return;
                    }
                    return;
                }
                this.resolvedLeftToLeft = this.leftToLeft;
                if (this.leftMargin <= 0 && preLeftMargin > 0) {
                    this.leftMargin = preLeftMargin;
                }
            }
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        super.requestLayout();
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
        this.mLastMeasureWidth = -1;
        this.mLastMeasureHeight = -1;
        this.mLastMeasureWidthSize = -1;
        this.mLastMeasureHeightSize = -1;
        this.mLastMeasureWidthMode = 0;
        this.mLastMeasureHeightMode = 0;
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }
}
