package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.view.View;

import com.choco_tyranno.mycardtree.card_crud_feature.presentation.DetailCardActivity;
import com.choco_tyranno.mycardtree.databinding.ActivityDetailFrameBinding;

public class OnClickListenerForTakePictureFab implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        ActivityDetailFrameBinding binding = ((DetailCardActivity)v.getContext()).getBinding();

    }
}
