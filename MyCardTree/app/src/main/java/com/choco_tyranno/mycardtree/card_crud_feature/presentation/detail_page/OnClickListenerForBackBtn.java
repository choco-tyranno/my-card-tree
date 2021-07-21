package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.choco_tyranno.mycardtree.databinding.ActivityDetailFrameBinding;

public class OnClickListenerForBackBtn implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        DetailCardActivity detailCardActivity = ((DetailCardActivity)v.getContext());
        ActivityDetailFrameBinding binding = detailCardActivity.getBinding();
        binding.utilContainerFab.setVisibility(View.GONE);
        binding.takePictureFab.setVisibility(View.GONE);
        binding.openGalleryFab.setVisibility(View.GONE);
        binding.loadContactInfoFab.setVisibility(View.GONE);
        Intent intent = new Intent();
        intent.putExtra("post_card", detailCardActivity.getCardDto());
        detailCardActivity.setResult(Activity.RESULT_OK, intent);
        detailCardActivity.supportFinishAfterTransition();
    }
}
