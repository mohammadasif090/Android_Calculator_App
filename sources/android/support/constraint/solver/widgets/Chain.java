package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class Chain {
    private static final boolean DEBUG = false;

    Chain() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem system, int orientation) {
        int offset;
        int chainsSize;
        ConstraintWidget[] chainsArray;
        if (orientation == 0) {
            offset = 0;
            chainsSize = constraintWidgetContainer.mHorizontalChainsSize;
            chainsArray = constraintWidgetContainer.mHorizontalChainsArray;
        } else {
            offset = 2;
            chainsSize = constraintWidgetContainer.mVerticalChainsSize;
            chainsArray = constraintWidgetContainer.mVerticalChainsArray;
        }
        for (int i = 0; i < chainsSize; i++) {
            ConstraintWidget first = chainsArray[i];
            if (constraintWidgetContainer.optimizeFor(4)) {
                if (!Optimizer.applyChainOptimized(constraintWidgetContainer, system, orientation, offset, first)) {
                    applyChainConstraints(constraintWidgetContainer, system, orientation, offset, first);
                }
            } else {
                applyChainConstraints(constraintWidgetContainer, system, orientation, offset, first);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0061  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0064  */
    /* JADX WARN: Removed duplicated region for block: B:287:0x05d1  */
    /* JADX WARN: Removed duplicated region for block: B:288:0x05d6  */
    /* JADX WARN: Removed duplicated region for block: B:291:0x05dd  */
    /* JADX WARN: Removed duplicated region for block: B:292:0x05e2  */
    /* JADX WARN: Removed duplicated region for block: B:295:0x05e8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static void applyChainConstraints(android.support.constraint.solver.widgets.ConstraintWidgetContainer r59, android.support.constraint.solver.LinearSystem r60, int r61, int r62, android.support.constraint.solver.widgets.ConstraintWidget r63) {
        /*
            Method dump skipped, instructions count: 1575
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.Chain.applyChainConstraints(android.support.constraint.solver.widgets.ConstraintWidgetContainer, android.support.constraint.solver.LinearSystem, int, int, android.support.constraint.solver.widgets.ConstraintWidget):void");
    }
}
