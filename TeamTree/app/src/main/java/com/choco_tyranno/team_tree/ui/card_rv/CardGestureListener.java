package com.choco_tyranno.team_tree.ui.card_rv;

import android.content.ClipData;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.choco_tyranno.team_tree.databinding.ItemCardframeBinding;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.ui.CardViewModel;
import com.choco_tyranno.team_tree.ui.container_rv.CloneCardShadow;
import com.choco_tyranno.team_tree.ui.container_rv.Container;
import com.choco_tyranno.team_tree.ui.container_rv.ContainerAdapter;
import com.choco_tyranno.team_tree.ui.container_rv.ContainerRecyclerView;
import com.choco_tyranno.team_tree.ui.main.MainCardActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class CardGestureListener extends GestureDetector.SimpleOnGestureListener {
    private View view;

    /*
    * Moving card entry point.
    * */
    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        CardDto cardDto = getCardDto();
        DragMoveDataContainer dragMoveDataContainer = prepareStartingDragMove(view);
        view.startDragAndDrop(ClipData.newPlainText("", "")
                , new CloneCardShadow(CardViewShadowProvider.getInstance(view.getContext(), cardDto))
                , dragMoveDataContainer, 0);
    }

    /*
     * Prepare moving cards data & detach moving cards.
     * moving data - has no change. for drop.
     * next data - seq reduced. for rollback.
     * return : Pair<> (first : moving-data, second : next data),
     * */
    private DragMoveDataContainer prepareStartingDragMove(View view) {
        CardRecyclerView cardRecyclerView = getCardRecyclerView(view);
        CardViewModel viewModel = getCardViewModel(view);
        List<CardDto> movingCardList = new ArrayList<>();
        List<CardDto> currentNextCardList = new ArrayList<>();
        List<Integer> currentOnFocusPositionList = new ArrayList<>();
        CardDto rootCardDto = getCardDto();
        viewModel.findCurrentOnFocusCardPositions(rootCardDto.getContainerNo(), currentOnFocusPositionList);
        DragMoveDataContainer dragMoveDataContainer = new DragMoveDataContainer();
        dragMoveDataContainer.setRootCard(rootCardDto);
        dragMoveDataContainer.setMovingCardList(movingCardList);
        dragMoveDataContainer.setPastLocationNextCardList(currentNextCardList);
        dragMoveDataContainer.setPastOnFocusPositionList(currentOnFocusPositionList);
        viewModel.findChildrenCards(rootCardDto, movingCardList);
        movingCardList.add(rootCardDto);
        viewModel.findNextCards(rootCardDto.getContainerNo(), rootCardDto.getSeqNo(), currentNextCardList);
        viewModel.removeFromAllList(movingCardList.toArray(new CardDto[0]));

        final boolean isItemLeftInTargetContainer = viewModel.removeSinglePresentCardDto(rootCardDto);
        if (!currentNextCardList.isEmpty()) {
            viewModel.reduceCardSeqOneStep(currentNextCardList);
        }
        if (isItemLeftInTargetContainer) {
            if (cardRecyclerView.getAdapter() == null)
                throw new RuntimeException("#prepareDragStart - cardRecyclerView is null");
            cardRecyclerView.getAdapter().notifyItemRemoved(rootCardDto.getSeqNo());
            ((MainCardActivity) cardRecyclerView.getContext()).getMainHandler().postDelayed(() -> {
                final int newFocusPosition = viewModel.findNearestItemPosition(rootCardDto.getContainerNo(), rootCardDto.getSeqNo());
                Container container = viewModel.getContainer(rootCardDto.getContainerNo());
                if (container != null) {
                    container.setFocusCardPosition(newFocusPosition);
                    cardRecyclerView.smoothScrollToPosition(newFocusPosition);
                    viewModel.presentChildren(cardRecyclerView, rootCardDto.getContainerNo(), newFocusPosition);
                }
            }, 150);
        } else {
            ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) cardRecyclerView.getParent().getParent();
            ContainerAdapter containerAdapter = (ContainerAdapter) containerRecyclerView.getAdapter();
            if (containerAdapter == null)
                return dragMoveDataContainer;
            LinearLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
            if (containerLayoutManager == null)
                return dragMoveDataContainer;
            int cardContainerCount = containerLayoutManager.getItemCount() - 1;
            int removeCount = cardContainerCount - (rootCardDto.getContainerNo() + 1) + 1;
            viewModel.clearContainerPositionPresentData(rootCardDto.getContainerNo());
            viewModel.clearContainerAtPosition(rootCardDto.getContainerNo());
            containerAdapter.notifyItemRangeRemoved(rootCardDto.getContainerNo(), removeCount);
        }
        return dragMoveDataContainer;
    }


    private CardRecyclerView getCardRecyclerView(View view) {
        ConstraintLayout cardFrame = (ConstraintLayout) view.getParent();
        return (CardRecyclerView) cardFrame.getParent();
    }

    private CardViewModel getCardViewModel(View view) {
        return ((MainCardActivity) view.getContext()).getCardViewModel();
    }


    /* end long pressed methods*/

    private ItemCardframeBinding getItemCardFrameBinding() {
        ConstraintLayout cardFrame = (ConstraintLayout) view.getParent();
        CardRecyclerView cardRecyclerView = (CardRecyclerView) cardFrame.getParent();
        ContactCardViewHolder cardViewHolder = (ContactCardViewHolder) cardRecyclerView.getChildViewHolder(cardFrame);
        return cardViewHolder.getBinding();
    }

    private CardDto getCardDto() {
        return getItemCardFrameBinding().getCard();
    }

    private CardState getCardState() {
        return getItemCardFrameBinding().getCardState();
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (view == null) {
            return false;
        }
        ItemCardframeBinding binding = getItemCardFrameBinding();
        MaterialCardView frontCardView = binding.cardFrontLayout.frontCardCardView;
        MaterialCardView backCardView = binding.cardBackLayout.materialCardViewCardBackFrame;
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
