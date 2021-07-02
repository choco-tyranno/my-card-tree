package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardRecyclerView;

import java.util.HashMap;
import java.util.LinkedList;
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
        super.setLayoutManager(layout);
        layout.setContainerRecyclerView(ContainerRecyclerView.this);
    }

    @Nullable
    @Override
    public CardContainerViewHolder findViewHolderForAdapterPosition(int position) {
        ViewHolder viewHolder = super.findViewHolderForAdapterPosition(position);
        final boolean mDataSetHasChangedAfterLayout = viewHolder == null;
        if (mDataSetHasChangedAfterLayout) {
            int waitSec = 0;
            Handler mainHandler = ((MainCardActivity) this.getContext()).getMainHandler();
            while (waitSec < 10) {
                mainHandler.postDelayed(() -> {
                }, 1000);
                waitSec++;
                ViewHolder testViewHolder = super.findViewHolderForAdapterPosition(position);
                if (testViewHolder != null) {
                    viewHolder = testViewHolder;
                    break;
                }
            }
            if (viewHolder == null)
                return null;
        }
        if (viewHolder instanceof CardContainerViewHolder)
            return (CardContainerViewHolder) viewHolder;
        throw new RuntimeException("[className : " + viewHolder.getClass().getName() + "]containerRecyclerView#findViewHolderForAdapterPosition(" + position + ") - the position item is not CardContainerViewHolder instance.");
    }

    public ItemScrollingControlLayoutManager getLayoutManager() {
        return (ItemScrollingControlLayoutManager) super.getLayoutManager();
    }

    //Nested recyclerView listener's events handled singly by ItemScrollingControlLayoutManager.
    public static class ItemScrollingControlLayoutManager extends LinearLayoutManager {
        private RecyclerView containerRecyclerView;
        private Runnable containerScrollAction;
        private Runnable exitAction;
        private int scrollOccupyingContainerPosition;
        private Queue<Runnable> scrollLockedQueue;
        private Runnable cardMoveRollbackAction;
        private HashMap<Integer, Runnable> dragMoveEndActionMap;
        private static final int VERTICAL_ARROW_REMOVE_ACTION = 1;
        private static final int CARD_ROLLBACK_ACTION = 2;
        public static final int NO_SCROLL_OCCUPYING_POSITION = -1;

        public void waitLayoutForPresentChildrenIfNecessary(int containerPosition, Runnable presentChildAction) {
            Logger.hotfixMessage("rootCard - containerPosition : " + containerPosition + "/present Container count :" + this.getItemCount());
            final int emptySpaceCount = 1;
            int waitCount = 0;
            while (waitCount < 30) {
                mainHandler().postDelayed(() -> {
                }, 1000);
                int itemCount = this.getItemCount() - emptySpaceCount;
                if (itemCount == containerPosition + 1) {
                    presentChildAction.run();
                    break;
                }
                waitCount++;
            }
        }

        public void putVerticalArrowRemoveActionForDragMoveEnd(Runnable action) {
            dragMoveEndActionMap.put(VERTICAL_ARROW_REMOVE_ACTION, action);
        }

        public boolean hasVerticalArrowRemoveAction() {
            boolean verticalArrowActionExist = false;
            if (dragMoveEndActionMap.containsKey(VERTICAL_ARROW_REMOVE_ACTION))
                verticalArrowActionExist = true;
            return verticalArrowActionExist;
        }

        public void executeDragMoveEndAction() {
            mainHandler().postDelayed(() -> {
                if (dragMoveEndActionMap.containsKey(VERTICAL_ARROW_REMOVE_ACTION)) {
                    dragMoveEndActionMap.get(VERTICAL_ARROW_REMOVE_ACTION).run();
                    dragMoveEndActionMap.remove(VERTICAL_ARROW_REMOVE_ACTION);
                }
                if (dragMoveEndActionMap.containsKey(CARD_ROLLBACK_ACTION)) {
                    dragMoveEndActionMap.get(CARD_ROLLBACK_ACTION).run();
                    dragMoveEndActionMap.remove(CARD_ROLLBACK_ACTION);
                }
            }, 500);
        }

        public boolean hasRollbackAction() {
            boolean cardRollbackActionExist = false;
            if (dragMoveEndActionMap.containsKey(CARD_ROLLBACK_ACTION))
                cardRollbackActionExist = true;
            return cardRollbackActionExist;
        }

        public void putCardRollbackActionForDragMoveEnd(Runnable action) {
            dragMoveEndActionMap.put(CARD_ROLLBACK_ACTION, action);
        }

//        public void executeRollback() {
//            mainHandler().postDelayed(cardMoveRollbackAction, 500);
//            cardMoveRollbackAction = null;
//        }

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
            if (containerRecyclerView == null)
                throw new RuntimeException("ItemScrollingControlLayoutManager#getCardRecyclerViewPosition - containerRecyclerView is null");
            final int pos = containerRecyclerView.getChildAdapterPosition((View) childCardRecyclerView.getParent());
            Logger.hotfixMessage("pos:" + pos);
            return 0;
        }

        public void setContainerRecyclerView(ContainerRecyclerView containerRecyclerView) {
            this.containerRecyclerView = containerRecyclerView;
        }

        /*
         * Below :
         * As ContainerRecyclerViews scroll
         * */

        public void scrollDelayed(int delayed) {
            if (containerRecyclerView == null)
                return;
            mainHandler().postDelayed(() -> {
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
            if (containerRecyclerView == null)
                return null;
            return ((MainCardActivity) containerRecyclerView.getContext()).getMainHandler();
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
                scrollLockedQueue.poll().run();
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
