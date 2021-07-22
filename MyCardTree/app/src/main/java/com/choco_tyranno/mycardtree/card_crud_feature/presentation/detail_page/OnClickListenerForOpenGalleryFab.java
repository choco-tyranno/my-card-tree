package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;

public class OnClickListenerForOpenGalleryFab implements View.OnClickListener {
    public static final int REQUEST_OPEN_GALLERY = 20;
    @Override
    public void onClick(View v) {
        DetailCardActivity detailCardActivity = (DetailCardActivity) v.getContext();
        Intent intent = new Intent();
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setAction(Intent.ACTION_PICK);
        detailCardActivity.startActivityForResult(intent, REQUEST_OPEN_GALLERY);
        detailCardActivity.getDetailFab().animateFab(detailCardActivity.getBinding());
    }
}
