package com.choco_tyranno.team_tree.presentation.container_rv;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.MainCardActivity;
import com.choco_tyranno.team_tree.presentation.card_rv.CardRecyclerView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CloneCardShadow extends View.DragShadowBuilder {
    private CardDto cardDto;

    public CloneCardShadow(View view, CardDto cardDto) {
        super(view);
        this.cardDto = cardDto;
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

    private Handler findMainHandler(){
        return ((MainCardActivity)getView().getContext()).getMainHandler();
    }

    private CardRecyclerView findTargetCardRecyclerView() {
        ContainerRecyclerView containerRecyclerView = ((MainCardActivity) getView().getContext()).getMainBinding().mainScreen.mainBody.containerRecyclerview;
        RecyclerView.ViewHolder viewHolder = containerRecyclerView.findViewHolderForAdapterPosition(cardDto.getContainerNo());
        if (viewHolder==null)
            return null;
        if (!(viewHolder instanceof CardContainerViewHolder)){
            return null;
        }
        CardContainerViewHolder containerViewHolder =(CardContainerViewHolder) viewHolder;
        return containerViewHolder.getBinding().cardRecyclerview;
    }

    private CardViewModel findCardViewModel(){
        return ((MainCardActivity) getView().getContext()).getCardViewModel();
    }
}
