package com.choco_tyranno.team_tree.presentation.card_rv;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.choco_tyranno.team_tree.BR;

public class ObservableBitmap extends BaseObservable {
    private Bitmap cardThumbnail;
    public ObservableBitmap(){

    }

    public ObservableBitmap(Bitmap bitmap){
        this.cardThumbnail = bitmap;
    }

    public void setCardThumbnail(@Nullable Bitmap bitmap){
        this.cardThumbnail = bitmap;
        notifyPropertyChanged(BR.thumbnail);
    }

    @Bindable
    public Bitmap getThumbnail(){
        return cardThumbnail;
    }

}
