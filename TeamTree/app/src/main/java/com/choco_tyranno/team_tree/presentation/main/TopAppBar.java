package com.choco_tyranno.team_tree.presentation.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import java.util.Optional;

public class TopAppBar extends View implements DependentView{
    public TopAppBar(Context context) {
        super(context);
    }

    public TopAppBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TopAppBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TopAppBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void ready() {
        if (ready.get())
            return;
        TopAppBar view = this;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                view.ready.set(true);
                if (!attributeSettingActions.isEmpty()) {
                    while (!attributeSettingActions.isEmpty()) {
                        Runnable action = attributeSettingActions.poll();
                        Optional.ofNullable(action).ifPresent(Runnable::run);
                    }
                }
            }
        });
    }

    @Override
    public void postAttributeSettingAction(Runnable action) {

    }

}
