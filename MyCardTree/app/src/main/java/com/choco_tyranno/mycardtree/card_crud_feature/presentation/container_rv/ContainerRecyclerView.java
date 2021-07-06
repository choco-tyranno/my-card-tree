package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardRecyclerView;
import com.choco_tyranno.mycardtree.databinding.ItemCardcontainerBinding;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContainerRecyclerView extends RecyclerView {

    public ContainerRecyclerView(@NonNull Context context) {
        super(context);
    }

    public ContainerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContainerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayoutManager(@Nullable ItemScrollingControlLayoutManager layout) {
        if (layout == null)
            throw new RuntimeException("ContainerRecyclerView#setLayoutManager - ItemScrollingControlLayoutManager.class layout is null");
        super.setLayoutManager(layout);
        layout.setContainerRecyclerView(ContainerRecyclerView.this);
    }

    public ItemScrollingControlLayoutManager getLayoutManager() {
        return (ItemScrollingControlLayoutManager) super.getLayoutManager();
    }

    //Nested recyclerView listener's events handled singly by ItemScrollingControlLayoutManager.
    public static class ItemScrollingControlLayoutManager extends LinearLayoutManager {
        private ContainerRecyclerView mContainerRecyclerView;
        private ImageView topArrow;
        private ImageView bottomArrow;
        private Runnable containerScrollAction;
        private Runnable exitAction;
        // release.
        public static final int DIRECTION_NO_ARROW = -1;
        public static final int DIRECTION_TWO_WAY_ARROW = 0;
        public static final int DIRECTION_TOP_ARROW = 1;
        public static final int DIRECTION_BOTTOM_ARROW = 2;
        private AtomicBoolean layoutArrows;
        private AtomicBoolean containerRollback;
        private AtomicBoolean movingDragExited;

        private int scrollOccupyingContainerPosition;
        private Queue<Runnable> scrollLockedQueue;
        private Runnable movedCardsRollbackAction;
        private HashMap<Integer, Runnable> dragMoveEndActionMap;
        private static final int VERTICAL_ARROW_REMOVE_ACTION = 1;
        private static final int CARD_ROLLBACK_ACTION = 2;
        public static final int NO_SCROLL_OCCUPYING_POSITION = -1;


        public void clearListener(int startPosition) {
            int itemCount = getItemCount();
            for (int i = startPosition; i < itemCount; i++) {
                CardRecyclerView cardRecyclerView = findCardRecyclerView(i);
                if (cardRecyclerView == null) {
                    continue;
                }
                cardRecyclerView.clearOnScrollListeners();
            }
        }

        private CardRecyclerView findCardRecyclerView(int position) {
            RecyclerView.ViewHolder viewHolder = mContainerRecyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder instanceof CardContainerViewHolder) {
                return ((CardContainerViewHolder) viewHolder).getBinding().cardRecyclerview;
            }
            return null;
        }

        public boolean isContainerRollback() {
            if (containerRollback == null)
                throw new RuntimeException("ContainerRecyclerView.ItemScrollingControlLayoutManager - #isContainerRollback() AtomicBoolean.class containerRollback is null");
            return containerRollback.get();
        }

        public void setContainerRollback(boolean rollback) {
            this.containerRollback.set(rollback);
        }

        public void onDragEnd(@Nullable Runnable rollbackAction) {
            if (rollbackAction != null) {
                setContainerRollback(true);
                rollbackAction.run();
                Objects.requireNonNull(mainHandler()).postDelayed(() ->
                        setContainerRollback(false), 260);
            }
        }

        public void onDragStart() {
            mainHandler().postDelayed(()->{
                final int firstVisibleContainerPosition = findFirstCompletelyVisibleItemPosition();
                final int lastVisibleContainerPosition = findLastCompletelyVisibleItemPosition();
                final int itemCount = getItemCount();
                boolean topContainerArrowNeeded = false;
                boolean bottomContainerArrowNeeded = false;
                if (firstVisibleContainerPosition != 0) {
                    topContainerArrowNeeded = true;
                }
                if (lastVisibleContainerPosition < itemCount - 1) {
                    bottomContainerArrowNeeded = true;
                }
                if (topContainerArrowNeeded && bottomContainerArrowNeeded) {
                    showCardArrowsDelayed(ContainerRecyclerView.ItemScrollingControlLayoutManager.DIRECTION_TWO_WAY_ARROW);
                }
                if (topContainerArrowNeeded && !bottomContainerArrowNeeded) {
                    showCardArrowsDelayed(ContainerRecyclerView.ItemScrollingControlLayoutManager.DIRECTION_TOP_ARROW);
                }
                if (!topContainerArrowNeeded && bottomContainerArrowNeeded) {
                    showCardArrowsDelayed(ContainerRecyclerView.ItemScrollingControlLayoutManager.DIRECTION_BOTTOM_ARROW);
                }
                if (!topContainerArrowNeeded && !bottomContainerArrowNeeded) {
                    showCardArrowsDelayed(ContainerRecyclerView.ItemScrollingControlLayoutManager.DIRECTION_NO_ARROW);
                }
            },260);
        }

        private void initArrows() {
            if (mContainerRecyclerView == null)
                return;
            ViewGroup viewGroup = ((ViewGroup) mContainerRecyclerView.getParent());
            this.topArrow = viewGroup.findViewById(R.id.prev_container_arrow);
            this.bottomArrow = viewGroup.findViewById(R.id.next_container_arrow);
            layoutArrows = new AtomicBoolean(false);
        }

        private void showTopArrow() {
            if (topArrow.getVisibility() == INVISIBLE)
                topArrow.setVisibility(VISIBLE);
        }

        private void hideTopArrow() {
            if (topArrow.getVisibility() == VISIBLE)
                topArrow.setVisibility(INVISIBLE);
        }

        private void showBottomArrow() {
            if (bottomArrow.getVisibility() == INVISIBLE)
                bottomArrow.setVisibility(VISIBLE);
        }

        private void hideBottomArrow() {
            if (bottomArrow.getVisibility() == VISIBLE)
                bottomArrow.setVisibility(INVISIBLE);
        }

        private void showCardArrows(int direction) {
            switch (direction) {
                case DIRECTION_TWO_WAY_ARROW:
                    showTopArrow();
                    showBottomArrow();
                    break;
                case DIRECTION_TOP_ARROW:
                    showTopArrow();
                    hideBottomArrow();
                    break;
                case DIRECTION_BOTTOM_ARROW:
                    showBottomArrow();
                    hideTopArrow();
                    break;
                case DIRECTION_NO_ARROW:
                    hideTopArrow();
                    hideBottomArrow();
                    break;
            }

        }

        public void showCardArrowsDelayed(int direction) {
            final int duration = 260;
            layoutArrows.set(true);
            Objects.requireNonNull(mainHandler()).postDelayed(() -> {
                if (!isMovingDragExited() || direction == DIRECTION_NO_ARROW) {
                    showCardArrows(direction);
                }
                layoutArrows.set(false);
            }, duration);
        }

        public void setMovingDragExited(boolean exited) {
            movingDragExited.set(exited);
        }

        public boolean isMovingDragExited() {
            if (movingDragExited == null)
                throw new RuntimeException("#isMovingDragExited() - AtomicBoolean.Class movingDragExited is null");
            return movingDragExited.get();
        }

        public void smoothScrollToPosition(int toPosition) {
            mContainerRecyclerView.smoothScrollToPosition(toPosition);
        }

        /*end*/

        public ItemScrollingControlLayoutManager(Context context) {
            super(context);
            scrollOccupyingContainerPosition = NO_SCROLL_OCCUPYING_POSITION;
            this.scrollLockedQueue = new LinkedList<>();
        }

        public ItemScrollingControlLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            scrollOccupyingContainerPosition = NO_SCROLL_OCCUPYING_POSITION;
            this.scrollLockedQueue = new LinkedList<>();
            this.dragMoveEndActionMap = new HashMap<>();
        }

        public ItemScrollingControlLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            scrollOccupyingContainerPosition = NO_SCROLL_OCCUPYING_POSITION;
            this.scrollLockedQueue = new LinkedList<>();
        }

        public int getCardRecyclerViewPosition(@NonNull CardRecyclerView childCardRecyclerView) {
            if (mContainerRecyclerView == null)
                throw new RuntimeException("ItemScrollingControlLayoutManager#getCardRecyclerViewPosition - mContainerRecyclerView is null");
            final int pos = mContainerRecyclerView.getChildAdapterPosition((View) childCardRecyclerView.getParent());
            return 0;
        }

        public void setContainerRecyclerView(ContainerRecyclerView containerRecyclerView) {
            this.mContainerRecyclerView = containerRecyclerView;
            initArrows();
            this.containerRollback = new AtomicBoolean(false);
            this.movingDragExited = new AtomicBoolean(false);
        }

        /*
         * Below :
         * As ContainerRecyclerViews scroll
         * */

        public void scrollDelayed(int delayed) {
            if (mContainerRecyclerView == null)
                return;
            Objects.requireNonNull(mainHandler()).postDelayed(() -> {
                if (exitAction != null) {
                    clearContainerScrollAction();
                    exitAction.run();
                    clearContainerExitAction();
                    return;
                }
                containerScrollAction.run();
                clearContainerScrollAction();
            }, delayed);
        }

        private Handler mainHandler() {
            if (mContainerRecyclerView == null)
                return null;
            return ((MainCardActivity) mContainerRecyclerView.getContext()).getMainHandler();
        }

        public boolean hasScrollAction() {
            return containerScrollAction != null;
        }

        public void setContainerScrollAction(Runnable containerScrollAction) {
            this.containerScrollAction = containerScrollAction;
        }

        public void clearContainerScrollAction() {
            this.containerScrollAction = null;
        }

        public void setExitAction(Runnable exitAction) {
            this.exitAction = exitAction;
        }

        public void clearContainerExitAction() {
            this.exitAction = null;
        }

        /*
         * Below :
         * Children Nested RecyclerView scrolls control
         * */

        public void unlockAll() {
            while (!scrollLockedQueue.isEmpty()) {
                Objects.requireNonNull(scrollLockedQueue.poll()).run();
            }
        }

        public void enqueueLockedItem(Runnable unlockAction) {
            scrollLockedQueue.add(unlockAction);
        }

        public void registerScrollOccupyingContainerPosition(int position) {
            scrollOccupyingContainerPosition = position;
        }

        public int getScrollOccupyingContainerPosition() {
            return scrollOccupyingContainerPosition;
        }

        public void unregisterOccupyingContainerPosition() {
            this.scrollOccupyingContainerPosition = NO_SCROLL_OCCUPYING_POSITION;
        }

        public boolean hasRegisteredPosition() {
            return scrollOccupyingContainerPosition != NO_SCROLL_OCCUPYING_POSITION;
        }
    }
}
