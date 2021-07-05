package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

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

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.DisplayUtil;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.SingleToastManager;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.SingleToaster;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardRecyclerView;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.ContactCardViewHolder;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CloneCardShadow extends View.DragShadowBuilder {
    private CardDTO cardDTO;

    public CloneCardShadow(View view, CardDTO cardDTO) {
        super(view);
        this.cardDTO = cardDTO;
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
        RecyclerView.ViewHolder viewHolder = containerRecyclerView.findViewHolderForAdapterPosition(cardDTO.getContainerNo());
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
