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
        if (viewHolder instanceof CardContainerViewHolder)
            return (CardContainerViewHolder) viewHolder;
        throw new RuntimeException("containerRecyclerView#findViewHolderForAdapterPosition() - the position item is not CardContainerViewHolder instance.");
    }

    public ItemScrollingControlLayoutManager getLayoutManager() {
        return (ItemScrollingControlLayoutManager) super.getLayoutManager();
    }

    public static class ItemScrollingControlLayoutManager extends LinearLayoutManager {
        private RecyclerView containerRecyclerView;
        private Runnable containerScrollAction;
        private Runnable exitAction;
        private int scrollOccupyingContainerPosition;
        private Queue<Runnable> scrollLockedQueue;
        public static final int NO_SCROLL_OCCUPYING_POSITION = -1;

        public ItemScrollingControlLayoutManager(Context context) {
            super(context);
            scrollOccupyingContainerPosition = NO_SCROLL_OCCUPYING_POSITION;
            this.scrollLockedQueue = new LinkedList<>();
        }

        public ItemScrollingControlLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            scrollOccupyingContainerPosition = NO_SCROLL_OCCUPYING_POSITION;
            this.scrollLockedQueue = new LinkedList<>();
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
