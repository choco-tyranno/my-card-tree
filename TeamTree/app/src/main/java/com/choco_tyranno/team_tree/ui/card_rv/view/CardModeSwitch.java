package com.choco_tyranno.team_tree.ui.card_rv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.ui.main.DependentView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Optional;

public class CardModeSwitch extends SwitchMaterial implements DependentView {
    public CardModeSwitch(@NonNull Context context) {
        super(context);
        ready();
    }

    public CardModeSwitch(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ready();
    }

    public CardModeSwitch(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                CardModeSwitch.this.ready.set(true);
                if (!attributeSettingActions.isEmpty()) {
                    while (!attributeSettingActions.isEmpty()) {
                        Runnable action = attributeSettingActions.poll();
                        Optional.ofNullable(action).ifPresent(Runnable::run);
                    }
                }
            }
        });
    }

    public void setScaleByCardFrame(View cardFrame){
        Runnable action = ()->{
            float switchRatioToCardFrame = Float.parseFloat(getResources().getString(R.string.cardFront_cardSwitchRatioToCardFrame));
            int cardFramePx = cardFrame.getWidth();
            int switchPx = getWidth();
            if (cardFramePx != 0) {
                float multipleValue = switchRatioToCardFrame * cardFramePx / switchPx;
                setScaleX(multipleValue);
                setScaleY(multipleValue);
            }
        };
        postAttributeSettingAction(action);
    }
}
