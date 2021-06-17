package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.os.Bundle;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.choco_tyranno.mycardtree.BR;

import java.util.List;

public class Container extends BaseObservable {
    private int mRootNo;
    private int mFocusCardPosition;
    private boolean mLayoutSuppressed;
    private Bundle mContainerScrollState;
    private static final int NO_ROOT_NO = -999;
    private static final int DEFAULT_CARD_POSITION = 0;

    public Container(){
        this.mRootNo = NO_ROOT_NO;
        this.mFocusCardPosition = DEFAULT_CARD_POSITION;
        this.mLayoutSuppressed = false;
    }

    public void setRootNo(int rootNo){
        this.mRootNo = rootNo;
        notifyPropertyChanged(BR.rootNo);
    }
    public void setFocusCardPosition(int position){
        this.mFocusCardPosition = position;
        notifyPropertyChanged(BR.focusCardPosition);
    }
    public void setLayoutSuppressed(boolean state){
        this.mLayoutSuppressed = state;
        notifyPropertyChanged(BR.layoutSuppressed);
    }

    public void setContainerScrollState(Bundle scrollState){
        this.mContainerScrollState = scrollState;
    }

    public Bundle getContainerScrollState(){
        return mContainerScrollState;
    }

    @Bindable
    public int getRootNo() {
        return mRootNo;
    }

    @Bindable
    public int getFocusCardPosition() {
        return mFocusCardPosition;
    }

    @Bindable
    public boolean getLayoutSuppressed() {
        return mLayoutSuppressed;
    }
}
