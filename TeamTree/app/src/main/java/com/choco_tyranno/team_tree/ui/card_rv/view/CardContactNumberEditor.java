package com.choco_tyranno.team_tree.ui.card_rv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.choco_tyranno.team_tree.ui.main.DependentView;
import com.google.android.material.textview.MaterialTextView;

import java.util.Optional;

public class CardContactNumberEditor extends AppCompatEditText implements DependentView {

    public CardContactNumberEditor(@NonNull Context context) {
        super(context);
        ready();
    }

    public CardContactNumberEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ready();
    }

    public CardContactNumberEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ready();
    }

    @Override
    public void ready() {
        if (ready.get())
            return;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                CardContactNumberEditor.this.ready.set(true);
                if (!attributeSettingActions.isEmpty()) {
                    while (!attributeSettingActions.isEmpty()) {
                        Runnable action = attributeSettingActions.poll();
                        Optional.ofNullable(action).ifPresent(Runnable::run);
                    }
                }
            }
        });
    }

    public void setTextSizeByTitle(View baseView){
        int autoSizedTextPx = (int) ((MaterialTextView)baseView).getTextSize();
        setTextSize(TypedValue.COMPLEX_UNIT_PX, autoSizedTextPx);
    }
}
