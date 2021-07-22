package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.view.View;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.databinding.ActivityDetailFrameBinding;

public class OnClickListenerForUtilContainerFab implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        DetailCardActivity detailCardActivity = (DetailCardActivity) v.getContext();
        ActivityDetailFrameBinding binding = detailCardActivity.getBinding();
        detailCardActivity.getDetailFab().animateFab(binding);
    }

}
