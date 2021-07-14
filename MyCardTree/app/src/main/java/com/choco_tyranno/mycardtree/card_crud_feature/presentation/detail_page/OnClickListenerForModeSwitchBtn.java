package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.view.View;

public class OnClickListenerForModeSwitchBtn implements View.OnClickListener {
    DetailPageState pageState;

    public OnClickListenerForModeSwitchBtn(DetailPageState pageState){
        this.pageState = pageState;
    }

    @Override
    public void onClick(View v) {
        pageState.switchMode();

    }
}
