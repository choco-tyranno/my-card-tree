package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerRecyclerView;

public class CardRecyclerView extends RecyclerView {
    public static final int DEFAULT_CARD_POSITION = 0;

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
    public CardAdapter getAdapter(){
        return (CardAdapter)super.getAdapter();
    }

    @Nullable
    @Override
    public ScrollControllableLayoutManager getLayoutManager() {
        return (ScrollControllableLayoutManager) super.getLayoutManager();
    }

    /**
     * Nested RecyclerView the child item of ContainerRecyclerView delegates scroll control to ItemScrollingControlLayoutManager.
     *
     * @see ContainerRecyclerView.ItemScrollingControlLayoutManager
     */
    public static class ScrollControllableLayoutManager extends LinearLayoutManager {
        private boolean scrollable;
        private CardRecyclerView mRecyclerView;
        private Runnable scrollAction;
        private Runnable exitAction;

        public void setExitAction(Runnable exitAction) {
            this.exitAction = exitAction;
        }

        public void clearExitAction() {
            this.exitAction = null;
        }

        public boolean hasScrollAction() {
            return scrollAction != null;
        }

        public void setScrollAction(Runnable scrollAction) {
            this.scrollAction = scrollAction;
        }

        public void scrollDelayed(int delay) {
            if (mRecyclerView == null)
                return;
            if (scrollAction == null)
                return;
            mainHandler().postDelayed(() -> {
                if (exitAction != null) {
                    clearScrollAction();
                    if (exitAction==null)
                        return;
                    exitAction.run();
                    clearExitAction();
                } else {
                    if (scrollAction == null)
                        return;
                    scrollAction.run();
                    clearScrollAction();
                }
            }, delay);
        }

        public void clearScrollAction() {
            this.scrollAction = null;
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

        public ScrollControllableLayoutManager(Context context) {
            super(context);
            this.scrollable = true;
        }

        public ScrollControllableLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            this.scrollable = true;
        }

        public ScrollControllableLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            this.scrollable = true;
        }

        public boolean isScrollable() {
            return scrollable;
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
            CardAdapter cardAdapter = (CardAdapter) mRecyclerView.getAdapter();
            int position = NO_POSITION;
            if (cardAdapter != null) {
                position = cardAdapter.getPosition();
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
        }

    }
}
