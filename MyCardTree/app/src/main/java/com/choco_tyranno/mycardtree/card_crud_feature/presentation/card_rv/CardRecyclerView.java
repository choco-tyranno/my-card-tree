package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerRecyclerView;

public class CardRecyclerView extends RecyclerView {

    public CardRecyclerView(@NonNull Context context) {
        super(context);
    }

    public CardRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CardRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayoutManager(@Nullable ScrollingControlLayoutManager layout) {
        if (layout == null) {
            super.setLayoutManager(null);
            return;
        }
        clearLayoutManager();
        super.setLayoutManager(layout);
        layout.setRecyclerView(this);
    }

    public void clearLayoutManager() {
        if (getLayoutManager() != null)
            super.setLayoutManager(null);
    }

    public void suppressLayout() {
        this.suppressLayout(true);
    }

    public void unsuppressLayout() {
        this.suppressLayout(false);
    }

    @Nullable
    @Override
    public ScrollingControlLayoutManager getLayoutManager() {
        return (ScrollingControlLayoutManager) super.getLayoutManager();
    }

    /**
     * Nested RecyclerView the child item of ContainerRecyclerView delegates scroll control to ItemScrollingControlLayoutManager.
     *
     * @see ContainerRecyclerView.ItemScrollingControlLayoutManager
     */
    public static class ScrollingControlLayoutManager extends LinearLayoutManager {
        private boolean scrollable;
        private CardRecyclerView mRecyclerView;
        private Runnable scrollAction;

        public boolean hasScrollAction() {
            return scrollAction != null;
        }

        public void setScrollAction(Runnable scrollAction) {
            this.scrollAction = scrollAction;
        }

        public void executeScroll() {
            if (scrollAction != null)
                scrollAction.run();
        }

        public void clearScrollAction(){
            this.scrollAction = null;
        }

        @Override
        public boolean canScrollHorizontally() {
            return scrollable;
        }

        public ScrollingControlLayoutManager(Context context) {
            super(context);
            this.scrollable = true;
        }

        public ScrollingControlLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            this.scrollable = true;
        }

        public ScrollingControlLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
