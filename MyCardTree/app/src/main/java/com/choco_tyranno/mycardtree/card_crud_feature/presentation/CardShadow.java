package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.TextDrawable;

import java.util.concurrent.ExecutionException;

public class CardShadow extends View.DragShadowBuilder {
    int mWidth, mHeight;
    final float textStrokeWidth = 4.0f;
    final float boundStrokeWidth = 15.0f;

    public CardShadow(View v) {
        super(v);
//        this.mWidth = v.getResources().getDimensionPixelSize(R.dimen.createCardFab_shadow_width);
//        this.mHeight = v.getResources().getDimensionPixelSize(R.dimen.createCardFab_shadow_height);

        Paint textPaint = new Paint();
        String text = "CARD";
        Rect rect = new Rect();
        textPaint.setTextSize(100.f);
        textPaint.getTextBounds(text,0,text.length(),rect);
        mWidth = rect.right - rect.left;
        mHeight =rect.bottom -rect.top;
//        this.mHeight = v.getHeight();
    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
//        outShadowSize.set(mWidth+(int)strokeWidth+60, mHeight+(int)strokeWidth+60);
//        outShadowSize.set(mWidth*2+(int)boundStrokeWidth, mHeight*2+(int)boundStrokeWidth);
        outShadowSize.set(400+(int)boundStrokeWidth*2, 300+(int)boundStrokeWidth*2);
        outShadowTouchPoint.set(0, 0);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        Paint boundPaint = new Paint();
        boundPaint.setColor(Color.BLUE);
        boundPaint.setStyle(Paint.Style.STROKE);
        boundPaint.setStrokeWidth(boundStrokeWidth);
//        boundPaint

        Paint textPaint = new Paint();
        String text = "CARD";
        Rect rect = new Rect();
        textPaint.setTextSize(100.f);
        textPaint.getTextBounds(text,0,text.length(),rect);
//        textPaint.setTextAlign(Paint.Align.CENTER);
        Logger.message("textBounds/left:"+rect.left+"/top:"+rect.top+"/right:"+rect.right+"/bottom:"+rect.bottom);

        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        textPaint.setStrokeWidth(textStrokeWidth);
        textPaint.setColor(Color.BLACK);
        canvas.drawText(text, (-rect.left+rect.right)/2, 150+(-rect.top), textPaint);
//        canvas.drawText(text, rect.left, rect.bottom, textPaint);

        canvas.drawRoundRect(new RectF(boundStrokeWidth, boundStrokeWidth,400f+boundStrokeWidth,300f+boundStrokeWidth),15.0f,15.0f,boundPaint);

        /*textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.YELLOW);
        canvas.drawText(text, -rect.left+strokeWidth/2, -rect.top+strokeWidth/2, textPaint);*/

//        textPaint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 50, 50, paint);
    }
}
