package com.choco_tyranno.team_tree.presentation.card_rv;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.presentation.MainCardActivity;
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerRecyclerView;

import java.util.concurrent.atomic.AtomicBoolean;

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
    public CardAdapter getAdapter() {
        return (CardAdapter) super.getAdapter();
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
        private ImageView leftArrow;
        private ImageView rightArrow;
        public static final int DIRECTION_NO_ARROW = -1;
        public static final int DIRECTION_TWO_WAY_ARROW = 0;
        public static final int DIRECTION_LEFT_ARROW = 1;
        public static final int DIRECTION_RIGHT_ARROW = 2;
        private AtomicBoolean layoutArrows;
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

        public boolean isLayoutArrows() {
            return layoutArrows.get();
        }


        private void initArrows() {
            if (mRecyclerView == null)
                return;
            ViewGroup viewGroup = ((ViewGroup) mRecyclerView.getParent());
            this.leftArrow = viewGroup.findViewById(R.id.prev_card_arrow);
            this.rightArrow = viewGroup.findViewById(R.id.next_card_arrow);
            layoutArrows = new AtomicBoolean(false);
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

        private void showCardArrows(int direction) {
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
            final int duration = 260;
            layoutArrows.set(true);
            mainHandler().postDelayed(() -> {
                if ((!isMovingDragExited() && !isMovingDragEnded()) || direction == DIRECTION_NO_ARROW) {
                    showCardArrows(direction);
                    if (isMovingDragExited())
                        setMovingDragExited(false);
                    if (isMovingDragEnded())
                        setMovingDragEnded(false);
                }
                layoutArrows.set(false);
            }, duration);
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
            initArrows();
            this.movingDragEnded = new AtomicBoolean(false);
            this.movingDragExited = new AtomicBoolean(false);
        }

    }
}
