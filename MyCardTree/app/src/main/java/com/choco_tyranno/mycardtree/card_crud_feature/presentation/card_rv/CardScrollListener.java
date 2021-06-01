package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.NullPassUtil;

import java.util.Objects;
import java.util.Optional;

public class CardScrollListener extends RecyclerView.OnScrollListener {
    private final OnFocusChangedListener focusChangedListener;
    private final OnScrollStateChangeListener scrollStateChangeListener;
    private LinearLayoutManager layoutManager;
    private int registeredPosition;
    private int containerPosition;
    private int centerX;
    private Runnable finalEvent;

    private int getCenterX() {
        return centerX;
    }

    private void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public CardScrollListener(OnFocusChangedListener focusListener, OnScrollStateChangeListener stateListener) {
        Logger.message("cardScrollListener#constructor");
        this.focusChangedListener = focusListener;
        this.scrollStateChangeListener = stateListener;
        this.layoutManager = null;
        this.finalEvent = null;
        this.registeredPosition = RecyclerView.NO_POSITION;
        this.containerPosition = -1;
        this.centerX = -1;
    }

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        Logger.message("cardScrollLsn#setLM");
        this.layoutManager = layoutManager;
    }

    public void setContainerPosition(int containerPosition) {
        Logger.message("cardScrollListener#setContainerPos : " + containerPosition);
        this.containerPosition = containerPosition;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                Logger.hotfixMessage("SCROLL_STATE_IDLE");
                Optional.ofNullable(finalEvent).ifPresent(Runnable::run);
                finalEvent = null;
                scrollStateChangeListener.onStateIdle(recyclerView, containerPosition);
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                Logger.hotfixMessage("SCROLL_STATE_DRAGGING");
                scrollStateChangeListener.onStateDragging(recyclerView, containerPosition);
            case RecyclerView.SCROLL_STATE_SETTLING:
                Logger.hotfixMessage("SCROLL_STATE_SETTLING");
                break;
        }

    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (layoutManager == null) {
            Logger.message("cardScrollLsn#onScrolled : lm null");
            return;
        }
        if (containerPosition == -1) {
            Logger.message("cardScrollLsn#onScrolled : containerPos is -1");
            return;
        }
        if (getCenterX() == -1) {
            setCenterX(((Activity) recyclerView.getContext()).getWindowManager().getCurrentWindowMetrics().getBounds().centerX());
        }
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (firstVisibleItemPosition == -1) {
            return;
        }

        if (registeredPosition == -1) {
            registeredPosition = firstVisibleItemPosition;
        }

        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

        if (firstVisibleItemPosition == lastVisibleItemPosition) {
            handelSingleItemVisible(recyclerView, firstVisibleItemPosition);
        } else {
            handleMultiItemVisible(recyclerView, firstVisibleItemPosition, lastVisibleItemPosition);
        }
    }

    private synchronized void handelSingleItemVisible(RecyclerView recyclerView, int itemPosition) {
        Logger.hotfixMessage("handleSingle/itemPos:" + itemPosition + "/reg Pos:" + registeredPosition);
        if (itemPosition == registeredPosition) {
            return;
        }
        if (itemPosition > registeredPosition) {
            registeredPosition = itemPosition;
            finalEvent = () -> focusChangedListener.onNextFocused(recyclerView, containerPosition, itemPosition);
            return;
        }
        registeredPosition = itemPosition;
        finalEvent = () -> focusChangedListener.onPreviousFocused(recyclerView, containerPosition, itemPosition);
    }

    private synchronized void handleMultiItemVisible(RecyclerView recyclerView, int firstItemPosition, int lastItemPosition) {
        Logger.hotfixMessage("handleMulti/f itemPos:" + firstItemPosition + "/l itemPos :" + lastItemPosition + "/reg Pos:" + registeredPosition);
        if (firstItemPosition == registeredPosition) {
            float b = Objects.requireNonNull(layoutManager.getChildAt(1)).getX();
            if (b > centerX) {
                return;
            }
            Logger.hotfixMessage("onNext / to pos :" + lastItemPosition);
            registeredPosition = lastItemPosition;
            finalEvent = () -> focusChangedListener.onNextFocused(recyclerView, containerPosition, lastItemPosition);
            return;
        }

        if (lastItemPosition == registeredPosition) {
            float b = Objects.requireNonNull(layoutManager.getChildAt(1)).getX();
            if (b <= centerX) {
                return;
            }
            Logger.hotfixMessage("onPrev / to pos :" + firstItemPosition);
            registeredPosition = firstItemPosition;
            finalEvent = () -> focusChangedListener.onPreviousFocused(recyclerView, containerPosition, firstItemPosition);
            return;
        }

        if (firstItemPosition < registeredPosition && lastItemPosition < registeredPosition){
            float lastItemX = Objects.requireNonNull(layoutManager.getChildAt(1)).getX();
            if (lastItemX <= centerX){
                registeredPosition = lastItemPosition;
                finalEvent = () -> focusChangedListener.onPreviousFocused(recyclerView, containerPosition, lastItemPosition);
            }else {
                registeredPosition = firstItemPosition;
                finalEvent = () -> focusChangedListener.onPreviousFocused(recyclerView, containerPosition, firstItemPosition);
            }
        }


    }

    public interface OnFocusChangedListener {
        void onNextFocused(RecyclerView view, int containerPosition, int cardPosition);

        void onPreviousFocused(RecyclerView view, int containerPosition, int cardPosition);
    }

    public interface OnScrollStateChangeListener {
        void onStateIdle(RecyclerView view, int containerPosition);

        void onStateDragging(RecyclerView view, int containerPosition);
    }
}
