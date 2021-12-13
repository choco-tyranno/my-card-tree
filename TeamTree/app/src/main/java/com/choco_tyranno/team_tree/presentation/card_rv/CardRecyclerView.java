package com.choco_tyranno.team_tree.presentation.card_rv;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.container_rv.Container;
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerRecyclerView;
import com.choco_tyranno.team_tree.presentation.main.MainCardActivity;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardRecyclerView extends RecyclerView {
    public static final int DEFAULT_CARD_POSITION = 0;
    private OnScrollListenerForHorizontalArrow onScrollListenerForHorizontalArrow;
//    private boolean onDragMove = false;
    private final HashMap<Integer, Runnable> scrolledActionMap = new HashMap<>();
    private final static int ACTION_CHANGING_FOCUS = 1;
    private final static int ACTION_SHOW_HORIZONTAL_ARROW = 2;

    public CardRecyclerView(@NonNull Context context) {
        super(context);
    }

    public CardRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CardRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayoutManager(@Nullable ScrollControllableLayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout != null)
            layout.setRecyclerView(this);
    }

    public void clearLayoutManager() {
        super.setLayoutManager(null);
    }

    public void suppressLayout() {
        this.suppressLayout(true);
    }

    public void unsuppressLayout() {
        this.suppressLayout(false);
    }

    @Override
    public CardAdapter getAdapter() {
        return (CardAdapter) super.getAdapter();
    }

    public ContainerRecyclerView getContainerRecyclerView() {
        return (ContainerRecyclerView) this.getParent().getParent();
    }

    @Nullable
    @Override
    public ScrollControllableLayoutManager getLayoutManager() {
        return (ScrollControllableLayoutManager) super.getLayoutManager();
    }

    public void attachOnScrollListenerForHorizontalArrow() {
        onScrollListenerForHorizontalArrow = new OnScrollListenerForHorizontalArrow();
        addOnScrollListener(onScrollListenerForHorizontalArrow);
    }

    public void detachOnScrollListenerForHorizontalArrow() {
        if (onScrollListenerForHorizontalArrow != null)
            removeOnScrollListener(onScrollListenerForHorizontalArrow);
        onScrollListenerForHorizontalArrow = null;
    }

    public void postChangingFocusAction(Runnable focusChangeAction) {
        synchronized (scrolledActionMap) {
            scrolledActionMap.put(ACTION_CHANGING_FOCUS, focusChangeAction);
        }
        if (isAllScrolledActionCollected()) {
            executeScrolledAction();
        }
    }

    private void postShowingArrowAction(Runnable showingArrowAction) {
        synchronized (scrolledActionMap) {
            scrolledActionMap.put(ACTION_SHOW_HORIZONTAL_ARROW, showingArrowAction);
        }
        if (isAllScrolledActionCollected()) {
            executeScrolledAction();
        }
    }

    private boolean isAllScrolledActionCollected() {
        boolean allScrolledActionCollected;
        synchronized (scrolledActionMap) {
            allScrolledActionCollected = scrolledActionMap.size() == 2;
        }
        return allScrolledActionCollected;
    }

    private void executeScrolledAction() {
        Runnable changingFocusAction = null;
        Runnable showingHorizontalArrowAction = null;
        if (scrolledActionMap.containsKey(ACTION_CHANGING_FOCUS)) {
            changingFocusAction = scrolledActionMap.get(ACTION_CHANGING_FOCUS);
        }
        if (scrolledActionMap.containsKey(ACTION_SHOW_HORIZONTAL_ARROW)) {
            showingHorizontalArrowAction = scrolledActionMap.get(ACTION_SHOW_HORIZONTAL_ARROW);
        }
        scrolledActionMap.clear();
        Optional.ofNullable(changingFocusAction).ifPresent(Runnable::run);
        Optional.ofNullable(showingHorizontalArrowAction).ifPresent(Runnable::run);
    }

    /**
     * Nested RecyclerView the child item of ContainerRecyclerView delegates scroll control to ItemScrollingControlLayoutManager.
     *
     * @see ContainerRecyclerView.ItemScrollingControlLayoutManager
     */
    public static class ScrollControllableLayoutManager extends LinearLayoutManager {
        private boolean scrollable;
        private CardRecyclerView mRecyclerView;
        private ImageView leftArrow;
        private ImageView rightArrow;
        public static final int DIRECTION_NO_ARROW = -1;
        public static final int DIRECTION_TWO_WAY_ARROW = 0;
        public static final int DIRECTION_LEFT_ARROW = 1;
        public static final int DIRECTION_RIGHT_ARROW = 2;
        private AtomicBoolean deployingArrow;
        private AtomicBoolean movingDragExited;
        private AtomicBoolean movingDragEnded;

        public void setMovingDragExited(boolean exited) {
            movingDragExited.set(exited);
        }

        public boolean isMovingDragExited() {
            if (movingDragExited == null)
                throw new RuntimeException("#isMovingDragExited() - AtomicBoolean.Class movingDragExited is null");
            return movingDragExited.get();
        }

        public void setMovingDragEnded(boolean ended) {
            movingDragEnded.set(ended);
        }

        public boolean isMovingDragEnded() {
            if (movingDragEnded == null)
                throw new RuntimeException("#isMovingDragExited() - AtomicBoolean.Class movingDragExited is null");
            return movingDragEnded.get();
        }

        public void smoothScrollToPosition(int toPosition) {
            mRecyclerView.smoothScrollToPosition(toPosition);
        }

        public boolean isDeployingArrow() {
            return deployingArrow.get();
        }

        public void setDeployingArrow(boolean deployingArrow) {
            this.deployingArrow.set(deployingArrow);
        }

        private void initArrows() {
            if (mRecyclerView == null)
                return;
            ViewGroup viewGroup = ((ViewGroup) mRecyclerView.getParent());
            this.leftArrow = viewGroup.findViewById(R.id.imageView_cardContainer_leftArrow);
            this.rightArrow = viewGroup.findViewById(R.id.imageView_cardContainer_rightArrow);
            deployingArrow = new AtomicBoolean(false);
        }

        private void showLeftArrow() {
            if (leftArrow.getVisibility() == INVISIBLE)
                leftArrow.setVisibility(VISIBLE);
        }

        private void hideLeftArrow() {
            if (leftArrow.getVisibility() == VISIBLE)
                leftArrow.setVisibility(INVISIBLE);
        }

        private void showRightArrow() {
            if (rightArrow.getVisibility() == INVISIBLE)
                rightArrow.setVisibility(VISIBLE);
        }

        private void hideRightArrow() {
            if (rightArrow.getVisibility() == VISIBLE)
                rightArrow.setVisibility(INVISIBLE);
        }

        public void showCardArrows(int direction) {
            switch (direction) {
                case DIRECTION_TWO_WAY_ARROW:
                    showLeftArrow();
                    showRightArrow();
                    break;
                case DIRECTION_LEFT_ARROW:
                    hideRightArrow();
                    showLeftArrow();
                    break;
                case DIRECTION_RIGHT_ARROW:
                    hideLeftArrow();
                    showRightArrow();
                    break;
                case DIRECTION_NO_ARROW:
                    hideLeftArrow();
                    hideRightArrow();
                    break;
            }

        }

        public void showCardArrowsDelayed(int direction) {
            final int delay = 260;
            mainHandler().postDelayed(() ->
                    showCardArrows(direction), delay);
        }

        private Handler mainHandler() {
            if (mRecyclerView == null)
                throw new RuntimeException("CardRecyclerView.ScrollControllableLayoutManager's recyclerView instance is null");
            return ((MainCardActivity) mRecyclerView.getContext()).getMainHandler();
        }

        @Override
        public boolean canScrollHorizontally() {
            return scrollable;
        }

        public ScrollControllableLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            this.scrollable = true;
        }

        public void setScrollable(boolean sign) {
            this.scrollable = sign;
        }

        @Override
        public void onScrollStateChanged(int state) {
            if (mRecyclerView == null)
                return;
            ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) mRecyclerView.getParent().getParent();
            if (containerRecyclerView == null)
                return;
            ContainerRecyclerView.ItemScrollingControlLayoutManager itemScrollingControlLayoutManager = containerRecyclerView.getLayoutManager();
            if (itemScrollingControlLayoutManager == null)
                return;
            CardAdapter cardAdapter = mRecyclerView.getAdapter();
            int position = NO_POSITION;
            if (cardAdapter != null) {
                position = cardAdapter.getContainerPosition();
            }
            if (position == NO_POSITION)
                return;
            final boolean registeredPositionExist = itemScrollingControlLayoutManager.hasRegisteredPosition();
            final boolean scrollOwner = position == itemScrollingControlLayoutManager.getScrollOccupyingContainerPosition();

            if (!registeredPositionExist) {
                unlockScroll();
                itemScrollingControlLayoutManager.registerScrollOccupyingContainerPosition(position);
            }

            if (registeredPositionExist && scrollOwner && state == SCROLL_STATE_IDLE) {
                itemScrollingControlLayoutManager.unregisterOccupyingContainerPosition();
                itemScrollingControlLayoutManager.unlockAll();
            }

            if (registeredPositionExist && !scrollOwner) {
                lockScroll();
                itemScrollingControlLayoutManager.enqueueLockedItem(this::unlockScroll);
            }
            super.onScrollStateChanged(state);
        }

        private void unlockScroll() {
            setScrollable(true);
        }

        private void lockScroll() {
            setScrollable(false);
        }

        private void setRecyclerView(CardRecyclerView recyclerView) {
            this.mRecyclerView = recyclerView;
            initArrows();
            this.movingDragEnded = new AtomicBoolean(false);
            this.movingDragExited = new AtomicBoolean(false);
        }

    }

    public static class OnScrollListenerForHorizontalArrow extends OnScrollListener {
        int registeredCardPosition;

        public OnScrollListenerForHorizontalArrow() {
            registeredCardPosition = -1;
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                CardViewModel viewModel = ((MainCardActivity) recyclerView.getContext()).getCardViewModel();
                CardRecyclerView cardRecyclerView = (CardRecyclerView) recyclerView;
                cardRecyclerView.postShowingArrowAction(() -> {
                    CardRecyclerView.ScrollControllableLayoutManager cardRecyclerViewLayoutManager = cardRecyclerView.getLayoutManager();
                    if (cardRecyclerViewLayoutManager==null)
                        return;
                    ContainerRecyclerView.ItemScrollingControlLayoutManager containerRecyclerViewLayoutManager = cardRecyclerView.getContainerRecyclerView().getLayoutManager();
                    if (containerRecyclerViewLayoutManager==null)
                        return;
                    boolean rightArrowNeeded = true;
                    boolean leftArrowNeeded = true;
                    final int onFocusCardPosition;
                    if (cardRecyclerView.getAdapter()==null)
                        return;
                    final int containerPosition = cardRecyclerView.getAdapter().getContainerPosition();
                    final Container container = viewModel.getContainer(containerPosition);
                    onFocusCardPosition = container.getFocusCardPosition();
                    if (onFocusCardPosition == registeredCardPosition) {
                        if (cardRecyclerViewLayoutManager.isDeployingArrow())
                            cardRecyclerViewLayoutManager.setDeployingArrow(false);
                        return;
                    }
                    final int cardItemCount = viewModel.getPresentData().get(containerPosition).size();
                    if (onFocusCardPosition == 0)
                        leftArrowNeeded = false;
                    if (onFocusCardPosition + 1 == cardItemCount)
                        rightArrowNeeded = false;
                    if (leftArrowNeeded && rightArrowNeeded)
                        cardRecyclerViewLayoutManager.showCardArrows(ScrollControllableLayoutManager.DIRECTION_TWO_WAY_ARROW);
                    if (leftArrowNeeded && !rightArrowNeeded)
                        cardRecyclerViewLayoutManager.showCardArrows(ScrollControllableLayoutManager.DIRECTION_LEFT_ARROW);
                    if (!leftArrowNeeded && rightArrowNeeded)
                        cardRecyclerViewLayoutManager.showCardArrows(ScrollControllableLayoutManager.DIRECTION_RIGHT_ARROW);
                    if (!leftArrowNeeded && !rightArrowNeeded)
                        cardRecyclerViewLayoutManager.showCardArrows(ScrollControllableLayoutManager.DIRECTION_NO_ARROW);
                    containerRecyclerViewLayoutManager.refreshArrows();
                    registeredCardPosition = onFocusCardPosition;
                    cardRecyclerViewLayoutManager.setDeployingArrow(false);
                });
            }
        }
    }
}
