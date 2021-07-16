package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;

import com.choco_tyranno.mycardtree.card_crud_feature.presentation.DetailCardActivity;
import com.choco_tyranno.mycardtree.databinding.ActivityDetailFrameBinding;

public class OnClickListenerForOpenGalleryFab implements View.OnClickListener {
    public static final int REQUEST_OPEN_GALLERY = 20;
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setAction(Intent.ACTION_PICK);
        ((DetailCardActivity)v.getContext()).getBinding();
        ((DetailCardActivity)v.getContext()).startActivityForResult(intent, REQUEST_OPEN_GALLERY);
    }
}
