package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.TextDrawable;

import java.util.concurrent.ExecutionException;

public class CardShadow extends View.DragShadowBuilder {
    int mWidth, mHeight;

    public CardShadow(View v) {
        super(v);
        this.mWidth = v.getWidth();
        this.mHeight = v.getHeight();
    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        outShadowSize.set(mWidth*3, mHeight*2);
        outShadowTouchPoint.set(mWidth / 2, mHeight);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(new RectF(mWidth*3,0, 0, mHeight*2), 50, 50, paint);
    }
}
