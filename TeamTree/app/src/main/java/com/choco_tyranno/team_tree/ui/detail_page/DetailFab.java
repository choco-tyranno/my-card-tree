package com.choco_tyranno.team_tree.ui.detail_page;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.databinding.ActivityDetailBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailFab {
    private Animation subFabOpen, subFabClose, fabRotateForward, fabRotateBackward;

    public DetailFab(Context context){
        fabRotateForward = AnimationUtils.loadAnimation(context, R.anim.rotate_forward_add_fab);
        fabRotateBackward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward_add_fab);
        subFabOpen = AnimationUtils.loadAnimation(context, R.anim.detail_page_subfab_open);
        subFabClose = AnimationUtils.loadAnimation(context, R.anim.detail_page_subfab_close);
    }

    public void animateFab(ActivityDetailBinding binding) {
        FloatingActionButton utilContainerFab = binding.floatingActionButtonDetailUtilContainer;
        FloatingActionButton takePictureFab = binding.floatingActionButtonDetailCamera;
        FloatingActionButton openGalleryFab = binding.floatingActionButtonDetailGallery;
        FloatingActionButton loadContactInfoFab = binding.floatingActionButtonDetailContactInfo;
        DetailPage detailPage = binding.getDetailPage();
        if (detailPage.isUtilContainerOpened()) {
            utilContainerFab.startAnimation(fabRotateBackward);
            takePictureFab.startAnimation(subFabClose);
            openGalleryFab.startAnimation(subFabClose);
            loadContactInfoFab.startAnimation(subFabClose);
            takePictureFab.setClickable(false);
            openGalleryFab.setClickable(false);
            loadContactInfoFab.setClickable(false);
        } else {
            utilContainerFab.startAnimation(fabRotateForward);
            takePictureFab.startAnimation(subFabOpen);
            openGalleryFab.startAnimation(subFabOpen);
            loadContactInfoFab.startAnimation(subFabOpen);
            takePictureFab.setClickable(true);
            openGalleryFab.setClickable(true);
            loadContactInfoFab.setClickable(true);
        }
        detailPage.toggleUtilContainerState();
    }
}
