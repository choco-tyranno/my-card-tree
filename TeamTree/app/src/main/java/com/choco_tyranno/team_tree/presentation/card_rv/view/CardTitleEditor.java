package com.choco_tyranno.team_tree.presentation.card_rv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.choco_tyranno.team_tree.presentation.main.DependentView;
import com.google.android.material.textview.MaterialTextView;

import java.util.Optional;

public class CardTitleEditor extends AppCompatEditText implements DependentView {

    public CardTitleEditor(@NonNull Context context) {
        super(context);
        ready();
    }

    public CardTitleEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ready();
    }

    public CardTitleEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                CardTitleEditor.this.ready.set(true);
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
