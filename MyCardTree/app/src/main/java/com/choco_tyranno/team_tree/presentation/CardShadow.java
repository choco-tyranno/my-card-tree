package com.choco_tyranno.team_tree.presentation;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.choco_tyranno.team_tree.R;

public class CardShadow extends View.DragShadowBuilder {
    private final float textStrokeWidth;
    private final float boundStrokeWidth;
    private final int rectWidth;
    private final int rectHeight;
    private final String text;
    private final Paint textPaint, boundPaint;
    private final Rect textRect;
    private final float ascent, descent;
    private final float xRadius, yRadius;

    public CardShadow(View v) {
        super(v);
        this.textStrokeWidth = v.getResources().getInteger(R.integer.createCardFab_shadow_textStrokeWidth);
        this.boundStrokeWidth = v.getResources().getInteger(R.integer.createCardFab_shadow_boundStrokeWidth);
        this.rectWidth = v.getResources().getInteger(R.integer.createCardFab_shadow_boundRectWidth);
        this.rectHeight = v.getResources().getInteger(R.integer.createCardFab_shadow_boundRectHeight);
        this.xRadius = v.getResources().getInteger(R.integer.createCardFab_shadow_xRadius);
        this.yRadius = v.getResources().getInteger(R.integer.createCardFab_shadow_yRadius);
        this.boundPaint = new Paint();
        boundPaint.setColor(Color.BLUE);
        boundPaint.setStyle(Paint.Style.STROKE);
        boundPaint.setStrokeWidth(boundStrokeWidth);
        this.textPaint = new Paint();
        this.text = v.getResources().getString(R.string.card_shadow_text);
        this.textRect = new Rect();
        textPaint.setTextSize(v.getResources().getInteger(R.integer.createCardFab_shadow_textSize));
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        this.ascent = textPaint.getFontMetrics().ascent;
        this.descent = textPaint.getFontMetrics().descent;
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setStrokeWidth(textStrokeWidth);
        textPaint.setColor(Color.BLACK);
    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        outShadowSize.set(rectWidth + (int) boundStrokeWidth * 2, rectHeight + (int) boundStrokeWidth * 2);
        outShadowTouchPoint.set(rectWidth/2+(int)boundStrokeWidth, rectHeight+(int)boundStrokeWidth);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        canvas.drawText(text, (float) (textRect.right - textRect.left) / 2 + boundStrokeWidth,
                (float)(-textRect.top+rectHeight)/2 + boundStrokeWidth, textPaint);
        canvas.drawRoundRect(new RectF(boundStrokeWidth, (float) boundStrokeWidth,
                rectWidth + boundStrokeWidth, rectHeight + boundStrokeWidth), xRadius, yRadius, boundPaint);
    }
}
