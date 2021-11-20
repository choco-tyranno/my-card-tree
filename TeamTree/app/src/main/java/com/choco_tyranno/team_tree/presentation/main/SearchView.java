package com.choco_tyranno.team_tree.presentation.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Optional;

public class SearchView extends androidx.appcompat.widget.SearchView implements DependentView{

    @Override
    public void ready() {
        if (ready.get())
            return;
        SearchView view = this;
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

    public void setConstrainMinHeightByTopAppBar(@NonNull View baseView){
        TopAppBar topAppBar = (TopAppBar) baseView;
        Runnable action = ()->{
            final ConstraintSet constraintSet = new ConstraintSet();
            ConstraintLayout parent = (ConstraintLayout) this.getParent();
            constraintSet.clone(parent);
            constraintSet.constrainMinHeight(this.getId(), topAppBar.getHeight());
            constraintSet.applyTo(parent);
        };
        postAttributeSettingAction(action);
    }

    public SearchView(@NonNull Context context) {
        super(context);
        ready();
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ready();
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ready();
    }
}
