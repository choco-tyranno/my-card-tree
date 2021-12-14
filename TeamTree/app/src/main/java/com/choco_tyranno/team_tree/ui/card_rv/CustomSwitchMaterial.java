package com.choco_tyranno.team_tree.ui.card_rv;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class CustomSwitchMaterial extends SwitchMaterial {
    public CustomSwitchMaterial(@NonNull Context context) {
        super(context);
    }

    public CustomSwitchMaterial(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasuredWidth()*3;
        super.setMeasuredDimension(width,width*2/3);
        super.onMeasure(width, MeasureSpec.makeMeasureSpec(width*2/3, MeasureSpec.AT_MOST));
    }
}
