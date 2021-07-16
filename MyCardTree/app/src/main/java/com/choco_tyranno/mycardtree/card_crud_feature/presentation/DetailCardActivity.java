package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page.OnClickListenerForOpenGalleryFab;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page.OnClickListenerForTakePictureFab;
import com.choco_tyranno.mycardtree.databinding.ActivityDetailFrameBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.DIRECTORY_PICTURES;

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
        loadDefaultCardImage();
        loadImage();
        mainBinding();
        binding.setViewModel(viewModel);
        binding.setCard(ownerCardDTO);
        binding.setDetailPage(detailPage);
        binding.executePendingBindings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OnClickListenerForTakePictureFab.REQUEST_TAKE_PICTURE && resultCode == RESULT_OK) {
            ownerCardDTO.setImagePath(detailPage.getPhotoPath());
            viewModel.update(ownerCardDTO);
            loadImage();
            SingleToastManager.show(SingleToaster.makeTextShort(this, "사진이 저장되었습니다."));
        }

        if (requestCode == OnClickListenerForOpenGalleryFab.REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {
            OutputStream outputStream = null;
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                File photoFile = null;
                photoFile = createImageFile(this, detailPage);
                outputStream = new FileOutputStream(photoFile.getAbsolutePath());
                img.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ownerCardDTO.setImagePath(detailPage.getPhotoPath());
            viewModel.update(ownerCardDTO);
            loadImage();
            SingleToastManager.show(SingleToaster.makeTextShort(this, "사진이 변경되었습니다."));
        }
    }

    private File createImageFile(Context context, DetailPage pageState) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        pageState.setPhotoPath(image.getAbsolutePath());
        return image;
    }

    private void loadDefaultCardImage() {
        new Thread(() -> {
            try {
                int width = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_width));
                int height = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_height));
                Glide.with(DetailCardActivity.this).asBitmap()
                        .load(R.drawable.default_card_image_01).addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        viewModel.setDefaultCardImage(resource);
                        return false;
                    }
                }).submit(width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadImage() {
        if (TextUtils.equals(ownerCardDTO.getImagePath(), ""))
            return;
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

    public ActivityDetailFrameBinding getBinding() {
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
        if (detailPage.isEditMode()) {
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