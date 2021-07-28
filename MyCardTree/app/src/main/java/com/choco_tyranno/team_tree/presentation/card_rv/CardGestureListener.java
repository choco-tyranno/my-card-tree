package com.choco_tyranno.team_tree.presentation.card_rv;

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

import com.choco_tyranno.team_tree.databinding.ItemCardFrameBinding;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.MainCardActivity;
import com.choco_tyranno.team_tree.presentation.SingleToastManager;
import com.choco_tyranno.team_tree.presentation.SingleToaster;
import com.choco_tyranno.team_tree.presentation.container_rv.CloneCardShadow;
import com.choco_tyranno.team_tree.presentation.container_rv.Container;
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerRecyclerView;
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
        CardDto cardDto = getCardDto();
        Pair<List<CardDto>, List<CardDto>> savedOriginData = prepareDragStart(view);
        view.startDragAndDrop(ClipData.newPlainText("", "")
                , new CloneCardShadow(CardViewShadowProvider.getInstance(view.getContext(), cardDto), cardDto)
                , Pair.create("MOVE", Pair.create(cardDto, savedOriginData)), 0);
    }

    /*
     * Prepare moving cards data & detach moving cards.
     * moving data - has no change. for drop.
     * next data - seq reduced. for rollback.
     * return : Pair<> (first : moving-data, second : next data),
     * */
    private Pair<List<CardDto>, List<CardDto>> prepareDragStart(View view) {
        CardRecyclerView cardRecyclerView = getCardRecyclerView(view);
        CardViewModel viewModel = getCardViewModel(view);
        List<CardDto> savedMovingData = new ArrayList<>();
        List<CardDto> savedNextData = new ArrayList<>();
        CardDto cardDto = getCardDto();
        Pair<List<CardDto>, List<CardDto>> preparedData = Pair.create(savedMovingData, savedNextData);
        viewModel.findChildrenCards(cardDto, savedMovingData);
        savedMovingData.add(cardDto);
        viewModel.findNextCards(cardDto.getContainerNo(), cardDto.getSeqNo(), savedNextData);
        viewModel.removeFromAllList(savedMovingData.toArray(new CardDto[0]));
        final boolean hasLeftItemInTargetContainer = viewModel.removeSinglePresentCardDto(cardDto);
        if (!savedNextData.isEmpty()) {
            viewModel.reduceListSeq(savedNextData);
        }
        if (hasLeftItemInTargetContainer) {
            if (cardRecyclerView.getAdapter() == null)
                throw new RuntimeException("#prepareDragStart - cardRecyclerView is null");
            cardRecyclerView.getAdapter().notifyItemRemoved(cardDto.getSeqNo());
            ((MainCardActivity) cardRecyclerView.getContext()).getMainHandler().postDelayed(() -> {
                final int newFocusPosition = viewModel.findNearestItemPosition(cardDto.getContainerNo(), cardDto.getSeqNo());
                Container container = viewModel.getContainer(cardDto.getContainerNo());
                if (container != null) {
                    container.setFocusCardPosition(newFocusPosition);
                    cardRecyclerView.smoothScrollToPosition(newFocusPosition);
                    viewModel.presentChildren(cardRecyclerView, cardDto.getContainerNo(), newFocusPosition);
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
            int removeCount = cardContainerCount - (cardDto.getContainerNo() + 1) + 1;
            viewModel.clearContainerPositionPresentData(cardDto.getContainerNo());
            viewModel.clearContainerAtPosition(cardDto.getContainerNo());
            containerAdapter.notifyItemRangeRemoved(cardDto.getContainerNo(), removeCount);
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

    private CardDto getCardDto(){
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
