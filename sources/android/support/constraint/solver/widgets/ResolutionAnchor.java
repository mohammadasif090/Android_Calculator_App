package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintAnchor;

/* loaded from: classes.dex */
public class ResolutionAnchor extends ResolutionNode {
    public static final int BARRIER_CONNECTION = 5;
    public static final int CENTER_CONNECTION = 2;
    public static final int CHAIN_CONNECTION = 4;
    public static final int DIRECT_CONNECTION = 1;
    public static final int MATCH_CONNECTION = 3;
    public static final int UNCONNECTED = 0;
    float computedValue;
    ConstraintAnchor myAnchor;
    float offset;
    private ResolutionAnchor opposite;
    private float oppositeOffset;
    float resolvedOffset;
    ResolutionAnchor resolvedTarget;
    ResolutionAnchor target;
    int type = 0;
    private ResolutionDimension dimension = null;
    private int dimensionMultiplier = 1;
    private ResolutionDimension oppositeDimension = null;
    private int oppositeDimensionMultiplier = 1;

    public ResolutionAnchor(ConstraintAnchor anchor) {
        this.myAnchor = anchor;
    }

    @Override // android.support.constraint.solver.widgets.ResolutionNode
    public void remove(ResolutionDimension resolutionDimension) {
        if (this.dimension == resolutionDimension) {
            this.dimension = null;
            this.offset = this.dimensionMultiplier;
        } else if (this.dimension == this.oppositeDimension) {
            this.oppositeDimension = null;
            this.oppositeOffset = this.oppositeDimensionMultiplier;
        }
        resolve();
    }

    public String toString() {
        if (this.state == 1) {
            if (this.resolvedTarget == this) {
                return "[" + this.myAnchor + ", RESOLVED: " + this.resolvedOffset + "]  type: " + sType(this.type);
            }
            return "[" + this.myAnchor + ", RESOLVED: " + this.resolvedTarget + ":" + this.resolvedOffset + "] type: " + sType(this.type);
        }
        return "{ " + this.myAnchor + " UNRESOLVED} type: " + sType(this.type);
    }

    public void resolve(ResolutionAnchor target, float offset) {
        if (this.state == 0 || (this.resolvedTarget != target && this.resolvedOffset != offset)) {
            this.resolvedTarget = target;
            this.resolvedOffset = offset;
            if (this.state == 1) {
                invalidate();
            }
            didResolve();
        }
    }

    String sType(int type) {
        if (type == 1) {
            return "DIRECT";
        }
        if (type == 2) {
            return "CENTER";
        }
        if (type == 3) {
            return "MATCH";
        }
        if (type == 4) {
            return "CHAIN";
        }
        if (type == 5) {
            return "BARRIER";
        }
        return "UNCONNECTED";
    }

