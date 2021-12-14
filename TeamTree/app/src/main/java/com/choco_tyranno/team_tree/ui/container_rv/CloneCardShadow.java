package com.choco_tyranno.team_tree.ui.container_rv;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

public class CloneCardShadow extends View.DragShadowBuilder {
    public CloneCardShadow(View view) {
        super(view);
    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
        outShadowSize.set(getView().getWidth(), getView().getHeight());
        outShadowTouchPoint.set(getView().getWidth() / 2, getView().getHeight());
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        super.onDrawShadow(canvas);
    }
}
