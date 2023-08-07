package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

/* loaded from: classes.dex */
public class ActionMenuView extends LinearLayoutCompat implements MenuBuilder.ItemInvoker, MenuView {
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "ActionMenuView";
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private MenuBuilder mMenu;
    MenuBuilder.Callback mMenuBuilderCallback;
    private int mMinCellSize;
    OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* loaded from: classes.dex */
    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    /* loaded from: classes.dex */
    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBaselineAligned(false);
        float density = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int) (56.0f * density);
        this.mGeneratedItemPadding = (int) (4.0f * density);
        this.mPopupContext = context;
        this.mPopupTheme = 0;
    }

    public void setPopupTheme(@StyleRes int resId) {
        if (this.mPopupTheme != resId) {
            this.mPopupTheme = resId;
            if (resId == 0) {
                this.mPopupContext = getContext();
            } else {
                this.mPopupContext = new ContextThemeWrapper(getContext(), resId);
            }
        }
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setPresenter(ActionMenuPresenter presenter) {
        this.mPresenter = presenter;
        this.mPresenter.setMenuView(this);
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.mPresenter != null) {
            this.mPresenter.updateMenuView(false);
            if (this.mPresenter.isOverflowMenuShowing()) {
                this.mPresenter.hideOverflowMenu();
                this.mPresenter.showOverflowMenu();
            }
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.mOnMenuItemClickListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean wasFormatted = this.mFormatItems;
        this.mFormatItems = View.MeasureSpec.getMode(widthMeasureSpec) == 1073741824;
        if (wasFormatted != this.mFormatItems) {
            this.mFormatItemsWidth = 0;
        }
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        if (this.mFormatItems && this.mMenu != null && widthSize != this.mFormatItemsWidth) {
            this.mFormatItemsWidth = widthSize;
            this.mMenu.onItemsChanged(true);
        }
        int childCount = getChildCount();
        if (this.mFormatItems && childCount > 0) {
            onMeasureExactFormat(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.rightMargin = 0;
            lp.leftMargin = 0;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /* JADX WARN: Type inference failed for: r13v4 */
    /* JADX WARN: Type inference failed for: r13v5, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r13v9 */
    private void onMeasureExactFormat(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize;
        boolean needsExpansion;
        int i;
        int cellSizeRemaining;
        int visibleItemCount;
        ?? r13;
        int heightPadding;
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize2 = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightPadding2 = getPaddingTop() + getPaddingBottom();
        int itemHeightSpec = getChildMeasureSpec(heightMeasureSpec, heightPadding2, -2);
        int widthSize3 = widthSize2 - widthPadding;
        int cellCount = widthSize3 / this.mMinCellSize;
        int cellSizeRemaining2 = widthSize3 % this.mMinCellSize;
        if (cellCount == 0) {
            setMeasuredDimension(widthSize3, 0);
            return;
        }
        int cellSize = this.mMinCellSize + (cellSizeRemaining2 / cellCount);
        boolean hasOverflow = false;
        long smallestItemsAt = 0;
        int childCount = getChildCount();
        int heightSize2 = 0;
        int maxChildHeight = 0;
        int visibleItemCount2 = 0;
        int expandableItemCount = 0;
        int maxCellsUsed = cellCount;
        int cellsRemaining = 0;
        while (true) {
            int widthPadding2 = widthPadding;
            if (cellsRemaining >= childCount) {
                break;
            }
            View child = getChildAt(cellsRemaining);
            int cellCount2 = cellCount;
            if (child.getVisibility() == 8) {
                heightPadding = heightPadding2;
                cellSizeRemaining = cellSizeRemaining2;
            } else {
                boolean isGeneratedItem = child instanceof ActionMenuItemView;
                int visibleItemCount3 = maxChildHeight + 1;
                if (isGeneratedItem) {
                    cellSizeRemaining = cellSizeRemaining2;
                    visibleItemCount = visibleItemCount3;
                    r13 = 0;
                    child.setPadding(this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0);
                } else {
                    cellSizeRemaining = cellSizeRemaining2;
                    visibleItemCount = visibleItemCount3;
                    r13 = 0;
                }
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                lp.expanded = r13;
                lp.extraPixels = r13;
                lp.cellsUsed = r13;
                lp.expandable = r13;
                lp.leftMargin = r13;
                lp.rightMargin = r13;
                lp.preventEdgeOffset = isGeneratedItem && ((ActionMenuItemView) child).hasText();
                int cellsAvailable = lp.isOverflowButton ? 1 : maxCellsUsed;
                int cellsUsed = measureChildForCells(child, cellSize, cellsAvailable, itemHeightSpec, heightPadding2);
                expandableItemCount = Math.max(expandableItemCount, cellsUsed);
                heightPadding = heightPadding2;
                if (lp.expandable) {
                    visibleItemCount2++;
                }
                if (lp.isOverflowButton) {
                    hasOverflow = true;
                }
                maxCellsUsed -= cellsUsed;
                heightSize2 = Math.max(heightSize2, child.getMeasuredHeight());
                if (cellsUsed == 1) {
                    smallestItemsAt |= 1 << cellsRemaining;
                    maxChildHeight = visibleItemCount;
                    heightSize2 = heightSize2;
                } else {
                    maxChildHeight = visibleItemCount;
                }
            }
            cellsRemaining++;
            widthPadding = widthPadding2;
            cellCount = cellCount2;
            cellSizeRemaining2 = cellSizeRemaining;
            heightPadding2 = heightPadding;
        }
        boolean centerSingleExpandedItem = hasOverflow && maxChildHeight == 2;
        boolean needsExpansion2 = false;
        while (visibleItemCount2 > 0 && maxCellsUsed > 0) {
            long minCellsAt = 0;
            int minCells = Integer.MAX_VALUE;
            int minCellsItemCount = 0;
            int minCells2 = 0;
            while (true) {
                int i2 = minCells2;
                if (i2 >= childCount) {
                    break;
                }
                boolean needsExpansion3 = needsExpansion2;
                LayoutParams lp2 = (LayoutParams) getChildAt(i2).getLayoutParams();
                if (lp2.expandable) {
                    if (lp2.cellsUsed < minCells) {
                        minCells = lp2.cellsUsed;
                        minCellsAt = 1 << i2;
                        minCellsItemCount = 1;
                    } else if (lp2.cellsUsed == minCells) {
                        minCellsAt |= 1 << i2;
                        minCellsItemCount++;
                    }
                }
                minCells2 = i2 + 1;
                needsExpansion2 = needsExpansion3;
            }
            needsExpansion = needsExpansion2;
            smallestItemsAt |= minCellsAt;
            if (minCellsItemCount > maxCellsUsed) {
                widthSize = widthSize3;
                break;
            }
            int minCells3 = minCells + 1;
            int i3 = 0;
            while (i3 < childCount) {
                View child2 = getChildAt(i3);
                LayoutParams lp3 = (LayoutParams) child2.getLayoutParams();
                int widthSize4 = widthSize3;
                int minCellsItemCount2 = minCellsItemCount;
                int widthSize5 = 1 << i3;
                boolean centerSingleExpandedItem2 = centerSingleExpandedItem;
                if ((minCellsAt & widthSize5) != 0) {
                    if (centerSingleExpandedItem2 && lp3.preventEdgeOffset && maxCellsUsed == 1) {
                        child2.setPadding(this.mGeneratedItemPadding + cellSize, 0, this.mGeneratedItemPadding, 0);
                    }
                    lp3.cellsUsed++;
                    lp3.expanded = true;
                    maxCellsUsed--;
                } else if (lp3.cellsUsed == minCells3) {
                    smallestItemsAt |= 1 << i3;
                }
                i3++;
                minCellsItemCount = minCellsItemCount2;
                widthSize3 = widthSize4;
                centerSingleExpandedItem = centerSingleExpandedItem2;
            }
            needsExpansion2 = true;
        }
        widthSize = widthSize3;
        needsExpansion = needsExpansion2;
        boolean singleItem = !hasOverflow && maxChildHeight == 1;
        if (maxCellsUsed > 0 && smallestItemsAt != 0 && (maxCellsUsed < maxChildHeight - 1 || singleItem || expandableItemCount > 1)) {
            float expandCount = Long.bitCount(smallestItemsAt);
            if (!singleItem) {
                if ((smallestItemsAt & 1) != 0 && !((LayoutParams) getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                    expandCount -= 0.5f;
                }
                if ((smallestItemsAt & (1 << (childCount - 1))) != 0 && !((LayoutParams) getChildAt(childCount - 1).getLayoutParams()).preventEdgeOffset) {
                    expandCount -= 0.5f;
                }
            }
            int extraPixels = expandCount > 0.0f ? (int) ((maxCellsUsed * cellSize) / expandCount) : 0;
            for (int i4 = 0; i4 < childCount; i4 = i + 1) {
                int i5 = i4;
                if ((smallestItemsAt & (1 << i4)) == 0) {
                    i = i5;
                } else {
                    i = i5;
                    View child3 = getChildAt(i);
                    LayoutParams lp4 = (LayoutParams) child3.getLayoutParams();
                    if (child3 instanceof ActionMenuItemView) {
                        lp4.extraPixels = extraPixels;
                        lp4.expanded = true;
                        if (i == 0 && !lp4.preventEdgeOffset) {
                            lp4.leftMargin = (-extraPixels) / 2;
                        }
                        needsExpansion = true;
                    } else {
                        if (lp4.isOverflowButton) {
                            lp4.extraPixels = extraPixels;
                            lp4.expanded = true;
                            lp4.rightMargin = (-extraPixels) / 2;
                            needsExpansion = true;
                        } else {
                            if (i != 0) {
                                lp4.leftMargin = extraPixels / 2;
                            }
                            if (i != childCount - 1) {
                                lp4.rightMargin = extraPixels / 2;
                            }
                        }
                    }
                }
            }
        }
        if (needsExpansion) {
            int i6 = 0;
            while (true) {
                int i7 = i6;
                if (i7 >= childCount) {
                    break;
                }
                View child4 = getChildAt(i7);
                LayoutParams lp5 = (LayoutParams) child4.getLayoutParams();
                if (lp5.expanded) {
                    int width = (lp5.cellsUsed * cellSize) + lp5.extraPixels;
                    child4.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), itemHeightSpec);
                }
                i6 = i7 + 1;
            }
        }
        setMeasuredDimension(widthSize, heightMode != 1073741824 ? heightSize2 : heightSize);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int measureChildForCells(View child, int cellSize, int cellsRemaining, int parentHeightMeasureSpec, int parentHeightPadding) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int childHeightSize = View.MeasureSpec.getSize(parentHeightMeasureSpec) - parentHeightPadding;
        int childHeightMode = View.MeasureSpec.getMode(parentHeightMeasureSpec);
        int childHeightSpec = View.MeasureSpec.makeMeasureSpec(childHeightSize, childHeightMode);
        ActionMenuItemView itemView = child instanceof ActionMenuItemView ? (ActionMenuItemView) child : null;
        boolean expandable = false;
        boolean hasText = itemView != null && itemView.hasText();
        int cellsUsed = 0;
        if (cellsRemaining > 0 && (!hasText || cellsRemaining >= 2)) {
            int childWidthSpec = View.MeasureSpec.makeMeasureSpec(cellSize * cellsRemaining, Integer.MIN_VALUE);
            child.measure(childWidthSpec, childHeightSpec);
            int measuredWidth = child.getMeasuredWidth();
            cellsUsed = measuredWidth / cellSize;
            if (measuredWidth % cellSize != 0) {
                cellsUsed++;
            }
            if (hasText && cellsUsed < 2) {
                cellsUsed = 2;
            }
        }
        if (!lp.isOverflowButton && hasText) {
            expandable = true;
        }
        lp.expandable = expandable;
        lp.cellsUsed = cellsUsed;
        int targetWidth = cellsUsed * cellSize;
        child.measure(View.MeasureSpec.makeMeasureSpec(targetWidth, 1073741824), childHeightSpec);
        return cellsUsed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int dividerWidth;
        int overflowWidth;
        int midVertical;
        boolean isLayoutRtl;
        int r;
        int l;
        if (!this.mFormatItems) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        int childCount = getChildCount();
        int midVertical2 = (bottom - top) / 2;
        int dividerWidth2 = getDividerWidth();
        int nonOverflowCount = 0;
        int widthRemaining = ((right - left) - getPaddingRight()) - getPaddingLeft();
        boolean hasOverflow = false;
        boolean isLayoutRtl2 = ViewUtils.isLayoutRtl(this);
        int widthRemaining2 = widthRemaining;
        int widthRemaining3 = 0;
        int nonOverflowWidth = 0;
        int overflowWidth2 = 0;
        while (overflowWidth2 < childCount) {
            View v = getChildAt(overflowWidth2);
            if (v.getVisibility() == 8) {
                midVertical = midVertical2;
                isLayoutRtl = isLayoutRtl2;
            } else {
                LayoutParams p = (LayoutParams) v.getLayoutParams();
                if (p.isOverflowButton) {
                    int overflowWidth3 = v.getMeasuredWidth();
                    if (hasSupportDividerBeforeChildAt(overflowWidth2)) {
                        overflowWidth3 += dividerWidth2;
                    }
                    int height = v.getMeasuredHeight();
                    if (isLayoutRtl2) {
                        isLayoutRtl = isLayoutRtl2;
                        l = getPaddingLeft() + p.leftMargin;
                        r = l + overflowWidth3;
                    } else {
                        isLayoutRtl = isLayoutRtl2;
                        r = (getWidth() - getPaddingRight()) - p.rightMargin;
                        l = r - overflowWidth3;
                    }
                    int t = midVertical2 - (height / 2);
                    midVertical = midVertical2;
                    int midVertical3 = t + height;
                    v.layout(l, t, r, midVertical3);
                    widthRemaining2 -= overflowWidth3;
                    hasOverflow = true;
                    nonOverflowWidth = overflowWidth3;
                } else {
                    midVertical = midVertical2;
                    isLayoutRtl = isLayoutRtl2;
                    int size = v.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                    widthRemaining3 += size;
                    widthRemaining2 -= size;
                    if (hasSupportDividerBeforeChildAt(overflowWidth2)) {
                        widthRemaining3 += dividerWidth2;
                    }
                    nonOverflowCount++;
                }
            }
            overflowWidth2++;
            isLayoutRtl2 = isLayoutRtl;
            midVertical2 = midVertical;
        }
        int midVertical4 = midVertical2;
        boolean isLayoutRtl3 = isLayoutRtl2;
        if (childCount == 1 && !hasOverflow) {
            View v2 = getChildAt(0);
            int width = v2.getMeasuredWidth();
            int height2 = v2.getMeasuredHeight();
            int midHorizontal = (right - left) / 2;
            int l2 = midHorizontal - (width / 2);
            int t2 = midVertical4 - (height2 / 2);
            v2.layout(l2, t2, l2 + width, t2 + height2);
            return;
        }
        int spacerCount = nonOverflowCount - (hasOverflow ? 0 : 1);
        int i = 0;
        int spacerSize = Math.max(0, spacerCount > 0 ? widthRemaining2 / spacerCount : 0);
        if (!isLayoutRtl3) {
            int startLeft = getPaddingLeft();
            while (i < childCount) {
                View v3 = getChildAt(i);
                LayoutParams lp = (LayoutParams) v3.getLayoutParams();
                if (v3.getVisibility() != 8 && !lp.isOverflowButton) {
                    int startLeft2 = startLeft + lp.leftMargin;
                    int width2 = v3.getMeasuredWidth();
                    int height3 = v3.getMeasuredHeight();
                    int t3 = midVertical4 - (height3 / 2);
                    v3.layout(startLeft2, t3, startLeft2 + width2, t3 + height3);
                    startLeft = startLeft2 + lp.rightMargin + width2 + spacerSize;
                }
                i++;
            }
            return;
        }
        int startRight = getWidth() - getPaddingRight();
        while (i < childCount) {
            View v4 = getChildAt(i);
            LayoutParams lp2 = (LayoutParams) v4.getLayoutParams();
            int spacerCount2 = spacerCount;
            if (v4.getVisibility() == 8) {
                dividerWidth = dividerWidth2;
                overflowWidth = nonOverflowWidth;
            } else if (lp2.isOverflowButton) {
                dividerWidth = dividerWidth2;
                overflowWidth = nonOverflowWidth;
            } else {
                int startRight2 = startRight - lp2.rightMargin;
                int width3 = v4.getMeasuredWidth();
                int height4 = v4.getMeasuredHeight();
                int t4 = midVertical4 - (height4 / 2);
                dividerWidth = dividerWidth2;
                overflowWidth = nonOverflowWidth;
                int overflowWidth4 = t4 + height4;
                v4.layout(startRight2 - width3, t4, startRight2, overflowWidth4);
                startRight = startRight2 - ((lp2.leftMargin + width3) + spacerSize);
            }
            i++;
            spacerCount = spacerCount2;
            dividerWidth2 = dividerWidth;
            nonOverflowWidth = overflowWidth;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissPopupMenus();
    }

    public void setOverflowIcon(@Nullable Drawable icon) {
        getMenu();
        this.mPresenter.setOverflowIcon(icon);
    }

    @Nullable
    public Drawable getOverflowIcon() {
        getMenu();
        return this.mPresenter.getOverflowIcon();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setOverflowReserved(boolean reserveOverflow) {
        this.mReserveOverflow = reserveOverflow;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = new LayoutParams(-2, -2);
        params.gravity = 16;
        return params;
    }

    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p != null) {
            LayoutParams result = p instanceof LayoutParams ? new LayoutParams((LayoutParams) p) : new LayoutParams(p);
            if (result.gravity <= 0) {
                result.gravity = 16;
            }
            return result;
        }
        return generateDefaultLayoutParams();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && (p instanceof LayoutParams);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams result = generateDefaultLayoutParams();
        result.isOverflowButton = true;
        return result;
    }

    @Override // android.support.v7.view.menu.MenuBuilder.ItemInvoker
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public boolean invokeItem(MenuItemImpl item) {
        return this.mMenu.performItemAction(item, 0);
    }

    @Override // android.support.v7.view.menu.MenuView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getWindowAnimations() {
        return 0;
    }

    @Override // android.support.v7.view.menu.MenuView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void initialize(MenuBuilder menu) {
        this.mMenu = menu;
    }

    public Menu getMenu() {
        if (this.mMenu == null) {
            Context context = getContext();
            this.mMenu = new MenuBuilder(context);
            this.mMenu.setCallback(new MenuBuilderCallback());
            this.mPresenter = new ActionMenuPresenter(context);
            this.mPresenter.setReserveOverflow(true);
            this.mPresenter.setCallback(this.mActionMenuPresenterCallback != null ? this.mActionMenuPresenterCallback : new ActionMenuPresenterCallback());
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return this.mMenu;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setMenuCallbacks(MenuPresenter.Callback pcb, MenuBuilder.Callback mcb) {
        this.mActionMenuPresenterCallback = pcb;
        this.mMenuBuilderCallback = mcb;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public MenuBuilder peekMenu() {
        return this.mMenu;
    }

    public boolean showOverflowMenu() {
        return this.mPresenter != null && this.mPresenter.showOverflowMenu();
    }

    public boolean hideOverflowMenu() {
        return this.mPresenter != null && this.mPresenter.hideOverflowMenu();
    }

    public boolean isOverflowMenuShowing() {
        return this.mPresenter != null && this.mPresenter.isOverflowMenuShowing();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public boolean isOverflowMenuShowPending() {
        return this.mPresenter != null && this.mPresenter.isOverflowMenuShowPending();
    }

    public void dismissPopupMenus() {
        if (this.mPresenter != null) {
            this.mPresenter.dismissPopupMenus();
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    protected boolean hasSupportDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return false;
        }
        View childBefore = getChildAt(childIndex - 1);
        View child = getChildAt(childIndex);
        boolean result = false;
        if (childIndex < getChildCount() && (childBefore instanceof ActionMenuChildView)) {
            result = false | ((ActionMenuChildView) childBefore).needsDividerAfter();
        }
        if (childIndex > 0 && (child instanceof ActionMenuChildView)) {
            return result | ((ActionMenuChildView) child).needsDividerBefore();
        }
        return result;
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return false;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setExpandedActionViewsExclusive(boolean exclusive) {
        this.mPresenter.setExpandedActionViewsExclusive(exclusive);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MenuBuilderCallback implements MenuBuilder.Callback {
        MenuBuilderCallback() {
        }

        @Override // android.support.v7.view.menu.MenuBuilder.Callback
        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(item);
        }

        @Override // android.support.v7.view.menu.MenuBuilder.Callback
        public void onMenuModeChange(MenuBuilder menu) {
            if (ActionMenuView.this.mMenuBuilderCallback != null) {
                ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(menu);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        ActionMenuPresenterCallback() {
        }

        @Override // android.support.v7.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        @Override // android.support.v7.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder subMenu) {
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends LinearLayoutCompat.LayoutParams {
        @ViewDebug.ExportedProperty
        public int cellsUsed;
        @ViewDebug.ExportedProperty
        public boolean expandable;
        boolean expanded;
        @ViewDebug.ExportedProperty
        public int extraPixels;
        @ViewDebug.ExportedProperty
        public boolean isOverflowButton;
        @ViewDebug.ExportedProperty
        public boolean preventEdgeOffset;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams other) {
            super(other);
        }

        public LayoutParams(LayoutParams other) {
            super((ViewGroup.LayoutParams) other);
            this.isOverflowButton = other.isOverflowButton;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.isOverflowButton = false;
        }

        LayoutParams(int width, int height, boolean isOverflowButton) {
            super(width, height);
            this.isOverflowButton = isOverflowButton;
        }
    }
}
