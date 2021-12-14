package com.choco_tyranno.team_tree.ui.card_rv;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.databinding.ItemCardframeBinding;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.ui.CardViewModel;
import com.choco_tyranno.team_tree.ui.DisplayUtil;
import com.choco_tyranno.team_tree.ui.SingleToaster;
import com.choco_tyranno.team_tree.ui.container_rv.Container;
import com.choco_tyranno.team_tree.ui.container_rv.ContainerRecyclerView;
import com.choco_tyranno.team_tree.ui.main.MainCardActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class OnDragListenerForCardRecyclerView implements View.OnDragListener {
    private final int CARD_LOCATION_LEFT = 0;
    private final int CARD_LOCATION_RIGHT = 1;
    private final int ACTION_SCROLL_TO_POSITION = 1;
    private final int ACTION_PRESENT_CHILDREN = 2;
    private final int ACTION_SHOW_MOVE_SUCCESS_MESSAGE = 3;

    @Override
    public boolean onDrag(View v, DragEvent event) {
        String dragType = "";
        if (event.getLocalState() instanceof Pair)
            dragType = (String) ((Pair) event.getLocalState()).first;
        if (event.getLocalState() instanceof DragMoveDataContainer)
            dragType = ((DragMoveDataContainer) event.getLocalState()).getDragType();
        if (TextUtils.equals(dragType, "CREATE")) {
            return handleCreateService((CardRecyclerView) v, event);
        }
        if (TextUtils.equals(dragType, DragMoveDataContainer.DRAG_TYPE)) {
            return handleMoveService((CardRecyclerView) v, event);
        }
        return false;
    }

    //****** Start - Create service
    private boolean handleCreateService(CardRecyclerView view, DragEvent event) {
        CardRecyclerView.ScrollControllableLayoutManager layoutManager = view.getLayoutManager();
        if (layoutManager == null)
            return false;
        if (event.getAction() == DragEvent.ACTION_DRAG_STARTED)
            return true;
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        if (firstVisibleItemPosition == lastVisibleItemPosition) {
            return handleCreateServiceSingleItemVisibleCase(view, firstVisibleItemPosition, event);
        }
        return handleCreateServiceMultiItemVisibleCase(view, firstVisibleItemPosition, lastVisibleItemPosition, event);
    }

    private boolean handleCreateServiceSingleItemVisibleCase(CardRecyclerView view, int firstVisibleItemPosition, DragEvent event) {
        RecyclerView.ViewHolder firstVisibleItemViewHolder = view.findViewHolderForAdapterPosition(firstVisibleItemPosition);
        if (firstVisibleItemViewHolder == null)
            return false;
        if (!(firstVisibleItemViewHolder instanceof ContactCardViewHolder))
            return false;
        ContactCardViewHolder cardViewHolder = (ContactCardViewHolder) firstVisibleItemViewHolder;
        ConstraintLayout firstVisibleItemCardFrame = cardViewHolder.getBinding().constraintLayoutMainCardFramePositioningManager;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                animateCard(firstVisibleItemCardFrame, false, CARD_LOCATION_LEFT);
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                animateCard(firstVisibleItemCardFrame, true, CARD_LOCATION_LEFT);
                return true;
            case DragEvent.ACTION_DROP:
                animateCard(firstVisibleItemCardFrame, true, CARD_LOCATION_LEFT);
                createCard(view, firstVisibleItemCardFrame);
                return true;
        }
        return false;
    }

    private void createCard(CardRecyclerView cardRecyclerView, ConstraintLayout firstVisibleItemCardFrame) {
        if (cardRecyclerView == null || firstVisibleItemCardFrame == null) {
            return;
        }
        ItemCardframeBinding cardFrameBinding = ((ContactCardViewHolder) cardRecyclerView.getChildViewHolder(firstVisibleItemCardFrame)).getBinding();
        CardDto firstVisibleItemCardDto = cardFrameBinding.getCard();
        CardState firstVisibleItemCardState = cardFrameBinding.getCardState();
        int firstVisibleItemCardSeqNo = firstVisibleItemCardDto.getSeqNo();
        int firstVisibleItemRootNo = firstVisibleItemCardDto.getRootNo();
        int firstVisibleItemContainerNo = firstVisibleItemCardDto.getContainerNo();
        CardViewModel viewModel = ((MainCardActivity) cardRecyclerView.getContext()).getCardViewModel();
        int targetContainerPresentCardCount = viewModel.getPresentData().get(firstVisibleItemContainerNo).size();
        CardDto newCardDTO = new CardDto.Builder().seqNo(firstVisibleItemCardSeqNo + 1).rootNo(firstVisibleItemRootNo).containerNo(firstVisibleItemContainerNo).build();
        if (targetContainerPresentCardCount > firstVisibleItemCardSeqNo + 1) {
            viewModel.insertAndUpdates(newCardDTO, firstVisibleItemCardState, cardRecyclerView);
        } else {
            viewModel.insert(newCardDTO, firstVisibleItemCardState, cardRecyclerView);
        }
    }

    private boolean handleCreateServiceMultiItemVisibleCase(CardRecyclerView cardRecyclerView, int firstVisibleItemPosition, int lastVisibleItemPosition, DragEvent event) {
        if (firstVisibleItemPosition == RecyclerView.NO_POSITION)
            return false;
        RecyclerView.ViewHolder firstItemViewHolder = cardRecyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition);
        RecyclerView.ViewHolder lastItemViewHolder = cardRecyclerView.findViewHolderForAdapterPosition(lastVisibleItemPosition);
        if (firstItemViewHolder == null || lastItemViewHolder == null)
            return false;
        ContactCardViewHolder firstVisibleItemViewHolder = (ContactCardViewHolder) firstItemViewHolder;
        ContactCardViewHolder lastVisibleItemViewHolder = (ContactCardViewHolder) lastItemViewHolder;
        ConstraintLayout firstVisibleView = firstVisibleItemViewHolder.getBinding().constraintLayoutMainCardFramePositioningManager;
        ConstraintLayout lastVisibleView = lastVisibleItemViewHolder.getBinding().constraintLayoutMainCardFramePositioningManager;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                animateCard(firstVisibleView, false, CARD_LOCATION_LEFT);
                animateCard(lastVisibleView, false, CARD_LOCATION_RIGHT);
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                animateCard(firstVisibleView, true, CARD_LOCATION_LEFT);
                animateCard(lastVisibleView, true, CARD_LOCATION_RIGHT);
                return true;
            case DragEvent.ACTION_DROP:
                animateCard(firstVisibleView, true, CARD_LOCATION_LEFT);
                animateCard(lastVisibleView, true, CARD_LOCATION_RIGHT);
                createCard(cardRecyclerView, firstVisibleView);
                return true;
        }
        return false;
    }

    private void animateCard(ConstraintLayout view, boolean reverse, int toLocation) {
        int screenWidth = ((Activity) view.getContext()).getWindowManager().getCurrentWindowMetrics().getBounds().right;
        int fromXCoordinate = -1;
        int toXCoordinate = -1;

        if (toLocation == CARD_LOCATION_LEFT) {
            if (!reverse) {
                fromXCoordinate = 0;
                toXCoordinate = -screenWidth;
            } else {
                fromXCoordinate = -screenWidth;
                toXCoordinate = 0;
            }
        }

        if (toLocation == CARD_LOCATION_RIGHT) {
            if (!reverse) {
                fromXCoordinate = 0;
                toXCoordinate = screenWidth;
            } else {
                fromXCoordinate = screenWidth;
                toXCoordinate = 0;
            }
        }

        if (fromXCoordinate == -1 || toXCoordinate == -1)
            throw new RuntimeException("slowOut/fromXCoordinate or toXCoordinate has no validated value");

        view.animate()
                .setInterpolator(AnimationUtils.loadInterpolator(view.getContext(), android.R.anim.accelerate_decelerate_interpolator))
                .setDuration(200)
                .translationXBy(fromXCoordinate)
                .translationX(toXCoordinate).start();
    }
    //****** End - Create service

    //****** Start - Move Service
    private boolean handleMoveService(CardRecyclerView cardRecyclerView, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_LOCATION:
                return handleMoveServiceDragLocationEvent(cardRecyclerView, event.getX());
            case DragEvent.ACTION_DRAG_STARTED:
                return handleMoveServiceDragStarted(cardRecyclerView);
            case DragEvent.ACTION_DRAG_ENTERED:
                return handleMoveServiceDragEnteredEvent(cardRecyclerView);
            case DragEvent.ACTION_DRAG_EXITED:
                return handleMoveServiceDragExitedEvent(cardRecyclerView);
            case DragEvent.ACTION_DRAG_ENDED:
                return handleMoveServiceDragEndedEvent(cardRecyclerView);
            case DragEvent.ACTION_DROP:
                return handleMoveServiceDropEvent(cardRecyclerView, (DragMoveDataContainer) event.getLocalState());
        }
        return false;
    }

    private boolean handleMoveServiceDragStarted(CardRecyclerView cardRecyclerView) {
        cardRecyclerView.attachOnScrollListenerForHorizontalArrow();
        cardRecyclerView.getContainerRecyclerView().setOnDragMove(true);
        return true;
    }

    private boolean handleMoveServiceDropEvent(CardRecyclerView cardRecyclerView, DragMoveDataContainer dragMoveDataContainer) {
        CardDto movedRootCard = dragMoveDataContainer.getRootCard();
        List<CardDto> movedCardList = dragMoveDataContainer.getMovingCardList();
        List<CardDto> pastLocationNextCardList = dragMoveDataContainer.getPastLocationNextCardList();
        ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) cardRecyclerView.getParent().getParent();
        ContainerRecyclerView.ItemScrollingControlLayoutManager containerRecyclerViewLayoutManager = containerRecyclerView.getLayoutManager();
        CardViewModel viewModel = ((MainCardActivity) cardRecyclerView.getContext()).getCardViewModel();
        if (containerRecyclerViewLayoutManager == null)
            return false;
        CardAdapter cardAdapter = cardRecyclerView.getAdapter();
        if (cardAdapter == null)
            return false;
        final int targetContainerPosition = cardAdapter.getContainerPosition();
        if (targetContainerPosition < 0)
            return false;
        //movedCardList contains rootCard. the caution of duplicate update.
        //pastLocationNextCardList can be empty.

        //prepare update.
        //-find target container nextCards & increase seq.
        //-set seqNo/rootNo for movedRootCard.
        //-set containerNo for movedCardList.
        //->
        if (viewModel.presentContainerCount() < targetContainerPosition + 1)
            return false;
        Container container = viewModel.getContainer(targetContainerPosition);
        if (container == null)
            return false;
        final int onFocusCardPosition = container.getFocusCardPosition();
        List<CardDto> currentNextCards = new ArrayList<>();
        viewModel.findNextCards(targetContainerPosition, onFocusCardPosition - 1, currentNextCards);
        viewModel.increaseListSeq(currentNextCards);
        int rootCardNo = container.getRootNo();
        if (rootCardNo == Container.NO_ROOT_NO)
            throw new RuntimeException("[now work]targetContainerRootNo == Container.NO_ROOT_NO / #onMovingCardDroppedInContainer /");
        movedRootCard.setRootNo(container.getRootNo());
        movedRootCard.setSeqNo(onFocusCardPosition);
        final int pastLocationContainerPosition = movedRootCard.getContainerNo();
        final int containerQuantityToBeAdjusted = targetContainerPosition - pastLocationContainerPosition;
        viewModel.adjustListContainerNo(movedCardList, containerQuantityToBeAdjusted);
        //updateData
        List<CardDto> toUpdateCardList = new ArrayList<>();
        toUpdateCardList.addAll(movedCardList);
        toUpdateCardList.addAll(pastLocationNextCardList);
        toUpdateCardList.addAll(currentNextCards);
        //movingCards, nextCards, targetContainerNextCards.
        viewModel.updateCards(toUpdateCardList, makeDropEventUiUpdateActionRunnable(movedCardList, movedRootCard, cardRecyclerView, viewModel));
        return true;
    }

    private Runnable makeDropEventUiUpdateActionRunnable(List<CardDto> movedCardList, CardDto movedRootCard, CardRecyclerView cardRecyclerView, CardViewModel viewModel) {
        return () -> {
            Handler uiHandler = ((MainCardActivity) cardRecyclerView.getContext()).getMainHandler();
            //add movingCards into AllData
            viewModel.addToAllData(movedCardList.toArray(new CardDto[0]));
            //update UI
            //(later) animate blow away kicked out card && after notifyInserted, animate return.
            //
            //add movingRootCard into mPresentData && target container CardRecyclerView.notifyItemInserted.
            //setFocusCardPosition to target container. && {later} smoothScrollToPosition(movingRootCard.getSeqNo) && presentChildren().
            viewModel.addSinglePresentCardDto(movedRootCard);
            Queue<Pair<Integer, Runnable>> delayActionQueue = new LinkedList<>();
            if (cardRecyclerView.getAdapter() == null)
                return;
            Runnable itemInsertAction = () -> cardRecyclerView.getAdapter().notifyItemInserted(movedRootCard.getSeqNo());
            Runnable smoothScrollToPositionAction = () -> cardRecyclerView.scrollToPosition(movedRootCard.getSeqNo());
            Runnable presentChildrenAction = () -> viewModel.presentChildren(cardRecyclerView, movedRootCard.getContainerNo(), movedRootCard.getSeqNo());
            Runnable finishToastMessageAction = () -> SingleToaster.makeTextShort(cardRecyclerView.getContext(), "카드 세트가 이동되었습니다.").show();
            delayActionQueue.offer(Pair.create(ACTION_SCROLL_TO_POSITION, smoothScrollToPositionAction));
            delayActionQueue.offer(Pair.create(ACTION_PRESENT_CHILDREN, presentChildrenAction));
            delayActionQueue.offer(Pair.create(ACTION_SHOW_MOVE_SUCCESS_MESSAGE, finishToastMessageAction));
            uiHandler.post(itemInsertAction);
            enqueueDelayedActions(delayActionQueue, uiHandler);
        };
    }

    private void enqueueDelayedActions(Queue<Pair<Integer, Runnable>> actionsQueue, Handler uiHandler) {
        if (actionsQueue.isEmpty())
            return;
        Optional.ofNullable(actionsQueue.poll()).ifPresent(pair -> {
            if (pair.first == ACTION_SCROLL_TO_POSITION) {
                uiHandler.postDelayed(pair.second, 800 + 30);
            }
            if (pair.first == ACTION_PRESENT_CHILDREN) {
                uiHandler.postDelayed(pair.second, 250 + 100 + 30);
            }
            if (pair.first == ACTION_SHOW_MOVE_SUCCESS_MESSAGE) {
                uiHandler.postDelayed(pair.second, 30);
            }
        });
        enqueueDelayedActions(actionsQueue, uiHandler);
    }

    private boolean handleMoveServiceDragEnteredEvent(CardRecyclerView cardRecyclerView) {
        final CardAdapter cardAdapter = cardRecyclerView.getAdapter();
        if (cardAdapter == null)
            return false;
        final int containerPosition = cardAdapter.getContainerPosition();
        final boolean noCardItem = containerPosition == -1;
        if (noCardItem)
            return false;
        final CardRecyclerView.ScrollControllableLayoutManager cardRecyclerViewLayoutManager = cardRecyclerView.getLayoutManager();
        if (cardRecyclerViewLayoutManager == null)
            return false;
        final CardViewModel viewModel = ((MainCardActivity) cardRecyclerView.getContext()).getCardViewModel();
        if (containerPosition + 1 > viewModel.presentContainerCount())
            return false;
        if (viewModel.getPresentData().size() < containerPosition + 1)
            return false;
        final int cardCount = viewModel.getPresentData().get(containerPosition).size();
        Container container = viewModel.getContainer(containerPosition);
        final int onFocusCardPosition = container.getFocusCardPosition();
        boolean leftCardArrowNeeded = true;
        boolean rightCardArrowNeeded = true;
        if (onFocusCardPosition == 0) {
            leftCardArrowNeeded = false;
        }
        if (onFocusCardPosition + 1 == cardCount) {
            rightCardArrowNeeded = false;
        }
        if (leftCardArrowNeeded && rightCardArrowNeeded)
            cardRecyclerViewLayoutManager.showCardArrows(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_TWO_WAY_ARROW);
        if (!leftCardArrowNeeded && rightCardArrowNeeded)
            cardRecyclerViewLayoutManager.showCardArrows(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_RIGHT_ARROW);
        if (leftCardArrowNeeded && !rightCardArrowNeeded)
            cardRecyclerViewLayoutManager.showCardArrows(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_LEFT_ARROW);
        if (!leftCardArrowNeeded && !rightCardArrowNeeded)
            cardRecyclerViewLayoutManager.showCardArrows(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_NO_ARROW);
        return true;
    }

    private boolean handleMoveServiceDragLocationEvent(CardRecyclerView cardRecyclerView, float xCoordinate) {
        Handler uiHandler = ((MainCardActivity) cardRecyclerView.getContext()).getMainHandler();
        final CardAdapter cardAdapter = cardRecyclerView.getAdapter();
        if (cardAdapter == null) {
            return false;
        }
        final int containerPosition = cardAdapter.getContainerPosition();
        final boolean noCardItem = containerPosition == -1;
        if (noCardItem) {
            return false;
        }
        final CardRecyclerView.ScrollControllableLayoutManager cardRecyclerViewLayoutManager = cardRecyclerView.getLayoutManager();
        if (cardRecyclerViewLayoutManager == null) {
            return false;
        }
        if (cardRecyclerViewLayoutManager.isDeployingArrow()) {
            return false;
        }
        final ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) cardRecyclerView.getParent().getParent();
        final ContainerRecyclerView.ItemScrollingControlLayoutManager containerRecyclerViewLayoutManager = containerRecyclerView.getLayoutManager();
        if (containerRecyclerViewLayoutManager == null) {
            return false;
        }
        final CardViewModel viewModel = ((MainCardActivity) cardRecyclerView.getContext()).getCardViewModel();
        if (viewModel.getPresentData().size() < containerPosition + 1)
            return false;
        final int cardItemCount = viewModel.getPresentData().get(containerPosition).size();
        final Container container = viewModel.getContainer(containerPosition);
        final int onFocusCardPosition = container.getFocusCardPosition();
        final int screenWidth = DisplayUtil.getScreenWidthAsPixel(cardRecyclerView.getContext());
        final int MOVE_BOUNDARY_WIDTH = 200;
        final boolean onLeftBoundary = xCoordinate < MOVE_BOUNDARY_WIDTH;
        final boolean hasPrevPosition = onFocusCardPosition != 0;
        final boolean onRightBoundary = xCoordinate > screenWidth - MOVE_BOUNDARY_WIDTH;
        final boolean hasNextPosition = onFocusCardPosition + 1 != cardItemCount;
        if (onLeftBoundary && hasPrevPosition) {
            cardRecyclerViewLayoutManager.setDeployingArrow(true);
            final int prevCardPosition = onFocusCardPosition - 1;
            uiHandler.postDelayed(() -> cardRecyclerView.smoothScrollToPosition(prevCardPosition), 300);
            return true;
        }
        if (onRightBoundary && hasNextPosition) {
            cardRecyclerViewLayoutManager.setDeployingArrow(true);
            final int nextCardPosition = onFocusCardPosition + 1;
            uiHandler.postDelayed(() -> cardRecyclerView.smoothScrollToPosition(nextCardPosition), 300);
        }
        return true;
    }

    private boolean handleMoveServiceDragExitedEvent(CardRecyclerView cardRecyclerView) {
        CardRecyclerView.ScrollControllableLayoutManager cardRecyclerViewLayoutManager = cardRecyclerView.getLayoutManager();
        if (cardRecyclerViewLayoutManager == null)
            return false;
        cardRecyclerViewLayoutManager.showCardArrows(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_NO_ARROW);
        return true;
    }

    private boolean handleMoveServiceDragEndedEvent(CardRecyclerView cardRecyclerView) {
        cardRecyclerView.detachOnScrollListenerForHorizontalArrow();
        cardRecyclerView.getContainerRecyclerView().setOnDragMove(false);
        if (cardRecyclerView.getLayoutManager() == null)
            return false;
        cardRecyclerView.getLayoutManager().showCardArrows(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_NO_ARROW);
        return true;
    }
    //****** End - Move Service

}
