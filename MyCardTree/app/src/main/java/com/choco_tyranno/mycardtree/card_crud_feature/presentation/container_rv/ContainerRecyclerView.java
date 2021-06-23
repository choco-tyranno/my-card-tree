package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContainerRecyclerView extends RecyclerView {
//    private AtomicBoolean computingLayout;

    public ContainerRecyclerView(@NonNull Context context) {
        super(context);
    }

    public ContainerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContainerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public void setComputingLayoutAtomicInstance(AtomicBoolean atomicInstance){
//        this.computingLayout = atomicInstance;
//    }

    public void setLayoutManager(@Nullable ItemScrollingControlLayoutManager layout) {
        super.setLayoutManager(layout);
    }

    public ItemScrollingControlLayoutManager getLayoutManager() {
        return (ItemScrollingControlLayoutManager) super.getLayoutManager();
    }

    public static class ItemScrollingControlLayoutManager extends LinearLayoutManager {
        private boolean scrollItemExist;
        private int scrollOccupyingContainerPosition;
        private Queue<Runnable> scrollLockedQueue;
        public static final int NO_SCROLL_OCCUPYING_POSITION = -1;

        public ItemScrollingControlLayoutManager(Context context) {
            super(context);
            scrollItemExist = false;
            scrollOccupyingContainerPosition = NO_SCROLL_OCCUPYING_POSITION;
            this.scrollLockedQueue = new LinkedList<>();
        }

        public ItemScrollingControlLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            scrollItemExist = false;
            scrollOccupyingContainerPosition = NO_SCROLL_OCCUPYING_POSITION;
            this.scrollLockedQueue = new LinkedList<>();
        }

        public ItemScrollingControlLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            scrollItemExist = false;
            scrollOccupyingContainerPosition = NO_SCROLL_OCCUPYING_POSITION;
            this.scrollLockedQueue = new LinkedList<>();
        }

        public void unlockAll(){
            while (!scrollLockedQueue.isEmpty()){
                scrollLockedQueue.poll().run();
            }
        }

        public void enqueueLockedItem(Runnable unlockAction){
            scrollLockedQueue.add(unlockAction);
        }

        public boolean isScrollingItemExist() {
            return scrollItemExist;
        }

        public void setScrollItemExist(boolean sign) {
            scrollItemExist = sign;
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