    @Override // android.support.constraint.solver.widgets.ResolutionNode
    public void resolve() {
        float distance;
        float distance2;
        float percent;
        if (this.state == 1 || this.type == 4) {
            return;
        }
        if (this.dimension != null) {
            if (this.dimension.state != 1) {
                return;
            }
            this.offset = this.dimensionMultiplier * this.dimension.value;
        }
        if (this.oppositeDimension != null) {
            if (this.oppositeDimension.state != 1) {
                return;
            }
            this.oppositeOffset = this.oppositeDimensionMultiplier * this.oppositeDimension.value;
        }
        if (this.type == 1 && (this.target == null || this.target.state == 1)) {
            if (this.target == null) {
                this.resolvedTarget = this;
                this.resolvedOffset = this.offset;
            } else {
                this.resolvedTarget = this.target.resolvedTarget;
                this.resolvedOffset = this.target.resolvedOffset + this.offset;
            }
            didResolve();
        } else if (this.type != 2 || this.target == null || this.target.state != 1 || this.opposite == null || this.opposite.target == null || this.opposite.target.state != 1) {
            if (this.type == 3 && this.target != null && this.target.state == 1 && this.opposite != null && this.opposite.target != null && this.opposite.target.state == 1) {
                if (LinearSystem.getMetrics() != null) {
                    LinearSystem.getMetrics().matchConnectionResolved++;
                }
                this.resolvedTarget = this.target.resolvedTarget;
                this.opposite.resolvedTarget = this.opposite.target.resolvedTarget;
                this.resolvedOffset = this.target.resolvedOffset + this.offset;
                this.opposite.resolvedOffset = this.opposite.target.resolvedOffset + this.opposite.offset;
                didResolve();
                this.opposite.didResolve();
            } else if (this.type == 5) {
                this.myAnchor.mOwner.resolve();
            }
        } else {
            if (LinearSystem.getMetrics() != null) {
                LinearSystem.getMetrics().centerConnectionResolved++;
            }
            this.resolvedTarget = this.target.resolvedTarget;
            this.opposite.resolvedTarget = this.opposite.target.resolvedTarget;
            if (this.oppositeOffset > 0.0f) {
                distance = this.target.resolvedOffset - this.opposite.target.resolvedOffset;
            } else {
                distance = this.opposite.target.resolvedOffset - this.target.resolvedOffset;
            }
            if (this.myAnchor.mType == ConstraintAnchor.Type.LEFT || this.myAnchor.mType == ConstraintAnchor.Type.RIGHT) {
                distance2 = distance - this.myAnchor.mOwner.getWidth();
                percent = this.myAnchor.mOwner.mHorizontalBiasPercent;
            } else {
                distance2 = distance - this.myAnchor.mOwner.getHeight();
                percent = this.myAnchor.mOwner.mVerticalBiasPercent;
            }
            int margin = this.myAnchor.getMargin();
            int oppositeMargin = this.opposite.myAnchor.getMargin();
            if (this.myAnchor.getTarget() == this.opposite.myAnchor.getTarget()) {
                percent = 0.5f;
                margin = 0;
                oppositeMargin = 0;
            }
            float distance3 = (distance2 - margin) - oppositeMargin;
            if (this.oppositeOffset > 0.0f) {
                this.opposite.resolvedOffset = this.opposite.target.resolvedOffset + oppositeMargin + (distance3 * percent);
                this.resolvedOffset = (this.target.resolvedOffset - margin) - ((1.0f - percent) * distance3);
            } else {
                this.resolvedOffset = this.target.resolvedOffset + margin + (distance3 * percent);
                this.opposite.resolvedOffset = (this.opposite.target.resolvedOffset - oppositeMargin) - ((1.0f - percent) * distance3);
            }
            didResolve();
            this.opposite.didResolve();
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override // android.support.constraint.solver.widgets.ResolutionNode
    public void reset() {
        super.reset();
        this.target = null;
        this.offset = 0.0f;
        this.dimension = null;
        this.dimensionMultiplier = 1;
        this.oppositeDimension = null;
        this.oppositeDimensionMultiplier = 1;
        this.resolvedTarget = null;
        this.resolvedOffset = 0.0f;
        this.computedValue = 0.0f;
        this.opposite = null;
        this.oppositeOffset = 0.0f;
        this.type = 0;
    }

    public void update() {
        ConstraintAnchor targetAnchor = this.myAnchor.getTarget();
        if (targetAnchor == null) {
            return;
        }
        if (targetAnchor.getTarget() == this.myAnchor) {
            this.type = 4;
            targetAnchor.getResolutionNode().type = 4;
        }
        int margin = this.myAnchor.getMargin();
        if (this.myAnchor.mType == ConstraintAnchor.Type.RIGHT || this.myAnchor.mType == ConstraintAnchor.Type.BOTTOM) {
            margin = -margin;
        }
        dependsOn(targetAnchor.getResolutionNode(), margin);
    }

    public void dependsOn(int type, ResolutionAnchor node, int offset) {
        this.type = type;
        this.target = node;
        this.offset = offset;
        this.target.addDependent(this);
    }

    public void dependsOn(ResolutionAnchor node, int offset) {
        this.target = node;
        this.offset = offset;
        this.target.addDependent(this);
    }

    public void dependsOn(ResolutionAnchor node, int multiplier, ResolutionDimension dimension) {
        this.target = node;
        this.target.addDependent(this);
        this.dimension = dimension;
        this.dimensionMultiplier = multiplier;
        this.dimension.addDependent(this);
    }

    public void setOpposite(ResolutionAnchor opposite, float oppositeOffset) {
        this.opposite = opposite;
        this.oppositeOffset = oppositeOffset;
    }

    public void setOpposite(ResolutionAnchor opposite, int multiplier, ResolutionDimension dimension) {
        this.opposite = opposite;
        this.oppositeDimension = dimension;
        this.oppositeDimensionMultiplier = multiplier;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addResolvedValue(LinearSystem system) {
        SolverVariable sv = this.myAnchor.getSolverVariable();
        if (this.resolvedTarget == null) {
            system.addEquality(sv, (int) this.resolvedOffset);
            return;
        }
        SolverVariable v = system.createObjectVariable(this.resolvedTarget.myAnchor);
        system.addEquality(sv, v, (int) this.resolvedOffset, 6);
    }

    public float getResolvedValue() {
        return this.resolvedOffset;
    }
}
