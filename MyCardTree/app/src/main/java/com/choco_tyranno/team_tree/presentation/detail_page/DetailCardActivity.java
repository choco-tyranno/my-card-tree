package com.choco_tyranno.team_tree.presentation.detail_page;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.databinding.ActivityDetailFrameBinding;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.SingleToastManager;
import com.choco_tyranno.team_tree.presentation.SingleToaster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_PICTURES;

public class DetailCardActivity extends AppCompatActivity {
    private ActivityDetailFrameBinding binding;
    private DetailPageViewModel viewModel;
    private DetailPage detailPage;
    private CardDto ownerCardDto;
    private DetailFab detailFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(DetailCardActivity.this).get(DetailPageViewModel.class);
        Intent intent = getIntent();
        ownerCardDto = (CardDto) intent.getSerializableExtra("post_card");
        detailPage = new DetailPage();
        detailFab = new DetailFab(this);
        viewModel.setDetailPage(detailPage);
        viewModel.initListeners();
        loadDefaultCardImage();
        loadImage();
        mainBinding();
        binding.setViewModel(viewModel);
        binding.setCard(ownerCardDto);
        binding.setDetailPage(detailPage);
        binding.executePendingBindings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OnClickListenerForTakePictureFab.REQUEST_TAKE_PICTURE && resultCode == RESULT_OK) {
            ownerCardDto.setImagePath(detailPage.getPhotoPath());
            viewModel.update(ownerCardDto);
            loadImage();
            SingleToastManager.show(SingleToaster.makeTextShort(this, "사진이 저장되었습니다."));
        }

        if (requestCode == OnClickListenerForOpenGalleryFab.REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {
            OutputStream outputStream = null;
            if (data == null) {
                SingleToastManager.show(SingleToaster.makeTextShort(this, "사진 불러오기 실패."));
                return;
            }
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                File photoFile;
                photoFile = createImageFile(this, detailPage);
                outputStream = new FileOutputStream(photoFile.getAbsolutePath());
                img.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null)
                        outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ownerCardDto.setImagePath(detailPage.getPhotoPath());
            viewModel.update(ownerCardDto);
            loadImage();
            SingleToastManager.show(SingleToaster.makeTextShort(this, "사진이 변경되었습니다."));
        }

        if (requestCode == OnClickListenerForLoadContactInfoFab.REQUEST_LOAD_CONTACT_INFO && resultCode == RESULT_OK) {
            if (data == null) {
                SingleToastManager.show(SingleToaster.makeTextShort(this, "연락처 불러오기 실패."));
                return;
            }
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER},
                    null, null, null);
            cursor.moveToFirst();
            String retrievedName = cursor.getString(0);
            String retrievedPhone = cursor.getString(1);
            ownerCardDto.setTitle(retrievedName);
            ownerCardDto.setContactNumber(retrievedPhone);
            cursor.close();
            viewModel.update(ownerCardDto);
            SingleToastManager.show(SingleToaster.makeTextShort(this, "연락처를 불러왔습니다."));
        }
    }

    private File createImageFile(Context context, DetailPage pageState) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
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
        if (TextUtils.equals(ownerCardDto.getImagePath(), ""))
            return;
        new Thread(() -> {
            try {
                int width = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_width));
                int height = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_height));
                Glide.with(DetailCardActivity.this).asBitmap()
                        .load(ownerCardDto.getImagePath()).addListener(new RequestListener<Bitmap>() {
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

    public CardDto getCardDto() {
        return ownerCardDto;
    }

    public DetailFab getDetailFab() {
        return detailFab;
    }

    @Override
    public void onBackPressed() {
        binding.utilContainerFab.setVisibility(View.GONE);
        binding.takePictureFab.setVisibility(View.GONE);
        binding.openGalleryFab.setVisibility(View.GONE);
        binding.loadContactInfoFab.setVisibility(View.GONE);
        
        Intent intent = new Intent();
        intent.putExtra("post_card", getCardDto());
        setResult(Activity.RESULT_OK, intent);
        supportFinishAfterTransition();
    }

}