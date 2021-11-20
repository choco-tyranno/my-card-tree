package com.choco_tyranno.team_tree.presentation.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Optional;

public class NewCardButton extends androidx.appcompat.widget.AppCompatButton implements DependentView{
    public NewCardButton(@NonNull Context context) {
        super(context);
        ready();
    }

    public NewCardButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ready();
    }

    public NewCardButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ready();
    }


    @Override
    public void ready() {
        if (ready.get())
            return;
        NewCardButton view = this;
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

    private void initAttributes(){
        setMarginBottom();
    }

    private void setMarginBottom() {
        Runnable action = ()->{
            final int newCardButtonHeight = this.getHeight();
            final int newCardButtonMarginBottom = newCardButtonHeight / 4;
            ConstraintSet constraintSet = new ConstraintSet();
            ConstraintLayout parent = (ConstraintLayout) this.getParent();
            constraintSet.clone(parent);
            constraintSet.setMargin(this.getId(), ConstraintLayout.LayoutParams.BOTTOM, newCardButtonMarginBottom);
            constraintSet.applyTo(parent);
        };
        postAttributeSettingAction(action);
    }

}
