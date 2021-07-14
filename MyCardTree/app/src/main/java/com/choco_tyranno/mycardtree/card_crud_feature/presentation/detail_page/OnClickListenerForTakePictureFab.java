package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.DetailCardActivity;
import com.choco_tyranno.mycardtree.databinding.ActivityDetailFrameBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.DIRECTORY_PICTURES;

public class OnClickListenerForTakePictureFab implements View.OnClickListener {
    public static final int REQUEST_TAKE_PICTURE = 10;

    @Override
    public void onClick(View v) {
        String currentPhotoPath;
        Uri photoUri;
        //
        DetailCardActivity detailCardActivity = (DetailCardActivity)v.getContext();
//        ActivityDetailFrameBinding binding = ((DetailCardActivity)v.getContext()).getBinding();
//        CardDTO cardDTO = binding.getCard();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(detailCardActivity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(detailCardActivity);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(detailCardActivity.getApplicationContext(),
                        detailCardActivity.getPackageName(),
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                detailCardActivity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
            } else {
                Toast.makeText(detailCardActivity, "사진촬영을 할 수 없습니다. 저장공간을 확인하고, 관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(detailCardActivity, "사진촬영을 할 수 없습니다. 관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        setCurrentPhotoPath(image.getAbsolutePath());
        return image;
    }

}
