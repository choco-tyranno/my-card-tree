package com.choco_tyranno.team_tree.ui.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Optional;

public class TopAppBar extends View implements DependentView{
    public TopAppBar(Context context) {
        super(context);
        ready();
    }

    public TopAppBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ready();
    }

    public TopAppBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ready();
    }

    public TopAppBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        ready();
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
                initAttributes();
                if (!attributeSettingActions.isEmpty()) {
                    while (!attributeSettingActions.isEmpty()) {
                        Runnable action = attributeSettingActions.poll();
                        Optional.ofNullable(action).ifPresent(Runnable::run);
                    }
                }
            }
        });
    }

    private void initAttributes() {
        setConstrainFixedHeight();
    }

    private void setConstrainFixedHeight() {
        Runnable action = ()->{
            final ConstraintSet constraintSet = new ConstraintSet();
            ConstraintLayout parent = (ConstraintLayout) this.getParent();
            constraintSet.clone(parent);
            constraintSet.constrainHeight(this.getId(), this.getHeight());
            constraintSet.applyTo(parent);
        };
        
        postAttributeSettingAction(action);
    }
}
