package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.DisplayUtil;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;
import com.google.android.material.card.MaterialCardView;

import java.util.Optional;

public class CloneCardShadow extends View.DragShadowBuilder {
//    LayoutInflater layoutInflater;
//    View cloneCardView;

    public CloneCardShadow(View view) {
        super(view);
        Logger.hotfixMessage("CloneCardShadow/getView.getWidth:"+getView().getWidth()+"/height:"+getView().getHeight());
//        layoutInflater = ((Activity) view.getContext()).getLayoutInflater();
//        cloneCardView = layoutInflater.inflate(R.layout.item_card_front_clone, (ViewGroup) getView().getRootView(), false);
//        cloneCardView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
        outShadowSize.set(getView().getWidth(),getView().getHeight());
        outShadowTouchPoint.set(getView().getWidth()/2,getView().getHeight());
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        super.onDrawShadow(canvas);
    }
}
