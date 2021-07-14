package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page.DetailPage;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page.DetailPageViewModel;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page.OnClickListenerForTakePictureFab;
import com.choco_tyranno.mycardtree.databinding.ActivityDetailFrameBinding;

public class DetailCardActivity extends AppCompatActivity {
    private ActivityDetailFrameBinding binding;
    private DetailPageViewModel viewModel;
    private DetailPage detailPage;
    private CardDTO ownerCardDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(DetailCardActivity.this).get(DetailPageViewModel.class);

        Intent intent = getIntent();
        ownerCardDTO = (CardDTO) intent.getSerializableExtra("post_card");
        detailPage = new DetailPage();
        viewModel.setDetailPage(detailPage);
        viewModel.initListeners();
        loadImage();
        mainBinding();
        binding.setViewModel(viewModel);
        binding.setCard(ownerCardDTO);
        binding.setPageState(detailPage);
        binding.executePendingBindings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OnClickListenerForTakePictureFab.REQUEST_TAKE_PICTURE && resultCode == RESULT_OK){
            ownerCardDTO.setImagePath(detailPage.getPhotoPath());
            viewModel.update(ownerCardDTO);
            loadImage();
            SingleToastManager.show(SingleToaster.makeTextShort(this,"사진이 저장되었습니다."));
        }
    }

    private void loadImage() {
        new Thread(() -> {
            try {
                int width = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_width));
                int height = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_height));
                Glide.with(DetailCardActivity.this).asBitmap()
                        .load(ownerCardDTO.getImagePath()).addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Logger.hotfixMessage("DetailCardActivity#onResourceReady");
                        detailPage.setCardImage(resource);
                        return false;
                    }
                }).submit(width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void mainBinding() {
        binding = ActivityDetailFrameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setLifecycleOwner(this);
    }

    public ActivityDetailFrameBinding getBinding(){
        return binding;
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
    }

    public CardDTO getCardDto() {
        return ownerCardDTO;
    }


    @Override
    public void onBackPressed() {
        if (detailPage.isEditMode()){
            binding.utilContainerFab.setVisibility(View.GONE);
            binding.takePictureFab.setVisibility(View.GONE);
            binding.openGalleryFab.setVisibility(View.GONE);
            binding.loadContactInfoFab.setVisibility(View.GONE);
        }
        Intent intent = new Intent();
        intent.putExtra("post_card", getCardDto());
        setResult(Activity.RESULT_OK, intent);
        supportFinishAfterTransition();
    }

}