package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ObservableString extends BaseObservable {
    private String value;

    public ObservableString(){
        this.value = "";
    }

    public void setValue(String value){
        this.value = value;
    }

    @Bindable
    public String getValue(){
        return value;
    }
}
