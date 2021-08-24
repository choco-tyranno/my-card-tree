package com.choco_tyranno.team_tree.presentation.card_rv;

import android.app.Activity;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.choco_tyranno.team_tree.Logger;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.MainCardActivity;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class CardScrollListener extends RecyclerView.OnScrollListener {
    private OnFocusChangedListener focusChangedListener;
    private OnScrollStateChangeListener onStateChangeListener;
    private CardRecyclerView.ScrollControllableLayoutManager layoutManager;
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


    public CardScrollListener() {
        Logger.message("cardScrollListener#constructor");
        this.registeredPosition = RecyclerView.NO_POSITION;
        this.containerPosition = -1;
        this.centerX = -1;
    }

    public void initialize(CardRecyclerView.ScrollControllableLayoutManager layoutManager, OnFocusChangedListener focusChangedListener, OnScrollStateChangeListener stateChangeListener, int containerPosition) {
        this.layoutManager = null;
        setLayoutManager(layoutManager);
        if (!hasFocusChangeListener()) {
            setFocusChangedListener(focusChangedListener);
        }
        if (!hasStateChangeListener()) {
            setOnStateChangeListener(stateChangeListener);
        }
        this.finalEvent = null;
        this.containerPosition = containerPosition;
    }

    public boolean hasFocusChangeListener() {
        return focusChangedListener != null;
    }

    public void setFocusChangedListener(OnFocusChangedListener focusChangedListener) {
        this.focusChangedListener = focusChangedListener;
    }

    public boolean hasStateChangeListener() {
        return onStateChangeListener != null;
    }

    public void setOnStateChangeListener(OnScrollStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public boolean hasLayoutManager() {
        return layoutManager != null;
    }

    public void setLayoutManager(CardRecyclerView.ScrollControllableLayoutManager layoutManager) {
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
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            Optional.ofNullable(finalEvent).ifPresent(Runnable::run);
            onStateChangeListener.onStateIdle(layoutManager.onSaveInstanceState(), containerPosition);
            finalEvent = null;
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
            return;
        }
        handleMultiItemVisible(recyclerView, firstVisibleItemPosition, lastVisibleItemPosition);
    }

    private synchronized void handelSingleItemVisible(RecyclerView recyclerView, int visiblePosition) {
        Logger.message("handleSingle/itemPos:" + visiblePosition + "/reg Pos:" + registeredPosition);
        if (visiblePosition == registeredPosition) {
            return;
        }
        if (visiblePosition > registeredPosition) {
            registeredPosition = visiblePosition;
            finalEvent = () -> focusChangedListener.onNextFocused(recyclerView, containerPosition, visiblePosition);
            return;
        }
        registeredPosition = visiblePosition;
        finalEvent = () -> focusChangedListener.onPreviousFocused(recyclerView, containerPosition, visiblePosition);
    }

    private synchronized void handleMultiItemVisible(RecyclerView recyclerView, int firstVisibleItemPosition, int lastVisibleItemPosition) {
        Logger.message("handleMulti/f itemPos:" + firstVisibleItemPosition + "/l itemPos :" + lastVisibleItemPosition + "/reg Pos:" + registeredPosition);
        float lastVisibleItemX = Objects.requireNonNull(layoutManager.getChildAt(1)).getX();

        if (firstVisibleItemPosition == registeredPosition) {
            if (lastVisibleItemX > centerX) {
                return;
            }
            registeredPosition = lastVisibleItemPosition;
            finalEvent = () -> focusChangedListener.onNextFocused(recyclerView, containerPosition, lastVisibleItemPosition);
            return;
        }

        if (lastVisibleItemPosition == registeredPosition) {
            if (lastVisibleItemX <= centerX) {
                return;
            }
            registeredPosition = firstVisibleItemPosition;
            finalEvent = () -> focusChangedListener.onPreviousFocused(recyclerView, containerPosition, firstVisibleItemPosition);
            return;
        }

        if (firstVisibleItemPosition < registeredPosition && lastVisibleItemPosition < registeredPosition) {
            if (lastVisibleItemX <= centerX) {
                registeredPosition = lastVisibleItemPosition;
                finalEvent = () -> focusChangedListener.onPreviousFocused(recyclerView, containerPosition, lastVisibleItemPosition);
            } else {
                registeredPosition = firstVisibleItemPosition;
                finalEvent = () -> focusChangedListener.onPreviousFocused(recyclerView, containerPosition, firstVisibleItemPosition);
            }
            return;
        }

        if (firstVisibleItemPosition > registeredPosition && lastVisibleItemPosition > registeredPosition) {
            if (lastVisibleItemX <= centerX) {
                registeredPosition = lastVisibleItemPosition;
                finalEvent = () -> focusChangedListener.onNextFocused(recyclerView, containerPosition, lastVisibleItemPosition);
            } else {
                registeredPosition = firstVisibleItemPosition;
                finalEvent = () -> focusChangedListener.onNextFocused(recyclerView, containerPosition, firstVisibleItemPosition);
            }
        }
    }

    public interface OnFocusChangedListener {
        void onNextFocused(RecyclerView view, int containerPosition, int cardPosition);

        void onPreviousFocused(RecyclerView view, int containerPosition, int cardPosition);
    }

    public interface OnScrollStateChangeListener {
        void onStateIdle(Parcelable savedScrollState, int containerPosition);
    }
}
