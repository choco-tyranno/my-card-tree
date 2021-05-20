package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.NullPassUtil;

public class CardScrollListener extends RecyclerView.OnScrollListener {
    private final OnFocusChangedListener focusChangedListener;
    private LinearLayoutManager layoutManager;
    private int registeredPosition;
    private int containerPosition;

    public CardScrollListener(OnFocusChangedListener listener) {
        this.focusChangedListener = listener;
        this.layoutManager = null;
        this.registeredPosition = RecyclerView.NO_POSITION;
    }

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public void setContainerPosition(int containerPosition) {
        this.containerPosition = containerPosition;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        if (layoutManager == null) {
            return;
        }
        super.onScrolled(recyclerView, dx, dy);
        int completelyVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (completelyVisibleItemPosition == RecyclerView.NO_POSITION) {
            return;
        }
        if (registeredPosition == RecyclerView.NO_POSITION) {
            registeredPosition = completelyVisibleItemPosition;
            return;
        }

        if (registeredPosition == completelyVisibleItemPosition)
            return;

        if (registeredPosition < completelyVisibleItemPosition) {
//            Toast.makeText(recyclerView.getContext(), "onNext", Toast.LENGTH_SHORT).show();
            registeredPosition = completelyVisibleItemPosition;
            focusChangedListener.onNextFocused(recyclerView, completelyVisibleItemPosition, containerPosition);
            return;
        }

//        Toast.makeText(recyclerView.getContext(), "onPrev", Toast.LENGTH_SHORT).show();
        registeredPosition = completelyVisibleItemPosition;
        focusChangedListener.onPreviousFocused(recyclerView, completelyVisibleItemPosition, containerPosition);
    }

    public interface OnFocusChangedListener {
        void onNextFocused(RecyclerView view,int position, int containerPosition);
        void onPreviousFocused(RecyclerView view, int position, int containerPosition);
    }
}
