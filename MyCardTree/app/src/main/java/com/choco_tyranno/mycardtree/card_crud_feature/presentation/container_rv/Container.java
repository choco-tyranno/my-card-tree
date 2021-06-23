package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.choco_tyranno.mycardtree.BR;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardRecyclerView;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardScrollListener;

import java.util.List;

public class Container extends BaseObservable {
    private int mRootNo;
    private int mFocusCardPosition;
    private Parcelable mSavedScrollState;
    private CardScrollListener mCardScrollListener;
    private CardRecyclerView.ScrollingControlLayoutManager layoutManager;
    private static final int NO_ROOT_NO = -999;
    private static final int DEFAULT_CARD_POSITION = 0;

    public Container() {
        this.mRootNo = NO_ROOT_NO;
        this.mFocusCardPosition = DEFAULT_CARD_POSITION;
        this.mCardScrollListener = new CardScrollListener();
    }

    public CardScrollListener getCardScrollListener() {
        return this.mCardScrollListener;
    }

    public void setLayoutManager(CardRecyclerView.ScrollingControlLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public CardRecyclerView.ScrollingControlLayoutManager getLayoutManager(){
        return this.layoutManager;
    }

    public boolean hasLayoutManager() {
        return layoutManager != null;
    }

    public void setRootNo(int rootNo) {
        this.mRootNo = rootNo;
        notifyPropertyChanged(BR.rootNo);
    }

    public void setFocusCardPosition(int position) {
        this.mFocusCardPosition = position;
        notifyPropertyChanged(BR.focusCardPosition);
    }

    public boolean hasSavedState(){
        return mSavedScrollState != null;
    }

    public void setSavedScrollState(Parcelable scrollState) {
        this.mSavedScrollState = scrollState;
    }

    public Parcelable getSavedScrollState() {
        return mSavedScrollState;
    }

    @Bindable
    public int getRootNo() {
        return mRootNo;
    }

    @Bindable
    public int getFocusCardPosition() {
        return mFocusCardPosition;
    }
}
