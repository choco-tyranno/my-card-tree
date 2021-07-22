package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.databinding.BaseObservable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.SingleToastManager;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.SingleToaster;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.CardContainerViewHolder;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.CloneCardShadow;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.Container;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerRecyclerView;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardGestureListener extends GestureDetector.SimpleOnGestureListener {
    private View view;

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        SingleToastManager.show(SingleToaster.makeTextShort(view.getContext(), "long pressed"));
        CardDTO cardDTO = getCardDto();
        Pair<List<CardDTO>, List<CardDTO>> savedOriginData = prepareDragStart(view);
        view.startDragAndDrop(ClipData.newPlainText("", "")
                , new CloneCardShadow(CardViewShadowProvider.getInstance(view.getContext(), cardDTO), cardDTO)
                , Pair.create("MOVE", Pair.create(cardDTO, savedOriginData)), 0);
    }

    /*
     * Prepare moving cards data & detach moving cards.
     * moving data - has no change. for drop.
     * next data - seq reduced. for rollback.
     * return : Pair<> (first : moving-data, second : next data),
     * */
    private Pair<List<CardDTO>, List<CardDTO>> prepareDragStart(View view) {
        CardRecyclerView cardRecyclerView = getCardRecyclerView(view);
        CardViewModel viewModel = getCardViewModel(view);
        List<CardDTO> savedMovingData = new ArrayList<>();
        List<CardDTO> savedNextData = new ArrayList<>();
        CardDTO cardDTO = getCardDto();
        Pair<List<CardDTO>, List<CardDTO>> preparedData = Pair.create(savedMovingData, savedNextData);
        viewModel.findChildrenCards(cardDTO, savedMovingData);
        savedMovingData.add(cardDTO);
        viewModel.findNextCards(cardDTO.getContainerNo(), cardDTO.getSeqNo(), savedNextData);
        viewModel.removeFromAllList(savedMovingData.toArray(new CardDTO[0]));
        final boolean hasLeftItemInTargetContainer = viewModel.removeSinglePresentCardDto(cardDTO);
        if (!savedNextData.isEmpty()) {
            viewModel.reduceListSeq(savedNextData);
        }
        if (hasLeftItemInTargetContainer) {
            if (cardRecyclerView.getAdapter() == null)
                throw new RuntimeException("#prepareDragStart - cardRecyclerView is null");
            cardRecyclerView.getAdapter().notifyItemRemoved(cardDTO.getSeqNo());
            ((MainCardActivity) cardRecyclerView.getContext()).getMainHandler().postDelayed(() -> {
                final int newFocusPosition = viewModel.findNearestItemPosition(cardDTO.getContainerNo(), cardDTO.getSeqNo());
                Container container = viewModel.getContainer(cardDTO.getContainerNo());
                if (container != null) {
                    container.setFocusCardPosition(newFocusPosition);
                    cardRecyclerView.smoothScrollToPosition(newFocusPosition);
                    viewModel.presentChildren(cardRecyclerView, cardDTO.getContainerNo(), newFocusPosition);
                }
            }, 150);
        } else {
            ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) cardRecyclerView.getParent().getParent();
            ContainerAdapter containerAdapter = (ContainerAdapter) containerRecyclerView.getAdapter();
            if (containerAdapter == null)
                return preparedData;
            LinearLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
            if (containerLayoutManager == null)
                return preparedData;
            int cardContainerCount = containerLayoutManager.getItemCount() - 1;
            int removeCount = cardContainerCount - (cardDTO.getContainerNo() + 1) + 1;
            viewModel.clearContainerPositionPresentData(cardDTO.getContainerNo());
            viewModel.clearContainerAtPosition(cardDTO.getContainerNo());
            containerAdapter.notifyItemRangeRemoved(cardDTO.getContainerNo(), removeCount);
        }
        return preparedData;
    }


    private CardRecyclerView getCardRecyclerView(View view) {
        FrameLayout frameLayout = (FrameLayout) view.getParent().getParent();
        return (CardRecyclerView) frameLayout.getParent();
    }

    private CardViewModel getCardViewModel(View view) {
        return ((MainCardActivity) view.getContext()).getCardViewModel();
    }


    /* end long pressed methods*/

    private ItemCardFrameBinding getItemCardFrameBinding(){
        FrameLayout frameLayout = (FrameLayout) view.getParent().getParent();
        CardRecyclerView cardRecyclerView = (CardRecyclerView) frameLayout.getParent();
        ContactCardViewHolder cardViewHolder = (ContactCardViewHolder) cardRecyclerView.getChildViewHolder(frameLayout);
        return cardViewHolder.getBinding();
    }

    private CardDTO getCardDto(){
        return getItemCardFrameBinding().getCard();
    }

    private CardState getCardState(){
        return getItemCardFrameBinding().getCardState();
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (view == null) {
            return false;
        }
        ItemCardFrameBinding binding = getItemCardFrameBinding();
        MaterialCardView frontCardView = binding.cardFrontLayout.frontCardCardView;
        MaterialCardView backCardView = binding.cardBackLayout.backCardCardView;
        CardState cardState = getCardState();
        flippingCard(frontCardView, backCardView, cardState);
        clearView();
        return true;
    }

    public boolean hasView() {
        return view != null;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void clearView() {
        this.view = null;
    }

    private void flippingCard(View frontCardView, View backCardView, CardState cardState) {
        Context context = frontCardView.getContext();
        final boolean startWithFlipped = cardState.isFlipped();
        View startCardFace;
        View finishCardFace;
        if (startWithFlipped) {
            startCardFace = backCardView;
            finishCardFace = frontCardView;
        } else {
            startCardFace = frontCardView;
            finishCardFace = backCardView;
        }
        startCardFace.animate().rotationX(90f).alpha(0f).
                setInterpolator(AnimationUtils.loadInterpolator(context, android.R.anim.decelerate_interpolator)).
                setDuration(200).withEndAction(() -> {
            startCardFace.setAlpha(0f);
            startCardFace.setRotationX(0f);
            startCardFace.setVisibility(View.INVISIBLE);
            finishCardFace.setAlpha(0f);
            finishCardFace.setRotationX(-90f);
            finishCardFace.setVisibility(View.VISIBLE);
            if (startWithFlipped) {
                cardState.displayFront();
            } else {
                cardState.displayBack();
            }
            finishCardFace.animate().rotationX(0f).
                    setInterpolator(AnimationUtils.loadInterpolator(context, android.R.anim.decelerate_interpolator)).
                    alpha(1f).setDuration(200).start();
        }).start();
    }
}
