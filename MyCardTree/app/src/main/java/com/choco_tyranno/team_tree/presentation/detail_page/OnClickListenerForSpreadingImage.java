package com.choco_tyranno.team_tree.presentation.detail_page;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;

public class OnClickListenerForSpreadingImage implements View.OnClickListener{
    @Override
    public void onClick(View v) {
        DetailCardActivity detailCardActivity= (DetailCardActivity)v.getContext();
        CardDto theCardDto = detailCardActivity.getCardDto();
        if (theCardDto.getImagePath().length() > 0) {
            spreadImage(detailCardActivity, theCardDto);
        } else {
            Toast.makeText(detailCardActivity, "저장된 이미지가 없습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void spreadImage(Context context, CardDto theCardDto) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dialogImgWidth = Math.round(displayMetrics.widthPixels * 0.8f);
        int dialogImgHeight = Math.round(displayMetrics.heightPixels * 0.8f);

        ImageView spreadImageView = new ImageView(context);

        Glide.with(context)
                .load(theCardDto.getImagePath())
                .override(dialogImgWidth, dialogImgHeight)
                .into(spreadImageView);

        Dialog builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(dialogInterface->{});
        builder.addContentView(spreadImageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }
}
