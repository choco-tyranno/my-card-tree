package com.choco_tyranno.team_tree.ui.detail_page.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.choco_tyranno.team_tree.ui.main.DependentView;
import com.google.android.material.button.MaterialButton;

import java.util.Optional;

public class SearchPageItem extends MaterialButton implements DependentView {

    public SearchPageItem(@NonNull Context context) {
        super(context);
        ready();
    }

    public SearchPageItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ready();
    }

    public SearchPageItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                SearchPageItem.this.ready.set(true);
                if (!attributeSettingActions.isEmpty()) {
                    while (!attributeSettingActions.isEmpty()) {
                        Runnable action = attributeSettingActions.poll();
                        Optional.ofNullable(action).ifPresent(Runnable::run);
                    }
                }
            }
        });
    }

    public void setWidthByPrevArrow(View baseView){
        Runnable action = ()->{
            int searchPageItemWidth = (int)((float)baseView.getWidth()*1.6f);
            setWidth(searchPageItemWidth);
        };
        postAttributeSettingAction(action);
    }

    public void setHeightByPrevArrow(View baseView){
        Runnable action = ()->{
            int searchPageItemWidth = (int)((float)baseView.getWidth()*1.6f);
            setHeight(searchPageItemWidth);
        };
        postAttributeSettingAction(action);
    }
}
