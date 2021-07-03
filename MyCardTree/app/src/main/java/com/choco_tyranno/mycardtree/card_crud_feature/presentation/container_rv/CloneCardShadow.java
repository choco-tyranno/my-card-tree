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

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.DisplayUtil;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.SingleToastManager;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.SingleToaster;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardRecyclerView;
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
//
//        CardRecyclerView cardRecyclerView = findTargetCardRecyclerView();
//        CardViewModel viewModel = findCardViewModel();
//        List<CardDTO> moveItemList = new ArrayList<>();
//
//        viewModel.findChildrenCards(cardDTO, moveItemList);
//        moveItemList.add(cardDTO);
//        CardDTO.orderByContainerNoDesc(moveItemList);
//        viewModel.removeFromAllList(moveItemList.toArray(new CardDTO[0]));
//
//        List<CardDTO> foundNextCards = viewModel.findNextCards(cardDTO.getContainerNo(), cardDTO.getSeqNo());
//
//        if (!foundNextCards.isEmpty()) {
//            viewModel.reduceListSeq(foundNextCards);
//            for (CardDTO dto:foundNextCards){
//                Logger.hotfixMessage("dto seq : ("+dto.getSeqNo()+")");
//            }
//        }
//
//        final boolean hasLeftItem =viewModel.removeSinglePresentCardDto(cardDTO);
//        cardRecyclerView.getAdapter().notifyItemRemoved(cardDTO.getSeqNo());
//
//        if (!hasLeftItem){
//            viewModel.clearContainerPositionPresentData(cardDTO.getContainerNo());
//            return;
//        }
//        int newFocusPosition = viewModel.findNearestItemPosition(cardDTO.getContainerNo(), cardDTO.getSeqNo());
//        viewModel.getContainer(cardDTO.getContainerNo()).setFocusCardPosition(newFocusPosition);
//        findMainHandler().postDelayed(()->{
//            cardRecyclerView.smoothScrollToPosition(newFocusPosition);
//            viewModel.presentChildren(cardRecyclerView, cardDTO.getContainerNo(), newFocusPosition);
//        },500);
    }

    private Handler findMainHandler(){
        return ((MainCardActivity)getView().getContext()).getMainHandler();
    }

    private CardRecyclerView findTargetCardRecyclerView() {
        ContainerRecyclerView containerRecyclerView = ((MainCardActivity) getView().getContext()).getMainBinding().mainScreen.mainBody.containerRecyclerview;
        return Objects.requireNonNull(containerRecyclerView.findViewHolderForAdapterPosition(cardDTO.getContainerNo())).getBinding().cardRecyclerview;
    }

    private CardViewModel findCardViewModel(){
        return ((MainCardActivity) getView().getContext()).getCardViewModel();
    }
}
