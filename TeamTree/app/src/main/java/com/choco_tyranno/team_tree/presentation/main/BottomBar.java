package com.choco_tyranno.team_tree.presentation.main;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.choco_tyranno.team_tree.R;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class BottomBar extends View implements DependentView{

    public BottomBar(Context context) {
        super(context);
        ready();
    }

    public BottomBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ready();
    }

    public BottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ready();
    }

    public BottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        ready();
    }

    public void ready() {
        if (ready.get())
            return;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                BottomBar.this.ready.set(true);
                if (!attributeSettingActions.isEmpty()) {
                    while (!attributeSettingActions.isEmpty()) {
                        Runnable action = attributeSettingActions.poll();
                        Optional.ofNullable(action).ifPresent(Runnable::run);
                    }
                }
            }
        });
    }


    //Promise the param(Button newCardButton) view layout is ready.
    public void setHeightByNewCardButton(@NonNull View newCardButton) {
        Runnable action = () -> {
            ConstraintSet constraintSet = new ConstraintSet();
            ConstraintLayout parent = (ConstraintLayout) this.getParent();
            constraintSet.clone(parent);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) newCardButton.getLayoutParams();
            final int newCardBottomMargin = params.bottomMargin;
            final int bottomBarHeight = newCardButton.getHeight() / 2 + newCardBottomMargin;
            constraintSet.constrainHeight(this.getId(), bottomBarHeight);
            constraintSet.applyTo(parent);
        };
        postAttributeSettingAction(action);
    }

}
