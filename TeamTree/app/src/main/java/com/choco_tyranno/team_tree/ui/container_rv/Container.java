package com.choco_tyranno.team_tree.ui.container_rv;

import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.choco_tyranno.team_tree.BR;
import com.choco_tyranno.team_tree.ui.card_rv.CardScrollListener;

/**
 * Container.class will be injected into CardViewModel.class
 * So, only data instance can be available into field.
 * */
public class Container extends BaseObservable {
    private int mRootNo;
    private int mFocusCardPosition;
    private Parcelable mSavedScrollState;
    private CardScrollListener mCardScrollListener;
    public static final int NO_ROOT_NO = -999;
    public static final int DEFAULT_CARD_POSITION = 0;

    public Container() {
        this.mRootNo = NO_ROOT_NO;
        this.mFocusCardPosition = DEFAULT_CARD_POSITION;
        this.mCardScrollListener = new CardScrollListener();
    }

    public Container(int rootNo) {
        this.mRootNo = rootNo;
        this.mFocusCardPosition = DEFAULT_CARD_POSITION;
        this.mCardScrollListener = new CardScrollListener();
    }

    public CardScrollListener getCardScrollListener() {
        return this.mCardScrollListener;
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
