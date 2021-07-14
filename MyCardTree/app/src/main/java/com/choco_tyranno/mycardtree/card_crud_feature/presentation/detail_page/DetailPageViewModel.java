package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;

public class DetailPageViewModel extends AndroidViewModel {

    View.OnClickListener onClickListenerForModeSwitchBtn;
    View.OnClickListener onClickListenerForBackBtn;
    View.OnClickListener onClickListenerForSaveBtn;
    DetailPageState pageState;

    public DetailPageViewModel(@NonNull Application application) {
        super(application);
    }

    public void setPageState(DetailPageState pageState){
        this.pageState = pageState;
    };

    public void initListeners(){
        this.onClickListenerForModeSwitchBtn = new OnClickListenerForModeSwitchBtn(pageState);
        this.onClickListenerForBackBtn = new OnClickListenerForBackBtn();
        this.onClickListenerForSaveBtn = new OnClickListenerForSaveBtn();
    }

    public View.OnClickListener getOnClickListenerForModeSwitchBtn(){
        return onClickListenerForModeSwitchBtn;
    }

    public View.OnClickListener getOnClickListenerForBackBtn(){
        return onClickListenerForBackBtn;
    }

    public View.OnClickListener getOnClickListenerForSaveBtn(){
        return onClickListenerForSaveBtn;
    }

}
