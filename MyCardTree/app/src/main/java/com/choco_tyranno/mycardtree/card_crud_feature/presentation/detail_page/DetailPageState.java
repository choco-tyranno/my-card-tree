package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.choco_tyranno.mycardtree.BR;

public class DetailPageState extends BaseObservable {
    public static final int READ_MODE = 0;
    public static final int EDIT_MODE = 1;
    private int mode;

    public DetailPageState() {
        mode = READ_MODE;
    }

    @Bindable
    public int getPageMode() {
        return mode;
    }

    public void switchMode() {
        if (mode == READ_MODE) {
            setMode(EDIT_MODE);
            return;
        }
        if (mode == EDIT_MODE)
            setMode(READ_MODE);
    }

    private void setMode(int mode) {
        this.mode = mode;
        notifyPropertyChanged(BR.pageMode);
    }

    public int isEditMode() {
        if (mode == EDIT_MODE)
            return View.VISIBLE;
        return View.INVISIBLE;
    }

    public int isReadMode() {
        if (mode == READ_MODE)
            return View.VISIBLE;
        return View.INVISIBLE;
    }
}
