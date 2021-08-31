package com.choco_tyranno.team_tree.presentation.detail_page;

import android.graphics.Bitmap;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.choco_tyranno.team_tree.BR;

public class DetailPage extends BaseObservable {
    public static final int READ_MODE = 0;
    public static final int EDIT_MODE = 1;
    private int mode;
    private boolean utilContainerOpened;
    private String photoPath;
    private Bitmap cardImage;

    public DetailPage() {
        mode = READ_MODE;
        utilContainerOpened = false;
    }

    public void setCardImage(Bitmap resource) {
        this.cardImage = resource;
        notifyPropertyChanged(BR.cardImage);
    }

    @Bindable
    public Bitmap getCardImage() {
        return cardImage;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void toggleUtilContainerState() {
        setUtilContainerOpened(!utilContainerOpened);
    }

    @Bindable
    public boolean isUtilContainerOpened() {
        return utilContainerOpened;
    }

    public void setUtilContainerOpened(boolean state) {
        utilContainerOpened = state;
        notifyPropertyChanged(BR.utilContainerOpened);
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

    public boolean isEditMode() {
        return mode == EDIT_MODE;
    }

    public boolean isReadMode() {
        return mode == READ_MODE;
    }
}
