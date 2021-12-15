package com.choco_tyranno.team_tree.ui.detail_page;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.choco_tyranno.team_tree.databinding.ActivityDetailBinding;

public class OnClickListenerForBackBtn implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        DetailCardActivity detailCardActivity = ((DetailCardActivity)v.getContext());
        ActivityDetailBinding binding = detailCardActivity.getBinding();
        binding.floatingActionButtonDetailUtilContainer.setVisibility(View.GONE);
        binding.cameraButtonDetailCamera.setVisibility(View.GONE);
        binding.floatingActionButtonDetailGallery.setVisibility(View.GONE);
        binding.floatingActionButtonDetailContactInfo.setVisibility(View.GONE);
        Intent intent = new Intent();
        intent.putExtra("post_card", detailCardActivity.getCardDto());
        detailCardActivity.setResult(Activity.RESULT_OK, intent);
        detailCardActivity.supportFinishAfterTransition();
    }
}
