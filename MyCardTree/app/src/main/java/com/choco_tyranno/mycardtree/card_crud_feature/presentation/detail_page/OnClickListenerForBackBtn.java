package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.app.Activity;
import android.view.View;

import com.choco_tyranno.mycardtree.card_crud_feature.presentation.DetailCardActivity;

public class OnClickListenerForBackBtn implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        ((DetailCardActivity)v.getContext()).supportFinishAfterTransition();
    }
}
